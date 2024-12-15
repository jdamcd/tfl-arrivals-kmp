package com.jdamcd.tflarrivals.gtfs

internal class GtfsStops(stops: String) {

    private val stopIdToName: Map<String, String> =
        stops
            .split("\n")
            .mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size > 1) parts[0] to parts[1] else null
            }
            .drop(1) // Skip headers
            .toMap()

    fun stopIdToName(stopId: String) = stopIdToName[stopId]
}
