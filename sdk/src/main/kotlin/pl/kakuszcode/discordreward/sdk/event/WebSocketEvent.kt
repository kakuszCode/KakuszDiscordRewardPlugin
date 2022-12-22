package pl.kakuszcode.discordreward.sdk.event

import pl.kakuszcode.discordreward.sdk.response.SuccessfulAuthResponse

abstract class WebSocketEvent {
    abstract fun onMessage(response: SuccessfulAuthResponse)
    abstract fun onException(message: String)
}