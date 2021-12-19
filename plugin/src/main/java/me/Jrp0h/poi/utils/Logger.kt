package me.Jrp0h.poi.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor

object Logger {
    val PREFIX: String = "[Places of Interest]"

    private fun log(color: ChatColor, msg: String, vararg rest: String) {
        Bukkit.getConsoleSender().sendMessage("$PREFIX${color} $msg", *rest)
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
