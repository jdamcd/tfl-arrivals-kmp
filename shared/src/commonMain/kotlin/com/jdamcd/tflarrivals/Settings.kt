package com.jdamcd.tflarrivals

expect class Settings() {
    var mode: String
    var tflStopName: String
    var tflStopId: String
    var tflPlatform: String
    var tflDirection: String
    var gtfsStopsUpdated: Long
    var gtfsRealtime: String
    var gtfsSchedule: String
    var gtfsStop: String
}

object SettingsConfig {
    const val STORE_NAME = "arrivals_settings"

    const val MODE = "mode"
    const val MODE_TFL = "tfl"
    const val MODE_GTFS = "gtfs"

    const val TFL_STOP_NAME = "selected_stop_name"
    const val TFL_STOP_NAME_DEFAULT = "Shoreditch High Street"
    const val TFL_STOP_ID = "selected_stop_id"
    const val TFL_STOP_ID_DEFAULT = "910GSHRDHST"
    const val TFL_PLATFORM = "tfl_platform"
    const val TFL_PLATFORM_DEFAULT = "Platform 2"
    const val TFL_DIRECTION = "tfl_direction"
    const val TFL_DIRECTION_DEFAULT = "all"

    const val GTFS_STOPS_UPDATED = "gtfs_stops_updated"
    const val GTFS_REALTIME = "gtfs_realtime"
    const val GTFS_REALTIME_DEFAULT = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace"
    const val GTFS_SCHEDULE = "gtfs_schedule"
    const val GTFS_SCHEDULE_DEFAULT = "http://web.mta.info/developers/data/nyct/subway/google_transit.zip"
    const val GTFS_STOP = "gtfs_stop"
    const val GTFS_STOP_DEFAULT = "A42N"
}
