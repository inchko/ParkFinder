package com.inchko.parkfinder.hilt.di

import com.google.gson.GsonBuilder
import com.inchko.parkfinder.network.*
import com.inchko.parkfinder.network.models.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        return Retrofit.Builder()
            .baseUrl("https://us-central1-parkingfinder-305518.cloudfunctions.net/app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesRouteService(): RouteService {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/").client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(RouteService::class.java)
    }

    @Singleton
    @Provides
    fun providesRepo(
        apiService: ApiService,
        testMapper: TestDTO2Test,
        zoneMapper: ZoneDTO2Zone,
        ubiMapper: Ubi2UbiDTO
    ): Repository {
        return Repository(apiService, testMapper, zoneMapper, ubiMapper)
    }

    @Singleton
    @Provides
    fun providesRouteRepo(
        routeService: RouteService,
    ): RouteRepo {
        return RouteRepo(routeService)
    }

    @Singleton
    @Provides
    fun providesUserRepo(
        apiService: ApiService,
        mapper: UserDTO2User
    ): RepoUsers {
        return RepoUsers(apiService, mapper)
    }

    @Singleton
    @Provides
    fun providesPOIRepo(
        apiService: ApiService,
        mapper: POIDTO2POI
    ): RepoPOI {
        return RepoPOI(apiService, mapper)
    }

    @Singleton
    @Provides
    fun providesFavZoneRepo(
        apiService: ApiService,
        mapper: FavZoneDTO2FavZone
    ): RepoFavZones {
        return RepoFavZones(apiService, mapper)
    }
}