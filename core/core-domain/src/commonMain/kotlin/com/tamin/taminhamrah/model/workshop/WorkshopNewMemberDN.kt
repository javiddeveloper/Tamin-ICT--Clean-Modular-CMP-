package com.tamin.taminhamrah.model.workshop

/**
 * One نام نویسی غیر حضوری بیمه شده row.
 *
 * [request] is null while the registration is still a draft, and that single fact decides which
 * actions the row offers — confirm/edit/delete on a draft, follow-up once submitted.
 */
data class WorkshopNewMemberDN(
    val relationId: Long? = null,
    val personalId: Long? = null,
    val insuranceNumber: String = "",
    val nationalId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long? = null,
    /** Carried so a draft can be re-opened with the cities it was filed under. */
    val cityOfBirthId: String? = null,
    val cityOfIssueId: String? = null,
    val startDate: Long? = null,
    val job: String = "",
    val request: NewMemberRequestDN? = null,
) {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    /**
     * A draft can still be confirmed, edited or deleted; a submitted request can only be followed.
     * The service marks the difference by whether the request carries a status code yet.
     */
    val isDraft: Boolean get() = request?.statusCode.isNullOrBlank()

    /** A draft is only actually confirmable once the service has given its request an id. */
    val canConfirm: Boolean get() = isDraft && request?.requestId != null
}

data class NewMemberRequestDN(
    val requestId: Long? = null,
    val creationTime: Long? = null,
    val referenceCode: String = "",
    val statusCode: String = "",
    val statusDescription: String = "",
)

/**
 * The seven registration states the search sheet filters by, in the order it lists them.
 *
 * The codes are the service's, sent as `personal.request.status.requestCode`.
 */
enum class NewMemberRequestStatus(val code: String) {
    /** در انتظار تایید */
    AWAITING_CONFIRMATION("0000"),

    /** ثبت درخواست */
    SUBMITTED("0004"),

    /** درخواست نامعتبر */
    INVALID("0006"),

    /** درحال بررسی - نیاز به رسیدگی شعبه */
    NEEDS_BRANCH_REVIEW("0017"),

    /** درحال بررسی */
    UNDER_REVIEW("0005"),

    /** مختومه - تایید نهایی */
    CLOSED_APPROVED("0018"),

    /** مختومه - عدم تایید */
    CLOSED_REJECTED("0019"),
    ;

    companion object {
        fun fromCode(code: String?): NewMemberRequestStatus? = entries.firstOrNull { it.code == code }
    }
}
