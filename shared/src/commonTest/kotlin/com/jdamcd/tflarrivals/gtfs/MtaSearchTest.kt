package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedMessage
import com.jdamcd.tflarrivals.Fixtures
import kotlinx.coroutines.runBlocking
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MtaSearchTest : TestsWithMocks() {

    override fun setUpMocks() = mocker.injectMocks(this)

    @Mock lateinit var api: IGtfsApi

    private val search by withMocks { MtaSearch(api) }
    private val feedMessage = FeedMessage.ADAPTER.decode(Fixtures.FEED_MESSAGE)

    @Test
    fun `finds all stops from feed message`() = runBlocking {
        everySuspending { api.downloadStops(Mta.SCHEDULE, "mta") } returns Fixtures.STOPS_CSV_1
        everySuspending { api.fetchFeedMessage("realtime_url") } returns feedMessage

        val results = search.getStops("realtime_url")

        assertEquals(6, results.size)
        val first = results[0]
        assertEquals("F27N", first.id)
        assertEquals("Church Av (F27N)", first.name)
        assertFalse(first.isHub)
    }
}
