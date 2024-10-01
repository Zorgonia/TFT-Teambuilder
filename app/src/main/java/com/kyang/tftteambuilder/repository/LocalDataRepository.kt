package com.kyang.tftteambuilder.repository

import com.kyang.tftteambuilder.data.LocalDataSource
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.ChampionTrait
import com.kyang.tftteambuilder.data.model.ItemModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataRepository @Inject constructor(
    private val dataSource: LocalDataSource
): DataRepository {
    override suspend fun getUnitBox(): BoxModel {
        return dataSource.getUnitBox()
    }

    override suspend fun loadTraitData() {
        return dataSource.loadTraitData()
    }

    override suspend fun loadItems(): ItemModel {
        return dataSource.getItemData()
    }

    override fun getTraitData(): List<ChampionTrait> {
        return dataSource.getTraitData()
    }
}