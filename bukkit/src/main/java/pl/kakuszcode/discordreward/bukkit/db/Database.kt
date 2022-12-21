package pl.kakuszcode.discordreward.bukkit.db

import org.bukkit.plugin.java.JavaPlugin
import pl.kakuszcode.discordreward.bukkit.user.DiscordUser
import java.util.logging.Logger

interface Database {
    fun connect(password: String, logger: Logger)
    fun insertDiscordUser(user: DiscordUser, plugin: JavaPlugin)
    fun getDiscordUsers(logger: Logger): List<DiscordUser>
}