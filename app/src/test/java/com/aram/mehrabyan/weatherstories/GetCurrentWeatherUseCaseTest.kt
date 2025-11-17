package com.aram.mehrabyan.weatherstories

import android.location.Location
import com.aram.mehrabyan.weatherstories.data.repo.LocationRepository
import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepository
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import com.aram.mehrabyan.weatherstories.domain.usecase.GetCurrentWeatherUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetCurrentWeatherUseCase
 */
class GetCurrentWeatherUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    @Before
    fun setup() {
        weatherRepository = mockk()
        locationRepository = mockk()
        useCase = GetCurrentWeatherUseCase(weatherRepository, locationRepository)
    }

    @Test
    fun `invoke should return weather when location and weather fetch succeed`() = runTest {
        // Given
        val mockLocation = mockk<Location> {
            coEvery { latitude } returns 51.5074
            coEvery { longitude } returns -0.1278
        }
        val mockWeather = Weather(
            lat = 51.5074,
            lon = -0.1278,
            weatherConditions = emptyList(),
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

        coEvery { locationRepository.getCurrentLocation() } returns Result.success(mockLocation)
        coEvery { weatherRepository.fetchWeather(51.5074, -0.1278) } returns Result.success(
            mockWeather
        )

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockWeather, result.getOrNull())
        coVerify { locationRepository.getCurrentLocation() }
        coVerify { weatherRepository.fetchWeather(51.5074, -0.1278) }
    }

    @Test
    fun `invoke should return failure when location fetch fails`() = runTest {
        // Given
        val exception = SecurityException("Location permission denied")
        coEvery { locationRepository.getCurrentLocation() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { locationRepository.getCurrentLocation() }
        coVerify(exactly = 0) { weatherRepository.fetchWeather(any(), any()) }
    }

    @Test
    fun `invoke should return failure when weather fetch fails`() = runTest {
        // Given
        val mockLocation = mockk<Location> {
            coEvery { latitude } returns 51.5074
            coEvery { longitude } returns -0.1278
        }
        val exception = Exception("Network error")

        coEvery { locationRepository.getCurrentLocation() } returns Result.success(mockLocation)
        coEvery { weatherRepository.fetchWeather(51.5074, -0.1278) } returns Result.failure(
            exception
        )

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        coVerify { locationRepository.getCurrentLocation() }
        coVerify { weatherRepository.fetchWeather(51.5074, -0.1278) }
    }
}

