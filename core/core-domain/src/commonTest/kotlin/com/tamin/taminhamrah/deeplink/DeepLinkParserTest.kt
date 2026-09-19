package com.tamin.taminhamrah.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DeepLinkParserTest {

    @Test
    fun `shorthand key opens its feature`() {
        val link = assertIs<ParsedDeepLink.Feature>(DeepLinkParser.parse("@wedding_present", DeepLinkSource.AGENT))
        assertEquals(DeepLinkKey.WEDDING_PRESENT, link.key)
        assertEquals(emptyMap(), link.args)
    }

    @Test
    fun `app scheme, agent scheme and story scheme reach the same key`() {
        listOf(
            "mytamin://feature/contract_list",
            "agent://nav/contract_list",
            "tamin://feature/CONTRACTS",
            "@CONTRACT_LIST",
        ).forEach { raw ->
            val link = assertIs<ParsedDeepLink.Feature>(DeepLinkParser.parse(raw, DeepLinkSource.SYSTEM), raw)
            assertEquals(DeepLinkKey.CONTRACT_LIST.flag, link.key.flag, raw)
        }
    }

    @Test
    fun `aliases for screens that need a picked contract land on the contract list flag`() {
        listOf("@insurance_payment", "@cancel_contract").forEach { raw ->
            val link = assertIs<ParsedDeepLink.Feature>(DeepLinkParser.parse(raw, DeepLinkSource.AGENT))
            assertEquals(DeepLinkKey.CONTRACT_LIST.flag, link.key.flag)
        }
    }

    @Test
    fun `query arguments are percent decoded, including persian text`() {
        val link = assertIs<ParsedDeepLink.Feature>(
            DeepLinkParser.parse(
                "@prescription_detail?ARG_NATIONAL_CODE=0012345678&NOTE=%D9%86%D8%B3%D8%AE%D9%87+%D8%AC%D8%AF%DB%8C%D8%AF",
                DeepLinkSource.AGENT,
            )
        )
        assertEquals("0012345678", link.args["ARG_NATIONAL_CODE"])
        assertEquals("نسخه جدید", link.args["NOTE"])
    }

    @Test
    fun `malformed escapes are kept as text instead of failing`() {
        assertEquals("100%", DeepLinkParser.percentDecode("100%"))
        assertEquals("%zz", DeepLinkParser.percentDecode("%zz"))
    }

    @Test
    fun `native era links that name the screen as the host still resolve, with their arguments`() {
        val link = assertIs<ParsedDeepLink.Feature>(
            DeepLinkParser.parse("mytamin://prescription_detail?ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION=12", DeepLinkSource.SYSTEM)
        )
        assertEquals(DeepLinkKey.PRESCRIPTION_DETAIL, link.key)
        assertEquals("12", link.args["ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION"])
        assertEquals(
            DeepLinkKey.WORKERS_PAYMENT_INFO,
            assertIs<ParsedDeepLink.Feature>(DeepLinkParser.parse("mytamin://workers_payment_info", DeepLinkSource.SYSTEM)).key,
        )
    }

    @Test
    fun `unknown keys, unknown schemes and blanks are invalid`() {
        listOf(
            "@group_payment",
            "mytamin://feature/",
            "mytamin://login?code=1",
            "ftp://tamin.ir/file",
            "just text",
            "",
            null,
        ).forEach { raw ->
            assertEquals(ParsedDeepLink.Invalid, DeepLinkParser.parse(raw, DeepLinkSource.SYSTEM), raw.toString())
        }
    }

    @Test
    fun `prompt links carry their text and need some`() {
        val prompt = assertIs<ParsedDeepLink.Prompt>(
            DeepLinkParser.parse("agent://prompt?text=%D8%B3%D9%84%D8%A7%D9%85", DeepLinkSource.AGENT)
        )
        assertEquals("سلام", prompt.text)
        assertEquals(ParsedDeepLink.Invalid, DeepLinkParser.parse("agent://prompt?text=", DeepLinkSource.AGENT))
    }

    @Test
    fun `assistant web links are limited to tamin ir over https`() {
        assertIs<ParsedDeepLink.Web>(DeepLinkParser.parse("https://tamin.ir/news", DeepLinkSource.AGENT))
        assertIs<ParsedDeepLink.Web>(DeepLinkParser.parse("https://es.tamin.ir/x", DeepLinkSource.AGENT))
        assertEquals(ParsedDeepLink.Invalid, DeepLinkParser.parse("https://evil.com/tamin.ir", DeepLinkSource.AGENT))
        assertEquals(ParsedDeepLink.Invalid, DeepLinkParser.parse("https://nottamin.ir", DeepLinkSource.AGENT))
        assertEquals(ParsedDeepLink.Invalid, DeepLinkParser.parse("http://tamin.ir", DeepLinkSource.AGENT))
    }

    @Test
    fun `curated app content may link to any web page`() {
        assertIs<ParsedDeepLink.Web>(DeepLinkParser.parse("https://example.com", DeepLinkSource.APP_CONTENT))
    }
}
