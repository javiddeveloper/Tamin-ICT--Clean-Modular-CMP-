package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `workshop-services/get-workshops-info/{workshopId}/{branchCode}` — the read-only workshop panel
 * on step 1 of the ماده ۱۶ request.
 */
@Serializable
data class ArticleSixteenWorkshopInfoDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("character") val character: String? = null,
    @SerialName("lastAddress") val lastAddress: String? = null,
)

/**
 * `debit-objection/objection-request/{objectionNumber}` — what the کارشناس wrote back on a
 * نقص مدارک request, plus the documents already on file.
 */
@Serializable
data class ArticleSixteenRequestInfoDTO(
    @SerialName("defectDesc") val defectDescription: String? = null,
    // Nullable: the service can send `null`, which a defaulted non-null list does not survive.
    @SerialName("objectionPhotos") val objectionPhotos: List<ArticleSixteenPhotoDTO>? = null,
)

@Serializable
data class ArticleSixteenPhotoDTO(
    @SerialName("guid") val guid: String? = null,
    @SerialName("seqNo") val seqNo: Int? = null,
    @SerialName("type") val type: String? = null,
)

/**
 * Body of `POST debit-objection/debit-comitte-save`.
 *
 * Every debt figure is echoed straight back from the row the request was opened on; the widths
 * match [WorkshopsDebtListModelDTO] so nothing is narrowed on the way out.
 */
@Serializable
data class ArticleSixteenSaveRequestDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitStartDate") val debitStartDate: String? = null,
    @SerialName("debitEndDate") val debitEndDate: String? = null,
    @SerialName("debitStepCode") val debitStepCode: String? = null,
    @SerialName("peymanSequence") val agreementRow: String? = null,
    @SerialName("mastCustomerTypeCode") val customerTypeCode: String? = null,
    @SerialName("bimehAmount") val insuranceAmount: Long? = null,
    @SerialName("bikariAmount") val unemploymentAmount: Long? = null,
    @SerialName("jarimehAmount") val fineAmount: Long? = null,
    @SerialName("sayerAmount") val otherAmount: Long? = null,
    @SerialName("kindDoc") val kindDoc: String? = null,
    @SerialName("orderNumber") val orderNumber: String? = null,
    @SerialName("orderDate") val orderDate: String? = null,
    @SerialName("eblaghDate") val executiveNotifyDate: String? = null,
    @SerialName("objectionDate") val objectionDate: String = "",
    @SerialName("objectionType") val objectionType: String? = null,
    @SerialName("objectionPhotos") val objectionPhotos: List<ObjectionPhotoDTO> = emptyList(),
)

/** Result of `debit-comitte-save`; [refId] is the tracking code the success message quotes. */
@Serializable
data class ArticleSixteenSaveResultDTO(
    @SerialName("refId") val refId: String? = null,
)
