package com.jdamcd.tflarrivals

expect class Settings() {
    var selectedStop: String
    var platformFilter: String
}

object SettingsConfig {
    const val STORE_NAME = "tfl_arrivals_settings"

    const val SELECTED_STOP = "selected_stop"
    const val SELECTED_STOP_DEFAULT = "910GSHRDHST" // Shoreditch High Street

    const val PLATFORM_FILTER = "platform_filter"
    const val PLATFORM_FILTER_DEFAULT = "Platform 2"
}