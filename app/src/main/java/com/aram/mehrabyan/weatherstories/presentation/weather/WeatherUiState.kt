package com.aram.mehrabyan.weatherstories.presentation.weather

import com.aram.mehrabyan.weatherstories.domain.model.Weather

/**
 * UI State for Weather Screen
 */
sealed class WeatherUiState {
    data object Initial : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    data class PermissionDenied(val message: String) : WeatherUiState()
}