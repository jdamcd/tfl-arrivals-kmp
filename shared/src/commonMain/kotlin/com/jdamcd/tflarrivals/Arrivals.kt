package com.jdamcd.tflarrivals

import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

class Arrivals {

    private val api = TflApi()
    private val settings = Settings()

    @Throws(NoDataException::class, CancellationException::class)
    suspend fun fetchArrivals(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchArrivals(settings.selectedStopId))
            if (model.arrivals.isNotEmpty()) {
                return model
            } else throw NoDataException("No arrivals found")
        } catch (e: Exception) {
            throw NoDataException(e.message.orEmpty())
        }
    }

    @Throws(CancellationException::class)
    suspend fun searchStops(query: String): List<StopResult> {
        return api.searchStations(query).matches
            .map { StopResult(it.id, it.name) }
    }

    suspend fun stopDetails(id: String): StopDetails {
        val stopPoint = api.stopDetails(id)
        return StopDetails(stopPoint.naptanId, stopPoint.commonName,
            stopPoint.children
                .filter { it.stopType == "NaptanMetroStation" || it.stopType == "NaptanRailStation" }
                .map { StopResult(it.naptanId, it.commonName) }
        )
    }

    private fun formatArrivals(apiArrivals: List<ApiArrival>): ArrivalsInfo {
        val arrivals = apiArrivals
            .sortedBy { it.timeToStation }
            .filter {
                if (settings.platformFilter.isEmpty()) true
                else it.platformName.contains(settings.platformFilter, ignoreCase = true)
            }
            .filter { arrival ->
                if (settings.directionFilter == SettingsConfig.DIRECTION_FILTER_DEFAULT) true
                else arrival.direction.contains(settings.directionFilter) }
            .take(3)
            .map {
                Arrival(
                    it.hashCode(), // DLR arrivals all have the same ID?!
                    formatStation(it.destinationName),
                    formatTime(it.timeToStation)
                )
            }
        return ArrivalsInfo(
            station = stationInfo(),
            arrivals = arrivals
        )
    }

    private fun stationInfo(): String {
        val station = formatStation(settings.selectedStopName)
        return if (settings.platformFilter.isNotEmpty()) {
            "$station: ${settings.platformFilter}"
        } else if (settings.directionFilter != SettingsConfig.DIRECTION_FILTER_DEFAULT) {
            "$station: ${formatDirection(settings.directionFilter)}"
        } else {
            station
        }
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

data class StopResult(
    val id: String,
    val name: String
) {
    fun isHub() = id.startsWith("HUB")
}

data class StopDetails(
    val id: String,
    val name: String,
    val children: List<StopResult>
)

class NoDataException(message: String): Throwable(message = message)

private fun formatTime(seconds: Int) =
    if (seconds < 60) "Due"
    else "${(seconds / 60f).roundToInt()} min"

private fun formatStation(name: String) = name
    .replace("Rail Station", "")
    .replace("Underground Station", "")
    .replace("DLR Station", "")
    .trim()

private fun formatDirection(direction: String) =
    direction.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
