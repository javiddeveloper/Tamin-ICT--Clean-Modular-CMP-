package com.tamin.taminhamrah.feature.treatment.ui.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.tab_all
import taminx.core.core_ui.tab_medical_service
import taminx.core.core_ui.tab_medicine
import taminx.core.core_ui.tab_paraclinic
import taminx.core.core_ui.tab_pharmacy
import taminx.core.core_ui.tab_visit

/**
 * Categories the patient-history endpoint understands, taken from the previous app's
 * `PrescriptionType`: `0` داروخانه, `1` دارویی, `2` پاراکلینیک, `3` ویزیت, `5` خدمات پزشکی.
 */
private const val TYPE_PHARMACY = "0"
private const val TYPE_MEDICINE = "1"
private const val TYPE_PARACLINIC = "2"
private const val TYPE_VISIT = "3"
private const val TYPE_MEDICAL_SERVICE = "5"

/**
 * Record categories of سوابق درمانی, in the design's order.
 *
 * [requestTypeIds] is what the endpoint is queried with. It is a list because [ALL] has no
 * server-side value — the endpoint filters one type at a time — so «همه» fans out across the real
 * categories and the results are merged client-side. Every other tab holds a single type.
 * داروخانه (`0`) is not a tab: it only ever labeled a record, never filtered.
 */
@Serializable
enum class RecordTab(val label: StringResource, val requestTypeIds: List<String>) {
    ALL(
        label = Res.string.tab_all,
        requestTypeIds = listOf(
            TYPE_MEDICINE,
            TYPE_VISIT,
            TYPE_PARACLINIC,
            TYPE_MEDICAL_SERVICE,
        ),
    ),
    MEDICINE(Res.string.tab_medicine, listOf(TYPE_MEDICINE)),
    VISIT(Res.string.tab_visit, listOf(TYPE_VISIT)),
    PARACLINIC(Res.string.tab_paraclinic, listOf(TYPE_PARACLINIC)),
    MEDICAL_SERVICE(Res.string.tab_medical_service, listOf(TYPE_MEDICAL_SERVICE)),
    ;

    companion object {
        /** The default landing tab: «همه». */
        val Default: RecordTab = ALL

        val chips: ImmutableList<RecordTab> = entries.toImmutableList()

        val medicalServiceTypeId: String = TYPE_MEDICAL_SERVICE
        val pharmacyTypeId: String = TYPE_PHARMACY

        /** Name for a category id, including the two that have no tab. */
        fun labelForTypeId(typeId: String): StringResource? = when (typeId) {
            TYPE_PHARMACY -> Res.string.tab_pharmacy
            TYPE_MEDICAL_SERVICE -> Res.string.tab_medical_service
            else -> null
        }
    }
}

/**
 * The name for the endpoint's numeric category, or null when the id is one neither a tab nor
 * [RecordTab.labelForTypeId] knows — the caller shows the raw id then, so an unexpected value
 * stays visible instead of blank.
 *
 * Lives with the categories it maps rather than in the screen that renders them.
 */
fun String.toCategoryLabel(): StringResource? =
    RecordTab.entries.firstOrNull { it != RecordTab.ALL && this in it.requestTypeIds }?.label
        ?: RecordTab.labelForTypeId(this)

/**
 * The chips' labels, resolved once per composition rather than per recomposition.
 *
 * The filter row is redrawn on every list update; keying the remember on the resolved labels
 * hands it the same [ImmutableList] instance every time, which is the one thing that stops it
 * from skipping.
 */
@Composable
fun rememberRecordTabLabels(): ImmutableList<String> {
    val labels = RecordTab.chips.map { stringResource(it.label) }
    return remember(labels) { labels.toImmutableList() }
}
