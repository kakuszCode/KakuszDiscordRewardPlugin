package pl.kakuszcode.discordreward.bukkit.user.service

import pl.kakuszcode.discordreward.bukkit.db.Database
import pl.kakuszcode.discordreward.bukkit.user.DiscordUser
import java.util.*
import java.util.logging.Logger
import kotlin.collections.HashMap

class DiscordService(val database: Database) {
    var hashMap = HashMap<UUID, DiscordUser>()
    fun loadUsers(logger: Logger){
        database.getDiscordUsers(logger).forEach {
            hashMap[it.uuid] = it
        }
    }
    fun isContainsById(id: Long) : Boolean{
        for (user in hashMap.values) {
            if (user.discordID == id) return true
        }
        return false
    }

}