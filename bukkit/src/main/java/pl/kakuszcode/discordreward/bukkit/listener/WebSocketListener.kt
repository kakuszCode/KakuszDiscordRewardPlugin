package pl.kakuszcode.discordreward.bukkit.listener

import org.bukkit.plugin.java.JavaPlugin
import pl.kakuszcode.discordreward.bukkit.config.Configuration
import pl.kakuszcode.discordreward.bukkit.user.DiscordUser
import pl.kakuszcode.discordreward.bukkit.user.service.DiscordService
import pl.kakuszcode.discordreward.sdk.event.WebSocketEvent
import pl.kakuszcode.discordreward.sdk.response.SuccessfulAuthResponse

class WebSocketListener(private val service: DiscordService, private val config: Configuration, private val plugin: JavaPlugin) : WebSocketEvent() {
    override fun onMessage(response: SuccessfulAuthResponse) {
        val player = plugin.server.getPlayerExact(response.nickName) ?: return
        val user =DiscordUser(player.uniqueId, response.discordUserID)
        service.hashMap[player.uniqueId] = user
        service.database.insertDiscordUser(user, plugin)
        plugin.server.scheduler.runTask(plugin, Runnable {
            config.commands.forEach {
                plugin.server.dispatchCommand(plugin.server.consoleSender, it.replace("[player]", player.name))
            }
        })

    }
}