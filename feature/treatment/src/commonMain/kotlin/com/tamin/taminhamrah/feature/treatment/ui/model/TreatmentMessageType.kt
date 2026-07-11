package com.tamin.taminhamrah.feature.treatment.ui.model

/**
 * Categories of user-facing messages surfaced by the treatment feature.
 *
 * Names describe the concrete treatment outcome (what happened) rather than a
 * generic severity level, so call sites and the UI can react to a specific
 * situation instead of guessing intent from a plain SUCCESS/ERROR flag.
 */
enum class TreatmentMessageType {
    /** A PDF (prescription, test result or treatment-cost report) was downloaded. */
    PDF_DOWNLOADED,

    /** A treatment-cost report was successfully sent to the user's inbox. */
    SENT_TO_INBOX,

    /** A treatment operation (load/download/send) failed. */
    OPERATION_FAILED,

    /** A request completed but returned no records to display. */
    NO_DATA_FOUND
}
