package com.jdamcd.tflarrivals.tfl

import com.jdamcd.tflarrivals.Arrival
import com.jdamcd.tflarrivals.Arrivals
import com.jdamcd.tflarrivals.ArrivalsInfo
import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.Settings
import com.jdamcd.tflarrivals.SettingsConfig
import com.jdamcd.tflarrivals.StopDetails
import com.jdamcd.tflarrivals.StopResult
import com.jdamcd.tflarrivals.TflSearch
import com.jdamcd.tflarrivals.formatTime
import kotlin.coroutines.cancellation.CancellationException

internal class TflArrivals(
    private val api: ITflApi,
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
