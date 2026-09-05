package com.tamin.taminhamrah.mapper.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import kotlin.test.Test
import kotlin.test.assertEquals

class InquiryEducationUiMapperTest {

    @Test
    fun educationDependentsDn_toPresentation_mapsCorrectly() {
        val domain = EducationDependentsDN(
            total = 1,
            list = listOf(
                EducationDependentItemDN(
                    nationalId = "0012345678",
                    firstName = "علی",
                    lastName = "رضایی",
                    fullName = "علی رضایی",
                    relationCode = "101",
                    relationDescription = "پسر",
                    dateOfExpire = "1735689600000",
                )
            )
        )

        val presentation = domain.toPresentation()

        assertEquals(1, presentation.total)
        val item = presentation.list.first()
        assertEquals("0012345678", item.nationalId)
        assertEquals("علی", item.firstName)
        assertEquals("رضایی", item.lastName)
        assertEquals("علی رضایی", item.fullName)
        assertEquals("101", item.relationCode)
        assertEquals("پسر", item.relationDescription)
        assertEquals("1735689600000", item.dateOfExpire)
    }

    @Test
    fun educationDependentItemDn_buildsFullNameWhenMissing() {
        val presentation = EducationDependentItemDN(
            firstName = "سارا",
            lastName = "محمدی",
            fullName = null,
        ).toPresentation()

        assertEquals("سارا محمدی", presentation.fullName)
    }

    @Test
    fun certificateDn_toPresentation_mapsNullToEmpty() {
        assertEquals("", InquiryEducationCertificateDN(message = null).toPresentation().message)
        assertEquals("ok", InquiryEducationCertificateDN(message = "ok").toPresentation().message)
    }
}
