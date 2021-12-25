package me.Jrp0h.poi.utils

object Compare {
    fun ignoreCase(first: String, secound: String): Boolean {
        return first.toLowerCase().equals(secound.toLowerCase())
    }
}
