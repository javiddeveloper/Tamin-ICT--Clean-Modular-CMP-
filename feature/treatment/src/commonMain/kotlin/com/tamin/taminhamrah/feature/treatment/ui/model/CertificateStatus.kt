package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.model.treatment.TreatmentCostPR

/**
 * How a refund certificate's coded states read on screen.
 *
 * The service reports payment and file progress as bare numbers; the rules for turning those into
 * "settled or not" are the previous app's, kept here beside [CoverageStatus] so the screens stay
 * free of magic strings and the rules stay testable.
 */

/** Payment states the service considers settled. Anything else is pending or returned. */
private val PAID_STATUSES = setOf("4", "5")

/** The file state the service considers settled. */
private const val FILE_SETTLED_STATUS = "7"

/** A certificate that was never issued comes back with this id; there is nothing to act on. */
private const val ABSENT_REP_ID = "0"

/** Amount fields carry this when the service has nothing to report under that heading. */
const val NO_AMOUNT = "0"

/** Whether the refund has been paid, which is what colors the card. */
val TreatmentCostPR.isPaid: Boolean
    get() = payStatus in PAID_STATUSES

/** Whether the file itself is closed, which colors «وضعیت پرونده». */
val TreatmentCostPR.isFileSettled: Boolean
    get() = status == FILE_SETTLED_STATUS

/**
 * Whether this certificate can be opened or posted to the inbox.
 *
 * Acting on a certificate the service never issued would send a malformed request, so the previous
 * app refused it outright; the card simply offers no actions instead.
 */
val TreatmentCostPR.isActionable: Boolean
    get() = repId.isNotBlank() && repId != ABSENT_REP_ID
