@file:OptIn(DelicateCoroutinesApi::class)

package pl.kakuszcode.discordreward.bukkit.commands


import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.kakuszcode.discordreward.bukkit.extension.fixColors
import pl.kakuszcode.discordreward.sdk.Sdk
import pl.kakuszcode.discordreward.sdk.request.CreateOAuth2LinkRequest
import java.time.Duration
import java.util.UUID

class DiscordCommand(private val token: String,private val sdk: Sdk, private val cache: Cache<UUID, String> = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(1)).build()) : CommandExecutor {


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("&4Błąd: &cMusisz być graczem aby użyć tej komendy!".fixColors())
            return false
        }
        if (cache.getIfPresent(sender.uniqueId) != null) {
            sender.sendMessage("&2Sukces: &aTwój link do nagrody: ${cache.getIfPresent(sender.uniqueId)}".fixColors())
            return false
        }
        GlobalScope.async {
            val response = sdk.createOAuth2URL(token, CreateOAuth2LinkRequest(sender.name))
            cache.put(sender.uniqueId, response.url)
            sender.sendMessage("&2Sukces: &aTwój link do nagrody: ${response.url}".fixColors())
        }

        return false
    }
}