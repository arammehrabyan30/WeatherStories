package com.aram.mehrabyan.weatherstories.data.dto

import com.aram.mehrabyan.weatherstories.domain.model.Story
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Unsplash API response
 */
data class UnsplashPhotoDTO(
    @SerializedName("urls") val urls: UrlsDTO
) {
    data class UrlsDTO(
        @SerializedName("regular") val regular: String
    )

    /**
     * Convert DTO to Domain model
     */
    fun toDomain(): Story {
        return Story(imageUrl = urls.regular)
    }
}