package com.jdamcd.tflarrivals.gtfs

import com.jdamcd.tflarrivals.Fixtures
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GtfsStopsTest {

    private lateinit var stops: GtfsStops

    @Test
    fun `maps stop ID to name`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_1)

        stops.stopIdToName("G28") shouldBe "Nassau Av"
        stops.stopIdToName("G22S") shouldBe "Court Sq"
    }

    @Test
    fun `maps stop ID to name with different CSV orderings`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_2)

        stops.stopIdToName("1141") shouldBe "Evergreen Blvd & Farview Dr"
        stops.stopIdToName("3254") shouldBe "Hwy 99 & NE 104th St"
    }

    @Test
    fun `maps invalid stop ID to null`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_1)

        stops.stopIdToName("123456") shouldBe null
    }

    @Test
    fun `does not map header row`() {
        stops = GtfsStops(Fixtures.STOPS_CSV_2)

        stops.stopIdToName("stop_id") shouldBe null
    }
}
