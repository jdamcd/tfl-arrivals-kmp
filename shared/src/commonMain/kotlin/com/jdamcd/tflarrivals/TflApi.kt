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
            requestTimeoutMillis = 10_000
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

    suspend fun searchStations(query: String): List<ApiStopPoint> {
        return client.get("$BASE_URL/StopPoint/Search") {
            parameter("app_key", APP_KEY)
            parameter("query", query)
        }.body()
    }

}

@Serializable
data class ApiArrival(
    val id: Int,
    val stationName: String,
    val platformName: String,
    val destinationName: String,
    val timeToStation: Int
)

private const val DEBUG = false
private const val APP_KEY = "YOUR_APP_KEY"
@Serializable
data class ApiStopPoint(
    val id: String,
    val name: String
)

private const val BASE_URL = "https://api.tfl.gov.uk"
