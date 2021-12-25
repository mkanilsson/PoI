package me.Jrp0h.poi.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor

object Logger {
    val PREFIX: String = "[Places of Interest]"

    private fun log(color: ChatColor, vararg rest: String) {
        val builder = StringBuilder()
        rest.forEach { message -> builder.append(" ").append(message) }
        val message = builder.toString()
        Bukkit.getConsoleSender().sendMessage("$PREFIX${color}$message")
    }

    fun info(msg: String, vararg rest: String) {
        log(ChatColor.BLUE, msg, *rest)
    }

    fun success(msg: String, vararg rest: String) {
        log(ChatColor.GREEN, msg, *rest)
    }

    fun warning(msg: String, vararg rest: String) {
        log(ChatColor.YELLOW, msg, *rest)
    }

    public fun error(msg: String, vararg rest: String) {
        log(ChatColor.RED, msg, *rest)
    }
}
