package com.tamin.taminhamrah.useCases.inquiryEducation

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FakeInquiryEducationRepository : InquiryEducationRepository {
    var dependentsResult: EducationDependentsDN = EducationDependentsDN()
    var certificateResult: InquiryEducationCertificateDN = InquiryEducationCertificateDN()
    var lastCode: String? = null
    var lastEducationCode: String? = null

    override fun getDataForEducation(): Flow<EducationDependentsDN> = flowOf(dependentsResult)

    override fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): Flow<InquiryEducationCertificateDN> {
        lastCode = code
        lastEducationCode = educationCode
        return flowOf(certificateResult)
    }
}

class InquiryEducationUseCasesTest {

    @Test
    fun getDataForEducationUseCase_returnsDependentsFromRepository() = runTest {
        val repository = FakeInquiryEducationRepository().apply {
            dependentsResult = EducationDependentsDN(
                total = 1,
                list = listOf(
                    EducationDependentItemDN(
                        nationalId = "0012345678",
                        fullName = "علی رضایی",
                    )
                )
            )
        }
        val useCase = GetDataForEducationUseCase(repository)

        useCase().test {
            val item = awaitItem()
            assertEquals(1, item.total)
            assertEquals("0012345678", item.list.first().nationalId)
            awaitComplete()
        }
    }

    @Test
    fun inquiryEducationCertificateUseCase_returnsCertificateFromRepository() = runTest {
        val repository = FakeInquiryEducationRepository().apply {
            certificateResult = InquiryEducationCertificateDN(message = "گواهی معتبر است")
        }
        val useCase = InquiryEducationCertificateUseCase(repository)

        useCase(code = "0012345678", educationCode = "EDU123").test {
            assertEquals("گواهی معتبر است", awaitItem().message)
            awaitComplete()
        }

        assertEquals("0012345678", repository.lastCode)
        assertEquals("EDU123", repository.lastEducationCode)
    }

    @Test
    fun inquiryEducationCertificateUseCase_preservesNullMessage() = runTest {
        val repository = FakeInquiryEducationRepository().apply {
            certificateResult = InquiryEducationCertificateDN(message = null)
        }
        val useCase = InquiryEducationCertificateUseCase(repository)

        useCase(code = "1", educationCode = "X").test {
            assertNull(awaitItem().message)
            awaitComplete()
        }
    }
}
