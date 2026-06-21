package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.repository.workshops.FakeWorkShopsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetAllEmployerAgreementByNationalIdUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeWorkShopsRepository
    private lateinit var useCase: GetAllEmployerAgreementByNationalIdUseCase

    @BeforeTest
    fun setup() {
        repository = FakeWorkShopsRepository()
        useCase = GetAllEmployerAgreementByNationalIdUseCase(repository)
    }

    @Test
    fun `invoke should return employer agreement list from repository successfully`() = runTest {
        val expectedList = EmployerAgreementListDN(
            list = listOf(
                EmployerAgreementDN(
                    workshop = EmployerWorkshopDN(workshopName = "Workshop 1", workshopId = "111", branchCode = "01", employerName = "John Doe", actitvityCode = null, activityName = null, branchTitle = null, brhCode = null, inclusionDate = null, sswn = null, userId = null, workshopApproveDate = null, workshopRegisterDate = null, workshopUnemployedStat = null),
                    nationalcode = "1234567890",
                    createdt = null, createuid = null, dname = null, emailaddr = null, enddate = null, firstname = null, lastname = null, letDate = null, letNo = null, logicalDeleted = null, mastcusttype = null, masttyp = null, mobileno = null, nationalno = null, pymseq = null, regdate = null, regemailseq = null, regno = null, risuid = null, roletype = null, special = null, startdate = null
                )
            ),
            total = 1
        )
        repository.result = expectedList

        val result = useCase.invoke(ApiQueryParamDN())

        assertEquals(1, result?.list?.size)
        assertEquals("Workshop 1", result?.list?.get(0)?.workshop?.workshopName)
        assertEquals("111", result?.list?.get(0)?.workshop?.workshopId)
    }

    @Test
    fun `invoke should throw exception when repository fails`() = runTest {
        val expectedException = RuntimeException("Network Error")
        repository.shouldThrowError = true
        repository.error = expectedException

        val exception = assertFailsWith<RuntimeException> {
            useCase.invoke(ApiQueryParamDN())
        }
        
        assertEquals(expectedException.message, exception.message)
    }
}
