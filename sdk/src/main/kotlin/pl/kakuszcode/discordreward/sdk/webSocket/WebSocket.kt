package pl.kakuszcode.discordreward.sdk.webSocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import pl.kakuszcode.discordreward.sdk.event.WebSocketEvent
import pl.kakuszcode.discordreward.sdk.response.SuccessfulAuthResponse

class WebSocket {
    private val client: HttpClient
    private val events: List<WebSocketEvent>

    constructor(client: HttpClient, events: List<WebSocketEvent>) {
        this.client = client
        this.events = events
    }


    suspend fun run() {
        client.webSocket(
            method = HttpMethod.Get,
            host = "https://api.discordreward.kakuszcode.pl",
            port = 80,
            path = "/successful/oauth2"
        ) {
            try {
                val response = receiveDeserialized<SuccessfulAuthResponse>()
                events.forEach { it.onMessage(response) }
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }
}