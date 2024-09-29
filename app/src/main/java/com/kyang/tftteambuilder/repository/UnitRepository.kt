package com.kyang.tftteambuilder.repository

import android.content.Context
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.ChampionTrait

interface UnitRepository {

    suspend fun getUnitBox(context: Context): BoxModel

    suspend fun loadTraitData(context: Context)

    fun getTraitData(): List<ChampionTrait>
}