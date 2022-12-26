@file:OptIn(DelicateCoroutinesApi::class)

package pl.kakuszcode.discordreward.bukkit.commands


import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.kakuszcode.discordreward.bukkit.DiscordReward
import pl.kakuszcode.discordreward.bukkit.config.Configuration
import pl.kakuszcode.discordreward.bukkit.extension.fixColors
import pl.kakuszcode.discordreward.bukkit.user.service.DiscordService
import pl.kakuszcode.discordreward.sdk.Sdk
import pl.kakuszcode.discordreward.sdk.request.CreateOAuth2LinkRequest
import java.time.Duration
import java.util.*

class DiscordCommand(
    private val config: Configuration,
    private val service: DiscordService,
    private val token: String,
    private val sdk: Sdk,
    private val cache: Cache<UUID, String> = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build()
) : CommandExecutor {


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("&4Błąd: &cMusisz być graczem aby użyć tej komendy!".fixColors())
            return false
        }
        if (!DiscordReward.webSocketIsRunning) {
            sender.sendMessage("&4Błąd: &cProblem z połączeniem!, sprobój pózniej!".fixColors())
            return false
        }
        if (service.hashMap[sender.uniqueId] != null && !config.multiplyVerify) {
            sender.sendMessage("&4Błąd: &cOdebrałeś już nagrodę!".fixColors())
            return false
        }
        if (cache.getIfPresent(sender.uniqueId) != null) {
            sender.sendMessage("&2Sukces: &aTwój link do nagrody: ${cache.getIfPresent(sender.uniqueId)}".fixColors())
            return false
        }
        GlobalScope.async {
            try {
                val response = sdk.createOAuth2URL(
                    token,
                    CreateOAuth2LinkRequest(sender.name, PlaceholderAPI.setPlaceholders(sender, config.nickName))
                )
                cache.put(sender.uniqueId, response.url)
                sender.sendMessage("&2Sukces: &aTwój link do nagrody: ${response.url}".fixColors())
            } catch (e: Exception) {
                sender.sendMessage("&4Błąd: Nie udało się zdobyć nagrody!".fixColors())
            }
        }

        return false
    }
}