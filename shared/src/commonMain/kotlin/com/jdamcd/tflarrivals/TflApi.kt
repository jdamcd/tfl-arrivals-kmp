package com.jdamcd.tflarrivals

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
    }

    suspend fun fetchArrivals(line: String, station: String, direction: String): List<ApiArrivals> {
        return client.get("$BASE_URL/Line/$line/Arrivals/$station") {
            parameter("direction", direction)
            parameter("app_key", "xxxx")
        }.body()
    }

}

@Serializable
data class ApiArrivals(
    val destinationName: String,
    val timeToStation: Int
)

private const val BASE_URL = "https://api.tfl.gov.uk/"