package com.aram.mehrabyan.weatherstories.presentation.stories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aram.mehrabyan.weatherstories.domain.usecase.GetStoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Stories Screen
 * Handles business logic and state management for story display
 */
@HiltViewModel
class StoriesViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<StoriesUiState>(StoriesUiState.Loading)
    val uiState: StateFlow<StoriesUiState> = _uiState.asStateFlow()

    private val city: String = savedStateHandle.get<String>("city") ?: "city"

    init {
        fetchStories()
    }

    /**
     * Fetch stories for the city
     */
    private fun fetchStories() {
        viewModelScope.launch {
            _uiState.value = StoriesUiState.Loading

            val result = getStoriesUseCase(city = city, count = 5)

            _uiState.value = if (result.isSuccess) {
                val stories = result.getOrNull()
                if (stories != null && stories.isNotEmpty()) {
                    StoriesUiState.Success(stories = stories)
                } else {
                    StoriesUiState.Error("No stories available")
                }
            } else {
                StoriesUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to fetch stories"
                )
            }
        }
    }

    /**
     * Move to next story
     */
    fun nextStory() {
        val currentState = _uiState.value
        if (currentState is StoriesUiState.Success) {
            val nextIndex = if (currentState.currentIndex < currentState.stories.size - 1) {
                currentState.currentIndex + 1
            } else {
                0 // Loop back to first story
            }
            _uiState.value = currentState.copy(
                currentIndex = nextIndex,
                progress = 0f
            )
        }
    }

    /**
     * Move to previous story
     */
    fun previousStory() {
        val currentState = _uiState.value
        if (currentState is StoriesUiState.Success) {
            val prevIndex = if (currentState.currentIndex > 0) {
                currentState.currentIndex - 1
            } else {
                0 // Stay at first story
            }
            _uiState.value = currentState.copy(
                currentIndex = prevIndex,
                progress = 0f
            )
        }
    }

    /**
     * Update progress for current story
     */
    fun updateProgress(progress: Float) {
        val currentState = _uiState.value
        if (currentState is StoriesUiState.Success) {
            _uiState.value = currentState.copy(progress = progress)
        }
    }

    /**
     * Retry fetching stories
     */
    fun retry() {
        fetchStories()
    }
}