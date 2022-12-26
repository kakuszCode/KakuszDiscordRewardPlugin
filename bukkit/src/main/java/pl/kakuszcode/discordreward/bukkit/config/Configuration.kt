package pl.kakuszcode.discordreward.bukkit.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import eu.okaeri.configs.annotation.CustomKey
import pl.kakuszcode.discordreward.bukkit.db.DatabaseType




class Configuration : OkaeriConfig() {
    @CustomKey("settings.url")
    @Comment("without http and https and end without /")
    var url = "discordreward.kakuszcode.pl"
    @CustomKey("settings.license")
    var license = "test"
    @CustomKey("settings.discordToken")
    var token = "token"
    @CustomKey("settings.secretToken")
    var discordOAuth2Token = "secretToken"
    @CustomKey("settings.idDiscord")
    var idDiscord = "24737503826"
    @CustomKey("settings.guildIdDiscord")
    var guildIdDiscord = 34523452345
    @CustomKey("settings.roles")
    var roles = arrayListOf(347134734595)

    @CustomKey("settings.databaseType")
    @Comment("Są: ", "H2", "MySQL", "PostgreSQL")
    var databaseEnum = DatabaseType.H2

    @CustomKey("settings.database.jdbc")
    @Comment("jdbc:mysql://localhost:3306/discordreward", "jdbc:postgresql://localhost:5432/discordreward")
    var jdbc = "jdbc:h2:~/discordreward"

    @CustomKey("settings.database.username")
    var username = ""

    @CustomKey("settings.database.password")
    var password = ""
    @CustomKey("settings.settings.multiplyVerify")
    var multiplyVerify = false
    @CustomKey("settings.database.reload")
    var databaseReload = true
    @CustomKey("settings.commands")
    @Comment("start without /")
    var commands = arrayListOf("say [player] sie zweryfikował!", "say pozdro 600")
    @CustomKey("settings.bot.presence")
    var presence = "Bot stworzony przez kakuszcode.pl"
    @CustomKey("settings.bot.nickName")
    @Comment("support PlaceHolderAPI")
    var nickName = "%player_name%"
    @CustomKey("settings.messages.link")
    var link = "&2Sukces: &aTwój link do nagrody: {url}"
    @CustomKey("settings.messages.isPicked")
    var isPicked = "&4Błąd: &cOdebrałeś już nagrodę!"
    @CustomKey("settings.messages.webSocketError")
    var webSocketError = "&4Błąd: &cProblem z połączeniem!, sprobój pózniej!"
    @CustomKey("settings.messages.error")
    var error = "&4Błąd: Nie udało się zdobyć nagrody!"
}