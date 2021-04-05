package com.inchko.parkfinder.hilt.di

import com.inchko.parkfinder.network.ApiService
import com.inchko.parkfinder.network.Repository
import com.inchko.parkfinder.network.models.TestDTO2Test
import com.inchko.parkfinder.network.models.Ubi2UbiDTO
import com.inchko.parkfinder.network.models.ZoneDTO2Zone
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun textOnPlaces(): String {
        return "This text has been provided by hilt"
    }

    @Singleton
    @Provides
    @Named("Test_Text")
    fun texttest(): String {
        return "Second text"
    }

    @Singleton
    @Provides
    fun providesTestMapper(): TestDTO2Test {
        return TestDTO2Test()
    }

    @Singleton
    @Provides
    fun providesZoneMapper(): ZoneDTO2Zone {
        return ZoneDTO2Zone()
    }

    @Singleton
    @Provides
    fun providesUbiMapper(): Ubi2UbiDTO {
        return Ubi2UbiDTO()
    }
}

