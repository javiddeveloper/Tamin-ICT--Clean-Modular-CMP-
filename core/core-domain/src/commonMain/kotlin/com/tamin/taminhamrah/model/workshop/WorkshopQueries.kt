package com.tamin.taminhamrah.model.workshop

/** Every workshop list is served ten rows at a time; the service caps nothing higher. */
const val WORKSHOP_PAGE_SIZE = 10

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

/**
 * What one page of a ردیف پیمان list is asked for.
 *
 * The workshop and branch are *path segments* on both contract-row endpoints, not filter entries —
 * which is why this carries no filter fields at all, unlike every other query here.
 */
data class ContractRowQuery(
    val workshopId: String,
    val branchCode: String,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/**
 * What one page of the واگذارندگان list is asked for.
 *
 * All three are **filter clauses**, not path segments — the opposite of [ContractRowQuery]. A blank
 * one is left out of the filter array and widens the result rather than addressing another route,
 * which is why only [workshopId] is required, and required by the *design* rather than by the
 * service: an employer with many پیمان would otherwise open the screen onto an unbounded list.
 */
data class AssignerContractQuery(
    val workshopId: String,
    val branchCode: String? = null,
    val contractRow: String? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)

/**
 * What one page of a پیمان's مبانی محاسباتی is asked for.
 *
 * Four keys, all non-null, and all four are needed: the service treats a missing one as "no
 * filter on that column" and answers with bases belonging to other contracts of the same workshop.
 * The old app reads all four off the tapped row — workshop and branch from the **پیمانکار** side —
 * and so does [com.tamin.taminhamrah.model.workshop.AssignerContractDN].
 */
data class ComputationalBaseQuery(
    val workshopId: String,
    val branchCode: String,
    val contractRow: String,
    val contractSequence: String,
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
 *
 * **Search fields**: despite the names, [insuranceNumber] maps to `workshopId.id` (a person
 * registration ID) and [nationalId] maps to `workshopId.nationalId` on the wire. The old client
 * sent these under `insurance.*`, which the endpoint silently ignored — stakeholder search was
 * always unfiltered. The names are kept for compatibility with [PersonSearch], which both this
 * list and کارکنان share.
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

/**
 * Filters of the پیگیری وضعیت اعتراض search sheet. All optional, all matched exactly.
 *
 * Unlike every other list on this repository, this one has no required workshop/branch identity —
 * it is reached from the services menu, not from a picked workshop row, and shows every objection
 * the employer has filed across all of their workshops.
 */
data class WorkShopObjectionQuery(
    val workshopId: String? = null,
    /** شمارهٔ اعتراض — filtered on `seqNo`, see [com.tamin.taminhamrah.model.request.FilterProperty.SEQ_NO]. */
    val objectionNumber: String? = null,
    val debitNumber: String? = null,
    val page: Int = 0,
    val pageSize: Int = WORKSHOP_PAGE_SIZE,
)
