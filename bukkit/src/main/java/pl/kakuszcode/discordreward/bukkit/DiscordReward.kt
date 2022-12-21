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
            }
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
            server.getPluginCommand("discord")?.setExecutor(DiscordCommand(token, sdk))
            service.loadUsers(logger)
            sdk.loadWebSocket(token, listOf(WebSocketListener(service, config, this@DiscordReward)))
        }

    }


}