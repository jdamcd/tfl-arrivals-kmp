package com.jdamcd.tflarrivals.tfl

import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.Settings
import kotlinx.coroutines.runBlocking
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TflArrivalsTest : TestsWithMocks() {

    override fun setUpMocks() = mocker.injectMocks(this)

    @Mock lateinit var api: ITflApi

    private lateinit var settings: Settings

    private val arrivals by withMocks { TflArrivals(api, settings) }

    private val response = listOf(
        ApiArrival(123, "", "Platform 2", "outbound", "New Cross", 456),
        ApiArrival(124, "", "Platform 2", "outbound", "Crystal Palace Rail Station", 10),
        ApiArrival(125, "", "Platform 1", "inbound", "Dalston Junction", 10),
        ApiArrival(126, "", "Platform 1", "inbound", "Highbury & Islington Underground Station", 456)
    )

    @BeforeTest
    fun setup() {
        settings = Settings()
        settings.tflStopId = "123"
        settings.tflStopName = "Test Stop"
        settings.tflPlatform = "Platform 2"
        settings.tflDirection = "all"
    }

    @Test
    fun `fetches latest arrivals`() = runBlocking {
        everySuspending { api.fetchArrivals("123") } returns response

        val latest = arrivals.latest()

        assertEquals(2, latest.arrivals.size)
        val first = latest.arrivals[0]
        assertEquals("Crystal Palace", first.destination)
        assertEquals("Due", first.time)
        assertEquals(10, first.secondsToStop)
        val second = latest.arrivals[1]
        assertEquals("New Cross", second.destination)
        assertEquals("8 min", second.time)
        assertEquals(456, second.secondsToStop)
    }

    @Test
    fun `formats station name with filters`() = runBlocking {
        everySuspending { api.fetchArrivals("123") } returns response

        settings.tflDirection = "all"
        settings.tflPlatform = "Platform 2"
        val first = arrivals.latest()
        assertEquals("Test Stop: Platform 2", first.station)

        settings.tflDirection = "inbound"
        settings.tflPlatform = ""
        val second = arrivals.latest()
        assertEquals("Test Stop: Inbound", second.station)

        settings.tflDirection = "all"
        settings.tflPlatform = ""
        val third = arrivals.latest()
        assertEquals("Test Stop", third.station)
    }

    @Test
    fun `returns up to 3 arrivals`() = runBlocking {
        settings.tflPlatform = ""
        everySuspending { api.fetchArrivals("123") } returns response

        val latest = arrivals.latest()

        assertEquals(3, latest.arrivals.size)
    }

    @Test
    fun `throws NoDataException if results are empty`() = runBlocking<Unit> {
        everySuspending { api.fetchArrivals("123") } returns emptyList()

        assertFailsWith<NoDataException> {
            arrivals.latest()
        }
    }
}
