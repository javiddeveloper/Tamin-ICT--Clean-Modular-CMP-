package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_42
import taminx.core.core_ui.article_43
import taminx.core.core_ui.article_44

/**
 * Presentation mapping for رسیدگی به بدهی ماده ۱۶.
 *
 * Verifies kindDoc selects the correct proceeding type (ماده ۴۲ / ۴۳ / ۴۴).
 */
class WorkshopArticleSixteenUiMapperTest {

    @Test
    fun `kindDoc 1 maps to article 42`() {
        val domain = WorkshopsDebtListModelDN(
            debitNumber = "123",
            kindDoc = "1",
        )
        val pr = domain.toPresentation()
        assertEquals(Res.string.article_42, pr.proceedingType)
    }

    @Test
    fun `kindDoc 2 maps to article 43`() {
        val domain = WorkshopsDebtListModelDN(
            debitNumber = "123",
            kindDoc = "2",
        )
        val pr = domain.toPresentation()
        assertEquals(Res.string.article_43, pr.proceedingType)
    }

    @Test
    fun `kindDoc 3 maps to article 44`() {
        val domain = WorkshopsDebtListModelDN(
            debitNumber = "123",
            kindDoc = "3",
        )
        val pr = domain.toPresentation()
        assertEquals(Res.string.article_44, pr.proceedingType)
    }

    @Test
    fun `unknown or empty kindDoc maps to null`() {
        val emptyDomain = WorkshopsDebtListModelDN(debitNumber = "123", kindDoc = "")
        assertNull(emptyDomain.toPresentation().proceedingType)

        val unknownDomain = WorkshopsDebtListModelDN(debitNumber = "123", kindDoc = "9")
        assertNull(unknownDomain.toPresentation().proceedingType)
    }
}
