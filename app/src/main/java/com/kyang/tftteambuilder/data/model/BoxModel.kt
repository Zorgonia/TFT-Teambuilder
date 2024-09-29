package com.kyang.tftteambuilder.data.model

data class BoxModel(
    val tiers: List<BoxTier>
)


data class BoxTier(
    val cost:ChampionCost,
    val champions: List<BoardChampion>
)