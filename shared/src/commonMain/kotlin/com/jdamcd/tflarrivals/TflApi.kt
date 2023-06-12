package com.jdamcd.tflarrivals

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class TflApi {

    private val client = HttpClient {
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
        if (DEBUG) {
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    suspend fun fetchArrivals(station: String): List<ApiArrival> {
        return client.get("$BASE_URL/StopPoint/$station/Arrivals") {
            parameter("app_key", APP_KEY)
        }.body()
    }

    suspend fun searchStations(query: String): ApiSearchResult {
        return client.get("$BASE_URL/StopPoint/Search") {
            parameter("app_key", APP_KEY)
            parameter("query", query)
            parameter("modes", "dlr,elizabeth-line,national-rail,overground,tube")
            parameter("tflOperatedNationalRailStationsOnly", true)
        }.body()
    }

    suspend fun stopDetails(id: String): ApiStopPoint {
        return client.get("$BASE_URL/StopPoint/$id") {
            parameter("app_key", APP_KEY)
        }.body()
    }
}

@Serializable
data class ApiArrival(
    val id: Int,
    val stationName: String,
    val platformName: String,
    val direction: String,
    val destinationName: String,
    val timeToStation: Int
)

@Serializable
data class ApiSearchResult(
    val matches: List<ApiMatchedStop>
)

@Serializable
data class ApiMatchedStop(
    val id: String,
    val name: String
)

@Serializable
data class ApiStopPoint(
    val commonName: String,
    val naptanId: String,
    val children: List<ApiStopPoint>
)

private const val BASE_URL = "https://api.tfl.gov.uk"
