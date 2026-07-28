package com.tamin.taminhamrah.feature.treatment.ui.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

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
enum class RecordTab(val label: String, val requestTypeIds: List<String>) {
    ALL(
        label = "همه",
        requestTypeIds = listOf(
            TYPE_MEDICINE,
            TYPE_VISIT,
            TYPE_PARACLINIC,
            TYPE_MEDICAL_SERVICE,
        ),
    ),
    MEDICINE("دارو", listOf(TYPE_MEDICINE)),
    VISIT("ویزیت", listOf(TYPE_VISIT)),
    PARACLINIC("پاراکلینیک", listOf(TYPE_PARACLINIC)),
    MEDICAL_SERVICE("خدمات پزشکی", listOf(TYPE_MEDICAL_SERVICE)),
    ;

    companion object {
        /** The default landing tab: «همه». */
        val Default: RecordTab = ALL

        val chips: ImmutableList<RecordTab> = entries.toImmutableList()

        /**
         * The chips' labels, resolved once. The filter row is redrawn on every list update, and
         * mapping them per recomposition would hand it a new list each time — which is the one
         * thing that stops it skipping.
         */
        val chipLabels: ImmutableList<String> = chips.map { it.label }.toImmutableList()

        val medicalServiceTypeId: String = TYPE_MEDICAL_SERVICE
        val pharmacyTypeId: String = TYPE_PHARMACY

        /** Persian name for a category id, including the two that have no tab. */
        fun labelForTypeId(typeId: String): String? = when (typeId) {
            TYPE_PHARMACY -> "داروخانه"
            TYPE_MEDICAL_SERVICE -> "خدمات پزشکی"
            else -> null
        }
    }
}
