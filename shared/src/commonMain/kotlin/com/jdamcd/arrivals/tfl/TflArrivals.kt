package com.jdamcd.arrivals.tfl

import com.jdamcd.arrivals.Arrival
import com.jdamcd.arrivals.Arrivals
import com.jdamcd.arrivals.ArrivalsInfo
import com.jdamcd.arrivals.NoDataException
import com.jdamcd.arrivals.Settings
import com.jdamcd.arrivals.SettingsConfig
import com.jdamcd.arrivals.StopDetails
import com.jdamcd.arrivals.StopResult
import com.jdamcd.arrivals.TflSearch
import com.jdamcd.arrivals.formatTime
import kotlin.coroutines.cancellation.CancellationException

internal class TflArrivals(
    private val api: TflApi,
    private val settings: Settings
) : Arrivals,
    TflSearch {

    @Throws(NoDataException::class, CancellationException::class)
    override suspend fun latest(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchArrivals(settings.tflStopId))
            if (model.arrivals.isNotEmpty()) {
                return model
            } else {
                throw NoDataException("No arrivals found")
            }
        } catch (e: Exception) {
            throw NoDataException("No connection")
        }
    }

    @Throws(CancellationException::class)
    override suspend fun searchStops(query: String): List<StopResult> = api
        .searchStations(query)
        .matches
        .map { StopResult(it.id, it.name, it.id.startsWith("HUB")) }

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
                .asSequence()
                .sortedBy { it.timeToStation }
                .filter {
                    settings.tflPlatform.isEmpty() ||
                        it.platformName.contains(settings.tflPlatform, ignoreCase = true)
                }.filter { arrival ->
                    settings.tflDirection == SettingsConfig.TFL_DIRECTION_DEFAULT ||
                        arrival.direction.contains(settings.tflDirection)
                }.take(3)
                .map {
                    Arrival(
                        // DLR arrivals all have the same ID, so use hash
                        it.hashCode(),
                        formatStation(it.destinationName),
                        formatTime(it.timeToStation),
                        it.timeToStation
                    )
                }
                .toList()
        return ArrivalsInfo(
            station = stationInfo(),
            arrivals = arrivals
        )
    }

    private fun stationInfo(): String {
        val station = formatStation(settings.tflStopName)
        return if (settings.tflPlatform.isNotEmpty()) {
            "$station: ${settings.tflPlatform}"
        } else if (settings.tflDirection != SettingsConfig.TFL_DIRECTION_DEFAULT) {
            "$station: ${formatDirection(settings.tflDirection)}"
        } else {
            station
        }
    }
}

private fun formatStation(name: String) = name
    .replace("Rail Station", "")
    .replace("Underground Station", "")
    .replace("DLR Station", "")
    .trim()

private fun formatDirection(direction: String) = direction.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}
