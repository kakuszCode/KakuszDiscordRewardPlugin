package pl.kakuszcode.discordreward.bukkit

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit
import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin
import pl.kakuszcode.discordreward.bukkit.commands.DiscordCommand
import pl.kakuszcode.discordreward.bukkit.config.Configuration
import pl.kakuszcode.discordreward.bukkit.db.Database
import pl.kakuszcode.discordreward.bukkit.db.DatabaseEnum
import pl.kakuszcode.discordreward.bukkit.listener.WebSocketListener
import pl.kakuszcode.discordreward.bukkit.user.service.DiscordService
import pl.kakuszcode.discordreward.sdk.Sdk
import pl.kakuszcode.discordreward.sdk.request.AuthorizationRequest
import java.io.File


@OptIn(DelicateCoroutinesApi::class)
class DiscordReward : JavaPlugin() {
    companion object {
        var webSocketIsRunning = false
    }
    private lateinit var config: Configuration
    private lateinit var sdk: Sdk
    private lateinit var token: String
    private lateinit var database: Database
    private lateinit var service: DiscordService

    override fun onEnable() {

        config = ConfigManager.create(Configuration::class.java) {
            it.withConfigurer(YamlBukkitConfigurer(), SerdesBukkit())
                .withBindFile(File(dataFolder, "config.yml"))
                .saveDefaults()
                .load()
        }
        sdk = Sdk(config.url)
        GlobalScope.launch {
            try {
                token = sdk.registerOAuth2API(
                    AuthorizationRequest(
                        config.token,
                        config.discordOAuth2Token,
                        config.idDiscord,
                        config.license,
                        config.guildIdDiscord,
                        config.roles
                    )
                ).token
            } catch (e: Exception) {
                logger.severe("Wystąpił problem z rejestracja error: ${e.message}")
                server.pluginManager.disablePlugin(this@DiscordReward)
            }

            try {
                database = config.databaseEnum.databaseClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                logger.severe("Błąd: $e")
                server.pluginManager.disablePlugin(this@DiscordReward)
            }
            try {
            when (config.databaseEnum) {
                DatabaseEnum.H2 -> database.connect(config.jdbc, logger)
                DatabaseEnum.MYSQL -> database.connect(
                    config.jdbc + ":" + config.username + ":" + config.password,
                    logger
                )

                DatabaseEnum.POSTGRESQL -> database.connect(
                    config.jdbc + ":" + config.username + ":" + config.password,
                    logger
                )
            }
            service = DiscordService(database)
            server.getPluginCommand("discord")?.setExecutor(DiscordCommand(service,token, sdk))
            service.loadUsers(logger)
            } catch (e: Exception) {
                logger.severe("Błąd: $e")
                server.pluginManager.disablePlugin(this@DiscordReward)
            }
            loadWebSocket()
        }

    }
    private fun loadWebSocket(){
        GlobalScope.launch {
            try {
                sdk.loadWebSocket(token, listOf(WebSocketListener(service, config, this@DiscordReward)))
                webSocketIsRunning = true
            } catch (e: IllegalStateException) {
                webSocketIsRunning = false
                if (e.message == "Invalid license or license is usage!"){
                    logger.severe("Wystąpił problem z licencja error: ${e.message}")
                    server.pluginManager.disablePlugin(this@DiscordReward)
                }
                logger.severe("Wystąpił problem z WebSocket error: ${e.message}")
                logger.severe("WebSocket zostanie ponownie połączony za 120s!")
                server.scheduler.runTaskLaterAsynchronously(this@DiscordReward, Runnable{
                    loadWebSocket()
                }, 20L *120)


            }
        }
    }


}