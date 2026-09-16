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
 * `PrescriptionType`.
 *
 * [id] is the wire value and the only thing the endpoint accepts, so it stays a string — the ids
 * are not contiguous (`4` is absent) and nothing is gained by treating them as numbers.
 *
 * The label lives here too, so the id, the name and the record's capabilities are one table rather
 * than three that drift the first time one is edited.
 */
enum class RecordType(val id: String) {
    PHARMACY("0"),
    MEDICINE("1"),
    PARACLINIC("2"),
    VISIT("3"),
    MEDICAL_SERVICE("5"),
    ;

    /**
     * Resolved lazily — Compose Multiplatform shards string accessors across `StringN` files, so
     * putting [Res.string] in the enum constructor breaks `testReleaseUnitTest` whenever a stale
     * compile still points at the previous shard (`NoSuchMethodError` → `ExceptionInInitializerError`).
     */
    val label: StringResource
        get() = when (this) {
            PHARMACY -> Res.string.tab_pharmacy
            MEDICINE -> Res.string.tab_medicine
            PARACLINIC -> Res.string.tab_paraclinic
            VISIT -> Res.string.tab_visit
            MEDICAL_SERVICE -> Res.string.tab_medical_service
        }

    companion object {
        /** `null` for an id the endpoint has grown since; the caller shows the raw value then. */
        fun fromId(id: String): RecordType? = entries.firstOrNull { it.id == id }
    }
}

/**
 * Record categories of سوابق درمانی, in the design's order.
 *
 * [requestTypeIds] is what the endpoint is queried with. It is a list because [ALL] has no
 * server-side value — the endpoint filters one type at a time — so «همه» fans out across the real
 * categories and the results are merged client-side. Every other tab holds a single type.
 * داروخانه (`0`) is not a tab: it only ever labeled a record, never filtered.
 */
@Serializable
enum class RecordTab(val requestTypeIds: List<String>) {
    ALL(
        requestTypeIds = listOf(
            RecordType.MEDICINE.id,
            RecordType.VISIT.id,
            RecordType.PARACLINIC.id,
            RecordType.MEDICAL_SERVICE.id,
        ),
    ),
    MEDICINE(listOf(RecordType.MEDICINE.id)),
    VISIT(listOf(RecordType.VISIT.id)),
    PARACLINIC(listOf(RecordType.PARACLINIC.id)),
    MEDICAL_SERVICE(listOf(RecordType.MEDICAL_SERVICE.id)),
    ;

    /** See [RecordType.label] — kept off the constructor so JVM unit tests can load the enum. */
    val label: StringResource
        get() = when (this) {
            ALL -> Res.string.tab_all
            MEDICINE -> Res.string.tab_medicine
            VISIT -> Res.string.tab_visit
            PARACLINIC -> Res.string.tab_paraclinic
            MEDICAL_SERVICE -> Res.string.tab_medical_service
        }

    companion object {
        /** The default landing tab: «همه». */
        val Default: RecordTab = ALL

        val chips: ImmutableList<RecordTab> = entries.toImmutableList()

        val medicalServiceTypeId: String = RecordType.MEDICAL_SERVICE.id
        val pharmacyTypeId: String = RecordType.PHARMACY.id

        /** Name for a category id, including the two that have no tab. */
        fun labelForTypeId(typeId: String): StringResource? = RecordType.fromId(typeId)?.label
    }
}

/**
 * The name for the endpoint's numeric category, or null when the id is one neither a tab nor
 * [RecordTab.labelForTypeId] knows — the caller shows the raw id then, so an unexpected value
 * stays visible instead of blank.
 *
 * Lives with the categories it maps rather than in the screen that renders them.
 */
fun String.toCategoryLabel(): StringResource? = RecordType.fromId(this)?.label

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
