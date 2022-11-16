package pl.kakuszcode.discordreward.sdk.response

import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class CreateOAuth2LinkResponse(val url: URL)