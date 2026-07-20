package com.tamin.taminhamrah.feature.treatment.ui.model

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
 * Record categories of سوابق درمانی.
 *
 * [requestTypeIds] is what the endpoint is actually queried with. It is a list because [ALL] has no
 * server-side equivalent — the previous app tried one and shipped without it — so that tab fans out
 * across the real categories and the results are merged client-side.
 */
@Serializable
enum class RecordTab(val label: String, val requestTypeIds: List<String>) {
    ALL(
        label = "همه",
        // The four the previous app actually queried. داروخانه is excluded on purpose: it was only
        // ever used to label a record, never as a filter, so it stays unverified as a query value.
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
        /**
         * The chips the design shows, in order. خدمات پزشکی and داروخانه are deliberately not
         * chips — the design has four — but [ALL] still queries خدمات پزشکی so those records
         * are not lost.
         */
        val chips: List<RecordTab> = entries

        /** داروخانه has no chip but can appear in results, so records still style correctly. */
        val medicalServiceTypeId: String = TYPE_MEDICAL_SERVICE
        val pharmacyTypeId: String = TYPE_PHARMACY
    }
}
