package com.jdamcd.tflarrivals.gtfs

import com.jdamcd.tflarrivals.GtfsSearch

internal class MtaSearch(private val api: GtfsApi) : GtfsSearch {

    private lateinit var stops: GtfsStops

    private suspend fun updateStops() {
        if (!::stops.isInitialized) {
            stops = GtfsStops(api.downloadStops(Mta.SCHEDULE, "mta"))
        }
    }

    override suspend fun getStops(feedUrl: String): Map<String, String> {
        updateStops()
        val feedMessage = api.fetchFeedMessage(feedUrl)

        return feedMessage.entity
            .asSequence()
            .mapNotNull { it.trip_update }
            .flatMap { it.stop_time_update }
            .mapNotNull { it.stop_id }
            .distinct()
            .associateWith { "${stops.stopIdToName(it)} ($it)" }
    }
}
