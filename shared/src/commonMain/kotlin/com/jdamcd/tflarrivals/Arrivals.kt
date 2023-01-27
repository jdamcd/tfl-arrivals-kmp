package com.jdamcd.tflarrivals

import kotlin.math.roundToInt

class Arrivals {

    private val api = TflApi()

    suspend fun fetchArrivals(): List<Arrival> {
        return try {
            val arrivals = api.fetchArrivals(
                line = "london-overground",
                station = "910GSHRDHST",
                direction = "outbound"
            )
            arrivals.take(3)
                .map { Arrival(it.destinationName, formatTime(it.timeToStation)) }
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
    }
}

data class Arrival(
    val destination: String,
    val time: String
)

private fun formatTime(seconds: Int) = "${(seconds / 60f).roundToInt()}min"