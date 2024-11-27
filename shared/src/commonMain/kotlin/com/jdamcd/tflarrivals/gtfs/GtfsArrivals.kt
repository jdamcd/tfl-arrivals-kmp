package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedEntity
import com.google.transit.realtime.FeedMessage
import com.jdamcd.tflarrivals.Arrival
import com.jdamcd.tflarrivals.Arrivals
import com.jdamcd.tflarrivals.ArrivalsInfo
import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.StopDetails
import com.jdamcd.tflarrivals.StopResult
import com.jdamcd.tflarrivals.formatTime
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException

internal class GtfsArrivals(
    private val api: GtfsApi
) : Arrivals {
    @Throws(NoDataException::class, CancellationException::class)
    override suspend fun latest(): ArrivalsInfo {
        try {
            val model = formatArrivals(api.fetchFeedMessage())
            if (model.arrivals.isNotEmpty()) {
                return model
            } else {
                throw NoDataException("No arrivals found")
            }
        } catch (e: Exception) {
            throw NoDataException(e.message.orEmpty())
        }
    }

    // TODO: Settings for GTFS feeds
    @Throws(CancellationException::class)
    override suspend fun searchStops(query: String): List<StopResult> = emptyList()

    // TODO: Settings for GTFS feeds
    @Throws(CancellationException::class)
    override suspend fun stopDetails(id: String): StopDetails = StopDetails("", "", emptyList())

    private fun formatArrivals(feedMessage: FeedMessage): ArrivalsInfo {
        // G28S = Nassau Ave (Southbound)
        val station = "G28S"
        val arrivals = getNextArrivalsForStop(station, feedMessage.entity)
        return ArrivalsInfo(station, arrivals)
    }

    private fun getNextArrivalsForStop(
        stopId: String,
        feedItems: List<FeedEntity>
    ): List<Arrival> =
        feedItems
            .asSequence()
            .mapNotNull { it.trip_update }
            .flatMap { tripUpdate ->
                tripUpdate.stop_time_update
                    .filter { it.stop_id == stopId }
                    .map { stopTimeUpdate ->
                        val destination = "${tripUpdate.trip.route_id} - ${tripUpdate.stop_time_update.last().stop_id}"
                        val secondsToStation =
                            stopTimeUpdate.arrival?.time?.let { arrivalTime ->
                                (arrivalTime - Clock.System.now().epochSeconds).toInt()
                            } ?: 0
                        Arrival(
                            stopTimeUpdate.hashCode(),
                            destination,
                            formatTime(secondsToStation)
                        )
                    }
            }.take(3)
            .toList()
}
