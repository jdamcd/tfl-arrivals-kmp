package com.jdamcd.tflarrivals

import com.jdamcd.tflarrivals.SettingsConfig.DIRECTION_FILTER
import com.jdamcd.tflarrivals.SettingsConfig.DIRECTION_FILTER_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.PLATFORM_FILTER
import com.jdamcd.tflarrivals.SettingsConfig.PLATFORM_FILTER_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP_ID
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP_ID_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP_NAME
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP_NAME_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.STORE_NAME
import platform.Foundation.NSUserDefaults

actual class Settings actual constructor() {
    private val defaults: NSUserDefaults = NSUserDefaults(suiteName = STORE_NAME)

    actual var selectedStopName: String
        get() = defaults.stringForKey(SELECTED_STOP_NAME) ?: SELECTED_STOP_NAME_DEFAULT
        set(value) {
            defaults.setObject(value, SELECTED_STOP_NAME)
        }

    actual var selectedStopId: String
        get() = defaults.stringForKey(SELECTED_STOP_ID) ?: SELECTED_STOP_ID_DEFAULT
        set(value) {
            defaults.setObject(value, SELECTED_STOP_ID)
        }

    actual var platformFilter: String
        get() = defaults.stringForKey(PLATFORM_FILTER) ?: PLATFORM_FILTER_DEFAULT
        set(value) {
            defaults.setObject(value, PLATFORM_FILTER)
        }

    actual var directionFilter: String
        get() = defaults.stringForKey(DIRECTION_FILTER) ?: DIRECTION_FILTER_DEFAULT
        set(value) {
            defaults.setObject(value, DIRECTION_FILTER)
        }
}
