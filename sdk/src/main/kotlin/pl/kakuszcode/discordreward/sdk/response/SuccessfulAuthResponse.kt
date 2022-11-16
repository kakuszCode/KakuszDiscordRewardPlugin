package pl.kakuszcode.discordreward.sdk.response

import kotlinx.serialization.Serializable

@Serializable
data class SuccessfulAuthResponse(val nickName: String, val discordUserID: Long)
