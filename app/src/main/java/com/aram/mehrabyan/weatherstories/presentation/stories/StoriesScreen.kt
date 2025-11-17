package com.aram.mehrabyan.weatherstories.presentation.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.aram.mehrabyan.weatherstories.domain.model.Story
import kotlinx.coroutines.delay

/**
 * Stories Screen Composable
 * Mimics Instagram stories with auto-progression and manual swipe
 */
@Composable
fun StoriesScreen(
    onClose: () -> Unit,
    viewModel: StoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val state = uiState) {
            is StoriesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            is StoriesUiState.Success -> {
                StoriesContent(
                    stories = state.stories,
                    currentIndex = state.currentIndex,
                    progress = state.progress,
                    onNext = { viewModel.nextStory() },
                    onPrevious = { viewModel.previousStory() },
                    onUpdateProgress = { viewModel.updateProgress(it) },
                    onClose = onClose
                )
            }

            is StoriesUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.retry() },
                    onClose = onClose
                )
            }
        }
    }
}

@Composable
private fun StoriesContent(
    stories: List<Story>,
    currentIndex: Int,
    progress: Float,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onUpdateProgress: (Float) -> Unit,
    onClose: () -> Unit
) {
    // Auto-progress timer
    LaunchedEffect(currentIndex) {
        val duration = 3000L // 3 seconds
        val interval = 50L // Update every 50ms
        val steps = duration / interval

        repeat(steps.toInt()) { step ->
            delay(interval)
            val newProgress = (step + 1).toFloat() / steps
            onUpdateProgress(newProgress)

            // Auto advance to next story when progress reaches 100%
            if (newProgress >= 1.0f) {
                onNext()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Current story image
        val currentStory = stories[currentIndex]

        SubcomposeAsyncImage(
            model = currentStory.imageUrl,
            contentDescription = "Story ${currentIndex + 1}",
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        when {
                            dragAmount.x > 50 -> onPrevious()
                            dragAmount.x < -50 -> onNext()
                        }
                    }
                },
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load image",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )

        // Progress bars at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        val progressValue = when {
                            index < currentIndex -> 1f
                            index == currentIndex -> progress
                            else -> 0f
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressValue)
                                .background(Color.White)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Touch zones for manual navigation
        Row(modifier = Modifier.fillMaxSize()) {
            // Left side - previous story
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { _, _ ->
                            // Handled by parent
                        }
                    }
            )

            // Right side - next story
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { _, _ ->
                            // Handled by parent
                        }
                    }
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚠️ Error",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClose,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
