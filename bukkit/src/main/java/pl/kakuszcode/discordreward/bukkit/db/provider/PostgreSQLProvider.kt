package pl.kakuszcode.discordreward.bukkit.db.provider

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import pl.kakuszcode.discordreward.bukkit.db.Database
import pl.kakuszcode.discordreward.bukkit.user.DiscordUser
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class PostgreSQLProvider : Database {
    private lateinit var connection: Connection
    override fun connect(password: String, logger: Logger) {
        try {
            Class.forName("org.postgresql.Driver")
        } catch (e: ClassNotFoundException) {
            logger.severe("Błąd: $e")
        }
        val config = HikariConfig()
        config.jdbcUrl = password.split("§")[0]
        config.username = password.split("§")[1]
        config.password = password.split("§")[2]
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        val db = HikariDataSource(config)
        try {
            connection = db.connection
            val statement = connection.createStatement()
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `DiscordUsers` (`uuid` VARCHAR(36) NOT NULL, `discordID` LONG NOT NULL)")
            statement.close()
        } catch (e: SQLException) {
            logger.severe("Problem z połączeniem z bazą danych!$e")
        }
    }

    override fun insertDiscordUser(user: DiscordUser, plugin: JavaPlugin) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            try {
                val ps =
                    connection.prepareStatement("INSERT INTO `DiscordUsers` (`uuid`, `discordID`) VALUES (?, ?)")
                ps.setString(1, user.uuid.toString())
                ps.setLong(2, user.discordID)
                ps.executeUpdate()
                ps.close()
            } catch (e: SQLException) {
                plugin.logger.severe("Problem z bazą danych!$e")
            }
        })
    }

    override fun getDiscordUsers(logger: Logger): List<DiscordUser> {
        val users = ArrayList<DiscordUser>()
        try {
            val set = connection.prepareStatement("SELECT * FROM `DiscordUsers`").executeQuery()
            while (set.next()) {
                val user = DiscordUser(UUID.fromString(set.getString(1)), set.getLong(2))
                users.add(user)
            }
        } catch (e: SQLException) {
            logger.severe("Błąd:$e")
        }
        return users
    }
    override fun removeDiscordUser(user: DiscordUser, plugin: JavaPlugin) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            val ps = connection.prepareStatement("DELETE FROM `DiscordUsers` WHERE uuid=?");
            ps.setString(1, user.uuid.toString())
            ps.execute()
            ps.close()
        })
    }
}