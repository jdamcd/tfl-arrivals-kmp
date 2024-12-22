package com.jdamcd.arrivals.gtfs

import com.google.transit.realtime.FeedMessage
import com.jdamcd.arrivals.Fixtures
import com.jdamcd.arrivals.TestHelper
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class MtaSearchTest {

    private val api = mockk<GtfsApi>()
    private val search = MtaSearch(api)

    private lateinit var feedMessage: FeedMessage

    @BeforeTest
    fun setup() {
        feedMessage = TestHelper.resource("feed_message.pb").let {
            FeedMessage.ADAPTER.decode(it)
        }
    }

    @Test
    fun `finds all stops from feed message`() = runBlocking<Unit> {
        coEvery { api.downloadStops(Mta.SCHEDULE, "mta") } returns Fixtures.STOPS_CSV_1
        coEvery { api.fetchFeedMessage("realtime_url") } returns feedMessage

        val results = search.getStops("realtime_url")

        results.size shouldBe 6 // Fixture contains 6 stops
        val first = results[0]
        first.id shouldBe "F27N"
        first.name shouldBe "Church Av (F27N)"
        first.isHub shouldBe false
    }
}
