package com.tamin.taminhamrah.data.repository.inquiryEducation

import com.tamin.taminhamrah.dataSource.inquiryEducation.InquiryEducationRemoteDataSource
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentPersonalDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentRelationDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FakeInquiryEducationRemoteDataSource : InquiryEducationRemoteDataSource {
    var getDataForEducationResult: EducationDependentsListDTO? = EducationDependentsListDTO()
    var certificateResult: String? = null
    var lastCode: String? = null
    var lastEducationCode: String? = null

    override suspend fun getDataForEducation(): EducationDependentsListDTO? = getDataForEducationResult

    override suspend fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): String? {
        lastCode = code
        lastEducationCode = educationCode
        return certificateResult
    }
}

class InquiryEducationRepositoryImplTest {

    @Test
    fun getDataForEducation_mapsDtoToDomain() = runTest {
        val fake = FakeInquiryEducationRemoteDataSource().apply {
            getDataForEducationResult = EducationDependentsListDTO(
                total = 1,
                list = listOf(
                    EducationDependentItemDTO(
                        relationWithTamin = EducationDependentRelationDTO(
                            personal = EducationDependentPersonalDTO(
                                firstName = "علی",
                                lastName = "رضایی",
                                nationalId = "0012345678",
                            )
                        )
                    )
                )
            )
        }
        val repository = InquiryEducationRepositoryImpl(fake)

        val result = repository.getDataForEducation().first()

        assertEquals(1, result.total)
        assertEquals("0012345678", result.list.first().nationalId)
        assertEquals("علی رضایی", result.list.first().fullName)
    }

    @Test
    fun getDataForEducation_nullResponse_emitsEmpty() = runTest {
        val fake = FakeInquiryEducationRemoteDataSource().apply {
            getDataForEducationResult = null
        }
        val repository = InquiryEducationRepositoryImpl(fake)

        val result = repository.getDataForEducation().first()

        assertEquals(0, result.total)
        assertEquals(emptyList(), result.list)
    }

    @Test
    fun inquiryEducationCertificate_mapsNullableMessage() = runTest {
        val fake = FakeInquiryEducationRemoteDataSource().apply {
            certificateResult = null
        }
        val repository = InquiryEducationRepositoryImpl(fake)

        val result = repository.inquiryEducationCertificate("1", "EDU").first()

        assertNull(result.message)
        assertEquals("1", fake.lastCode)
        assertEquals("EDU", fake.lastEducationCode)
    }

    @Test
    fun inquiryEducationCertificate_mapsMessage() = runTest {
        val fake = FakeInquiryEducationRemoteDataSource().apply {
            certificateResult = "گواهی معتبر است"
        }
        val repository = InquiryEducationRepositoryImpl(fake)

        val result = repository.inquiryEducationCertificate("001", "CODE").first()

        assertEquals("گواهی معتبر است", result.message)
    }
}
