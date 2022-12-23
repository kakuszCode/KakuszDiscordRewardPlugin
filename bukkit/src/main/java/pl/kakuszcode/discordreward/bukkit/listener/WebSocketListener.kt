package pl.kakuszcode.discordreward.bukkit.listener

import pl.kakuszcode.discordreward.bukkit.DiscordReward
import pl.kakuszcode.discordreward.bukkit.config.Configuration
import pl.kakuszcode.discordreward.bukkit.extension.fixColors
import pl.kakuszcode.discordreward.bukkit.user.DiscordUser
import pl.kakuszcode.discordreward.bukkit.user.service.DiscordService
import pl.kakuszcode.discordreward.sdk.event.WebSocketEvent
import pl.kakuszcode.discordreward.sdk.response.SuccessfulAuthResponse

class WebSocketListener(private val service: DiscordService, private val config: Configuration, private val plugin: DiscordReward) : WebSocketEvent() {
    override fun onMessage(response: SuccessfulAuthResponse) {
        val player = plugin.server.getPlayerExact(response.nickName) ?: return
        if (service.isContainsById(response.discordUserID)) {
            player.sendMessage("&4Błąd: &cJuż odebrałeś nagrodę!".fixColors())
            return
        }
        val user =DiscordUser(player.uniqueId, response.discordUserID)

        service.hashMap[player.uniqueId] = user
        service.database.insertDiscordUser(user, plugin)
        plugin.server.scheduler.runTask(plugin, Runnable {
            config.commands.forEach {
                plugin.server.dispatchCommand(plugin.server.consoleSender, it.replace("[player]", player.name))
            }
        })

    }

    override fun onException(message: String) {
        DiscordReward.webSocketIsRunning = false
        if (message == "Invalid license or license is usage!"){
            plugin.logger.severe("Wystąpił problem z licencja error: $message")
            plugin.server.pluginManager.disablePlugin(plugin)
        }
        plugin.logger.severe("Wystąpił problem z WebSocket error: $message")
        plugin.logger.severe("WebSocket zostanie ponownie połączony za 30s!")
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, kotlinx.coroutines.Runnable {
            plugin.loadWebSocket()
        }, 20L *30)
    }
}