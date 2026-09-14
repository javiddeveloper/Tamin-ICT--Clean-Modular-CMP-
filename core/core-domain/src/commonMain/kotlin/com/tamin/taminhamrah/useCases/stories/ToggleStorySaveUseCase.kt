package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.repository.stories.StoryRepository

/** Bookmarks a slide, or removes the bookmark. */
class ToggleStorySaveUseCase(
    private val storyRepository: StoryRepository,
) {
    suspend operator fun invoke(itemId: String) = storyRepository.toggleSave(itemId)
}
