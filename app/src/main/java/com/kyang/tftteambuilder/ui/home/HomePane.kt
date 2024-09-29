package com.kyang.tftteambuilder.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyang.tftteambuilder.ui.theme.TFTTeambuilderTheme

@Composable
fun HomePane(modifier: Modifier = Modifier, viewModel: HomeViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChampionBoard(
            board = uiState.board,
            onSwap = { index -> viewModel.swapSpaces(index) },
            fromBox = { boxIndex, boardIndex -> viewModel.addFromBox(boxIndex, boardIndex) },
            onRemove = { index -> viewModel.removeChampion(index) },
            swapIndex = uiState.swapIndex
        )
        ChampionBox(boxModel = uiState.box, onClick = { viewModel.addChampion(it) })
    }
}

@Preview
@Composable
fun HomePanePreview() {
    TFTTeambuilderTheme {
        HomePane(
            viewModel = HomeViewModel(hiltViewModel()),
            modifier = Modifier.background(Color.White)
        )
    }
}