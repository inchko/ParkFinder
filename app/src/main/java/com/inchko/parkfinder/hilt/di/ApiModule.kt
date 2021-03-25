package com.inchko.parkfinder.hilt.di

import com.inchko.parkfinder.Apis.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun providesRepo():Repository{
        return Repository()
    }
}