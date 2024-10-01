package com.kyang.tftteambuilder.ui.home

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyang.tftteambuilder.data.model.ItemModel
import com.kyang.tftteambuilder.data.model.TftItem

@Composable
fun ItemBox(modifier: Modifier = Modifier, onClick: (Int) -> Unit, itemModel: ItemModel) {
    val localConfig = LocalConfiguration.current
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.FixedSize((localConfig.screenWidthDp / 6).dp),
    ) {
        itemsIndexed(itemModel.items) { index, item ->
            ItemBoxItem(item = item, onClick = onClick, index = index, modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemBoxItem(modifier: Modifier = Modifier, item: TftItem, onClick: (Int) -> Unit, index: Int) {
    Box(modifier.dragAndDropSource {
        detectTapGestures(onLongPress = {
            startTransfer(
                DragAndDropTransferData(ClipData.newPlainText("Item", index.toString()))
            )
        }, onTap = {
            onClick(index)
        })
    }) {
        AsyncImage(
            model = item.image,
            contentDescription = item.name,
            contentScale = ContentScale.FillBounds
        )
    }
}