package com.kyang.tftteambuilder.data.model

import androidx.compose.ui.graphics.Color

data class ChampionTrait(
    val breakpoints: List<TraitBreakpoint>,
    val name: String,
    val description: String,
    val image: String
)

data class TraitBreakpoint(
    val breakPoint: Int,
    val tier: TraitTier,
    val subtext: String
)

enum class TraitTier(val image: String, val color: Color, val compareValue: Int) {
    NONE("https://cdn.metatft.com/file/metatft/traits/base.png", Color(16,16,16), 0),
    BRONZE("https://ap.tft.tools/img/general/trait_1.png", Color(205,177,50), 1),
    SILVER("https://ap.tft.tools/img/general/trait_2.png", Color(212,212,212), 2),
    GOLD("https://ap.tft.tools/img/general/trait_3.png", Color(255,215,0), 3),
    PRISMATIC("https://ap.tft.tools/img/general/trait_4.png", Color(255,255,255), 4),
    UNIQUE("https://ap.tft.tools/img/general/trait_5.png", Color(255,165,0), 5),
}

data class TraitModel(
    val traits: List<ActiveTrait> = listOf()
)

data class ActiveTrait(
    val trait: ChampionTrait,
    val current: Int,
    val breakpoint: TraitBreakpoint
)