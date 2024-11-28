package com.jdamcd.tflarrivals.gtfs

import com.google.transit.realtime.FeedMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes

internal class GtfsApi {
    private val client =
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000 // 10 seconds
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }

    suspend fun fetchFeedMessage(): FeedMessage {
        val bodyBytes = client.get(ENDPOINT).bodyAsBytes()
        return FeedMessage.ADAPTER.decode(bodyBytes)
    }
}

private const val ENDPOINT = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace"
