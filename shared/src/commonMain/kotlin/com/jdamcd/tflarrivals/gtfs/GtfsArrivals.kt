package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedEntity
import com.google.transit.realtime.FeedMessage
import com.jdamcd.tflarrivals.Arrival
import com.jdamcd.tflarrivals.Arrivals
import com.jdamcd.tflarrivals.ArrivalsInfo
import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.formatTime
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException

internal class GtfsArrivals(
    private val api: GtfsApi
) : Arrivals {

    private lateinit var stops: GtfsStops

    @Throws(NoDataException::class, CancellationException::class)
    override suspend fun latest(): ArrivalsInfo {
        updateStops()
        try {
            val model = formatArrivals(api.fetchFeedMessage())
            if (model.arrivals.isNotEmpty()) {
                return model
            } else {
                throw NoDataException("No arrivals found")
            }
        } catch (e: Exception) {
            throw NoDataException("No connection")
        }
    }

    private suspend fun updateStops() {
        if (!::stops.isInitialized) {
            try {
                val stopsCsv = api.downloadStops("http://web.mta.info/developers/data/nyct/subway/google_transit.zip")
                stops = GtfsStops(stopsCsv)
            } catch (e: Exception) {
                throw NoDataException("Stop data unavailable")
            }
        }
    }

    private fun formatArrivals(feedMessage: FeedMessage): ArrivalsInfo {
        val stop = "A42N"
        val arrivals = getNextArrivalsForStop(stop, feedMessage.entity)
        return ArrivalsInfo(stops.stopIdToName(stop), arrivals)
    }

    private fun getNextArrivalsForStop(
        stopId: String,
        feedItems: List<FeedEntity>
    ): List<Arrival> = feedItems
        .asSequence()
        .mapNotNull { it.trip_update }
        .flatMap { tripUpdate ->
            tripUpdate.stop_time_update
                .filter { it.stop_id == stopId }
                .map { stopTimeUpdate ->
                    val lastStop = stops.stopIdToName(tripUpdate.stop_time_update.last().stop_id)
                    val seconds = secondsToStop(stopTimeUpdate.arrival?.time)
                    Arrival(
                        stopTimeUpdate.hashCode(),
                        "${tripUpdate.trip.route_id} - $lastStop",
                        formatTime(seconds),
                        seconds
                    )
                }
        }
        .filter { it.secondsToStop >= 0 }
        .sortedBy { it.secondsToStop }
        .take(3)
        .toList()

    private fun secondsToStop(time: Long?): Int {
        if (time == null) {
            return Int.MAX_VALUE
        } else {
            val now = Clock.System.now().epochSeconds
            return (time - now).toInt()
        }
    }
}
