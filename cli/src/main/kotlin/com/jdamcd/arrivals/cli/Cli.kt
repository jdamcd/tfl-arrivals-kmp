package com.jdamcd.arrivals.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.defaultByName
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.help
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

    val mode by option()
        .help("Transit feed type")
        .groupChoice("tfl" to Tfl(), "gtfs" to Gtfs())
        .defaultByName("tfl")

    override suspend fun run() {
        configure()
        try {
            val result = arrivals.latest()
            echo(result.station)
            result.arrivals.forEach {
                echo("%-24s\t%6s".format(it.destination, it.time))
            }
        } catch (e: Exception) {
            echo(e.message)
        }
    }

    private fun configure() {
        when (mode) {
            is Tfl -> {
                val mode = mode as Tfl
                settings.mode = SettingsConfig.MODE_TFL
                mode.station?.let { settings.tflStopId = it }
                mode.platform?.let { settings.tflPlatform = it }
                mode.direction?.let { settings.tflDirection = it }
            }

            is Gtfs -> {
                val mode = mode as Gtfs
                settings.mode = SettingsConfig.MODE_GTFS
                mode.stop?.let { settings.gtfsStop = it }
                mode.realtime?.let { settings.gtfsRealtime = it }
                mode.schedule?.let { settings.gtfsSchedule = it }
            }
        }
    }
}

private sealed class TransitProvider(name: String) : OptionGroup(name)

private class Tfl : TransitProvider("TfL options") {
    val station by option().help("Station ID")
    val platform by option().help("Platform filter (optional)")
    val direction by option()
        .choice("inbound", "outbound", "all")
        .help("Direction filter (optional)")
}

private class Gtfs : TransitProvider("GTFS options") {
    val stop by option().help("Stop ID")
    val realtime by option().help("GTFS-RT feed URL")
    val schedule by option().help("GTFS schedule URL")
}
