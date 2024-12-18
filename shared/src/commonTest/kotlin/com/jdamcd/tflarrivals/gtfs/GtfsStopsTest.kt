package com.jdamcd.tflarrivals.gtfs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GtfsStopsTest {

    private lateinit var stops: GtfsStops

    @Test
    fun `maps stop ID to name`() {
        stops = GtfsStops(STOPS_CSV_1)

        val first = stops.stopIdToName("101")
        val last = stops.stopIdToName("104S")

        assertEquals("Van Cortlandt Park-242 St", first)
        assertEquals("231 St", last)
    }

    @Test
    fun `maps stop ID to name with different CSV orderings`() {
        stops = GtfsStops(STOPS_CSV_2)

        val first = stops.stopIdToName("1141")
        val last = stops.stopIdToName("3254")

        assertEquals("Evergreen Blvd & Farview Dr", first)
        assertEquals("Hwy 99 & NE 104th St", last)
    }

    @Test
    fun `maps invalid stop ID to unknown`() {
        stops = GtfsStops(STOPS_CSV_1)

        val unknown = stops.stopIdToName("123456")

        assertNull(unknown)
    }

    @Test
    fun `does not map header row`() {
        stops = GtfsStops(STOPS_CSV_2)

        val header = stops.stopIdToName("stop_id")

        assertNull(header)
    }
}

const val STOPS_CSV_1 = """
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

const val STOPS_CSV_2 = """
stop_lat,wheelchair_boarding,stop_code,stop_lon,stop_timezone,stop_url,parent_station,stop_desc,stop_name,location_type,stop_id,zone_id
45.623013,0,1141,-122.625739,,,,,Evergreen Blvd & Farview Dr,0,1141,
45.663343,0,2172,-122.559985,,,,,Gher Rd & Coxley Dr,0,2172,
45.657287,0,4149,-122.666827,,,,,Hazel Dell Ave 4900 Block,0,4149,
45.660011,0,4148,-122.667381,,,,,Hazel Dell Ave 5200 Block,0,4148,
45.67769,0,2173,-122.569274,,,,,NE 76th St & 101st Ave,0,2173,
45.714981,0,4145,-122.651305,,,,,Hwy 99 & 129th St,0,4145,
45.665885,0,4147,-122.668916,,,,,Hazel Dell Ave & 60th St,0,4147,
45.678661,0,3253,-122.635948,,,,,NE 78th St 3300 Block,0,3253,
45.696539,0,3254,-122.654579,,,,,Hwy 99 & NE 104th St,0,3254,
"""
