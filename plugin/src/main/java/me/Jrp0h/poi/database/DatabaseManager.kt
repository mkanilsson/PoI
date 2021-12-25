package me.Jrp0h.poi.database

import java.lang.Exception
import java.sql.DriverManager
import kotlin.math.floor
import kotlin.math.sqrt
import me.Jrp0h.poi.AddCoord.Category
import me.Jrp0h.poi.AddCoord.World
import me.Jrp0h.poi.Config
import me.Jrp0h.poi.PoI
import me.Jrp0h.poi.database.entites.PointsOfInterestsTable
import me.Jrp0h.poi.database.entites.ServerEntity
import me.Jrp0h.poi.database.entites.ServerTable
import me.Jrp0h.poi.utils.Logger
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.filter
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toMutableList

object DatabaseManager {

    val connection: Database
        get() = database!!

    val pointsOfInterest
        get() = this.connection.sequenceOf(PointsOfInterestsTable)
    private var database: Database? = null
    private var _plugin: PoI? = null
    private val plugin
        get() = _plugin!!

    val server
        get() = _server!!

    private var _server: ServerEntity? = null

    fun connect(plugin: PoI) {
        _plugin = plugin

        val hostname = Config.database.host
        val databaseName = Config.database.name
        val user = Config.database.user
        val password = Config.database.password

        val jdbcUrl = "jdbc:mysql://$hostname:3306/$databaseName?user=$user&password=$password"
        try {
            database = Database.connect(jdbcUrl)
        } catch (e: Exception) {
            Logger.error(
                    "Something went wrong when connection to the database. Shutting down",
                    e.toString()
            )
        }

        setup()
    }

    private fun setup() {
        val hostname = Config.database.host
        val databaseName = Config.database.name
        val user = Config.database.user
        val password = Config.database.password
        val jdbcUrl = "jdbc:mysql://$hostname:3306/$databaseName?user=$user&password=$password"

        try {
            val conn = DriverManager.getConnection(jdbcUrl, user, password)
            val stmt = conn.createStatement()
            Logger.info("Creating tables if they don't exist")
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS servers (id int(11) unsigned primary key AUTO_INCREMENT, name varchar(255) unique)"
            )
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS points_of_interests (
                        id int(11) unsigned primary key AUTO_INCREMENT,
                        server_id int(11) unsigned,
                        name varchar(255),
                        description varchar(255),
                        x int(11),
                        y int(11),
                        z int(11),
                        category enum(
                            'Home',
                            'Spawner',
                            'Farm',
                            'Village',
                            'Stronghold',
                            'DesertTemple',
                            'JungleTemple',
                            'GuardianTemple',
                            'OceanRuins',
                            'Mineshaft',
                            'PillagerOutpost',
                            'Shipwreck',
                            'Biome',
                            'EndPortal',
                            'NetherPortal',
                            'Bastion',
                            'NetherFortress',
                            'Mansion',
                            'MushroomIsland',
                            'Misc'
                        ),
                        world enum('Overworld', 'Nether', 'End'),
                        FOREIGN KEY (server_id) REFERENCES servers(id) on delete cascade 
                    )
                         """.trimIndent()
            )
            conn.close()
        } catch (e: Exception) {
            Logger.error("Something went wrong when creating tables. Shutting down", e.toString())
            plugin.server.shutdown()
        }

        // Check if server exists, otherwise create it
        var server = findServer()
        if (server == null) {
            connection.insertAndGenerateKey(ServerTable) {
                set(ServerTable.name, Config.server.name)
            }

            findServer()
        }
    }

    private fun findServer(): ServerEntity? {
        _server = connection.sequenceOf(ServerTable).firstOrNull { it.name eq Config.server.name }
        return _server
    }

    fun getPointsOfInterest(x: Int, y: Int, z: Int, page: Int = 0) {
        val server = findServer()!!
        val rows = pointsOfInterest.filter { it.serverId eq server.id }.toMutableList().toList()

        class FoundPointsOfInterest {
            val name: String
            val description: String

            val x: Int
            val y: Int
            val z: Int

            val distance: Int

            val category: Category
            val world: World

            constructor(
                    name: String,
                    description: String,
                    x: Int,
                    y: Int,
                    z: Int,
                    distance: Int,
                    category: Category,
                    world: World
            ) {
                this.name = name
                this.description = description
                this.x = x
                this.y = y
                this.z = z
                this.distance = distance
                this.category = category
                this.world = world
            }
        }

        var results = mutableListOf<FoundPointsOfInterest>()

        rows.forEach { row ->
            val xsqrt: Double = (row.x.toDouble() - x) * (row.x - x)
            val ysqrt: Double = (row.y.toDouble() - y) * (row.y - y)
            val zsqrt: Double = (row.z.toDouble() - z) * (row.z - z)

            var distance: Int = floor(sqrt(xsqrt + ysqrt + zsqrt)).toInt()

            results.add(
                    FoundPointsOfInterest(
                            row.name,
                            row.description,
                            row.x,
                            row.y,
                            row.z,
                            distance,
                            row.category,
                            row.world
                    )
            )
        }

        results.sortBy { it.distance }
        results.forEach { row -> Logger.info("Name:", row.name, "Distance: " + row.distance) }
    }
}
