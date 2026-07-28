package com.tamin.taminhamrah.feature.agent.audio

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Guards the teardown ordering that crashed playback.
 *
 * `MediaPlayer` throws `IllegalStateException` from every accessor once it has errored
 * or been released. The position poller runs on its own coroutine, so releasing while it
 * is mid-loop used to leave it holding a live reference to a dead player — the exact
 * `MediaPlayer.isPlaying` crash seen in the field.
 *
 * The real player needs an Android runtime, so this exercises the same contract through
 * the interface: releasing must leave the player quiet, reset and safe to poke again.
 */
class AndroidVoicePlayerTest {

    @Test
    fun `releasing resets playback state and stays safe to call twice`() = runTest {
        val player = createVoicePlayer()

        player.release()
        // A second release must not throw: teardown can arrive from the error listener
        // and from onCleared at nearly the same moment.
        player.release()

        assertFalse(player.isPlaying.value)
        assertEquals(0, player.positionMs.value)
        assertEquals(0, player.durationMs.value)
    }

    @Test
    fun `transport calls on a released player are ignored rather than throwing`() = runTest {
        val player = createVoicePlayer()
        player.release()

        // Each of these reaches a null or dead MediaPlayer; none may propagate.
        player.playPause()
        player.seekTo(1_000)
        player.stop()

        assertFalse(player.isPlaying.value)
    }
}
