package com.jdamcd.tflarrivals

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.net.URI

object TestHelper {
    fun resource(filename: String): ByteArray {
        val url = javaClass.getResource("/$filename")!!.toString()
        val localPath: Path = URI(url).path.toPath()
        return FileSystem.SYSTEM.read(localPath) { readByteArray() }
    }
}
