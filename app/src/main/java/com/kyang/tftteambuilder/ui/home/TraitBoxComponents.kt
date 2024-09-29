package com.kyang.tftteambuilder.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyang.tftteambuilder.R
import com.kyang.tftteambuilder.data.model.ActiveTrait
import com.kyang.tftteambuilder.data.model.TraitModel
import org.jetbrains.annotations.Async
import kotlin.math.min

@Composable
fun TraitBox(modifier: Modifier = Modifier, traits: TraitModel) {
    val localConfig = LocalConfiguration.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive((localConfig.screenWidthDp * 0.45).dp),
        modifier = modifier
            .sizeIn(minHeight = 0.dp, maxHeight = localConfig.screenHeightDp.dp / 5)
            .padding(horizontal = 8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RectangleShape)
    ) {
        items(traits.traits) { trait ->
            TraitRow(
                trait = trait,
                modifier = Modifier.padding(all = 4.dp)
            )
        }
    }
}

@Composable
fun TraitRow(modifier: Modifier = Modifier, trait: ActiveTrait) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = trait.breakpoint?.tier?.image ?: "https://cdn.metatft.com/file/metatft/traits/base.png",
                contentDescription = trait.breakpoint?.tier?.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(48.dp)
            )
            AsyncImage(
                model = if (trait.breakpoint == null) trait.trait.image.replace(
                    ".svg",
                    "_w.svg"
                ) else trait.trait.image,
                contentDescription = trait.trait.name,
                contentScale = ContentScale.Inside,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            stringResource(
                R.string.active_trait,
                trait.numOfUnits,
                trait.breakpoint?.breakpoint ?: trait.trait.breakpoints.first().breakpoint,
                trait.trait.name
            )
        )
    }

}