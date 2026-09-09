package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.repository.stories.StoryRepository

/** Likes a slide, or takes the like back. */
class ToggleStoryLikeUseCase(
    private val storyRepository: StoryRepository,
) {
    suspend operator fun invoke(itemId: String) = storyRepository.toggleLike(itemId)
}
