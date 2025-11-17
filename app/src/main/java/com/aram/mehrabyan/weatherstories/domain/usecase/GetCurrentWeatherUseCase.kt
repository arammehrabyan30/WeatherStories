package com.aram.mehrabyan.weatherstories.domain.usecase

import com.aram.mehrabyan.weatherstories.data.repo.LocationRepository
import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepository
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import javax.inject.Inject

/**
 * Use case for fetching current weather based on device location
 * This encapsulates the business logic of:
 * 1. Getting current location
 * 2. Fetching weather for that location
 */
class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<Weather> {
        return try {
            // First, get current location
            val locationResult = locationRepository.getCurrentLocation()

            if (locationResult.isFailure) {
                return Result.failure(
                    locationResult.exceptionOrNull()
                        ?: Exception("Failed to get location")
                )
            }

            val location = locationResult.getOrNull() ?: return Result.failure(
                Exception("Location is null")
            )

            // Then fetch weather for that location
            weatherRepository.fetchWeather(
                lat = location.latitude,
                lon = location.longitude
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

