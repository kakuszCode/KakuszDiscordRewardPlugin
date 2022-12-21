package pl.kakuszcode.discordreward.sdk.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateOAuth2LinkRequest(val nickName: String)
