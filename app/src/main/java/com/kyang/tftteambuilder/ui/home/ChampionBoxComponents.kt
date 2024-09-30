package com.kyang.tftteambuilder.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoxModel

@Composable
fun ChampionBox(
    modifier: Modifier = Modifier,
    onClick: (BoardChampion) -> Unit,
    boxModel: BoxModel
) {
    val localConfig = LocalConfiguration.current
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.FixedSize((localConfig.screenWidthDp / 6).dp),
        contentPadding = PaddingValues(all = 0.dp)
    ) {
        for ((index, tier) in boxModel.tiers.withIndex()) {
            itemsIndexed(tier.champions) { columnNum, champion ->
                ChampionBoxItem(
                    champion = champion,
                    onClick = onClick,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
internal fun ChampionBoxItem(
    modifier: Modifier = Modifier,
    champion: BoardChampion,
    onClick: (BoardChampion) -> Unit
) {
    Box(modifier = modifier
        .border(
            width = 2.dp, shape = RectangleShape, color = champion.cost.color
        )
        .clickable { onClick(champion) }) {
        AsyncImage(
            model = champion.image,
            contentDescription = champion.name,
            contentScale = ContentScale.Crop
        )
    }
}