package me.Jrp0h.poi

import java.io.File
import me.Jrp0h.poi.utils.Logger
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

object Config {

    public var database = ConfigDatabase("", "", "", "")
        get() = hiddenDatabase!!

    public var server = ConfigServer("")
        get() = hiddenServer!!

    private var hiddenDatabase: ConfigDatabase? = null
    private var hiddenServer: ConfigServer? = null

    private var dataConfig: FileConfiguration? = null
    private var configFile: File? = null

    fun load(plugin: PoI) {
        if (configFile == null) configFile = File("plugins/PoI/config.yml")

        if (!configFile!!.exists()) {
            val defaultConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(configFile!!)

            defaultConfig.set("server.name", "localhost")

            defaultConfig.set("database.host", "localhost")
            defaultConfig.set("database.name", "poi")
            defaultConfig.set("database.user", "root")
            defaultConfig.set("database.password", "root")

            try {
                defaultConfig.save(configFile!!)
            } catch (e: Exception) {
                Logger.error("Failed saving default config", e.toString())
            }

            Logger.error("Config file is missing! Please edit ./plugins/PoI/config.yml")
            plugin.server.shutdown()
        }

        dataConfig = YamlConfiguration.loadConfiguration(configFile!!)

        val SERVER_NAME = dataConfig!!.getString("server.name")!!

        val DB_HOST = dataConfig!!.getString("database.host")!!
        val DB_NAME = dataConfig!!.getString("database.name")!!
        val DB_USER = dataConfig!!.getString("database.user")!!
        val DB_PASSWORD = dataConfig!!.getString("database.password")!!

        hiddenDatabase = ConfigDatabase(DB_HOST, DB_NAME, DB_USER, DB_PASSWORD)
        hiddenServer = ConfigServer(SERVER_NAME)
    }
}

class ConfigDatabase {
    val host: String
    val name: String
    val user: String
    val password: String

    constructor(host: String, name: String, user: String, password: String) {
        this.host = host
        this.name = name
        this.user = user
        this.password = password
    }
}

class ConfigServer {
    val name: String

    constructor(name: String) {
        this.name = name
    }
}
