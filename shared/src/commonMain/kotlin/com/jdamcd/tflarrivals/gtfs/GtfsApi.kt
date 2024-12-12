package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.readRawBytes
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use

internal class GtfsApi {
    private val client =
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000 // 10 seconds
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }

    suspend fun fetchFeedMessage(url: String): FeedMessage {
        val bodyBytes = client.get(url).bodyAsBytes()
        return FeedMessage.ADAPTER.decode(bodyBytes)
    }

    suspend fun downloadStops(url: String): String {
        val baseDir = getFilesDir()
        val tempZipFile = "$baseDir/gtfs.zip".toPath()
        val outputDir = "$baseDir/gtfs".toPath()
        val stopsFileName = "stops.txt"

        try {
            val zipContent = client.get(url).readRawBytes()
            FileSystem.SYSTEM.write(tempZipFile) {
                write(zipContent)
            }
            unpackZip(tempZipFile, outputDir)

            val extractedFiles = FileSystem.SYSTEM.listRecursively(outputDir).toList()
            val stopsPath = extractedFiles.find { it.name == stopsFileName }
                ?: throw IllegalArgumentException("$stopsFileName not found in package")

            return FileSystem.SYSTEM.read(stopsPath) { readUtf8() }
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
