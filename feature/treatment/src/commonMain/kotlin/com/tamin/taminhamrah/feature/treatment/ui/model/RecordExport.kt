package com.tamin.taminhamrah.feature.treatment.ui.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_print
import taminx.core.core_ui.prescription_download_cd
import taminx.core.core_ui.prescription_lab_result_cd

/**
 * What a record offers to download, decided by its own `flagSata`.
 *
 * This is a per-record fact, not a per-category one. The previous app read it straight off the
 * record — `btnDownload.isVisible = item.flagSata != "1"`, then `flagSata == "2"` chose the
 * lab-result endpoint over the prescription PDF — and it is the same single button either way,
 * only relabeled.
 *
 * Deciding it from the category instead put a lab-result button on every paraclinic record,
 * including those whose result is not ready: the endpoint has nothing to return for them, so the
 * button could only fail. It also showed a download on records the previous app hid it on
 * entirely.
 *
 * The id, the glyph, the wording and the export it opens are one table, so a fourth flag cannot
 * arrive and be handled in three places out of four.
 */
internal enum class RecordExport(
    val flagSata: String,
    val icon: DrawableResource,
    val contentDescription: StringResource,
    val export: TreatmentRecordPdfExport,
) {
    /** The electronic prescription itself — the default for anything not flagged otherwise. */
    PRESCRIPTION(
        flagSata = "0",
        icon = Res.drawable.ic_tamin_download,
        contentDescription = Res.string.prescription_download_cd,
        export = TreatmentRecordPdfExport.PRESCRIPTION,
    ),

    /** A paraclinic result that is actually ready. */
    LAB_RESULT(
        flagSata = "2",
        icon = Res.drawable.ic_tamin_print,
        contentDescription = Res.string.prescription_lab_result_cd,
        export = TreatmentRecordPdfExport.LAB_RESULT,
    ),
    ;

    companion object {
        /** The one value that means "this record has nothing to download". */
        private const val NOTHING_TO_DOWNLOAD = "1"

        /**
         * `null` when the record offers no download at all.
         *
         * Anything that is neither [NOTHING_TO_DOWNLOAD] nor a known flag falls back to the
         * prescription, which is what the previous app's `else` branch did — a record the service
         * flags in some new way still offers the export every record has.
         */
        fun forFlagSata(flagSata: String): RecordExport? = when (flagSata) {
            NOTHING_TO_DOWNLOAD -> null
            LAB_RESULT.flagSata -> LAB_RESULT
            else -> PRESCRIPTION
        }
    }
}
