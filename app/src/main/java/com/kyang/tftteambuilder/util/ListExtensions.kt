package com.kyang.tftteambuilder.util

fun List<Int>.findHighestOf(max: Int): Int? {
    return this.filter {it <= max}.maxOrNull()
}