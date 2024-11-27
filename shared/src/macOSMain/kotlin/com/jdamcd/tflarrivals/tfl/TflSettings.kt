package com.jdamcd.tflarrivals.tfl

import platform.Foundation.NSUserDefaults

actual class TflSettings actual constructor() {
    private val defaults: NSUserDefaults = NSUserDefaults(suiteName = SettingsConfig.STORE_NAME)

    actual var selectedStopName: String
        get() = defaults.stringForKey(SettingsConfig.SELECTED_STOP_NAME) ?: SettingsConfig.SELECTED_STOP_NAME_DEFAULT
        set(value) {
            defaults.setObject(value, SettingsConfig.SELECTED_STOP_NAME)
        }

    actual var selectedStopId: String
        get() = defaults.stringForKey(SettingsConfig.SELECTED_STOP_ID) ?: SettingsConfig.SELECTED_STOP_ID_DEFAULT
        set(value) {
            defaults.setObject(value, SettingsConfig.SELECTED_STOP_ID)
        }

    actual var platformFilter: String
        get() = defaults.stringForKey(SettingsConfig.PLATFORM_FILTER) ?: SettingsConfig.PLATFORM_FILTER_DEFAULT
        set(value) {
            defaults.setObject(value, SettingsConfig.PLATFORM_FILTER)
        }

    actual var directionFilter: String
        get() = defaults.stringForKey(SettingsConfig.DIRECTION_FILTER) ?: SettingsConfig.DIRECTION_FILTER_DEFAULT
        set(value) {
            defaults.setObject(value, SettingsConfig.DIRECTION_FILTER)
        }
}
