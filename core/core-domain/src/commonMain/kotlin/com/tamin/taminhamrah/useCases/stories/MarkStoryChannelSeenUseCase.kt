package com.tamin.taminhamrah.useCases.stories

import com.tamin.taminhamrah.repository.stories.StoryRepository

/** Records that a channel has been watched through, or closed out of. */
class MarkStoryChannelSeenUseCase(
    private val storyRepository: StoryRepository,
) {
    suspend operator fun invoke(channelKey: String) = storyRepository.markChannelSeen(channelKey)
}
