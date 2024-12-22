package com.jdamcd.arrivals.gtfs

import com.jdamcd.arrivals.GtfsSearch
import com.jdamcd.arrivals.StopResult

internal class MtaSearch(private val api: GtfsApi) : GtfsSearch {

    private lateinit var stops: GtfsStops

    private suspend fun updateStops() {
        if (!::stops.isInitialized) {
            stops = GtfsStops(api.downloadStops(Mta.SCHEDULE, "mta"))
        }
    }

    override suspend fun getStops(feedUrl: String): List<StopResult> {
        updateStops()
        val feedMessage = api.fetchFeedMessage(feedUrl)

        return feedMessage.entity
            .asSequence()
            .mapNotNull { it.trip_update }
            .flatMap { it.stop_time_update }
            .mapNotNull { it.stop_id }
            .distinct()
            .filter { stops.stopIdToName(it) != null }
            .map { StopResult(it, "${stops.stopIdToName(it)!!} ($it)", false) }
            .toList()
            .sortedBy { it.name }
    }
}
