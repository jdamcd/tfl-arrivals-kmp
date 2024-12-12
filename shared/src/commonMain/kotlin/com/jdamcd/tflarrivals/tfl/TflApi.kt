package com.jdamcd.tflarrivals.tfl

import com.jdamcd.tflarrivals.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

internal class TflApi(private val client: HttpClient) {

    suspend fun fetchArrivals(station: String): List<ApiArrival> = client
        .get("$BASE_URL/StopPoint/$station/Arrivals") {
            parameter("app_key", BuildKonfig.TFL_APP_KEY)
        }.body()

    suspend fun searchStations(query: String): ApiSearchResult = client
        .get("$BASE_URL/StopPoint/Search") {
            parameter("app_key", BuildKonfig.TFL_APP_KEY)
            parameter("query", query)
            parameter("modes", "dlr,elizabeth-line,overground,tube,tram")
            parameter("tflOperatedNationalRailStationsOnly", true)
        }.body()

    suspend fun stopDetails(id: String): ApiStopPoint = client
        .get("$BASE_URL/StopPoint/$id") {
            parameter("app_key", BuildKonfig.TFL_APP_KEY)
        }.body()
}

@Serializable
internal data class ApiArrival(
    val id: Int,
    val stationName: String,
    val platformName: String,
    val direction: String,
    val destinationName: String,
    val timeToStation: Int
)

@Serializable
internal data class ApiSearchResult(
    val matches: List<ApiMatchedStop>
)

@Serializable
internal data class ApiMatchedStop(
    val id: String,
    val name: String
)

@Serializable
internal data class ApiStopPoint(
    val commonName: String,
    val naptanId: String,
    val stopType: String,
    val children: List<ApiStopPoint>
)

private const val BASE_URL = "https://api.tfl.gov.uk"
