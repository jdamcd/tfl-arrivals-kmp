package com.jdamcd.tflarrivals

import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

class Arrivals {

    private val api = TflApi()
    private val settings = Settings()

    @Throws(NoDataException::class, CancellationException::class)
    suspend fun fetchArrivals(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchArrivals(settings.selectedStop))
            if (model.arrivals.isNotEmpty()) {
                return model
            } else throw NoDataException("No arrivals found")
        } catch (e: Exception) {
            throw NoDataException(e.message.orEmpty())
        }
    }

    @Throws(CancellationException::class)
    suspend fun searchStations(query: String): List<StopPoint> {
        return api.searchStations(query).matches
            .map { StopPoint(it.id, it.name) }
    }

    private fun formatArrivals(apiArrivals: List<ApiArrival>): ArrivalsInfo {
        val station = formatStation(apiArrivals.firstOrNull()?.stationName.orEmpty())
        val arrivals = apiArrivals
            .sortedBy { it.timeToStation }
            .filter {
                if (settings.platformFilter.isEmpty()) true
                else it.platformName.contains(settings.platformFilter, ignoreCase = true)
            }
            .take(3)
            .map {
                Arrival(
                    it.id,
                    formatStation(it.destinationName),
                    formatTime(it.timeToStation)
                )
            }
        return ArrivalsInfo(
            station = "$station ${settings.platformFilter}",
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

data class StopPoint(
    val id: String,
    val name: String
)

class NoDataException(message: String): Throwable(message = message)

private fun formatTime(seconds: Int) =
    if (seconds < 60) "Due"
    else "${(seconds / 60f).roundToInt()} min"

private fun formatStation(name: String) = name.replace("Rail Station", "").trim()
