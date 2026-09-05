package com.tamin.taminhamrah.model.workshop

/** A document attached to an objection: the guid `upload-image` returned, plus its type code. */
data class ObjectionDocumentDN(
    val guid: String,
    val typeCode: String,
)

/**
 * Everything the اعتراض به بدهی form submits.
 *
 * The debt columns are echoed from the row the objection was opened on; only [description],
 * [documents] and [deposit] come from the user.
 */
data class DebitObjectionRequestDN(
    val workshopId: String,
    val branchCode: String,
    val debt: WorkShopDebtDN,
    val description: String,
    val documents: List<ObjectionDocumentDN>,
    val deposit: Boolean = false,
    val confirmed: Boolean = true,
)

/** Result of filing an objection; [referenceCode] is what the success message quotes. */
data class DebitObjectionResultDN(
    val seqNo: Long? = null,
    val referenceCode: String = "",
)
