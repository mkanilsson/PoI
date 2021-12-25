package me.Jrp0h.poi.models

import kotlin.math.floor
import kotlin.math.sqrt
import me.Jrp0h.poi.AddCoord
import me.Jrp0h.poi.AddCoord.Category
import me.Jrp0h.poi.AddCoord.World
import me.Jrp0h.poi.database.DatabaseManager
import me.Jrp0h.poi.database.entites.PointsOfInterestsTable
import me.Jrp0h.poi.utils.Logger
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.filter
import org.ktorm.entity.toMutableList

object PointsOfInterest {

    fun getPointsOfInterest(
            x: Int,
            y: Int,
            z: Int,
            world: World,
            page: Int = 1,
            category: Category? = null
    ): Pair<Boolean, List<FoundPointsOfInterest>> {
        var query =
                DatabaseManager.pointsOfInterest
                        .filter { it.serverId eq DatabaseManager.server.id }
                        .filter { it.world eq world }

        if (category != null) {
            query.filter { it.category eq category }
            Logger.info("Category is not null", category.toString())
        }

        var rows = query.toMutableList()

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
        val pageOffset = (page - 1) * 5
        for (i in 0..pageOffset) {
            results.removeFirst()
        }
        return Pair(results.size > pageOffset, results.take(5))
    }

    fun addCoord(coord: AddCoord): Boolean {
        try {
            DatabaseManager.connection.insertAndGenerateKey(PointsOfInterestsTable) {
                set(PointsOfInterestsTable.name, coord.name)
                set(PointsOfInterestsTable.description, coord.description)
                set(PointsOfInterestsTable.x, coord.x)
                set(PointsOfInterestsTable.y, coord.y)
                set(PointsOfInterestsTable.z, coord.z)
                set(PointsOfInterestsTable.category, coord.category)
                set(PointsOfInterestsTable.world, coord.world)
                set(PointsOfInterestsTable.serverId, DatabaseManager.server.id)
            }

            return true
        } catch (e: Exception) {
            Logger.error("Failed to save coords:", e.toString())
            return false
        }
    }
}

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
