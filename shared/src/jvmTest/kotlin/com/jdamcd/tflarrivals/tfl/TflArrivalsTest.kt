package com.jdamcd.tflarrivals.tfl

import com.jdamcd.tflarrivals.NoDataException
import com.jdamcd.tflarrivals.Settings
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TflArrivalsTest {

    private val api = mockk<TflApi>()
    private val settings = Settings()
    private val arrivals = TflArrivals(api, settings)

    private val response = listOf(
        ApiArrival(123, "", "Platform 2", "outbound", "New Cross", 456),
        ApiArrival(124, "", "Platform 2", "outbound", "Crystal Palace Rail Station", 10),
        ApiArrival(125, "", "Platform 1", "inbound", "Dalston Junction", 10),
        ApiArrival(126, "", "Platform 1", "inbound", "Highbury & Islington Underground Station", 456)
    )

    @BeforeTest
    fun setup() {
        settings.tflStopId = "123"
        settings.tflStopName = "Test Stop"
        settings.tflPlatform = "Platform 2"
        settings.tflDirection = "all"
    }

    @Test
    fun `fetches latest arrivals`() = runBlocking<Unit> {
        coEvery { api.fetchArrivals("123") } returns response

        val latest = arrivals.latest()

        assertEquals(2, latest.arrivals.size)
        val first = latest.arrivals[0]
        first.destination shouldBe "Crystal Palace"
        first.time shouldBe "Due"
        first.secondsToStop shouldBe 10
        val second = latest.arrivals[1]
        second.destination shouldBe "New Cross"
        second.time shouldBe "8 min"
        second.secondsToStop shouldBe 456
    }

    @Test
    fun `formats station name with filters`() = runBlocking<Unit> {
        coEvery { api.fetchArrivals("123") } returns response

        settings.tflDirection = "all"
        settings.tflPlatform = "Platform 2"
        arrivals.latest().station shouldBe "Test Stop: Platform 2"

        settings.tflDirection = "inbound"
        settings.tflPlatform = ""
        arrivals.latest().station shouldBe "Test Stop: Inbound"

        settings.tflDirection = "all"
        settings.tflPlatform = ""
        arrivals.latest().station shouldBe "Test Stop"
    }

    @Test
    fun `returns up to 3 arrivals`() = runBlocking<Unit> {
        settings.tflPlatform = ""
        coEvery { api.fetchArrivals("123") } returns response

        val latest = arrivals.latest()

        latest.arrivals shouldHaveSize 3
    }

    @Test
    fun `throws NoDataException if results are empty`() = runBlocking<Unit> {
        coEvery { api.fetchArrivals("123") } returns emptyList()

        assertFailsWith<NoDataException> {
            arrivals.latest()
        }
    }
}
