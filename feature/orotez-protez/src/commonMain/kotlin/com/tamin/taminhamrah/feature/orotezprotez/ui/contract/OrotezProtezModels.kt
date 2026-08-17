package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.orotez_protez_document_ear_mold
import taminx.core.core_ui.orotez_protez_document_hearing_aid_warranty
import taminx.core.core_ui.orotez_protez_document_invoice
import taminx.core.core_ui.orotez_protez_document_prescription

private const val PRESCRIPTION = "0401"
private const val INVOICE = "0402"
private const val EAR_MOLD = "0403"
private const val HEARING_AID_WARRANTY = "0404"

@Immutable
data class OrotezProtezOptionUi(
    val id: String,
    val label: String,
    val subtitle: String? = null,
)

enum class OrotezProtezPicker { NONE, BRANCH, INSURED_PERSON, DATE, DOCUMENT_SOURCE }
enum class OrotezProtezImageSource { CAMERA, GALLERY }
@Immutable
data class OrotezProtezDocumentUi(
    val id: String,
    val titleRes: StringResource,
    val isRequired: Boolean,
)

val OrotezProtezDocumentChecklist: ImmutableList<OrotezProtezDocumentUi> = persistentListOf(
    OrotezProtezDocumentUi(
        id = PRESCRIPTION,
        titleRes = Res.string.orotez_protez_document_prescription,
        isRequired = true,
    ),
    OrotezProtezDocumentUi(
        id = INVOICE,
        titleRes = Res.string.orotez_protez_document_invoice,
        isRequired = true,
    ),
    OrotezProtezDocumentUi(
        id = EAR_MOLD,
        titleRes = Res.string.orotez_protez_document_ear_mold,
        isRequired = false,
    ),
    OrotezProtezDocumentUi(
        id = HEARING_AID_WARRANTY,
        titleRes = Res.string.orotez_protez_document_hearing_aid_warranty,
        isRequired = false,
    ),
)

@Immutable
sealed interface OrotezProtezDocumentState {
    data object Empty : OrotezProtezDocumentState

    data class Uploading(
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : OrotezProtezDocumentState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Uploading

            if (platformFile != other.platformFile) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = platformFile.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    data class Uploaded(
        val guid: String,
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : OrotezProtezDocumentState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Uploaded

            if (guid != other.guid) return false
            if (platformFile != other.platformFile) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = guid.hashCode()
            result = 31 * result + platformFile.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    data class Failed(
        val message: String,
        val platformFile: PlatformFile? = null,
        val bytes: ByteArray? = null,
    ) : OrotezProtezDocumentState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Failed

            if (message != other.message) return false
            if (platformFile != other.platformFile) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message.hashCode()
            result = 31 * result + (platformFile?.hashCode() ?: 0)
            result = 31 * result + (bytes?.contentHashCode() ?: 0)
            return result
        }
    }
}

fun OrotezProtezDocumentState.platformFileOrNull(): PlatformFile? = when (this) {
    is OrotezProtezDocumentState.Uploading -> platformFile
    is OrotezProtezDocumentState.Uploaded -> platformFile
    is OrotezProtezDocumentState.Failed -> platformFile
    OrotezProtezDocumentState.Empty -> null
}

fun OrotezProtezDocumentState.bytesOrNull(): ByteArray? = when (this) {
    is OrotezProtezDocumentState.Uploading -> bytes
    is OrotezProtezDocumentState.Uploaded -> bytes
    is OrotezProtezDocumentState.Failed -> bytes
    OrotezProtezDocumentState.Empty -> null
}
data class OrotezProtezDocumentSubmissionUi(
    val documentFile: String,
    val documentType: String,
)

@Immutable
data class OrotezProtezInsuredDetailUi(
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val relation: String,
    val relationCode: String,
    val nationalCode: String,
    val birthCertificateNumber: String,
    val issuePlace: String,
    val birthDateLabel: String,
    val bookletValidUntilLabel: String,
)

@Immutable
data class OrotezProtezBranchDetailUi(
    val branchCode: String?,
    val branchName: String?,
)
@Immutable
data class OrotezProtezMainInfoUi(
    val risuid: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val mobileNumber: String?,
)
