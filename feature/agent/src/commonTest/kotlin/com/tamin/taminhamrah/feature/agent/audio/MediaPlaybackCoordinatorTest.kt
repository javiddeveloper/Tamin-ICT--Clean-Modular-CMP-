package com.tamin.taminhamrah.feature.agent.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The coordinator is what keeps a voice message and a video from playing at once, so
 * these cases pin the handover rules.
 */
class MediaPlaybackCoordinatorTest {

    private val coordinator = MediaPlaybackCoordinator()

    @Test
    fun `claiming playback takes it from the previous owner`() {
        val voice = MediaPlaybackCoordinator.voiceOwner("msg-1")
        val video = MediaPlaybackCoordinator.videoOwner("https://x/a.mp4")

        coordinator.claim(voice)
        assertTrue(coordinator.holdsPlayback(voice))

        coordinator.claim(video)

        assertEquals(video, coordinator.activeOwner.value)
        assertFalse(coordinator.holdsPlayback(voice), "the voice must lose the audio")
    }

    @Test
    fun `releasing only clears playback for the current owner`() {
        val first = MediaPlaybackCoordinator.voiceOwner("msg-1")
        val second = MediaPlaybackCoordinator.voiceOwner("msg-2")

        coordinator.claim(first)
        coordinator.claim(second)

        // A late release from the clip that already lost the audio must not stop the
        // one now playing.
        coordinator.release(first)

        assertEquals(second, coordinator.activeOwner.value)
    }

    @Test
    fun `releasing the current owner leaves nothing playing`() {
        val owner = MediaPlaybackCoordinator.videoOwner("https://x/a.mp4")

        coordinator.claim(owner)
        coordinator.release(owner)

        assertNull(coordinator.activeOwner.value)
    }

    @Test
    fun `owner ids are namespaced so voice and video never collide`() {
        val sameId = "same"

        assertTrue(MediaPlaybackCoordinator.voiceOwner(sameId).startsWith("voice:"))
        assertTrue(MediaPlaybackCoordinator.videoOwner(sameId).startsWith("video:"))
        assertTrue(MediaPlaybackCoordinator.PREVIEW_OWNER.startsWith("voice:"))
    }
}
