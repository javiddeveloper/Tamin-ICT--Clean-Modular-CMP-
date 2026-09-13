package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.SettlementRequestDN
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/*
 * درخواست مفاصاحساب — the three calls behind واگذارندگان's settlement request. An image attached to
 * it goes through the shared `UploadImageUseCase`; only a PDF has a route of its own.
 */

/** The موضوع کار picker. */
class GetSettlementSubjectsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(): List<SettlementSubjectDN> = repository.getSettlementSubjects()
}

/** Stores one PDF; returns the id the request names it by. */
class UploadSettlementPdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(fileName: String, bytes: ByteArray): String =
        repository.uploadSettlementPdf(fileName, bytes)
}

/** Files the request; returns the service's confirmation. */
class SubmitSettlementRequestUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: SettlementRequestDN): String =
        repository.submitSettlementRequest(request)
}
