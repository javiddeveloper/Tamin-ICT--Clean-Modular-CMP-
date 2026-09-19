package com.tamin.taminhamrah.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.tamin.taminhamrah.ui.toPriceFormat

/**
 * Shows a typed amount the way the app prints one — through [toPriceFormat], so `1250000` reads
 * `۱٬۲۵۰٬۰۰۰` — while the field's value stays the bare digits the form validates and sends.
 *
 * The grouping itself is [toPriceFormat]'s; this only maps the cursor across the separators it
 * inserts. An empty field is left alone so its placeholder still shows, and anything other than
 * plain digits is drawn as typed rather than handed to a formatter that rejects it.
 */
object ThousandsSeparatorTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty() || !digits.all { it in '0'..'9' }) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val grouped = digits.toPriceFormat()

        // Where each typed digit landed: every digit of the formatted text is one typed digit, and
        // everything between them is a separator. The extra slot is the end of the text.
        val toTransformed = IntArray(digits.length + 1)
        var typed = 0
        grouped.forEachIndexed { index, char ->
            if (char.isDigit() && typed < digits.length) toTransformed[typed++] = index
        }
        if (typed != digits.length) return TransformedText(text, OffsetMapping.Identity)
        toTransformed[digits.length] = grouped.length

        // A formatted offset maps to how many typed digits sit before it, so a cursor dropped beside
        // a separator lands on the nearest real position.
        val toOriginal = IntArray(grouped.length + 1)
        var consumed = 0
        for (offset in 0..grouped.length) {
            while (consumed < digits.length && toTransformed[consumed] < offset) consumed++
            toOriginal[offset] = consumed
        }

        return TransformedText(
            text = AnnotatedString(grouped),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int =
                    toTransformed[offset.coerceIn(0, digits.length)]

                override fun transformedToOriginal(offset: Int): Int =
                    toOriginal[offset.coerceIn(0, grouped.length)]
            },
        )
    }
}
