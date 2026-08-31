package com.tamin.taminhamrah.dataSource.inquiryEducation

import com.tamin.taminhamrah.apiService.inquiryEducation.InquiryEducationApiService
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentPersonalDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentRelationDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.getTaminErrorUri
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FakeInquiryEducationApiService : InquiryEducationApiService {
    var getDataForEducationResult: BaseDTO<EducationDependentsListDTO> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = EducationDependentsListDTO())
    var inquiryCertificateResult: BaseDTO<String?> =
        BaseDTO(status = 200, family = "OK", reason = "OK", data = null)
    var shouldThrowException: Exception? = null
    var lastCertificateCode: String? = null
    var lastEducationCode: String? = null

    override suspend fun getDataForEducation(): BaseDTO<EducationDependentsListDTO> {
        shouldThrowException?.let { throw it }
        return getDataForEducationResult
    }

    override suspend fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): BaseDTO<String?> {
        shouldThrowException?.let { throw it }
        lastCertificateCode = code
        lastEducationCode = educationCode
        return inquiryCertificateResult
    }
}

class InquiryEducationRemoteDataSourceImplTest {

    private lateinit var fakeApiService: FakeInquiryEducationApiService
    private lateinit var dataSource: InquiryEducationRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeInquiryEducationApiService()
        dataSource = InquiryEducationRemoteDataSourceImpl(
            inquiryEducationApiService = fakeApiService,
            errorParser = ErrorParserImpl()
        )
    }

    @Test
    fun getDataForEducation_success_returnsList() = runTest {
        val expected = EducationDependentsListDTO(
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
        fakeApiService.getDataForEducationResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = expected)

        val result = dataSource.getDataForEducation()

        assertEquals(expected, result)
    }

    @Test
    fun inquiryEducationCertificate_success_returnsMessage() = runTest {
        fakeApiService.inquiryCertificateResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = "گواهی معتبر است")

        val result = dataSource.inquiryEducationCertificate(
            code = "0012345678",
            educationCode = "EDU123"
        )

        assertEquals("گواهی معتبر است", result)
        assertEquals("0012345678", fakeApiService.lastCertificateCode)
        assertEquals("EDU123", fakeApiService.lastEducationCode)
    }

    @Test
    fun inquiryEducationCertificate_nullData_returnsNull() = runTest {
        fakeApiService.inquiryCertificateResult =
            BaseDTO(status = 200, family = "OK", reason = "OK", data = null)

        val result = dataSource.inquiryEducationCertificate(code = "1", educationCode = "EDU")

        assertNull(result)
    }

    @Test
    fun getDataForEducation_errorStatus_throws() = runTest {
        fakeApiService.getDataForEducationResult =
            BaseDTO(status = 400, family = "CLIENT_ERROR", reason = "Bad Request", data = null)

        assertFailsWith<TaminApiException> {
            dataSource.getDataForEducation()
        }
    }

    @Test
    fun getDataForEducation_networkException_throwsNoConnection() = runTest {
        fakeApiService.shouldThrowException = RuntimeException("network")

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getDataForEducation()
        }
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, exception.getTaminErrorUri())
    }
}
