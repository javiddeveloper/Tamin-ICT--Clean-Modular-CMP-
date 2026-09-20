package com.tamin.taminhamrah.feature.workshops.ui

/**
 * The non-visual constants the کارگاه‌های کارفرما screens run on.
 *
 * Sibling to `WorkshopDimens`, and deliberately separate from it: those are measurements the
 * design dictates, these are limits the *service* dictates and behavior the list agreed on. A
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

    /**
     * The tighter caps ردیف‌های پیمان holds its two codes to.
     *
     * Shorter than [WORKSHOP_CODE_MAX_LENGTH] on purpose. That is a *search* cap, where an over-long
     * value simply matches nothing; these two are **path segments** on both contract-row endpoints,
     * so a value the wrong length does not narrow the request — it addresses a different route. Ten
     * and four are what the design prints as their placeholders.
     */
    const val CONTRACT_ROW_WORKSHOP_CODE_LENGTH = 10
    const val CONTRACT_ROW_BRANCH_CODE_LENGTH = 4

    /**
     * The caps واگذارندگان holds its three search codes to — 10, 4 and 3, as the design prints
     * them.
     *
     * Declared apart from the contract-row pair above despite two of them matching, because they
     * are caps of a different *kind*: these three are filter clauses, so an over-long value simply
     * matches nothing, where a contract-row code the wrong length addresses another route. Folding
     * the two tables together would put a path-segment rule and a search rule under one name.
     */
    const val ASSIGNER_WORKSHOP_CODE_LENGTH = 10
    const val ASSIGNER_BRANCH_CODE_LENGTH = 4
    const val ASSIGNER_CONTRACT_ROW_LENGTH = 3

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
