package com.kyang.tftteambuilder.ui.home

import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoardModel
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.EmptyBoardSpace
import com.kyang.tftteambuilder.data.model.TraitModel

data class HomeUIState(
    val board: BoardModel = BoardModel(
        listOf(
            DEFAULT_BOARD_ROW,
            DEFAULT_BOARD_ROW,
            DEFAULT_BOARD_ROW,
            DEFAULT_BOARD_ROW
        )
    ),
    val box: BoxModel = BoxModel(listOf()),
    val traits: TraitModel = DEFAULT_TRAITS,

    )

val DEFAULT_BOARD_ROW = listOf(
    EmptyBoardSpace,
    EmptyBoardSpace,
    EmptyBoardSpace,
    EmptyBoardSpace,
    EmptyBoardSpace,
    EmptyBoardSpace,
    EmptyBoardSpace,
)

val DEFAULT_TRAITS = TraitModel()