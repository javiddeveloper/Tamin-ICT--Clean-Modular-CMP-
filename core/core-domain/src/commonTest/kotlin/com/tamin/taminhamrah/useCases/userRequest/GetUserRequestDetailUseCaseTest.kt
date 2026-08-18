package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserRequestDetailUseCaseTest {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetUserRequestDetailUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeUserRequestRepository()
        useCase = GetUserRequestDetailUseCase(repository)
    }

    @Test
    fun `invoke should return request detail from repository`() = runTest {
        val expected = sampleRequest()
        repository.userRequestDetailResult = expected

        val result = useCase(491371155L)

        assertEquals(expected, result)
        assertEquals(491371155L, repository.lastRequestId)
    }

    @Test
    fun `invoke should propagate repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("Network Error")

        assertFailsWith<RuntimeException> {
            useCase(1L)
        }
    }

    private fun sampleRequest() = UserRequestDN(
        id = 491371155L,
        refCode = "1075558440",
        title = "گواهی کسر اقساط معوق",
        comment = null,
        creationTime = 1785215695428L,
        createByName = "سیدرحمت اله میرفضلی",
        status = UserRequestStatusDN(requestCode = "0018", requestDesc = "تایید نهایی"),
        requestType = UserRequestTypeDN(id = 22L, title = "گواهی کسر اقساط معوق", description = null),
        referenceId = "491371155",
        requestDetails = null,
    )
}
