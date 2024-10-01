package com.kyang.tftteambuilder.ui.home

import android.graphics.Paint.Align
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import coil.compose.AsyncImage
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoardModel
import com.kyang.tftteambuilder.data.model.BoardSpace
import com.kyang.tftteambuilder.data.model.EmptyBoardSpace
import com.kyang.tftteambuilder.util.parseAsIndex

@Composable
fun ChampionBoard(
    modifier: Modifier = Modifier,
    onSwap: (Pair<Int, Int>) -> Unit,
    fromBox: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    onRemove: (Pair<Int, Int>) -> Unit,
    itemDrag: (Pair<Int, Int>, Int) -> Unit,
    itemRemove: (Pair<Int, Int>, Int) -> Unit,
    swapIndex: Pair<Int, Int>,
    board: BoardModel
) {
    val localConfig = LocalConfiguration.current
    LazyColumn(modifier = modifier) {
        itemsIndexed(board.rows) { rowNum, row ->
            LazyRow(
                modifier = Modifier.padding(
                    start = if (rowNum % 2 == 1) 32.dp else 0.dp,
                    end = if (rowNum % 2 == 1) 0.dp else 32.dp
                )
            ) {
                itemsIndexed(row) { columnNum, space ->
                    Space(
                        boardSpace = space,
                        onSwap = onSwap,
                        fromBox = fromBox,
                        isSwapping = swapIndex == Pair(rowNum, columnNum),
                        championIndex = Pair(rowNum, columnNum),
                        onRemove = onRemove,
                        itemRemove = itemRemove,
                        itemDrag = itemDrag,
                        width = ((localConfig.screenWidthDp - 32) / row.size).dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Space(
    modifier: Modifier = Modifier,
    boardSpace: BoardSpace,
    championIndex: Pair<Int, Int>,
    isSwapping: Boolean,
    onSwap: (Pair<Int, Int>) -> Unit,
    onRemove: (Pair<Int, Int>) -> Unit,
    itemDrag: (Pair<Int, Int>, Int) -> Unit,
    itemRemove: (Pair<Int, Int>, Int) -> Unit,
    fromBox: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    width: Dp = 64.dp
) {
    val dragDropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                event.mimeTypes().forEachIndexed { index, s ->
                    if (s == "text/plain") {
                        val dataIndex = event.toAndroidDragEvent().clipData.getItemAt(index).text
                        val label = event.toAndroidDragEvent().clipData.description.label
                        if (label == "Item") {
                            dataIndex.toString().toIntOrNull()?.let { itemDrag(championIndex, it) }
                        }
                    }
                }
                return true
            }

        }
    }

    if (boardSpace is BoardChampion) {
        Box(
            modifier = modifier
                .combinedClickable(onClick = {
                    onSwap(championIndex)
                }, onDoubleClick = { onRemove(championIndex) })
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains("text/plain")
                    },
                    target = dragDropCallback
                )
        ) {
            ChampionSpace(
                champion = boardSpace,
                championIndex = championIndex,
                width = width,
                itemRemove = itemRemove,
                isSwapping = isSwapping
            )
        }
    } else if (boardSpace is EmptyBoardSpace) {
        EmptySpace(
            modifier = modifier.clickable { onSwap(championIndex) },
            width = width,
            isSwapping = isSwapping
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChampionSpace(
    modifier: Modifier = Modifier,
    width: Dp = 96.dp,
    champion: BoardChampion,
    championIndex: Pair<Int, Int>,
    itemRemove: (Pair<Int, Int>, Int) -> Unit,
    isSwapping: Boolean,
) {
    val hexagon = remember {
        RoundedPolygon(6, rounding = CornerRounding(0.2f))
    }

    val clip = remember(hexagon) { RoundedPolygonShape(polygon = hexagon) }

    Box() {
        AsyncImage(
            model = champion.image,
            contentDescription = champion.name,
            contentScale = ContentScale.FillWidth,
            modifier = modifier
                .graphicsLayer {
                    this.shape = clip
                    this.clip = true
                }
                .size(width)
                .border(
                    width = 2.dp,
                    shape = clip,
                    color = if (isSwapping) Color.Red else Color.Black
                )
        )
        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            for ((index, item) in champion.items.withIndex()) {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width.times(0.25f))
                        .combinedClickable(
                            onDoubleClick = { itemRemove(championIndex, index) },
                            onClick = {})
                )
            }
        }
    }
}

@Composable
fun EmptySpace(modifier: Modifier = Modifier, width: Dp = 96.dp, isSwapping: Boolean) {
    val hexagon = remember {
        RoundedPolygon(6, rounding = CornerRounding(0.2f))
    }

    val clip = remember(hexagon) { RoundedPolygonShape(polygon = hexagon) }

    Box(modifier = modifier
        .graphicsLayer {
            this.shape = clip
            this.clip = true
        }
        .size(width)
        .border(width = 2.dp, shape = clip, color = if (isSwapping) Color.Red else Color.Black))
}