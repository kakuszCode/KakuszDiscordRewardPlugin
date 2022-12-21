package pl.kakuszcode.discordreward.sdk.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequest(val discordToken: String, val discordSecretToken: String, val idDiscord : String, val license:String, val guildIdDiscord: Long, val roles: List<Long>)
