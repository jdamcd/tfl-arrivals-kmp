package com.jdamcd.tflarrivals

import kotlin.test.Test
import kotlin.test.assertEquals

class ArrivalsTest {

    @Test
    fun testFormatTimeDue() {
        assertEquals("Due", formatTime(0))
    }

    @Test
    fun testFormatTimeMultipleMinutes() {
        assertEquals("5 min", formatTime(300))
    }

    @Test
    fun testFormatTimeBoundary() {
        assertEquals("Due", formatTime(59))
        assertEquals("1 min", formatTime(60))
    }
}
