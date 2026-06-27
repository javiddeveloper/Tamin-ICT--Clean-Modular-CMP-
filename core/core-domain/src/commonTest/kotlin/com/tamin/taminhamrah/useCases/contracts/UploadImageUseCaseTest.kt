package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UploadImageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: UploadImageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = UploadImageUseCase(repository)
    }

    @Test
    fun `invoke should upload image and return image id`() = runTest {
        val request = UploadImageRequestDN(
            fileName = "Image_Imp_0000.jpg",
            bytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xD9.toByte()),
            description = "تست",
        )

        useCase(request).test {
            val imageId = awaitItem()
            assertEquals("a4769aa8-b9af-4183-83b9-367dc9f52511", imageId)
            awaitComplete()
        }

        assertEquals(request, repository.lastUploadImageRequest)
    }
}
