package com.jdamcd.tflarrivals.gtfs

import kotlin.test.Test
import kotlin.test.assertEquals

class StopsTest {

    @Test
    fun `maps stop ID to name`() {
        val first = MtaStops.stopIdToName("101")
        val last = MtaStops.stopIdToName("S31S")

        assertEquals("Van Cortlandt Park-242 St", first)
        assertEquals("St George", last)
    }

    @Test
    fun `maps null stop ID to unknown`() {
        val unknown = MtaStops.stopIdToName(null)

        assertEquals("Unknown", unknown)
    }

    @Test
    fun `maps invalid stop ID to unknown`() {
        val unknown = MtaStops.stopIdToName("123456")

        assertEquals("Unknown", unknown)
    }
}
