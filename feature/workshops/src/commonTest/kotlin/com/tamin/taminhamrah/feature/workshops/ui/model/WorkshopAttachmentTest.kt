package com.tamin.taminhamrah.feature.workshops.ui.model

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The same image is not attached twice, whatever the picker names it. */
class WorkshopAttachmentTest {

    private val upload = WorkshopAttachmentUploader { "guid" }
    private val photo = byteArrayOf(1, 2, 3, 4)

    @Test
    fun `an uploaded image is recognised when picked again`() = runTest {
        val attached = listOf(upload("IMG_1.jpg", photo, "1", ArticleSixteenDocumentTypes))

        assertTrue(attached.holdsFile(photo.copyOf()))
    }

    @Test
    fun `a different image is not taken for a duplicate`() = runTest {
        val attached = listOf(upload("IMG_1.jpg", photo, "1", ArticleSixteenDocumentTypes))

        assertFalse(attached.holdsFile(byteArrayOf(1, 2, 3, 5)))
        assertFalse(attached.holdsFile(byteArrayOf(1, 2, 3)))
    }

    @Test
    fun `a document read back from the service is never a match`() {
        // Its bytes are counted, not decoded, so there is nothing to compare against.
        val onFile = listOf(WorkshopAttachment(guid = "g", type = ArticleSixteenDocumentTypes.first(), byteCount = 4))

        assertFalse(onFile.holdsFile(photo))
    }
}
