package com.tamin.taminhamrah.useCases.file

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import kotlinx.coroutines.flow.Flow

interface GetElectronicFileUseCase {
    suspend operator fun invoke(
        page: String = "1",
        start: String = "0",
        limit: String = "10",
        filter: String = "[]",
        sort: String = "[]"
    ): Flow<List<ElectronicFileDN>>
}
