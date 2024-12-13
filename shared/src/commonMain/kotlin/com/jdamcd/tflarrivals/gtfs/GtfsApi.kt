package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.readRawBytes
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use

internal class GtfsApi(private val client: HttpClient) {

    private val baseDir = getFilesDir()
    private val outputDir = "$baseDir/gtfs".toPath()
    private val stopsFileName = "stops.txt"

    suspend fun fetchFeedMessage(url: String): FeedMessage {
        val bodyBytes = client.get(url).bodyAsBytes()
        return FeedMessage.ADAPTER.decode(bodyBytes)
    }

    fun lastDownload(): Long? {
        val path = outputDir.resolve(stopsFileName)
        return if (FileSystem.SYSTEM.exists(path)) {
            FileSystem.SYSTEM.metadata(path).lastModifiedAtMillis
        } else {
            null
        }
    }

    fun readStops(): String {
        val stopsPath = outputDir.resolve(stopsFileName)
        return FileSystem.SYSTEM.read(stopsPath) { readUtf8() }
    }

    suspend fun downloadStops(url: String): String {
        val tempZipFile = "$baseDir/gtfs.zip".toPath()
        try {
            val zipContent = client.get(url).readRawBytes()
            FileSystem.SYSTEM.write(tempZipFile) {
                write(zipContent)
            }
            unpackZip(tempZipFile, outputDir)
            return readStops()
        } finally {
            FileSystem.SYSTEM.delete(tempZipFile)
        }
    }

    private fun unpackZip(source: Path, destination: Path) {
        val zipFile = FileSystem.SYSTEM.openZip(source)
        val paths = zipFile.listRecursively("/".toPath())
            .filter { zipFile.metadata(it).isRegularFile }
            .toList()

        paths.forEach { filePath ->
            zipFile.source(filePath).buffer().use { source ->
                val relativePath = filePath.toString().trimStart('/')
                val fileToWrite = destination.resolve(relativePath)
                fileToWrite.createParentDirectories()
                FileSystem.SYSTEM.sink(fileToWrite).buffer().use { sink ->
                    val bytes = sink.writeAll(source)
                    println("Unzipped: $relativePath to $fileToWrite; $bytes bytes written")
                }
            }
        }
    }
}

private fun Path.createParentDirectories() {
    this.parent?.let { parent ->
        FileSystem.SYSTEM.createDirectories(parent)
    }
}

expect fun getFilesDir(): String
