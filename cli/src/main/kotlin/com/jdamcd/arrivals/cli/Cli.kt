package com.jdamcd.arrivals.cli

import com.jdamcd.arrivals.Arrivals
import com.jdamcd.arrivals.initKoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun main(args: Array<String>) {
    initKoin()
    runBlocking {
        CliApplication().run()
    }
}

class CliApplication : KoinComponent {
    private val arrivals: Arrivals by inject()

    suspend fun run() {
        val result = arrivals.latest()
        println(result.station)
        result.arrivals.forEach {
            println("%-24s\t%6s".format(it.destination, it.time))
        }
    }
}
