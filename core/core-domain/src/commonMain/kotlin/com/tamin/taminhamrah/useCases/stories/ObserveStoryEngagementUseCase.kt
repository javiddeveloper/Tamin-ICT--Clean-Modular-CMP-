package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.model.stories.StoryEngagementDN
import com.tamin.taminhamrah.repository.stories.StoryRepository
import kotlinx.coroutines.flow.Flow

/** What the reader has liked and bookmarked, so the viewer can show it back to them. */
class ObserveStoryEngagementUseCase(
    private val storyRepository: StoryRepository,
) {
    operator fun invoke(): Flow<StoryEngagementDN> = storyRepository.observeEngagement()
}
