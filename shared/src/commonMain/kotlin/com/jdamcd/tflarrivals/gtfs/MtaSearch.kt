package com.jdamcd.tflarrivals.gtfs

import com.jdamcd.tflarrivals.GtfsSearch
import com.jdamcd.tflarrivals.StopResult

internal class MtaSearch(private val api: IGtfsApi) : GtfsSearch {

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
