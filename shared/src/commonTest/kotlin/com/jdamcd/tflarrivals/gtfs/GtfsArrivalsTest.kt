package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedMessage
import com.jdamcd.tflarrivals.Fixtures
import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.Settings
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GtfsArrivalsTest : TestsWithMocks() {

    override fun setUpMocks() = mocker.injectMocks(this)

    @Mock lateinit var api: IGtfsApi

    @Mock lateinit var clock: Clock

    private lateinit var settings: Settings

    private val fetchTime = 1734629501L

    private val arrivals by withMocks { GtfsArrivals(api, clock, settings) }
    private val feedMessage = FeedMessage.ADAPTER.decode(Fixtures.FEED_MESSAGE)

    @BeforeTest
    fun setup() {
        settings = Settings()
        settings.gtfsRealtime = "realtime_url"
        settings.gtfsSchedule = "schedule_url"
        settings.gtfsStop = "G28S"
        settings.gtfsStopsUpdated = fetchTime - 1000
    }

    @Test
    fun `fetches latest arrivals`() = runBlocking {
        everySuspending { api.fetchFeedMessage("realtime_url") } returns feedMessage
        every { clock.now() } returns Instant.fromEpochSeconds(fetchTime)
        every { api.readStops() } returns Fixtures.STOPS_CSV_1

        val latest = arrivals.latest()

        assertEquals(3, latest.arrivals.size)
        val first = latest.arrivals[0]
        assertEquals("G - Church Av", first.destination)
        assertEquals("Due", first.time)
        assertEquals(30, first.secondsToStop)
        val second = latest.arrivals[1]
        assertEquals("G - Church Av", second.destination)
        assertEquals("7 min", second.time)
        assertEquals(439, second.secondsToStop)
    }

    @Test
    fun `throws NoDataException if no arrivals match stop`() = runBlocking<Unit> {
        settings.gtfsStop = "1234"
        everySuspending { api.fetchFeedMessage("realtime_url") } returns feedMessage
        every { clock.now() } returns Instant.fromEpochSeconds(fetchTime)
        every { api.readStops() } returns Fixtures.STOPS_CSV_1

        assertFailsWith<NoDataException> {
            arrivals.latest()
        }
    }

    @Test
    fun `updates stops when stale`() = runBlocking {
        settings.gtfsStopsUpdated = fetchTime - 172801
        every { clock.now() } returns Instant.fromEpochSeconds(fetchTime)
        everySuspending { api.downloadStops("schedule_url") } returns Fixtures.STOPS_CSV_1
        everySuspending { api.fetchFeedMessage("realtime_url") } returns feedMessage

        val latest = arrivals.latest()

        assertEquals(3, latest.arrivals.size)
    }
}
