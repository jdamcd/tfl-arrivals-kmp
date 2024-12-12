package com.jdamcd.tflarrivals

import com.jdamcd.tflarrivals.gtfs.GtfsApi
import com.jdamcd.tflarrivals.gtfs.GtfsArrivals
import com.jdamcd.tflarrivals.tfl.TflApi
import com.jdamcd.tflarrivals.tfl.TflArrivals
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

@Suppress("Unused")
fun initKoin() {
    startKoin {
        modules(commonModule())
    }
}

@Suppress("Unused")
class MacDI : KoinComponent {
    val arrivals: Arrivals by inject()
    val settings: Settings by inject()
    val tflSearch: TflSearch by inject()
}

fun commonModule() = module {
    single { Settings() }
    single { TflApi(get()) }
    single { GtfsApi(get()) }
    single { TflArrivals(get(), get()) }
    single { GtfsArrivals(get(), get()) }
    single<Arrivals> { ArrivalsSwitcher(get(), get(), get()) }
    single<TflSearch> { get<TflArrivals>() }
    single {
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000 // 10 seconds
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
}

internal class ArrivalsSwitcher(
    private val tfl: TflArrivals,
    private val gtfs: GtfsArrivals,
    private val settings: Settings
) : Arrivals {

    override suspend fun latest(): ArrivalsInfo = if (settings.mode == SettingsConfig.MODE_TFL) {
        tfl.latest()
    } else {
        gtfs.latest()
    }
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
