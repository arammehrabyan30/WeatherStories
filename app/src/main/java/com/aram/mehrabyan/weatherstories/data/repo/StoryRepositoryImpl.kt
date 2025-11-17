package com.aram.mehrabyan.weatherstories.data.repo

import com.aram.mehrabyan.weatherstories.BuildConfig
import com.aram.mehrabyan.weatherstories.data.apiservice.UnsplashApiService
import com.aram.mehrabyan.weatherstories.domain.model.Story
import javax.inject.Inject

/**
 * Implementation of StoryRepository
 */
class StoryRepositoryImpl @Inject constructor(
    private val apiService: UnsplashApiService
) : StoryRepository {

    override suspend fun fetchRandomPhotos(city: String, count: Int): Result<List<Story>> {
        return try {
            val response = apiService.getRandomPhotos(
                authorization = "Client-ID ${BuildConfig.UNSPLASH_API_KEY}",
                query = city,
                count = count
            )

            if (response.isSuccessful) {
                val photosDTO = response.body()
                if (photosDTO != null) {
                    val stories = photosDTO.map { it.toDomain() }
                    Result.success(stories)
                } else {
                    Result.failure(Exception("Photos data is null"))
                }
            } else {
                Result.failure(
                    Exception("API Error: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

