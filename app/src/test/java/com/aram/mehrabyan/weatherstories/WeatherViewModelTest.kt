package com.aram.mehrabyan.weatherstories

import com.aram.mehrabyan.weatherstories.data.repo.LocationRepository
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import com.aram.mehrabyan.weatherstories.domain.usecase.GetCurrentWeatherUseCase
import com.aram.mehrabyan.weatherstories.presentation.weather.WeatherUiState
import com.aram.mehrabyan.weatherstories.presentation.weather.WeatherViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
* Unit tests for WeatherViewModel
*/
@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var locationRepository: LocationRepository
    private lateinit var viewModel: WeatherViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrentWeatherUseCase = mockk()
        locationRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should check permission and fetch weather if granted`() = runTest {
        // Given
        val mockWeather = Weather(
            lat = 51.5074,
            lon = -0.1278,
            weatherConditions = null,
            temp = 15.0,
            feelsLike = 13.0,
            tempMin = 10.0,
            tempMax = 18.0,
            pressure = 1013,
            humidity = 80,
            windSpeed = 5.0,
            windDeg = 180,
            windGust = 8.0,
            cloudsAll = 50,
            sunCountry = "GB",
            sunSunrise = null,
            sunSet = null,
            name = "London",
            visibility = 10000,
            timestamp = null
        )

        every { locationRepository.hasLocationPermission() } returns true
        coEvery { getCurrentWeatherUseCase() } returns Result.success(mockWeather)

        // When
        viewModel = WeatherViewModel(getCurrentWeatherUseCase, locationRepository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals(mockWeather, (state as WeatherUiState.Success).weather)
    }

    @Test
    fun `initial state should show permission denied if not granted`() = runTest {
        // Given
        every { locationRepository.hasLocationPermission() } returns false

        // When
        viewModel = WeatherViewModel(getCurrentWeatherUseCase, locationRepository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.PermissionDenied)
    }

    @Test
    fun `fetchWeather should update state to error when use case fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        every { locationRepository.hasLocationPermission() } returns true
        coEvery { getCurrentWeatherUseCase() } returns Result.failure(exception)

        // When
        viewModel = WeatherViewModel(getCurrentWeatherUseCase, locationRepository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Network error", (state as WeatherUiState.Error).message)
    }

    @Test
    fun `retry should fetch weather again`() = runTest {
        // Given
        val mockWeather = Weather(
            lat = 40.7128,
            lon = -74.0060,
            weatherConditions = null,
            temp = 20.0,
            feelsLike = 18.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1015,
            humidity = 65,
            windSpeed = 3.5,
            windDeg = 90,
            windGust = 5.0,
            cloudsAll = 20,
            sunCountry = "US",
            sunSunrise = null,
            sunSet = null,
            name = "New York",
            visibility = 10000,
            timestamp = null
        )

        every { locationRepository.hasLocationPermission() } returns true
        coEvery { getCurrentWeatherUseCase() } returns Result.success(mockWeather)

        viewModel = WeatherViewModel(getCurrentWeatherUseCase, locationRepository)

        // When
        viewModel.retry()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
    }
}

