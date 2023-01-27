package com.jdamcd.tflarrivals

import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    suspend fun fetchArrivals(line: String, station: String): List<ApiArrivals> {
        return client.get("$BASE_URL/Line/$line/Arrivals/$station") {
            parameter("app_key", "xxxx")
        }.body()
    }

}

@Serializable
data class ApiArrivals(
    val id: Int,
    val destinationName: String,
    val platformName: String,
    val timeToStation: Int
)

private const val BASE_URL = "https://api.tfl.gov.uk/"