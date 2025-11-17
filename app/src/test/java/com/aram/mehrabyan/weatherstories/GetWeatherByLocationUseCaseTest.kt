package com.aram.mehrabyan.weatherstories

import com.aram.mehrabyan.weatherstories.data.repo.WeatherRepository
import com.aram.mehrabyan.weatherstories.domain.model.Weather
import com.aram.mehrabyan.weatherstories.domain.usecase.GetWeatherByLocationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetWeatherByLocationUseCase
 */
class GetWeatherByLocationUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var useCase: GetWeatherByLocationUseCase

    @Before
    fun setup() {
        weatherRepository = mockk()
        useCase = GetWeatherByLocationUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return weather when fetch succeeds`() = runTest {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val mockWeather = Weather(
            lat = lat,
            lon = lon,
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

        coEvery { weatherRepository.fetchWeather(lat, lon) } returns Result.success(mockWeather)

        // When
        val result = useCase(lat, lon)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockWeather, result.getOrNull())
        coVerify { weatherRepository.fetchWeather(lat, lon) }
    }

    @Test
    fun `invoke should return failure when fetch fails`() = runTest {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val exception = Exception("API Error")

        coEvery { weatherRepository.fetchWeather(lat, lon) } returns Result.failure(exception)

        // When
        val result = useCase(lat, lon)

        // Then
        assertTrue(result.isFailure)
        coVerify { weatherRepository.fetchWeather(lat, lon) }
    }
}

