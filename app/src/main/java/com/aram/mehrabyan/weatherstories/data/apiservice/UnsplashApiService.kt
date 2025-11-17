package com.aram.mehrabyan.weatherstories.data.apiservice

import com.aram.mehrabyan.weatherstories.data.dto.UnsplashPhotoDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * API Service for Unsplash
 */
interface UnsplashApiService {

    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("orientation") orientation: String = "portrait",
        @Query("count") count: Int
    ): Response<List<UnsplashPhotoDTO>>
}