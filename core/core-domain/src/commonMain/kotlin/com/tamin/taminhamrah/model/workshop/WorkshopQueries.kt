package com.tamin.taminhamrah.model.workshop

/** Every workshop list is served ten rows at a time; the service caps nothing higher. */
const val WORKSHOP_PAGE_SIZE = 10

/**
 * Single-window size for workshop lists that don't have real paging wired yet: ask for one wide page
 * and render whatever comes back, the same `limit = 100` shortcut other single-shot list queries in
 * the app take (`ContractsRepositoryImpl.contractListQuery`, `InspectionRepositoryImpl`).
 */
const val WORKSHOP_FULL_PAGE_SIZE = 100

/**
 * What one page of a workshop list is asked for.
 *
 * Search and filter are two fields of one query rather than two calls: the old client sent
 * whichever it had just been given and silently dropped the other, so a status filter always
 * discarded an active code search.
 */
data class WorkshopListQuery(
    val workshopId: String? = null,
    val branchCode: String? = null,
    val status: WorkshopActivityStatus? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/** Filters of the برگ پرداخت‌ها search sheet. All optional, all matched exactly. */
data class PaymentSheetQuery(
    val workshopId: String,
    val branchCode: String,
    val payIdFrom: String? = null,
    val payIdTo: String? = null,
    /** Epoch millis, as the date pickers produce them. */
    val docDateFrom: Long? = null,
    val docDateTo: Long? = null,
    val debitReasonCode: String? = null,
    val status: PaymentSheetStatus? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/** Filters of the کارکنان search sheet. */
data class WorkshopMemberQuery(
    val workshopId: String,
    val branchCode: String,
    val insuranceNumber: String? = null,
    val nationalId: String? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/**
 * Filters of the ذینفعان list.
 *
 * The stakeholder service nests the workshop one level deeper (`workshopId.workshopId`) than every
 * other workshop list; that difference lives in the repository, not here.
 */
data class WorkshopStackHolderQuery(
    val workshopId: String,
    val branchCode: String,
    val insuranceNumber: String? = null,
    val nationalId: String? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/**
 * Filters of the نام نویسی غیر حضوری list.
 *
 * The branch travels as `organizationId` here — the one list that spells it differently. Passing
 * it through a typed field is what stops the old client's silent mismatch (it looked the branch up
 * under a key the caller never wrote, so the screen simply never made a request).
 */
data class WorkshopNewMemberQuery(
    val workshopId: String,
    val branchCode: String,
    val nationalId: String? = null,
    val requestStatus: NewMemberRequestStatus? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/** Filters of the ماده ۱۶ debt list search panel. */
data class ArticleSixteenDebtQuery(
    val workshopId: String,
    val branchCode: String,
    val debitNumber: String? = null,
    val agreementRow: String? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)
