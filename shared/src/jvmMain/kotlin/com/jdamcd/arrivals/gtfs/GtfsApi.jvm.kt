package com.jdamcd.arrivals.gtfs

actual fun getFilesDir(): String = System.getProperty("java.io.tmpdir")
