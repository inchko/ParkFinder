package com.inchko.parkfinder.hilt.di

import com.inchko.parkfinder.network.ApiService
import com.inchko.parkfinder.network.Repository
import com.inchko.parkfinder.network.ServiceRepo
import com.inchko.parkfinder.network.models.TestDTO2Test
import com.inchko.parkfinder.network.models.Ubi2UbiDTO
import com.inchko.parkfinder.network.models.ZoneDTO2Zone
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun providesRepoService(
        apiService: ApiService,
        testMapper: TestDTO2Test,
        zoneMapper: ZoneDTO2Zone,
        ubiMapper: Ubi2UbiDTO
    ): ServiceRepo {
        return ServiceRepo(apiService, testMapper, zoneMapper, ubiMapper)
    }
}