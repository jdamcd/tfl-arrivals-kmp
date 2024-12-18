package com.jdamcd.tflarrivals.gtfs

internal class GtfsStops(stops: String) {

    private val stopIdToName: Map<String, String> =
        stops
            .split("\n")
            .filter { it.isNotBlank() }
            .let { lines ->
                val header = lines.first().split(",")
                val stopIdIndex = header.indexOf("stop_id")
                val stopNameIndex = header.indexOf("stop_name")
                lines
                    .drop(1) // Skip header
                    .mapNotNull { line ->
                    val parts = line.split(",")
                    if (parts.size > maxOf(stopIdIndex, stopNameIndex)) {
                        parts[stopIdIndex] to parts[stopNameIndex]
                    } else {
                        null
                    }
                }
            }
            .toMap()

    fun stopIdToName(stopId: String) = stopIdToName[stopId]
}
