package com.aram.mehrabyan.weatherstories.data.apiservice

import com.aram.mehrabyan.weatherstories.data.dto.WeatherResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service for OpenWeatherMap
 */
interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponseDTO>
}