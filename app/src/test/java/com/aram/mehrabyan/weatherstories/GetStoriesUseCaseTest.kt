package com.aram.mehrabyan.weatherstories

import com.aram.mehrabyan.weatherstories.data.repo.StoryRepository
import com.aram.mehrabyan.weatherstories.domain.model.Story
import com.aram.mehrabyan.weatherstories.domain.usecase.GetStoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetStoriesUseCase
 */
class GetStoriesUseCaseTest {

    private lateinit var storyRepository: StoryRepository
    private lateinit var useCase: GetStoriesUseCase

    @Before
    fun setup() {
        storyRepository = mockk()
        useCase = GetStoriesUseCase(storyRepository)
    }

    @Test
    fun `invoke should return stories when fetch succeeds`() = runTest {
        // Given
        val city = "Paris"
        val count = 5
        val mockStories = listOf(
            Story("https://example.com/image1.jpg"),
            Story("https://example.com/image2.jpg"),
            Story("https://example.com/image3.jpg"),
            Story("https://example.com/image4.jpg"),
            Story("https://example.com/image5.jpg")
        )

        coEvery { storyRepository.fetchRandomPhotos(city, count) } returns Result.success(mockStories)

        // When
        val result = useCase(city, count)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockStories, result.getOrNull())
        assertEquals(5, result.getOrNull()?.size)
        coVerify { storyRepository.fetchRandomPhotos(city, count) }
    }

    @Test
    fun `invoke should use default count when not specified`() = runTest {
        // Given
        val city = "Tokyo"
        val mockStories = listOf(
            Story("https://example.com/image1.jpg"),
            Story("https://example.com/image2.jpg")
        )

        coEvery { storyRepository.fetchRandomPhotos(city, 5) } returns Result.success(mockStories)

        // When
        val result = useCase(city)

        // Then
        assertTrue(result.isSuccess)
        coVerify { storyRepository.fetchRandomPhotos(city, 5) }
    }

    @Test
    fun `invoke should return failure when fetch fails`() = runTest {
        // Given
        val city = "London"
        val count = 5
        val exception = Exception("Network error")

        coEvery { storyRepository.fetchRandomPhotos(city, count) } returns Result.failure(exception)

        // When
        val result = useCase(city, count)

        // Then
        assertTrue(result.isFailure)
        coVerify { storyRepository.fetchRandomPhotos(city, count) }
    }
}

