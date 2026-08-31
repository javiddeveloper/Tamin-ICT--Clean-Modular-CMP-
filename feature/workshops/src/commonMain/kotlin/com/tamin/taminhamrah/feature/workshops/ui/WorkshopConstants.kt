package com.tamin.taminhamrah.feature.workshops.ui

/**
 * The non-visual constants the کارگاه‌های کارفرما screens run on.
 *
 * Sibling to `WorkshopDimens`, and deliberately separate from it: those are measurements the
 * design dictates, these are limits the *service* dictates and behaviour the list agreed on. A
 * designer changing a padding should never be editing the same table as a field length the
 * server validates against.
 */
object WorkshopConstants {

    // ------------------------------------------------------------------ field limits
    /**
     * How long each searchable code is.
     *
     * Both a کد ملی and a شماره بیمه are ten digits — which is also what the design prints as
     * their placeholder — and the search is capped there rather than sending the service a value
     * it will only reject.
     */
    const val NATIONAL_ID_LENGTH = 10
    const val INSURANCE_NUMBER_LENGTH = 10

    /** The service's own cap on the کد کارگاه and کد شعبه the list is searched by. */
    const val WORKSHOP_CODE_MAX_LENGTH = 20

    // ---------------------------------------------------------------------- paging
    /**
     * How many card-shaped blocks stand in for the list until the first page lands.
     *
     * Four is roughly a screenful, so nothing jumps when the real rows replace them.
     */
    const val SKELETON_ROWS = 4

    /**
     * How many rows from the end the next page is asked for.
     *
     * Two, so the list grows while the last rows are still below the fold rather than after the
     * user has already hit the bottom and waited.
     */
    const val LOAD_MORE_THRESHOLD = 2

    // ------------------------------------------------------------------- list slots
    /**
     * Stable identities for the list's two non-row slots.
     *
     * They are keyed so that adding a page does not make Compose discard and rebuild the header,
     * which would drop the search panel's focus mid-type.
     */
    const val HEADER_KEY = "workshop-list-header"
    const val FOOTER_KEY = "workshop-list-footer"
}
