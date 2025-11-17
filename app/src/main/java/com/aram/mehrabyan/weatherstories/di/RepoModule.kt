package com.aram.mehrabyan.weatherstories.di

import com.aram.mehrabyan.weatherstories.data.repo.LocationRepository
import com.aram.mehrabyan.weatherstories.data.repo.LocationRepositoryImpl
import com.aram.mehrabyan.weatherstories.data.repo.StoryRepository
import com.aram.mehrabyan.weatherstories.data.repo.StoryRepositoryImpl
import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepository
import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to their implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ): StoryRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}

