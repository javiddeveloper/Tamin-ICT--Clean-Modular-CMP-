package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.repository.history.FakeHistoryRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GetUserInfosUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHistoryRepository
    private lateinit var useCase: GetUserInfosUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHistoryRepository()
        useCase = GetUserInfosUseCase(repository)
    }

    @Test
    fun `invoke should return user info from repository`() = runTest {
        repository.userInfoResult = UserInfoDN(
            firstName = "عادل",
            lastName = "حسين پناهي",
            socialSecurityNumber = "2062144681",
            insuranceNumber = "0082984639",
            nationalID = "5589743451",
            birthDate = "1361/10/01",
            id = "2782294052",
            serial1 = null, militaryServiceCode = null, fatherName = null,
            serial2 = null, creationTime = null, lastModificationTime = null,
            cityCode = null, lastModifiedBy = null, issueplaceName = null,
            genderCode = null, marriageCode = null, createdBy = null,
            identityNumber = null, countryCode = null, birthDateTimestamp = null,
            issueplace = null, nationCode = null
        )

        val result = useCase()

        assertEquals("عادل", result.firstName)
        assertEquals("حسين پناهي", result.lastName)
        assertEquals("2062144681", result.socialSecurityNumber)
        assertEquals("0082984639", result.insuranceNumber)
        assertEquals("5589743451", result.nationalID)
        assertEquals("1361/10/01", result.birthDate)
        assertEquals("2782294052", result.id)
    }

    @Test
    fun `invoke should preserve nullable fields`() = runTest {
        repository.userInfoResult = UserInfoDN(
            firstName = "عادل",
            lastName = "حسين پناهي",
            socialSecurityNumber = null,
            insuranceNumber = null,
            militaryServiceCode = null,
            marriageCode = null,
            nationalID = null, birthDate = null, id = null,
            serial1 = null, fatherName = null, serial2 = null,
            creationTime = null, lastModificationTime = null, cityCode = null,
            lastModifiedBy = null, issueplaceName = null, genderCode = null,
            createdBy = null, identityNumber = null, countryCode = null,
            birthDateTimestamp = null, issueplace = null, nationCode = null
        )

        val result = useCase()

        assertNull(result.socialSecurityNumber)
        assertNull(result.insuranceNumber)
        assertNull(result.militaryServiceCode)
        assertNull(result.marriageCode)
    }

    @Test
    fun `invoke should propagate exception from repository`() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase()
        }
    }
}
