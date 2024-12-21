package com.jdamcd.tflarrivals.gtfs

actual fun getFilesDir(): String = System.getProperty("java.io.tmpdir")
