package com.aram.mehrabyan.weatherstories.domain.usecase

import com.aram.mehrabyan.weatherstories.data.repo.StoryRepository
import com.aram.mehrabyan.weatherstories.domain.model.Story
import jakarta.inject.Inject

/**
 * Use case for fetching stories (random photos) for a given city
 */
class GetStoriesUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    companion object {
        private const val DEFAULT_STORY_COUNT = 5
    }

    suspend operator fun invoke(
        city: String,
        count: Int = DEFAULT_STORY_COUNT
    ): Result<List<Story>> {
        return try {
            storyRepository.fetchRandomPhotos(city, count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}