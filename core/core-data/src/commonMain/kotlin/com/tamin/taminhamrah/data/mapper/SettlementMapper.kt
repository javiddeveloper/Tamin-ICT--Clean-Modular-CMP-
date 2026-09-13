package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.SettlementDocumentDTO
import com.tamin.taminhamrah.model.workshop.SettlementRequestDN
import com.tamin.taminhamrah.model.workshop.SettlementRequestDTO
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDN
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDTO
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/*
 * درخواست مفاصاحساب, domain ↔ wire. Every rule below is the old app's (`MafasaHesabInfoViewModel`,
 * `MafasaHesabRequestModel`, `DatePickerWidget`), which is the only written record of this contract.
 */

fun SettlementSubjectDTO.toDomain(): SettlementSubjectDN = SettlementSubjectDN(
    code = code.orEmpty(),
    description = description.orEmpty(),
)

/**
 * The id `update-request-issuance-invoices38/{id}` is addressed with: five keys joined by `TT`, in the
 * old app's order — `0082810145TT02100001TT0210TT01TT01`. The request is a PUT onto this id, not a
 * POST that is handed one back.
 */
fun SettlementRequestDN.requestId(): String =
    listOf(workshopId, contractRow, branchCode, contractSequence, subjectCode).joinToString("TT")

fun SettlementRequestDN.toDto(): SettlementRequestDTO = SettlementRequestDTO(
    amount = amount.toString(),
    currencyAmount = currencyAmount.toString(),
    currencyAmountInRial = currencyAmountInRial.toString(),
    totalAmount = (amount + currencyAmountInRial).toDouble(),
    letterDate = letterDate.toWireDate(),
    startDate = startDate.toWireDate(),
    endDate = endDate.toWireDate(),
    letterNumber = letterNumber,
    documents = documents.map {
        SettlementDocumentDTO(
            documentId = it.documentId,
            documentCode = it.categoryCode,
            documentType = if (it.isPdf) PDF_DOCUMENT_TYPE else IMAGE_DOCUMENT_TYPE,
        )
    },
    // Any image at all — the old app's `imageFileList.isNotEmpty()`, not only a نامه filed as one.
    hasLetterImage = documents.any { !it.isPdf },
    contractorWorkshopId = workshopId,
    subcontractor = if (hasSubcontractor) "1" else "0",
    subjectCode = subjectCode,
    subjectOwner = subjectOwner,
    subjectAmount1 = subjectAmount1,
    subjectAmount2 = subjectAmount2.toSubjectAmount2(subjectCode),
    subjectAmount3 = subjectAmount3,
    subjectAmount4 = subjectAmount4,
    subjectImage = subjectImageGuid,
    subjectText1 = subjectText1,
    subjectText2 = subjectText2,
)

/**
 * The old app's date picker stamps `T19:30:00.000Z` onto the Gregorian calendar day of every date it
 * sends. Kept verbatim rather than "corrected" to a real Tehran midnight: the service reads the day
 * the way it has always been sent.
 */
private fun String.toWireDate(): String = "${this}T19:30:00.000Z"

/**
 * The one field whose JSON type follows the subject. The old app computes it as a `Long` for 04–07,
 * which Gson writes as a number, and types it as text for 11 and 29. Blank is left out, as Gson left
 * out the null.
 */
private fun String.toSubjectAmount2(subjectCode: String): JsonElement? = when {
    isBlank() -> null
    subjectCode in NUMERIC_AMOUNT2_SUBJECTS -> JsonPrimitive(toLongOrNull() ?: 0L)
    else -> JsonPrimitive(this)
}

private val NUMERIC_AMOUNT2_SUBJECTS = setOf("04", "05", "06", "07")
private const val IMAGE_DOCUMENT_TYPE = "1"
private const val PDF_DOCUMENT_TYPE = "2"
