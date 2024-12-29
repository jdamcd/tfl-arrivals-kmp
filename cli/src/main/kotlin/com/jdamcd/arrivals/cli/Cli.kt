package com.jdamcd.arrivals.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.jdamcd.arrivals.Arrivals
import com.jdamcd.arrivals.Settings
import com.jdamcd.arrivals.SettingsConfig
import com.jdamcd.arrivals.initKoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun main(args: Array<String>) {
    initKoin()
    runBlocking {
        Cli().main(args)
    }
}

private class Cli :
    SuspendingCliktCommand("arrivals"),
    KoinComponent {
    private val arrivals: Arrivals by inject()
    private val settings: Settings by inject()

    val mode by option().choice("tfl", "gtfs").default("tfl")

    override suspend fun run() {
        if (mode == "gtfs") {
            settings.mode = SettingsConfig.MODE_GTFS
        }

        val result = arrivals.latest()
        echo(result.station)
        result.arrivals.forEach {
            echo("%-24s\t%6s".format(it.destination, it.time))
        }
    }
}
