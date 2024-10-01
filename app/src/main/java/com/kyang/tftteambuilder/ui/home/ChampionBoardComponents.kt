package com.kyang.tftteambuilder.ui.home

import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
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
    onDrag: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    onRemove: (Pair<Int, Int>) -> Unit,
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
                itemsIndexed(row, key = { index, item -> if (item is BoardChampion) "${item.name} $index" else "$item $index" }) { columnNum, space ->
                    Space(
                        boardSpace = space,
                        onSwap = onSwap,
                        onDrag = onDrag,
                        isSwapping = swapIndex == Pair(rowNum, columnNum),
                        championIndex = Pair(rowNum, columnNum),
                        onRemove = onRemove,
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
    onDrag: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    width: Dp = 64.dp
) {
    val dragDropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                event.mimeTypes().forEachIndexed { index, s ->
                    if (s == "text/plain") {
                        val dataIndex = event.toAndroidDragEvent().clipData.getItemAt(index).text
                        dataIndex.toString().parseAsIndex()?.let {
                            Log.d("test", "$it $championIndex")
                            val label = event.toAndroidDragEvent().clipData.description.label
                            if (label == "Index") {
                                onDrag(it, championIndex)
                            }
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
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains("text/plain")
                    },
                    target = dragDropCallback
                )
                .dragAndDropSource {
                    detectTapGestures(onLongPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                ClipData.newPlainText("Index", championIndex.toString())
                            )
                        )
                    })
                }
        ) {
            ChampionSpace(
                champion = boardSpace,
                championIndex = championIndex,
                width = width,
                isSwapping = isSwapping,
            )
        }
    } else if (boardSpace is EmptyBoardSpace) {
        EmptySpace(
            modifier = modifier
                .clickable { onSwap(championIndex) }
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains("text/plain")
                    },
                    target = dragDropCallback
                ),
            width = width,
            isSwapping = isSwapping
        )
    }
}

@Composable
fun ChampionSpace(
    modifier: Modifier = Modifier,
    width: Dp = 96.dp,
    champion: BoardChampion,
    championIndex: Pair<Int, Int>,
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