package com.kyang.tftteambuilder.repository

import android.content.Context
import com.kyang.tftteambuilder.data.LocalDataSource
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.ChampionTrait
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUnitRepository @Inject constructor(
    private val dataSource: LocalDataSource
): UnitRepository {
    override suspend fun getUnitBox(): BoxModel {
        return dataSource.getUnitBox()
    }

    override suspend fun loadTraitData() {
        return dataSource.loadTraitData()
    }

    override fun getTraitData(): List<ChampionTrait> {
        TODO("Not yet implemented")
    }
}