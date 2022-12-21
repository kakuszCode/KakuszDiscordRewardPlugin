package pl.kakuszcode.discordreward.bukkit.db;

import pl.kakuszcode.discordreward.bukkit.db.provider.*


enum class DatabaseEnum(val databaseClass: Class<out Database>) {
    MYSQL(MySQLProvider::class.java), POSTGRESQL(PostgreSQLProvider::class.java), H2(
        H2Provider::class.java
    )

}
