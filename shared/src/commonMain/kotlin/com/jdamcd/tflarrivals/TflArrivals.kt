package com.jdamcd.tflarrivals

import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

object ArrivalsBuilder {
    fun tflArrivals(): Arrivals = TflArrivals(TflApi(), Settings())
}

interface Arrivals {
    @Throws(NoDataException::class, CancellationException::class)
    suspend fun latest(): ArrivalsInfo

    @Throws(CancellationException::class)
    suspend fun searchStops(query: String): List<StopResult>

    @Throws(CancellationException::class)
    suspend fun stopDetails(id: String): StopDetails
}

internal class TflArrivals(
    private val api: TflApi,
    private val settings: Settings
) : Arrivals {
    @Throws(NoDataException::class, CancellationException::class)
    override suspend fun latest(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchArrivals(settings.selectedStopId))
            if (model.arrivals.isNotEmpty()) {
                return model
            } else {
                throw NoDataException("No arrivals found")
            }
        } catch (e: Exception) {
            throw NoDataException(e.message.orEmpty())
        }
    }

    @Throws(CancellationException::class)
    override suspend fun searchStops(query: String): List<StopResult> {
        return api.searchStations(query).matches
            .map { StopResult(it.id, it.name, it.id.startsWith("HUB")) }
    }

    @Throws(CancellationException::class)
    override suspend fun stopDetails(id: String): StopDetails {
        val stopPoint = api.stopDetails(id)
        return StopDetails(
            stopPoint.naptanId,
            stopPoint.commonName,
            stopPoint.children
                .filter { it.stopType == "NaptanMetroStation" || it.stopType == "NaptanRailStation" }
                .map { StopResult(it.naptanId, it.commonName, it.naptanId.startsWith("HUB")) }
        )
    }

    private fun formatArrivals(apiArrivals: List<ApiArrival>): ArrivalsInfo {
        val arrivals =
            apiArrivals
                .sortedBy { it.timeToStation }
                .filter {
                    settings.platformFilter.isEmpty() ||
                        it.platformName.contains(settings.platformFilter, ignoreCase = true)
                }
                .filter { arrival ->
                    settings.directionFilter == SettingsConfig.DIRECTION_FILTER_DEFAULT ||
                        arrival.direction.contains(settings.directionFilter)
                }
                .take(3)
                .map {
                    Arrival(
                        // DLR arrivals all have the same ID, so use hash
                        it.hashCode(),
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
    val name: String,
    val isHub: Boolean
)

data class StopDetails(
    val id: String,
    val name: String,
    val children: List<StopResult>
)

class NoDataException(message: String) : Throwable(message = message)

private fun formatTime(seconds: Int) =
    if (seconds < 60) {
        "Due"
    } else {
        "${(seconds / 60f).roundToInt()} min"
    }

private fun formatStation(name: String) =
    name
        .replace("Rail Station", "")
        .replace("Underground Station", "")
        .replace("DLR Station", "")
        .trim()

private fun formatDirection(direction: String) =
    direction.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
