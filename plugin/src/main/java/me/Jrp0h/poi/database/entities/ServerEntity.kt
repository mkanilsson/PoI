package me.Jrp0h.poi.database.entites

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ServerTable : Table<ServerEntity>("servers") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}

interface ServerEntity : Entity<ServerEntity> {
    companion object : Entity.Factory<ServerEntity>()

    val id: Int
    val name: String
}
