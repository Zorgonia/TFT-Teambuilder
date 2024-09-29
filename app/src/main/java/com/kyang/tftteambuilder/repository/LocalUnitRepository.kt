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
    override suspend fun getUnitBox(context: Context): BoxModel {
        return dataSource.getUnitBox(context)
    }

    override suspend fun loadTraitData(context: Context) {
        return dataSource.loadTraitData(context)
    }

    override fun getTraitData(): List<ChampionTrait> {
        TODO("Not yet implemented")
    }
}