package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentTargetTest {

    private fun record(thumb: String, name: String = "کارت ملی") = ElectronicFilePR(
        id = "1",
        name = name,
        categoryName = "هویتی",
        thumb = thumb,
        type = "",
    )

    @Test
    fun `a pdf thumb becomes a full-pdf download`() {
        val target = record("https://host/erecords/thumbs/1.pdf").documentTarget()

        assertEquals(
            DocumentTarget.Pdf(url = "https://host/erecords/full-pdf/1.pdf", fileName = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `a tif thumb becomes a full-pdf download`() {
        val target = record("https://host/erecords/thumbs/1.tif").documentTarget()

        assertEquals(
            DocumentTarget.Pdf(url = "https://host/erecords/full-pdf/1.tif", fileName = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `any other thumb becomes a full image`() {
        val target = record("https://host/erecords/thumbs/1.jpg").documentTarget()

        assertEquals(
            DocumentTarget.Image(url = "https://host/erecords/full/1.jpg", title = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `only the thumbs segment is replaced and the rest of the url survives`() {
        val target = record("https://host/a/thumbs/b?token=x&size=thumbs2").documentTarget()

        assertEquals(
            DocumentTarget.Image(url = "https://host/a/full/b?token=x&size=full2", title = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `a blank thumb has nothing to open`() {
        assertNull(record("").documentTarget())
        assertNull(record("   ").documentTarget())
    }
}
