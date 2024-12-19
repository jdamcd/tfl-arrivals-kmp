package com.jdamcd.tflarrivals.gtfs

import com.jdamcd.tflarrivals.Fixtures
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GtfsStopsTest {

    private lateinit var stops: GtfsStops

    @Test
    fun `maps stop ID to name`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_1)

        val first = stops.stopIdToName("G28")
        val last = stops.stopIdToName("G22S")

        assertEquals("Nassau Av", first)
        assertEquals("Court Sq", last)
    }

    @Test
    fun `maps stop ID to name with different CSV orderings`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_2)

        val first = stops.stopIdToName("1141")
        val last = stops.stopIdToName("3254")

        assertEquals("Evergreen Blvd & Farview Dr", first)
        assertEquals("Hwy 99 & NE 104th St", last)
    }

    @Test
    fun `maps invalid stop ID to unknown`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_1)

        val unknown = stops.stopIdToName("123456")

        assertNull(unknown)
    }

    @Test
    fun `does not map header row`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_2)

        val header = stops.stopIdToName("stop_id")

        assertNull(header)
    }
}
