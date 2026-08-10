package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemDN
import com.tamin.taminhamrah.repository.FakeHistoryRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetHistoryJobInfosUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHistoryRepository
    private lateinit var useCase: GetHistoryJobInfosUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHistoryRepository()
        useCase = GetHistoryJobInfosUseCase(repository)
    }

    @Test
    fun `invoke should return job infos list`() = runTest {
        val expected = HistoryJobInfoDN(
            list = listOf(
                HistoryJobInfoItemDN(
                    risuid = "0081631829",
                    rwshName = "شرکت صنایع دما بخار مشهد",
                    brhcode = "6400",
                    id = 1,
                    jobDesc = "کارمند اداری ۱",
                    startDate = "139810",
                    rwshId = "6393610019"
                )
            ),
            total = 1
        )
        repository.getHistoryJobInfosResult = expected

        val result = useCase()

        assertEquals(expected, result)
        assertEquals(1, result.total)
        assertEquals(1, result.list?.size)
        assertEquals("کارمند اداری ۱", result.list?.first()?.jobDesc)
    }

    @Test
    fun `invoke should return empty list when no job infos`() = runTest {
        repository.getHistoryJobInfosResult = HistoryJobInfoDN(list = emptyList(), total = 0)

        val result = useCase()

        assertEquals(0, result.total)
        assertEquals(0, result.list?.size)
    }

    @Test
    fun `invoke should throw exception when repository fails`() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase()
        }
    }
}
