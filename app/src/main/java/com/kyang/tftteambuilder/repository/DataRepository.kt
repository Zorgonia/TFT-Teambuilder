package com.kyang.tftteambuilder.repository

import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.ChampionTrait
import com.kyang.tftteambuilder.data.model.ItemModel

interface DataRepository {

    suspend fun getUnitBox(): BoxModel

    suspend fun loadTraitData()

    suspend fun loadItems(): ItemModel

    fun getTraitData(): List<ChampionTrait>
}