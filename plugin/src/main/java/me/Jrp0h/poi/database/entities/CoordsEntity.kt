package me.Jrp0h.poi.database.entites

import me.Jrp0h.poi.AddCoord.Category
import me.Jrp0h.poi.AddCoord.World
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object PointsOfInterestsTable : Table<PointsOfInterestEntity>("points_of_interests") {
    val id = int("id").primaryKey().bindTo { it.id }
    val serverId = int("server_id").references(ServerTable) { it.server }

    val name = varchar("name").bindTo { it.name }
    val description = varchar("description").bindTo { it.description }
    val x = int("x").bindTo { it.x }
    val y = int("y").bindTo { it.y }
    val z = int("z").bindTo { it.z }

    val category = enum<Category>("category").bindTo { it.category }
    val world = enum<World>("world").bindTo { it.world }
}

interface PointsOfInterestEntity : Entity<PointsOfInterestEntity> {
    companion object : Entity.Factory<PointsOfInterestEntity>()

    val id: Int
    val server: ServerEntity

    val name: String
    val description: String
    val x: Int
    val y: Int
    val z: Int

    val category: Category
    val world: World
}
