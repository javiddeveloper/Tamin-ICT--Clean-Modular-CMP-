package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.repository.stories.StoryRepository
import kotlinx.coroutines.flow.Flow

/** Which channels have been watched, so the rail can grey their rings. */
class ObserveSeenStoryChannelsUseCase(
    private val storyRepository: StoryRepository,
) {
    operator fun invoke(): Flow<Set<String>> = storyRepository.observeSeenChannels()
}
