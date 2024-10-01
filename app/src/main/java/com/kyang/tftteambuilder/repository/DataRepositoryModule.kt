package com.kyang.tftteambuilder.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class DataRepositoryModule {

    @Binds
    abstract fun provideDataRepository(impl: LocalDataRepository): DataRepository
}