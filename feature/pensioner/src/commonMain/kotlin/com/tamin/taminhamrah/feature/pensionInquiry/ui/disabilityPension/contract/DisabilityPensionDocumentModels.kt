package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityDocumentDN
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_document_appeal_commission_opinion
import taminx.core.core_ui.disability_pension_document_incident_report
import taminx.core.core_ui.disability_pension_document_judicial_ruling
import taminx.core.core_ui.disability_pension_document_primary_commission_opinion
import taminx.core.core_ui.disability_pension_document_work_inspection_report

private const val PRIMARY_COMMISSION_OPINION = "13"
private const val APPEAL_COMMISSION_OPINION = "14"
private const val INCIDENT_REPORT = "15"
private const val WORK_INSPECTION_REPORT = "16"
private const val JUDICIAL_RULING = "17"

enum class DisabilityDocumentImageSource { CAMERA, GALLERY }

@Immutable
data class DisabilityDocumentUi(
    val id: String,
    val titleRes: StringResource,
)

val DisabilityDocumentChecklist: ImmutableList<DisabilityDocumentUi> = persistentListOf(
    DisabilityDocumentUi(
        id = PRIMARY_COMMISSION_OPINION,
        titleRes = Res.string.disability_pension_document_primary_commission_opinion,
    ),
    DisabilityDocumentUi(
        id = APPEAL_COMMISSION_OPINION,
        titleRes = Res.string.disability_pension_document_appeal_commission_opinion,
    ),
    DisabilityDocumentUi(
        id = INCIDENT_REPORT,
        titleRes = Res.string.disability_pension_document_incident_report,
    ),
    DisabilityDocumentUi(
        id = WORK_INSPECTION_REPORT,
        titleRes = Res.string.disability_pension_document_work_inspection_report,
    ),
    DisabilityDocumentUi(
        id = JUDICIAL_RULING,
        titleRes = Res.string.disability_pension_document_judicial_ruling,
    ),
)

@Immutable
sealed interface DisabilityDocumentState {
    data object Empty : DisabilityDocumentState

    data class Uploading(
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : DisabilityDocumentState

    data class Uploaded(
        val guid: String,
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : DisabilityDocumentState

    data class Failed(
        val message: String? = null,
        val messageRes: StringResource? = null,
        val platformFile: PlatformFile? = null,
        val bytes: ByteArray? = null,
    ) : DisabilityDocumentState
}

fun DisabilityDocumentState.platformFileOrNull(): PlatformFile? = when (this) {
    is DisabilityDocumentState.Uploading -> platformFile
    is DisabilityDocumentState.Uploaded -> platformFile
    is DisabilityDocumentState.Failed -> platformFile
    DisabilityDocumentState.Empty -> null
}

fun DisabilityDocumentState.bytesOrNull(): ByteArray? = when (this) {
    is DisabilityDocumentState.Uploading -> bytes
    is DisabilityDocumentState.Uploaded -> bytes
    is DisabilityDocumentState.Failed -> bytes
    DisabilityDocumentState.Empty -> null
}

/** Only [DisabilityDocumentState.Uploaded] entries carry a guid the backend can accept. */
fun ImmutableMap<String, DisabilityDocumentState>.toDisabilityDocumentDNs(): List<DisabilityDocumentDN> =
    mapNotNull { (documentType, state) ->
        (state as? DisabilityDocumentState.Uploaded)?.let {
            DisabilityDocumentDN(documentType = documentType, guid = it.guid)
        }
    }
