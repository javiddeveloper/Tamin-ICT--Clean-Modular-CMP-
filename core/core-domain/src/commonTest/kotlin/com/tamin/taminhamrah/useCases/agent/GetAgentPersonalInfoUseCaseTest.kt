package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.repository.FakeTokenStoreManager
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetAgentPersonalInfoUseCaseTest {

    private lateinit var users: FakeUserRepository
    private lateinit var pensions: FakePensionRepository
    private lateinit var tokens: FakeTokenStoreManager
    private lateinit var useCase: GetAgentPersonalInfoUseCaseImpl

    @BeforeTest
    fun setUp() {
        users = FakeUserRepository()
        pensions = FakePensionRepository()
        tokens = FakeTokenStoreManager().apply {
            saveToken("user-token")
            saveUserType("INSURED")
        }
        useCase = GetAgentPersonalInfoUseCaseImpl(users, pensions, tokens)
    }

    private fun identity(nationalId: String?, firstName: String? = "علی", lastName: String? = "رضایی") = IdentityInfoDN(
        cityOfBirthId = null, cityOfIssueId = null, countryId = null, dateOfBirth = null, fatherName = null,
        firstName = firstName, gender = null, id = null, idCardNumber = null, idCardSerial1 = null,
        idCardSerial2 = null, lastName = lastName, nationalId = nationalId, ssn = null,
    )

    @Test
    fun `an insured user is sent with name and national id but no pensioner id`() = runTest {
        users.identityInfoResult = identity("0012345678")
        pensions.pensionIdResult = listOf(PensionIdDN("999"))

        assertEquals(AgentPersonalInfoDN("0012345678", null, "علی", "رضایی"), useCase())
    }

    @Test
    fun `a pensioner also gets the pensioner id`() = runTest {
        tokens.saveUserType("PENSIONER")
        users.identityInfoResult = identity("0012345678")
        pensions.pensionIdResult = listOf(PensionIdDN("555"))

        assertEquals("555", useCase()?.pensionerId)
    }

    @Test
    fun `no personal info without a login or a real national id`() = runTest {
        users.identityInfoResult = identity("0")
        assertNull(useCase())

        users.identityInfoResult = identity("0012345678")
        tokens.saveToken(null)
        assertNull(useCase())
    }

    @Test
    fun `blank names are sent as absent`() = runTest {
        users.identityInfoResult = identity("0012345678", firstName = " ", lastName = "")
        val info = useCase()
        assertNull(info?.firstName)
        assertNull(info?.lastName)
    }

    @Test
    fun `a failing pensioner lookup still sends the rest`() = runTest {
        tokens.saveUserType("PENSIONER")
        users.identityInfoResult = identity("0012345678")
        pensions.shouldThrowError = true
        pensions.error = RuntimeException("offline")

        assertEquals(AgentPersonalInfoDN("0012345678", null, "علی", "رضایی"), useCase())
    }

    @Test
    fun `the pensioner id is looked up again when another user signs in`() = runTest {
        tokens.saveUserType("PENSIONER")
        users.identityInfoResult = identity("1111111111")
        pensions.pensionIdResult = listOf(PensionIdDN("first"))
        assertEquals("first", useCase()?.pensionerId)

        pensions.pensionIdResult = listOf(PensionIdDN("changed"))
        assertEquals("first", useCase()?.pensionerId)

        users.identityInfoResult = identity("2222222222")
        assertEquals("changed", useCase()?.pensionerId)
    }
}
