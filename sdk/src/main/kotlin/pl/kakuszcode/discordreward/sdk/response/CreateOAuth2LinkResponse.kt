package pl.kakuszcode.discordreward.sdk.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateOAuth2LinkResponse(val url: String)