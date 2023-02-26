package com.jdamcd.tflarrivals

import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

private const val LINE = "london-overground"
private const val STATION = "910GSHRDHST"
private const val PLATFORM = "Platform 2"

class Arrivals {

    private val api = TflApi()

    @Throws(NoDataException::class, CancellationException::class)
    suspend fun fetchArrivals(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchArrivals(LINE, STATION))
            if (model.arrivals.isNotEmpty()) {
                return model
            } else throw NoDataException("No arrivals found")
        } catch (e: Exception) {
            throw NoDataException(e.message.orEmpty())
        }
    }

    private fun formatArrivals(apiArrivals: List<ApiArrival>): ArrivalsInfo {
        val station = formatStation(apiArrivals.firstOrNull()?.stationName.orEmpty())
        val platform = apiArrivals.firstOrNull()?.platformName.orEmpty()
        val arrivals = apiArrivals
            .sortedBy { it.timeToStation }
            .filter { it.platformName == PLATFORM }
            .take(3)
            .map {
                Arrival(
                    it.id,
                    formatStation(it.destinationName),
                    formatTime(it.timeToStation)
                )
            }
        return ArrivalsInfo(
            station = "$station: $platform",
            arrivals = arrivals
        )
    }
}

data class ArrivalsInfo(
    val station: String,
    val arrivals: List<Arrival>
)

data class Arrival(
    val id: Int,
    val destination: String,
    val time: String
)

class NoDataException(message: String): Throwable(message = message)

private fun formatTime(seconds: Int) =
    if (seconds < 60) "Due"
    else "${(seconds / 60f).roundToInt()} min"

private fun formatStation(name: String) = name.replace("Rail Station", "").trim()