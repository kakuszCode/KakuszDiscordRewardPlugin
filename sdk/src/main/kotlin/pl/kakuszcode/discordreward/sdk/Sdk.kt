package pl.kakuszcode.discordreward.sdk

import io.ktor.client.*
import io.ktor.client.call.*

import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import pl.kakuszcode.discordreward.sdk.request.AuthorizationRequest
import pl.kakuszcode.discordreward.sdk.response.AuthorizationResponse
import pl.kakuszcode.discordreward.sdk.response.CreateOAuth2LinkResponse


class Sdk {
    private val client: HttpClient = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    suspend fun registerOAuth2API(authorizationResponse: AuthorizationRequest): AuthorizationResponse {
        val request = client.post("https://api.discordreward.kakuszcode.pl/register/") {
            contentType(ContentType.Application.Json)
            setBody(authorizationResponse)
        }
        return Json.decodeFromString(request.body())
    }

    suspend fun createOAuth2URL(token: String) : CreateOAuth2LinkResponse {
        val request =client.post("https://api.discordreward.kakuszcode.pl/register/") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        return Json.decodeFromString(request.body())
    }
}