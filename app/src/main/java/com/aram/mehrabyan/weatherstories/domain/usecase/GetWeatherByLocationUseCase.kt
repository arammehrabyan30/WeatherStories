package com.aram.mehrabyan.weatherstories.domain.usecase

import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepository
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import jakarta.inject.Inject

/**
 * Use case for fetching weather by specific coordinates
 */
class GetWeatherByLocationUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> {
        return try {
            weatherRepository.fetchWeather(lat, lon)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}