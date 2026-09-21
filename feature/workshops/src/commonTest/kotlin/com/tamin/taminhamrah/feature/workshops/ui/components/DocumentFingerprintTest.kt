package com.tamin.taminhamrah.feature.workshops.ui.components

import com.tamin.taminhamrah.feature.workshops.ui.model.ArticleSixteenDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The same image is not attached twice, whatever the picker names it. */
class DocumentFingerprintTest {

    private val photo = byteArrayOf(1, 2, 3, 4)
    private val attached = listOf(
        WorkshopAttachment(guid = "g1", type = ArticleSixteenDocumentTypes.first(), byteCount = 4),
    )

    @Test
    fun `an attached image is recognised when picked again`() {
        val fingerprints = mapOf("g1" to photo.fingerprint())

        assertTrue(attached.holdsFile(photo.copyOf().fingerprint(), fingerprints))
    }

    @Test
    fun `a different image is not taken for a duplicate`() {
        val fingerprints = mapOf("g1" to photo.fingerprint())

        assertFalse(attached.holdsFile(byteArrayOf(1, 2, 3, 5).fingerprint(), fingerprints))
        assertFalse(attached.holdsFile(byteArrayOf(1, 2, 3).fingerprint(), fingerprints))
    }

    @Test
    fun `a removed attachment no longer blocks its image`() {
        // The map outlives a removal; only attachments still listed count.
        val fingerprints = mapOf("gone" to photo.fingerprint())

        assertFalse(attached.holdsFile(photo.fingerprint(), fingerprints))
    }

    @Test
    fun `a document read back from the service is never a match`() {
        // Its bytes are counted, not decoded, so it has no fingerprint.
        assertFalse(attached.holdsFile(photo.fingerprint(), emptyMap()))
    }
}
