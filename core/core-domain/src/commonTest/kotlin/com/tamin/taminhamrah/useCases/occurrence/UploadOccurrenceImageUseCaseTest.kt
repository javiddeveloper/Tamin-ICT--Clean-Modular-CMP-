package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.repository.occurrence.FakeOccurrenceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UploadOccurrenceImageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeOccurrenceRepository
    private lateinit var useCase: UploadOccurrenceImageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeOccurrenceRepository()
        useCase = UploadOccurrenceImageUseCase(repository)
    }

    @Test
    fun `invoke should upload image bytes and return the guid`() = runTest {
        repository.uploadImageResult = "a4769aa8-b9af-4183-83b9-367dc9f52511"
        val fileName = "occurrence_doc_0001.jpg"
        val fileBytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xD9.toByte())

        val guid = useCase(fileName, fileBytes)

        assertEquals("a4769aa8-b9af-4183-83b9-367dc9f52511", guid)
        val (lastFileName, lastFileBytes) = requireNotNull(repository.lastUploadImageParams)
        assertEquals(fileName, lastFileName)
        assertEquals(fileBytes.toList(), lastFileBytes.toList())
    }

    @Test
    fun `invoke should propagate repository error when upload fails`() = runTest {
        val expectedError = RuntimeException("Upload failed")
        repository.shouldThrowError = true
        repository.error = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            useCase("occurrence_doc_0002.jpg", byteArrayOf(1, 2, 3))
        }

        assertEquals(expectedError.message, actualError.message)
    }
}
