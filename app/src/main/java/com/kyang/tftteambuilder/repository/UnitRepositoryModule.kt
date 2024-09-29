package com.kyang.tftteambuilder.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class UnitRepositoryModule {

    @Binds
    abstract fun provideUnitRepository(impl: LocalUnitRepository): UnitRepository
}