package com.jdamcd.tflarrivals

import com.jdamcd.tflarrivals.gtfs.GtfsApi
import com.jdamcd.tflarrivals.gtfs.GtfsArrivals
import com.jdamcd.tflarrivals.tfl.TflApi
import com.jdamcd.tflarrivals.tfl.TflArrivals
import com.jdamcd.tflarrivals.tfl.TflSettings
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

object TransitSystem {
    private val tflArrivals = TflArrivals(TflApi(), TflSettings())
    fun tfl(): Arrivals = tflArrivals
    fun tflSearch(): TflSearch = tflArrivals
    fun mta(): Arrivals = GtfsArrivals(GtfsApi())
}

interface Arrivals {
    @Throws(NoDataException::class, CancellationException::class)
    suspend fun latest(): ArrivalsInfo
}

interface TflSearch {
    @Throws(CancellationException::class)
    suspend fun searchStops(query: String): List<StopResult>

    @Throws(CancellationException::class)
    suspend fun stopDetails(id: String): StopDetails
}

data class ArrivalsInfo(
    val station: String,
    val arrivals: List<Arrival>
)

data class Arrival(
    val id: Int,
    val destination: String,
    val time: String,
    val secondsToStop: Int
)

data class StopResult(
    val id: String,
    val name: String,
    val isHub: Boolean
)

data class StopDetails(
    val id: String,
    val name: String,
    val children: List<StopResult>
)

class NoDataException(
    message: String
) : Throwable(message = message)

fun formatTime(seconds: Int) = if (seconds < 60) {
    "Due"
} else {
    "${(seconds / 60f).roundToInt()} min"
}
