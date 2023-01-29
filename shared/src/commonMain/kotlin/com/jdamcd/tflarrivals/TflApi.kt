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

    suspend fun fetchArrivals(line: String, station: String): List<ApiArrival> {
        return client.get("$BASE_URL/Line/$line/Arrivals/$station") {
            parameter("app_key", APP_KEY)
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
private const val BASE_URL = "https://api.tfl.gov.uk/"