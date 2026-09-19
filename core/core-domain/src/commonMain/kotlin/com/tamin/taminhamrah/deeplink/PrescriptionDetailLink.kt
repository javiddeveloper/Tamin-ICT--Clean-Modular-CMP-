package com.tamin.taminhamrah.deeplink

/**
 * The arguments of a `prescription_detail` link, read with the native app's keys
 * (`ElectronicPrescriptionDetailFragment`):
 * `ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION`, `ARG_REQUEST_TYPE` (or `PRES_TYPE`), `ARG_NATIONAL_CODE`,
 * `ARG_CHILD_NATIONAL_CODE` and `ARG_FLAG_SATA`.
 *
 * @param patientNationalCode whose prescription it is: the child's code when the link names one,
 *   otherwise the insured's own; blank when the link carries neither
 */
data class PrescriptionDetailLink(
    val noteHeadId: String,
    val type: String,
    val patientNationalCode: String,
    val flagSata: String,
) {
    companion object {
        private const val NOTE_HEAD = "ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION"
        private const val REQUEST_TYPE = "ARG_REQUEST_TYPE"
        private const val PRESCRIPTION_TYPE = "PRES_TYPE"
        private const val NATIONAL_CODE = "ARG_NATIONAL_CODE"
        private const val CHILD_NATIONAL_CODE = "ARG_CHILD_NATIONAL_CODE"
        private const val FLAG_SATA = "ARG_FLAG_SATA"

        /** The native app's "no child": the prescription is the insured's own. */
        private const val NO_CHILD = "0"

        /** Null when the link does not say which prescription, so the caller opens the list instead. */
        fun fromArgs(args: Map<String, String>): PrescriptionDetailLink? {
            val noteHeadId = args[NOTE_HEAD]?.trim()?.takeIf { it.isNotEmpty() && it != NO_CHILD } ?: return null
            val child = args[CHILD_NATIONAL_CODE]?.trim()?.takeIf { it.isNotEmpty() && it != NO_CHILD }
            return PrescriptionDetailLink(
                noteHeadId = noteHeadId,
                type = (args[REQUEST_TYPE] ?: args[PRESCRIPTION_TYPE]).orEmpty().trim(),
                patientNationalCode = child ?: args[NATIONAL_CODE].orEmpty().trim(),
                flagSata = args[FLAG_SATA].orEmpty().trim(),
            )
        }
    }
}
