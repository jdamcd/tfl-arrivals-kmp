package com.jdamcd.tflarrivals

import com.jdamcd.tflarrivals.SettingsConfig.PLATFORM_FILTER
import com.jdamcd.tflarrivals.SettingsConfig.PLATFORM_FILTER_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP
import com.jdamcd.tflarrivals.SettingsConfig.SELECTED_STOP_DEFAULT
import com.jdamcd.tflarrivals.SettingsConfig.STORE_NAME
import platform.Foundation.NSUserDefaults

actual class Settings actual constructor() {

    private val defaults: NSUserDefaults = NSUserDefaults(suiteName = STORE_NAME)

    actual var selectedStop: String
        get() = defaults.stringForKey(SELECTED_STOP) ?: SELECTED_STOP_DEFAULT
        set(value) { defaults.setObject(value, SELECTED_STOP) }

    actual var platformFilter: String
        get() = defaults.stringForKey(PLATFORM_FILTER) ?: PLATFORM_FILTER_DEFAULT
        set(value) { defaults.setObject(value, PLATFORM_FILTER) }
}
