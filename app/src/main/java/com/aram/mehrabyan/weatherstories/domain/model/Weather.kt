package com.aram.mehrabyan.weatherstories.domain.model

import java.util.Date

/**
 * Domain model representing weather data
 */
data class Weather(
    val lat: Double?,
    val lon: Double?,
    val weatherConditions: List<WeatherCondition>?,
    val temp: Double?,
    val feelsLike: Double?,
    val tempMin: Double?,
    val tempMax: Double?,
    val pressure: Int?,
    val humidity: Int?,
    val windSpeed: Double?,
    val windDeg: Int?,
    val windGust: Double?,
    val cloudsAll: Int?,
    val sunCountry: String?,
    val sunSunrise: Date?,
    val sunSet: Date?,
    val name: String?,
    val visibility: Int?,
    val timestamp: Date?
) {
    data class WeatherCondition(
        val id: Int?,
        val main: String?,
        val description: String?,
        val icon: String?
    )

    /**
     * Get the primary weather description
     */
    fun getMainDescription(): String {
        return weatherConditions?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            ?: "Unknown"
    }

    /**
     * Get formatted temperature
     */
    fun getFormattedTemp(): String {
        return temp?.let { "${it.toInt()}°C" } ?: "N/A"
    }

    /**
     * Get formatted feels like temperature
     */
    fun getFormattedFeelsLike(): String {
        return feelsLike?.let { "${it.toInt()}°C" } ?: "N/A"
    }
}
