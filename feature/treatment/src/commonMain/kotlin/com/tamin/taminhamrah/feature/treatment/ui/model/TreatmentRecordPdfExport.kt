package com.tamin.taminhamrah.feature.treatment.ui.model

/**
 * The PDF exports a medical record can show.
 *
 * Each names its own file, which is what lets the viewer recognize one it has downloaded before
 * and skip the request entirely.
 */
internal enum class TreatmentRecordPdfExport(private val filePrefix: String) {
    PRESCRIPTION(filePrefix = "prescription"),
    LAB_RESULT(filePrefix = "lab_result"),
    ;

    /**
     * Built rather than templated so the shape of the name lives in one place: each variant
     * carries only what makes it different, and a third export cannot invent its own spelling.
     */
    fun fileName(noteHeadId: String): String = buildString {
        append(filePrefix)
        append(NAME_SEPARATOR)
        append(noteHeadId)
        append(FILE_EXTENSION)
    }

    private companion object {
        const val NAME_SEPARATOR = '_'
        const val FILE_EXTENSION = ".pdf"
    }
}
