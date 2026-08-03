package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentTargetTest {

    private fun record(thumb: String, contentServer: String = "", name: String = "کارت ملی") = ElectronicFilePR(
        id = "1",
        name = name,
        categoryName = "هویتی",
        thumb = thumb,
        contentServer = contentServer,
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
        val target = record("https://host/a/thumbs/b.jpg?token=x&size=thumbs2").documentTarget()

        assertEquals(
            DocumentTarget.Image(url = "https://host/a/full/b.jpg?token=x&size=full2", title = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `a blank thumb has nothing to open`() {
        assertNull(record("").documentTarget())
        assertNull(record("   ").documentTarget())
    }

    @Test
    fun `a pdf mentioned in the query string does not make an image a pdf`() {
        val target = record("https://host/erecords/thumbs/1.jpg?ref=archive.pdf").documentTarget()

        assertEquals(
            DocumentTarget.Image(
                url = "https://host/erecords/full/1.jpg?ref=archive.pdf",
                title = "کارت ملی",
            ),
            target,
        )
    }

    @Test
    fun `the extension is matched regardless of case`() {
        val target = record("https://host/erecords/thumbs/1.PDF").documentTarget()

        assertEquals(
            DocumentTarget.Pdf(url = "https://host/erecords/full-pdf/1.PDF", fileName = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `a tiff is served as pdf just as a tif is`() {
        val target = record("https://host/erecords/thumbs/1.tiff").documentTarget()

        assertEquals(
            DocumentTarget.Pdf(url = "https://host/erecords/full-pdf/1.tiff", fileName = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `an extensionless thumb falls back to an image, as the previous app did`() {
        // Image is the safe default: only .pdf/.tif/.tiff are served as PDF, and sending anything
        // else to the PDF downloader produces a document that will not open.
        val target = record("https://host/erecords/thumbs/10293847").documentTarget()

        assertEquals(
            DocumentTarget.Image(url = "https://host/erecords/full/10293847", title = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `a url with no thumbs segment is opened as it came`() {
        val target = record("https://host/erecords/direct/10293847.jpg").documentTarget()

        assertEquals(
            DocumentTarget.Image(url = "https://host/erecords/direct/10293847.jpg", title = "کارت ملی"),
            target,
        )
    }

    @Test
    fun `real Tamin API thumb with id parameter ending in tif becomes full-pdf download`() {
        val target = record("https://eservices.tamin.ir/api/erecords/thumbs?id=0017312213669900959.tif&parent=0020939111&conf=0&cs=2", name = "شناسنامه").documentTarget()

        assertEquals(
            DocumentTarget.Pdf(
                url = "https://eservices.tamin.ir/api/erecords/full-pdf?id=0017312213669900959.tif&parent=0020939111&conf=0&cs=2",
                fileName = "شناسنامه",
            ),
            target,
        )
    }

    @Test
    fun `real Tamin API thumb with id parameter ending in jpg becomes full image`() {
        val target = record("https://eservices.tamin.ir/api/erecords/thumbs?id=0017312213744268155.jpg&parent=0020939111&conf=0&cs=2", name = "عکس ثبت احوال").documentTarget()

        assertEquals(
            DocumentTarget.Image(
                url = "https://eservices.tamin.ir/api/erecords/full?id=0017312213744268155.jpg&parent=0020939111&conf=0&cs=2",
                title = "عکس ثبت احوال",
            ),
            target,
        )
    }
}
