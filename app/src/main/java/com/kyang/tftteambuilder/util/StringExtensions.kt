package com.kyang.tftteambuilder.util

import android.util.Log

fun String.substringBetween(start: String, end: String): String {
    return substringAfter(start).substringBefore(end)
}

fun String.parseAsIndex(): Pair<Int, Int>? {
    val indices = this.substring(1, this.length - 1).split(", ")
    try {
        return Pair(indices[0].toInt(), indices[1].toInt())
    } catch (e: Exception) {
        Log.e("parseAsIndex", "error $e")
        return null
    }
}