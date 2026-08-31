package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentFamilyRelationDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentPersonalDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentRelationDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentRelationDetailDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentSubDominantDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InquiryEducationMapperTest {

    @Test
    fun educationDependentsListDto_toDomain_mapsCorrectly() {
        val dto = EducationDependentsListDTO(
            total = 1,
            list = listOf(
                EducationDependentItemDTO(
                    relationWithTamin = EducationDependentRelationDTO(
                        personal = EducationDependentPersonalDTO(
                            firstName = "علی",
                            lastName = "رضایی",
                            nationalId = "0012345678",
                            subDominant = EducationDependentSubDominantDTO(
                                dateOfExpire = "1735689600000"
                            ),
                        ),
                        relationWithTamin = EducationDependentFamilyRelationDTO(
                            baseTendency = EducationDependentRelationDetailDTO(
                                tendencyCode = "101",
                                tendencyDescription = "پسر",
                            )
                        ),
                    )
                )
            )
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.total)
        assertEquals(1, domain.list.size)
        val item = domain.list.first()
        assertEquals("0012345678", item.nationalId)
        assertEquals("علی", item.firstName)
        assertEquals("رضایی", item.lastName)
        assertEquals("علی رضایی", item.fullName)
        assertEquals("101", item.relationCode)
        assertEquals("پسر", item.relationDescription)
        assertEquals("1735689600000", item.dateOfExpire)
    }

    @Test
    fun educationDependentsListDto_nullList_mapsToEmpty() {
        val domain = EducationDependentsListDTO(list = null, total = null).toDomain()

        assertEquals(0, domain.total)
        assertEquals(emptyList(), domain.list)
    }

    @Test
    fun string_toInquiryEducationCertificateDN_preservesNull() {
        assertNull(null.toInquiryEducationCertificateDN().message)
        assertEquals("ok", "ok".toInquiryEducationCertificateDN().message)
    }
}
