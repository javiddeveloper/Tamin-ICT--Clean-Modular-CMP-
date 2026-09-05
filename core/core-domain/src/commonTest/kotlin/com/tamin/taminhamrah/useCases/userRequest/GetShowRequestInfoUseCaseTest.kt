package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetShowRequestInfoUseCaseTest {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetShowRequestInfoUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeUserRequestRepository()
        useCase = GetShowRequestInfoUseCase(repository)
    }

    @Test
    fun `invoke should return type specific details from repository`() = runTest {
        val expected = UserRequestDetailsDN(
            deferredInstallment = DeferredInstallmentDetailDN(
                borrowerName = "علی محمدی",
                pensionerFirstName = "کاربر",
            )
        )
        repository.showRequestInfoResult = expected

        val result = useCase("req-1", UserRequestTypeIds.DEFERRED_INSTALLMENT)

        assertEquals(expected, result)
        assertEquals("req-1", repository.lastReferenceId)
        assertEquals(UserRequestTypeIds.DEFERRED_INSTALLMENT, repository.lastRequestTypeId)
    }

    @Test
    fun `invoke should propagate repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("Network Error")

        assertFailsWith<RuntimeException> {
            useCase("req-1", UserRequestTypeIds.ARTICLE_SIXTEEN)
        }
    }
}
