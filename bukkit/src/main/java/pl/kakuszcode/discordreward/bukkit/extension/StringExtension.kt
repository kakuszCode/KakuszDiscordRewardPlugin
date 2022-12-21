package pl.kakuszcode.discordreward.bukkit.extension

import org.bukkit.ChatColor

fun String.fixColors(): String{
    return ChatColor.translateAlternateColorCodes('&', this)
}