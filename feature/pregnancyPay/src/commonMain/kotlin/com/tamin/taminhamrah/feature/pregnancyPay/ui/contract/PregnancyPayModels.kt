package com.tamin.taminhamrah.feature.pregnancyPay.ui.contract

import androidx.compose.runtime.Immutable
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.pregnancy_pay_document_employment_order
import taminx.core.core_ui.pregnancy_pay_document_employment_order_subtitle
import taminx.core.core_ui.pregnancy_pay_document_medical_rest
import taminx.core.core_ui.pregnancy_pay_document_mother_id_page1
import taminx.core.core_ui.pregnancy_pay_document_mother_id_page2

const val MEDICAL_REST_IMAGE_TYPE = "0205"
const val MOTHER_ID_PAGE1_IMAGE_TYPE = "0201"
const val MOTHER_ID_PAGE2_IMAGE_TYPE = "0202"
const val EMPLOYMENT_ORDER_IMAGE_TYPE = "0206"
const val MIN_REQUIRED_DOCUMENT_COUNT = 3

const val PREGNANCY_TYPE_SINGLE = "1"
const val PREGNANCY_TYPE_TWINS = "2"
const val PREGNANCY_TYPE_TRIPLET_OR_MORE = "3"

const val REQUEST_TYPE_SIX_MONTHS = "1"
const val REQUEST_TYPE_SIX_TO_NINE_MONTHS = "2"
const val REQUEST_TYPE_UP_TO_ONE_YEAR = "3"

@Immutable
data class PregnancyPayOptionUi(
    val id: String,
    val label: String,
)

enum class PregnancyPayPicker {
    NONE,
    BRANCH,
    PREGNANCY_STATUS,
    PREGNANCY_TYPE,
    REQUEST_TYPE,
    REST_START_DATE,
    REST_END_DATE,
    BABY_BIRTH_DATE,
    DOCUMENT_SOURCE,
}

enum class PregnancyPayImageSource { CAMERA, GALLERY }

@Immutable
data class PregnancyPayMainInfoUi(
    val risuid: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val mobileNumber: String?,
    val serviceDateTimeStamp: Int?,
    val bankAccount: String?,
    val bankName: String?,
    val insuranceTypeDesc: String?,
    val insuranceStatusDesc: String?,
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ")
}

@Immutable
data class PregnancyPayDocumentUi(
    val id: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource? = null,
    val isRequired: Boolean,
)

val PregnancyPayDocumentChecklist: ImmutableList<PregnancyPayDocumentUi> = persistentListOf(
    PregnancyPayDocumentUi(
        id = MEDICAL_REST_IMAGE_TYPE,
        titleRes = Res.string.pregnancy_pay_document_medical_rest,
        isRequired = true,
    ),
    PregnancyPayDocumentUi(
        id = MOTHER_ID_PAGE1_IMAGE_TYPE,
        titleRes = Res.string.pregnancy_pay_document_mother_id_page1,
        isRequired = true,
    ),
    PregnancyPayDocumentUi(
        id = MOTHER_ID_PAGE2_IMAGE_TYPE,
        titleRes = Res.string.pregnancy_pay_document_mother_id_page2,
        isRequired = true,
    ),
    PregnancyPayDocumentUi(
        id = EMPLOYMENT_ORDER_IMAGE_TYPE,
        titleRes = Res.string.pregnancy_pay_document_employment_order,
        subtitleRes = Res.string.pregnancy_pay_document_employment_order_subtitle,
        isRequired = false,
    ),
)

val PregnancyPayRequiredDocumentIds: ImmutableList<String> = PregnancyPayDocumentChecklist
    .filter { it.isRequired }
    .map { it.id }
    .let { persistentListOf(*it.toTypedArray()) }

@Immutable
sealed interface PregnancyPayDocumentState {
    data object Empty : PregnancyPayDocumentState

    data class Uploading(
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : PregnancyPayDocumentState

    data class Uploaded(
        val guid: String,
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : PregnancyPayDocumentState

    data class Failed(
        val message: String,
        val platformFile: PlatformFile? = null,
        val bytes: ByteArray? = null,
    ) : PregnancyPayDocumentState
}

fun PregnancyPayDocumentState.platformFileOrNull(): PlatformFile? = when (this) {
    is PregnancyPayDocumentState.Uploading -> platformFile
    is PregnancyPayDocumentState.Uploaded -> platformFile
    is PregnancyPayDocumentState.Failed -> platformFile
    PregnancyPayDocumentState.Empty -> null
}

fun PregnancyPayDocumentState.bytesOrNull(): ByteArray? = when (this) {
    is PregnancyPayDocumentState.Uploading -> bytes
    is PregnancyPayDocumentState.Uploaded -> bytes
    is PregnancyPayDocumentState.Failed -> bytes
    PregnancyPayDocumentState.Empty -> null
}

data class PregnancyPayDocumentSubmissionUi(
    val documentFile: String,
    val documentType: String,
)
