package com.kyang.tftteambuilder.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyang.tftteambuilder.R
import com.kyang.tftteambuilder.ui.theme.TFTTeambuilderTheme

@Composable
fun HomePane(modifier: Modifier = Modifier, viewModel: HomeViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.swapChampionView()
                }
            ) {
                Icon(painterResource(R.drawable.swords_24), contentDescription = "Swap to Items/Champions")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ChampionBoard(
                board = uiState.board,
                onSwap = { index -> viewModel.swapSpaces(index) },
                fromBox = { boxIndex, boardIndex -> viewModel.addFromBox(boxIndex, boardIndex) },
                onRemove = { index -> viewModel.removeChampion(index) },
                itemDrag = { championIndex, itemIndex ->
                    viewModel.addItemToChampion(championIndex, itemIndex)
                },
                itemRemove = { championIndex, itemIndex ->
                    viewModel.removeItemFromChampion(championIndex, itemIndex)
                },
                swapIndex = uiState.swapIndex
            )
            TraitBox(traits = uiState.traits)
            if (uiState.showChampions) {
                ChampionBox(boxModel = uiState.box, onClick = { viewModel.addChampion(it) })
            } else {
                ItemBox(itemModel = uiState.items, onClick = {})
            }
        }
    }
}

@Preview
@Composable
fun HomePanePreview() {
    TFTTeambuilderTheme {
        HomePane(
            viewModel = HomeViewModel(hiltViewModel()), modifier = Modifier.background(Color.White)
        )
    }
}