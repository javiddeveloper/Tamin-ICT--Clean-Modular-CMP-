package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.repository.stories.StoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * The channels for the «تازه‌ها» rail.
 *
 * [forceRefresh] belongs to the rail's retry affordance; every other caller leaves it alone and
 * shares the one fetch the repository has already made.
 */
class GetStoryChannelsUseCase(
    private val storyRepository: StoryRepository,
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<List<StoryChannelDN>> =
        storyRepository.getChannels(forceRefresh)
}
