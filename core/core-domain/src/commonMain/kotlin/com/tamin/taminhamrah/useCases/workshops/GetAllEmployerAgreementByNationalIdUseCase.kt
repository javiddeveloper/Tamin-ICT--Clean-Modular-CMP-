package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetAllEmployerAgreementByNationalIdUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        page: String = "1",
        start: String = "0",
        limit: String = "10",
        filter: String = "[]",
        sort: String = "[]"
    ): ListData<EmployerAgreementDTO>? {
        return repository.getAllEmployerAgreementByNationalId(
            page = page,
            start = start,
            limit = limit,
            filter = filter,
            sort = sort
        )
    }
}
