package com.tamin.taminhamrah.ui.components

import androidx.compose.ui.text.AnnotatedString
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The grouping an amount field draws, and the cursor mapping under it.
 *
 * A wrong offset neither fails to compile nor to render: it throws the moment the user taps between
 * two digits, or quietly types into the wrong place. Both directions are pinned for every length.
 */
class ThousandsSeparatorTransformationTest {

    private fun transform(digits: String) =
        ThousandsSeparatorTransformation.filter(AnnotatedString(digits))

    /** What is drawn is exactly what the app prints for the same amount. */
    @Test
    fun drawsTheAppsOwnPriceFormat() {
        listOf("7", "999", "1000", "12345", "1250000", "0012").forEach { digits ->
            assertEquals(digits.toPriceFormat(), transform(digits).text.text, "digits $digits")
        }
    }

    /** An empty field keeps its placeholder rather than drawing «۰». */
    @Test
    fun anEmptyFieldIsLeftAlone() {
        assertEquals("", transform("").text.text)
    }

    @Test
    fun theCursorStepsOverTheSeparator() {
        val mapping = transform("1234").offsetMapping // ۱٬۲۳۴

        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(2, mapping.originalToTransformed(1))
        assertEquals(5, mapping.originalToTransformed(4))
        // Either side of the separator is the same typed position.
        assertEquals(1, mapping.transformedToOriginal(1))
        assertEquals(1, mapping.transformedToOriginal(2))
        assertEquals(4, mapping.transformedToOriginal(5))
    }

    @Test
    fun everyOffsetRoundTripsAndStaysInBounds() {
        for (length in 0..13) {
            val digits = "9".repeat(length)
            val transformed = transform(digits)
            val groupedLength = transformed.text.length
            for (offset in 0..length) {
                val there = transformed.offsetMapping.originalToTransformed(offset)
                assertEquals(true, there in 0..groupedLength, "length $length offset $offset")
                assertEquals(offset, transformed.offsetMapping.transformedToOriginal(there))
            }
            for (offset in 0..groupedLength) {
                val back = transformed.offsetMapping.transformedToOriginal(offset)
                assertEquals(true, back in 0..length, "length $length grouped offset $offset")
            }
        }
    }
}
