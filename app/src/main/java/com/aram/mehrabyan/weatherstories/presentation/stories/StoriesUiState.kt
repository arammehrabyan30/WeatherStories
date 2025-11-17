package com.aram.mehrabyan.weatherstories.presentation.stories

import com.aram.mehrabyan.weatherstories.domain.model.Story


/**
 * UI State for Stories Screen
 */
sealed class StoriesUiState {
    data object Loading : StoriesUiState()
    data class Success(
        val stories: List<Story>,
        val currentIndex: Int = 0,
        val progress: Float = 0f
    ) : StoriesUiState()

    data class Error(val message: String) : StoriesUiState()
}