package pl.kakuszcode.discordreward.bukkit.bot.task

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import pl.kakuszcode.discordreward.bukkit.bot.Bot
import pl.kakuszcode.discordreward.bukkit.config.Configuration
import pl.kakuszcode.discordreward.bukkit.user.service.DiscordService

class BotTask(private val service: DiscordService, private val config: Configuration) : BukkitRunnable() {
    override fun run() {
        for (user in service.hashMap.values) {
            val player = Bukkit.getPlayer(user.uuid) ?: break
            val guild = Bot.jda.getGuildById(config.guildIdDiscord) ?: return
            val discordUser = guild.getMemberById(user.discordID) ?: break
            if (discordUser.nickname == PlaceholderAPI.setPlaceholders(player, config.nickName)) break
            discordUser.modifyNickname(PlaceholderAPI.setPlaceholders(player, config.nickName)).queue()
        }
    }
}