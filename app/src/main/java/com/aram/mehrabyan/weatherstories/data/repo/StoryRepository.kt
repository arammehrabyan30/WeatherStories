package com.aram.mehrabyan.weatherstories.data.repo

import com.aram.mehrabyan.weatherstories.domain.model.Story

/**
 * Repository interface for story data operations
 */
interface StoryRepository {
    /**
     * Fetch random photos for a given city
     * @param city City name to search for
     * @param count Number of photos to fetch
     * @return List of stories
     */
    suspend fun fetchRandomPhotos(city: String, count: Int): Result<List<Story>>
}