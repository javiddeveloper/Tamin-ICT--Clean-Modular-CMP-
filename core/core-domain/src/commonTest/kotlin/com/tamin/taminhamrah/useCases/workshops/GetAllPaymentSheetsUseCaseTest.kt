package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.repository.workshops.FakeWorkShopsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetAllPaymentSheetsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeWorkShopsRepository
    private lateinit var useCase: GetAllPaymentSheetsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeWorkShopsRepository()
        useCase = GetAllPaymentSheetsUseCase(repository)
    }

    @Test
    fun `invoke should return payment sheet list from repository successfully`() = runTest {
        val expectedList = PaymentSheetListDN(
            list = listOf(
                PaymentSheetDN(
                    orderNo = "12345",
                    orderRow = "1",
                    payId = "PAY-1",
                    mastCustomerCode = "CUST-1",
                    rcntrow = "1",
                    mastCustomerName = "John Doe",
                    debitCreateReasonCode = "10",
                    debitCreateReasonDesc = "Salary",
                    debitNo = "DEB-1",
                    docDate = 1600000000,
                    paySeqAmount = 50000,
                    orpStatusCode = "OK",
                    orpStatusDesc = "Success",
                    cardDate = 1600000000,
                    payKindCode = "CASH",
                    payKindDesc = "Cash",
                    ouragGno = "1",
                    ouragSDate = "2023-01-01"
                )
            ),
            total = 1
        )
        repository.paymentSheetsResult = expectedList

        val result = useCase.invoke(ApiQueryParamDN())

        assertEquals(1, result?.list?.size)
        assertEquals("12345", result?.list?.get(0)?.orderNo)
        assertEquals("PAY-1", result?.list?.get(0)?.payId)
        assertEquals(50000, result?.list?.get(0)?.paySeqAmount)
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
