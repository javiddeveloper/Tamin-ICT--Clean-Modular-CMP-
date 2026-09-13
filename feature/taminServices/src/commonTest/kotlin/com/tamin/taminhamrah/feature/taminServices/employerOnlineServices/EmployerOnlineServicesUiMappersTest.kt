package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices

import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toAgreementDocumentPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toIdentityCardPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toRowPR
import com.tamin.taminhamrah.model.content.LegalDocumentDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Domain → presentation for the Employer Online Services landing screen.
 *
 * The landing card, the identity block and the personalised تعهدنامه intro are all built here once,
 * at the mapping edge — Persian digits, separated Jalali dates, dashed blanks, `{name}` /
 * `{nationalCode}` substituted — so no composable formats per recomposition. These pin that.
 */
class EmployerOnlineServicesUiMappersTest {

    @Test
    fun `agreement row formats every label and keeps identity raw for the drill-down`() {
        val pr = EmployerAgreementDN(
            startDate = "14030101",
            commitmentDate = "14030102",
            email = "boss@example.com",
            mobile = "09120000000",
            workshop = WorkshopSummaryDN(
                workshopId = "1071410004",
                branchCode = "123",
                name = "کارگاه الف",
                address = "تهران",
                branchTitle = "شعبهٔ ۲ مشهد",
                branchOfficeCode = "1202",
                statusCode = "01",
                statusDescription = "فعال",
            ),
        ).toRowPR()

        // Raw — travels to the "ردیف‌های پیمان" screen and its query parameters.
        assertEquals("1071410004", pr.workshopId)
        assertEquals("123", pr.branchCode)
        assertEquals(true, pr.hasIdentity)
        assertEquals(true, pr.isActive)
        assertEquals("فعال", pr.statusLabel)
        // Display.
        assertEquals("کارگاه الف", pr.workshopName)
        assertEquals("۱۰۷۱۴۱۰۰۰۴", pr.workshopCodeLabel)
        assertEquals("شعبهٔ ۲ مشهد · ۱۲۰۲", pr.branchLabel)
        assertEquals("۱۴۰۳/۰۱/۰۲", pr.commitmentDate)
        assertEquals("۱۴۰۳/۰۱/۰۱", pr.startDate)
        assertEquals("۰۹۱۲۰۰۰۰۰۰۰", pr.mobile)
        assertEquals("boss@example.com", pr.email)
    }

    @Test
    fun `agreement row dashes what the service omitted and reads a non-active status`() {
        val pr = EmployerAgreementDN(
            workshop = WorkshopSummaryDN(workshopId = "1", branchCode = "2", statusCode = "03"),
        ).toRowPR()

        assertEquals(false, pr.isActive)
        assertEquals("-", pr.workshopName)
        assertEquals("-", pr.branchLabel)
        assertEquals("-", pr.commitmentDate)
        assertEquals("-", pr.address)
        assertEquals("-", pr.email)
    }

    @Test
    fun `identity card joins the name and Persian-digits the national code`() {
        val pr = UserProfileDN(
            entityId = null,
            login = null,
            firstName = "رضا",
            lastName = "کارفرما",
            email = "boss@example.com",
            nationalCode = "0012345678",
            mobile = "09120000000",
        ).toIdentityCardPR()

        assertEquals("رضا کارفرما", pr.fullName)
        assertEquals("۰۰۱۲۳۴۵۶۷۸", pr.nationalCode)
    }

    @Test
    fun `identity card dashes a blank name and a missing national code`() {
        val pr = UserProfileDN(
            entityId = null,
            login = null,
            firstName = " ",
            lastName = null,
            email = null,
            nationalCode = null,
            mobile = null,
        ).toIdentityCardPR()

        assertEquals("-", pr.fullName)
        assertEquals("-", pr.nationalCode)
    }

    @Test
    fun `agreement document bakes the signed-in identity into the intro placeholders`() {
        val pr = LegalDocumentDN(
            intro = "اینجانب {name} با کد ملی {nationalCode} متعهد می‌شوم",
            clauses = listOf("بند اول", "بند دوم"),
            acknowledgement = "می‌پذیرم",
        ).toAgreementDocumentPR(name = "رضا کارفرما", nationalCode = "۰۰۱۲۳۴۵۶۷۸")

        assertTrue(pr.intro.contains("رضا کارفرما"))
        assertTrue(pr.intro.contains("۰۰۱۲۳۴۵۶۷۸"))
        assertTrue(!pr.intro.contains("{name}") && !pr.intro.contains("{nationalCode}"))
        assertEquals(listOf("بند اول", "بند دوم"), pr.clauses)
        assertEquals("می‌پذیرم", pr.acknowledgement)
    }
}
