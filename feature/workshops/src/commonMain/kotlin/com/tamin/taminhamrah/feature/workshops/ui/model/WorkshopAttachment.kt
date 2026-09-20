package com.tamin.taminhamrah.feature.workshops.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.ui.looksLikePdf
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * A file a workshop form has attached, and what the service knows it by.
 *
 * One object rather than a display row plus a parallel list of guids: the two were removed by a
 * shared index, so any edit that shortened one and not the other would file a document under the
 * wrong type without failing.
 */
@Immutable
data class WorkshopAttachment(
    /** What the service hands back for the uploaded image, and refers to it by afterward. */
    val guid: String,
    val type: WorkshopDocumentType,
    /** Whole kilobytes, in Persian digits — what the upload box prints beside the name. */
    val size: String,
    /** Went up as a PDF rather than an image — which route took it, and so how a request names it. */
    val isPdf: Boolean = false,
) {
    /** An image of [byteCount] bytes, sized the way the upload box prints it. */
    constructor(guid: String, type: WorkshopDocumentType, byteCount: Int, isPdf: Boolean = false) :
        this(guid = guid, type = type, size = byteCount.asKilobytes(), isPdf = isPdf)
}

/**
 * Puts a picked image on the server and names what came back.
 *
 * All three workshop forms attach evidence the same way — ثبت اعتراض, درخواست رسیدگی and
 * نامنویسی — so the upload lives here once instead of in each ViewModel. The shared
 * `upload-image` endpoint is the same one addDependent and occurrence reporting use.
 */
class WorkshopAttachmentUploader(
    /**
     * Puts one image on the server and hands back the guid it is known by.
     *
     * A function rather than the use case itself, so nothing downstream of a workshop form —
     * a test included — has to know that the guid comes out of the contracts' repository.
     */
    private val uploadImage: suspend (UploadImageRequestDN) -> String,
) {

    /**
     * @param typeCode the code the user filed the image under, from [types].
     * @throws IllegalArgumentException if [typeCode] is not one of [types] — a form can only offer
     * codes from its own table, so this means the table and the sheet have gone out of step.
     */
    suspend operator fun invoke(
        fileName: String,
        bytes: ByteArray,
        typeCode: String,
        types: List<WorkshopDocumentType>,
        /**
         * Where a PDF goes, for the one form that files them (درخواست مفاصاحساب). Null — the default —
         * sends every file to `upload-image`, which is all the other forms let the user pick.
         */
        uploadPdf: (suspend (UploadImageRequestDN) -> String)? = null,
    ): WorkshopAttachment {
        val type = requireNotNull(types.firstOrNull { it.code == typeCode }) {
            "Unknown document type '$typeCode'"
        }
        val request = UploadImageRequestDN(fileName = fileName, bytes = bytes)
        // Decided by the bytes, not the name: a picker's extension filter is a hint, the header is not.
        val pdfRoute = uploadPdf?.takeIf { bytes.looksLikePdf() }
        val guid = pdfRoute?.invoke(request) ?: uploadImage(request)
        return WorkshopAttachment(
            guid = guid,
            type = type,
            byteCount = bytes.size,
            isPdf = pdfRoute != null,
        )
    }
}

/**
 * Reads back an image the service already holds, as the [WorkshopAttachment] an upload of it
 * would have produced.
 *
 * The counterpart of [WorkshopAttachmentUploader] for a form re-opened on documents already on
 * file: the same `upload-image` store, read by the guid each document was filed under.
 */
class WorkshopAttachmentDownloader(
    /**
     * The image's raw base64 payload for a guid, as the service returns it.
     *
     * A function rather than the use case itself, for the same reason the uploader takes one.
     */
    private val downloadImage: suspend (guid: String) -> String,
) {

    /** @throws IllegalStateException if the service has no image for [guid]. */
    suspend operator fun invoke(guid: String, type: WorkshopDocumentType): WorkshopAttachment {
        val payload = downloadImage(guid)
        check(payload.isNotBlank()) { "No image on file for '$guid'" }
        return WorkshopAttachment(guid = guid, type = type, byteCount = decodedByteCount(payload))
    }
}

/**
 * How many bytes a base64 payload decodes to, counted rather than decoded: only the size is kept,
 * and decoding a whole photograph on the caller's thread to learn it is wasted work.
 */
private fun decodedByteCount(base64: String): Int {
    val length = base64.count { !it.isWhitespace() }
    val padding = base64.trimEnd().takeLastWhile { it == '=' }.length
    return length / 4 * 3 - padding
}

/**
 * A byte count as the whole kilobytes the upload box prints.
 *
 * Rounded up, so a file that is genuinely there never reads as «۰ کیلوبایت».
 */
private fun Int.asKilobytes(): String =
    ((this + BYTES_PER_KILOBYTE - 1) / BYTES_PER_KILOBYTE).toString().toPersianDigits()

private const val BYTES_PER_KILOBYTE = 1024
