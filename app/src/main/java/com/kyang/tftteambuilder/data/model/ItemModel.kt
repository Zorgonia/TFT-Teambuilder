package com.kyang.tftteambuilder.data.model

data class ItemModel(
    val items: List<TftItem>
)

data class TftItem(
    val name: String,
    val image: String,
    val description: String,
)
