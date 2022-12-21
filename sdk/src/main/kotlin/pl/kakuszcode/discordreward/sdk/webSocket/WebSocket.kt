package pl.kakuszcode.discordreward.sdk.webSocket

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.WebSocket
import pl.kakuszcode.discordreward.sdk.event.WebSocketEvent


class WebSocket(private val events: List<WebSocketEvent>, private val bearer: String, private val url: String) :
    WebSocketListener() {
    private val client = OkHttpClient()

    fun run() {
        val request = Request.Builder()
            .url("wss://$url/successful/oauth2/$bearer/")
            .build()
        client.newWebSocket(request, this)
        client.dispatcher.executorService.shutdown();

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        events.forEach { it.onMessage(Json.decodeFromString(text)) }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null);
        throw IllegalStateException(if (code == 1003) "Invalid license or license is usage!" else "Unknown exception")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
    }


}


