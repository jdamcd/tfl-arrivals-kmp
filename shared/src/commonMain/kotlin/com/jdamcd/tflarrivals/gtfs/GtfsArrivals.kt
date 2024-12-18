package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedEntity
import com.google.transit.realtime.FeedMessage
import com.google.transit.realtime.TripUpdate
import com.jdamcd.tflarrivals.Arrival
import com.jdamcd.tflarrivals.Arrivals
import com.jdamcd.tflarrivals.ArrivalsInfo
import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.Settings
import com.jdamcd.tflarrivals.formatTime
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException

internal class GtfsArrivals(
    private val api: GtfsApi,
    private val clock: Clock,
    private val settings: Settings
) : Arrivals {

    private lateinit var stops: GtfsStops

    @Throws(NoDataException::class, CancellationException::class)
    override suspend fun latest(): ArrivalsInfo {
        updateStops()
        try {
            val model = formatArrivals(api.fetchFeedMessage(settings.gtfsRealtime))
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
        if (!hasFreshStops()) {
            stops = GtfsStops(api.downloadStops(settings.gtfsSchedule))
            settings.gtfsStopsUpdated = clock.now().epochSeconds
        } else if (!::stops.isInitialized) {
            stops = GtfsStops(api.readStops())
        }
    }

    private fun hasFreshStops(): Boolean {
        val twoDaysInSeconds = 48 * 60 * 60
        return settings.gtfsStopsUpdated + twoDaysInSeconds > clock.now().epochSeconds
    }

    private fun formatArrivals(feedMessage: FeedMessage): ArrivalsInfo {
        val stop = settings.gtfsStop
        val arrivals = getNextArrivalsForStop(stop, feedMessage.entity)
        return ArrivalsInfo(stops.stopIdToName(stop) ?: stop, arrivals)
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
                .map { createArrival(tripUpdate, it) }
        }
        .filter { it.secondsToStop >= 0 }
        .sortedBy { it.secondsToStop }
        .take(3)
        .toList()

    private fun createArrival(
        tripUpdate: TripUpdate,
        stopTimeUpdate: TripUpdate.StopTimeUpdate
    ): Arrival {
        val destinationId = tripUpdate.stop_time_update.last().stop_id!!
        val destinationName = stops.stopIdToName(destinationId) ?: destinationId
        val seconds = secondsToStop(stopTimeUpdate.arrival?.time ?: stopTimeUpdate.departure?.time)
        return Arrival(
            stopTimeUpdate.hashCode(),
            "${tripUpdate.trip.route_id} - $destinationName",
            formatTime(seconds),
            seconds
        )
    }

    private fun secondsToStop(time: Long?): Int {
        if (time == null) {
            return Int.MAX_VALUE
        } else {
            val now = clock.now().epochSeconds
            return (time - now).toInt()
        }
    }
}
