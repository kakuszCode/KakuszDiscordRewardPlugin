package pl.kakuszcode.discordreward.sdk

import io.ktor.client.*
import io.ktor.client.call.*

import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import pl.kakuszcode.discordreward.sdk.event.WebSocketEvent
import pl.kakuszcode.discordreward.sdk.request.AuthorizationRequest
import pl.kakuszcode.discordreward.sdk.request.CreateOAuth2LinkRequest
import pl.kakuszcode.discordreward.sdk.response.AuthorizationResponse
import pl.kakuszcode.discordreward.sdk.response.CreateOAuth2LinkResponse
import pl.kakuszcode.discordreward.sdk.webSocket.WebSocket


class Sdk(private val url: String) {
    private val client: HttpClient = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)

        }
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun registerOAuth2API(authorizationResponse: AuthorizationRequest): AuthorizationResponse {

        val request = client.post("https://${url}/register/") {
            contentType(ContentType.Application.Json)
            setBody(authorizationResponse)
        }
        return Json.decodeFromString(request.body())
    }

    suspend fun createOAuth2URL(
        token: String,
        createOAuth2LinkRequest: CreateOAuth2LinkRequest
    ): CreateOAuth2LinkResponse {
        val request = client.post("https://${url}/create/oauth2/") {
            headers {
                append("Authorization", "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createOAuth2LinkRequest)
        }
        return Json.decodeFromString(request.body())
    }

    suspend fun loadWebSocket(token: String, event: List<WebSocketEvent>) {
        WebSocket(event, token, url).run()
    }

}
