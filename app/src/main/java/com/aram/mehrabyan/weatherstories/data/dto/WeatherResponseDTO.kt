package com.aram.mehrabyan.weatherstories.data.dto

import com.aram.mehrabyan.weatherstories.domain.model.Weather
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Data Transfer Object for Weather API response
 */
data class WeatherResponseDTO(
    @SerializedName("coord") val coord: CoordDTO?,
    @SerializedName("weather") val weather: List<WeatherConditionDTO>?,
    @SerializedName("main") val main: MainDTO?,
    @SerializedName("wind") val wind: WindDTO?,
    @SerializedName("clouds") val clouds: CloudsDTO?,
    @SerializedName("sys") val sys: SysDTO?,
    @SerializedName("name") val name: String?,
    @SerializedName("visibility") val visibility: Int?,
    @SerializedName("dt") val timestamp: Long?
) {
    /**
     * Convert DTO to Domain model
     */
    fun toDomain(): Weather {
        val weatherConditions = weather?.map {
            Weather.WeatherCondition(
                id = it.id,
                main = it.main,
                description = it.description,
                icon = it.icon
            )
        }

        val sunriseDate = sys?.sunrise?.let { Date(it * 1000) }
        val sunsetDate = sys?.sunset?.let { Date(it * 1000) }
        val timestampDate = timestamp?.let { Date(it * 1000) }

        return Weather(
            lat = coord?.lat,
            lon = coord?.lon,
            weatherConditions = weatherConditions,
            temp = main?.temp,
            feelsLike = main?.feelsLike,
            tempMin = main?.tempMin,
            tempMax = main?.tempMax,
            pressure = main?.pressure,
            humidity = main?.humidity,
            windSpeed = wind?.speed,
            windDeg = wind?.deg,
            windGust = wind?.gust,
            cloudsAll = clouds?.all,
            sunCountry = sys?.country,
            sunSunrise = sunriseDate,
            sunSet = sunsetDate,
            name = name,
            visibility = visibility,
            timestamp = timestampDate
        )
    }
}

data class CoordDTO(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?
)

data class WeatherConditionDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)

data class MainDTO(
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("temp_min") val tempMin: Double?,
    @SerializedName("temp_max") val tempMax: Double?,
    @SerializedName("pressure") val pressure: Int?,
    @SerializedName("humidity") val humidity: Int?
)

data class WindDTO(
    @SerializedName("speed") val speed: Double?,
    @SerializedName("deg") val deg: Int?,
    @SerializedName("gust") val gust: Double?
)

data class CloudsDTO(
    @SerializedName("all") val all: Int?
)

data class SysDTO(
    @SerializedName("country") val country: String?,
    @SerializedName("sunrise") val sunrise: Long?,
    @SerializedName("sunset") val sunset: Long?
)