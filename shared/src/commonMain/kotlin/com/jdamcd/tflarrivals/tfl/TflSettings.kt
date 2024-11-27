package com.jdamcd.tflarrivals.tfl

expect class TflSettings() {
    var selectedStopName: String
    var selectedStopId: String
    var platformFilter: String
    var directionFilter: String
}

object SettingsConfig {
    const val STORE_NAME = "tfl_arrivals_settings"

    const val SELECTED_STOP_NAME = "selected_stop_name"
    const val SELECTED_STOP_NAME_DEFAULT = "Shoreditch High Street"
    const val SELECTED_STOP_ID = "selected_stop_id"
    const val SELECTED_STOP_ID_DEFAULT = "910GSHRDHST"

    const val PLATFORM_FILTER = "platform_filter"
    const val PLATFORM_FILTER_DEFAULT = "Platform 2"
    const val DIRECTION_FILTER = "direction_filter"
    const val DIRECTION_FILTER_DEFAULT = "all"
}
