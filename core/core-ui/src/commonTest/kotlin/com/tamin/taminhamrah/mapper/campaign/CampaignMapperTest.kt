package com.tamin.taminhamrah.mapper.campaign

import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.home.CampaignDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CampaignMapperTest {

    private fun campaign(kind: CampaignKind, isOpenable: Boolean) =
        CampaignDN(flag = kind.flag, title = kind.name, bannerUrl = null, isOpenable = isOpenable)

    @Test
    fun `toCampaignKinds resolves every openable row to its CampaignKind`() {
        val rows = CampaignKind.entries.map { campaign(it, isOpenable = true) }

        val resolved = rows.toCampaignKinds()

        assertEquals(CampaignKind.entries.toList(), resolved)
    }

    @Test
    fun `toCampaignKinds drops rows whose feature is not openable`() {
        val rows = listOf(
            campaign(CampaignKind.HOUSEWIFE, isOpenable = true),
            campaign(CampaignKind.FREELANCE, isOpenable = false),
            campaign(CampaignKind.STUDENT, isOpenable = true),
        )

        val resolved = rows.toCampaignKinds()

        assertEquals(listOf(CampaignKind.HOUSEWIFE, CampaignKind.STUDENT), resolved)
    }

    @Test
    fun `toCampaignKinds is empty for an empty cache`() {
        assertTrue(emptyList<CampaignDN>().toCampaignKinds().isEmpty())
    }
}
