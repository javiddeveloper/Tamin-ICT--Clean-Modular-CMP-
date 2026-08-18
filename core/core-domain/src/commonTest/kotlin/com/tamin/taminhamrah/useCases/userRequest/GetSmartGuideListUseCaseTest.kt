package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSmartGuideListUseCaseTest {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetSmartGuideListUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeUserRequestRepository()
        useCase = GetSmartGuideListUseCase(repository)
    }

    @Test
    fun `invoke should return smart guide list from repository`() = runTest {
        val expected = listOf(
            SmartGuideDN(
                id = 201L,
                question = "سوال نمونه",
                reply = "پاسخ نمونه",
                requestCode = "0018",
                requestDesc = "توضیح کد",
                isPublic = true,
                title = "عنوان راهنما",
                description = "توضیحات تکمیلی"
            )
        )
        repository.smartGuideResult = expected
        val searchParams = SmartGuideSearchParams(requestType = 3, requestStatus = "0018", isPublic = true)

        val result = useCase(searchParams)

        assertEquals(expected, result)
        assertEquals(searchParams, repository.lastSmartGuideParams)
    }

    @Test
    fun `invoke should propagate repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("Network Error")

        assertFailsWith<RuntimeException> {
            useCase(SmartGuideSearchParams())
        }
    }
}
