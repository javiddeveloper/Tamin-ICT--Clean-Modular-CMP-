package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/*
 * واگذارندگان — the three reads behind the feature's four screens.
 *
 * Each takes primitives or a query of primitives, never a UI model: the domain layer must not
 * learn what the screen calls its fields.
 */

/** پیمان‌هایی که کارفرما واگذارندهٔ آن‌هاست — the searched list. */
class GetAssignerContractsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: AssignerContractQuery): PagedListDN<AssignerContractDN> =
        repository.getAssignerContracts(query)
}

/** مبانی محاسباتی of one پیمان. */
class GetComputationalBasesUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ComputationalBaseQuery): PagedListDN<ComputationalBaseDN> =
        repository.getComputationalBases(query)
}

/** The PDF half of a مبنای محاسباتی's documents; the image half goes through `upload-image`. */
class GetComputationalBasePdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(documentId: String): PdfDownloadDN =
        repository.getComputationalBasePdf(documentId)
}
