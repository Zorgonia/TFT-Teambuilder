package com.kyang.tftteambuilder.data.model

import androidx.compose.ui.graphics.Color

data class BoardModel(
    val rows: List<List<BoardSpace>> = listOf()
)

sealed class BoardSpace

data class BoardChampion(
    val traits: List<ChampionTrait>,
    val image: String,
    val cost: ChampionCost,
    val name: String,
//    val items: List<TftItem> = listOf()
): BoardSpace()

data object EmptyBoardSpace : BoardSpace()

enum class ChampionCost(val color: Color, val cost: String) {
    ONE(Color.Gray, "1"),
    TWO(Color.Green, "2"),
    THREE(Color.Blue, "3"),
    FOUR(Color.Magenta, "4"),
    FIVE(Color.Yellow, "5")
}

//fun BoardSpace.copy(): BoardSpace {
//    if (this is BoardChampion) {
//        return this.copy(traits = this.traits.map { it.copy() })
//    } else if (this is EmptyBoardSpace) {
//        return EmptyBoardSpace
//    }
//}