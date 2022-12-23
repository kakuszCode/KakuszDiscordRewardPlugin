package pl.kakuszcode.discordreward.bukkit.bot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import pl.kakuszcode.discordreward.bukkit.config.Configuration

class Bot(private val config: Configuration) : ListenerAdapter() {
    companion object {
        lateinit var jda: JDA
    }
    fun start() {
        JDABuilder.create(config.token, GatewayIntent.values().toMutableList()).addEventListeners(this).setActivity(
            Activity.playing(config.presence)).build()
    }

    override fun onReady(event: ReadyEvent) {
        jda = event.jda
        jda.getGuildById(config.guildIdDiscord) ?: throw NullPointerException("Requiered bot in discord server!")
        println("Bot is ready!")
    }
}