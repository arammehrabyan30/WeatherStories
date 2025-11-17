package com.aram.mehrabyan.weatherstories.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aram.mehrabyan.weatherstories.data.repo.LocationRepository
import com.aram.mehrabyan.weatherstories.domain.usecase.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Weather Screen
 * Handles business logic and state management for weather display
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndFetchWeather()
    }

    /**
     * Check location permission and fetch weather
     */
    fun checkPermissionAndFetchWeather() {
        if (!locationRepository.hasLocationPermission()) {
            _uiState.value = WeatherUiState.PermissionDenied(
                "Location permission is required to fetch weather data"
            )
            return
        }
        fetchWeather()
    }

    /**
     * Fetch weather data
     */
    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            val result = getCurrentWeatherUseCase()

            _uiState.value = if (result.isSuccess) {
                val weather = result.getOrNull()
                if (weather != null) {
                    WeatherUiState.Success(weather)
                } else {
                    WeatherUiState.Error("Failed to fetch weather data")
                }
            } else {
                val error = result.exceptionOrNull()
                when (error) {
                    is SecurityException -> {
                        WeatherUiState.PermissionDenied(
                            error.message ?: "Location permission is required"
                        )
                    }
                    else -> {
                        WeatherUiState.Error(
                            error?.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    /**
     * Retry fetching weather
     */
    fun retry() {
        checkPermissionAndFetchWeather()
    }
}

