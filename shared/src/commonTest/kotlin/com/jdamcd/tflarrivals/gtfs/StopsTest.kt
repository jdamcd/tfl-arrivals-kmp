package com.jdamcd.tflarrivals.gtfs

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StopsTest {

    private lateinit var stops: GtfsStops

    @BeforeTest
    fun setup() {
        stops = GtfsStops(STOPS_CSV)
    }

    @Test
    fun `maps stop ID to name`() {
        val first = stops.stopIdToName("101")
        val last = stops.stopIdToName("104S")

        assertEquals("Van Cortlandt Park-242 St", first)
        assertEquals("231 St", last)
    }

    @Test
    fun `maps null stop ID to unknown`() {
        val unknown = stops.stopIdToName(null)

        assertEquals("Unknown", unknown)
    }

    @Test
    fun `maps invalid stop ID to unknown`() {
        val unknown = stops.stopIdToName("123456")

        assertEquals("Unknown", unknown)
    }

    @Test
    fun `does not map title row`() {
        val unknown = stops.stopIdToName("stop_id")

        assertEquals("Unknown", unknown)
    }
}

const val STOPS_CSV = """
stop_id,stop_name,stop_lat,stop_lon,location_type,parent_station
101,Van Cortlandt Park-242 St,40.889248,-73.898583,1,
101N,Van Cortlandt Park-242 St,40.889248,-73.898583,,101
101S,Van Cortlandt Park-242 St,40.889248,-73.898583,,101
103,238 St,40.884667,-73.900870,1,
103N,238 St,40.884667,-73.900870,,103
103S,238 St,40.884667,-73.900870,,103
104,231 St,40.878856,-73.904834,1,
104N,231 St,40.878856,-73.904834,,104
104S,231 St,40.878856,-73.904834,,104
"""
