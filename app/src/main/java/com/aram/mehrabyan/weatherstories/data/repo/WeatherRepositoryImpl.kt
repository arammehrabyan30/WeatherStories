package com.aram.mehrabyan.weatherstories.data.repo

import com.aram.mehrabyan.weatherstories.BuildConfig
import com.aram.mehrabyan.weatherstories.data.apiservice.WeatherApiService
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import javax.inject.Inject

/**
 * Implementation of WeatherRepository
 */
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double): Result<Weather> {
        return try {
            val response = apiService.getWeather(
                latitude = lat,
                longitude = lon,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )

            if (response.isSuccessful) {
                val weatherDTO = response.body()
                if (weatherDTO != null) {
                    Result.success(weatherDTO.toDomain())
                } else {
                    Result.failure(Exception("Weather data is null"))
                }
            } else {
                Result.failure(
                    Exception("API Error: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

