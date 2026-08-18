package com.tamin.taminhamrah.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Each case here is a spelling the records actually arrive in, paired with a spelling someone
 * plausibly types. Before folding, none of these matched.
 */
class PersianTextSearchTest {

    @Test
    fun testArabicAndPersianLettersFoldTogether() {
        // Arabic ي and ك against the Persian ی and ک a phone keyboard produces.
        assertMatch("دکتر علي اكبر رضايي", "علی اکبر")
        assertMatch("علی اکبر", "علي اكبر")
        // Alef with hamza, teh marbuta, and the waw with hamza.
        assertMatch("دکتر أحمد", "احمد")
        assertMatch("فاطمة", "فاطمه")
        assertMatch("مؤمنی", "مومنی")
    }

    /** All four ways a compound name gets written must find all four others. */
    @Test
    fun testWordBreakVariantsFoldTogether() {
        val spellings = listOf(
            "دکتر علی اکبر رضایی",   // open, plain space
            "دکتر علی‌اکبر رضایی",   // نیم‌فاصله
            "دکتر علیاکبر رضایی",    // closed
            "دکتر علی  اکبر رضایی",  // doubled space
        )
        for (record in spellings) {
            for (typed in spellings) {
                assertMatch(record, typed.removePrefix("دکتر ").removeSuffix(" رضایی"))
            }
        }
    }

    @Test
    fun testStraySpacingIsIgnored() {
        assertMatch("دکتر   علی    احمدی", "علی احمدی")
        assertMatch("دکتر علی احمدی", "  علی   احمدی  ")
    }

    @Test
    fun testWordOrderDoesNotMatter() {
        // Some systems record surname first; the typed order must not decide the result.
        assertMatch("دکتر علی احمدی", "احمدی علی")
        assertMatch("احمدی، علی", "علی احمدی")
    }

    @Test
    fun testTatweelAndDiacriticsAreIgnored() {
        assertMatch("دکتــر علی", "دکتر علی")
        assertMatch("عَلیّ اکبَر", "علی اکبر")
    }

    @Test
    fun testDigitsFoldToAscii() {
        assertMatch("درمانگاه ۱۲ فروردین", "12 فروردین")
        assertMatch("درمانگاه ٥ آذر", "5 اذر")
    }

    @Test
    fun testNonMatchesStillDoNotMatch() {
        // Folding must not make everything match everything.
        assertFalse("دکتر علی احمدی".containsFoldedWords("رضایی".toFoldedWords()))
        assertFalse("دکتر علی احمدی".containsFoldedWords("علی رضایی".toFoldedWords()))
    }

    @Test
    fun testEmptyQueryMatchesEverything() {
        assertTrue("دکتر علی".containsFoldedWords("".toFoldedWords()))
        assertTrue("دکتر علی".containsFoldedWords("   ".toFoldedWords()))
    }

    @Test
    fun testFoldingProducesACanonicalForm() {
        assertEquals("علی اکبر", "علي   اكبر".foldForSearch())
        assertEquals("علی اکبر", "  علي‌اكبر  ".foldForSearch())
        assertEquals("علی اکبر", "عَلـيّ اَكبَر".foldForSearch())
        assertEquals("abc", "  ABC  ".foldForSearch())
        assertEquals("", "   ".foldForSearch())
    }

    private fun assertMatch(haystack: String, query: String) {
        assertTrue(
            haystack.containsFoldedWords(query.toFoldedWords()),
            "«$query» should have matched «$haystack» (folded: «${haystack.foldForSearch()}»)",
        )
    }
}
