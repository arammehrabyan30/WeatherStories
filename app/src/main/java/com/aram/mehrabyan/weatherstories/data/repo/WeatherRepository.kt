package com.aram.mehrabyan.weatherstories.data.repo

import com.aram.mehrabyan.weatherstories.domain.model.Weather


/**
 * Repository interface for weather data operations
 */
interface WeatherRepository {
    /**
     * Fetch weather data for given coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @return Weather data or throws exception
     */
    suspend fun fetchWeather(lat: Double, lon: Double): Result<Weather>
}