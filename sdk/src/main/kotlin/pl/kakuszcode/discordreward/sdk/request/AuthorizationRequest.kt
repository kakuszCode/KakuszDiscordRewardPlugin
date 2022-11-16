package pl.kakuszcode.discordreward.sdk.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequest(val discordToken: String, val license:String)
