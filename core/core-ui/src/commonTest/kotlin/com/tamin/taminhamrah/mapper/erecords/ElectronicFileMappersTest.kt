package com.tamin.taminhamrah.mapper.erecords

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import kotlin.test.Test
import kotlin.test.assertEquals

class ElectronicFileMappersTest {

    @Test
    fun `null fields become empty strings so the card never renders null`() {
        val result = ElectronicFileDN().toPresentation()

        assertEquals("", result.id)
        assertEquals("", result.name)
        assertEquals("", result.categoryName)
        assertEquals("", result.thumb)
        assertEquals("", result.contentServer)
    }

    @Test
    fun `populated fields carry through unchanged`() {
        val dn = ElectronicFileDN(
            id = "42",
            name = "کارت ملی",
            categoryName = "هویتی",
            thumb = "https://host/erecords/thumbs/42.jpg",
            contentServer = "https://host/erecords/full/42.jpg",
            type = "image",
        )

        val result = dn.toPresentation()

        assertEquals("42", result.id)
        assertEquals("کارت ملی", result.name)
        assertEquals("هویتی", result.categoryName)
        assertEquals("https://host/erecords/thumbs/42.jpg", result.thumb)
        assertEquals("https://host/erecords/full/42.jpg", result.contentServer)
        assertEquals("image", result.type)
    }
}
