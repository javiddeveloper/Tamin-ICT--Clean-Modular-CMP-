package com.tamin.taminhamrah.model.userRequest

enum class UserRequestViewCapability {
    NONE,
    VIEW_DETAILS,
    FOLLOW_UP_OBJECTION,
}

/**
 * List-card action rules ported from `MyRequestAdapter.bindItem()` in my-tamin-droid.
 * Status codes are compared as integers so `"0021"` and `"21"` match.
 */
object UserRequestListPolicy {

    fun viewCapability(requestTypeId: Long, statusCode: String): UserRequestViewCapability {
        if (requestTypeId == UserRequestTypeIds.FOLLOW_UP_OBJECTION) {
            return UserRequestViewCapability.FOLLOW_UP_OBJECTION
        }
        val status = statusCode.toIntOrNull()
        val canView = when (requestTypeId) {
            UserRequestTypeIds.ILL_DAY -> status == UserRequestWorkflowStatus.DOCUMENT_DEFECT.code
            UserRequestTypeIds.PREGNANCY -> status == UserRequestWorkflowStatus.AWAITING_COMPLETION.code
            UserRequestTypeIds.ORTHOTICS_PROSTHESIS ->
                status == UserRequestWorkflowStatus.DOCUMENT_DEFECT.code ||
                    status == UserRequestWorkflowStatus.DISAPPROVED.code
            UserRequestTypeIds.ARTICLE16 -> status == UserRequestWorkflowStatus.ARTICLE16_APPROVED.code
            UserRequestTypeIds.DEFERRED_INSTALLMENT -> status == UserRequestWorkflowStatus.FINAL_APPROVED.code
            UserRequestTypeIds.MEDICAL_COMMISSION -> false
            else -> false
        }
        return if (canView) UserRequestViewCapability.VIEW_DETAILS else UserRequestViewCapability.NONE
    }

    fun canShowErrors(requestTypeId: Long, statusCode: String): Boolean {
        if (requestTypeId == UserRequestTypeIds.MEDICAL_COMMISSION ||
            requestTypeId == UserRequestTypeIds.FOLLOW_UP_OBJECTION
        ) {
            return false
        }
        val status = statusCode.toIntOrNull()
        return when (requestTypeId) {
            UserRequestTypeIds.OTHER_REQUEST -> status == UserRequestWorkflowStatus.SHOW_ERRORS.code
            UserRequestTypeIds.DOCUMENT_CHECK_9,
            UserRequestTypeIds.DOCUMENT_CHECK_19,
            -> status == UserRequestWorkflowStatus.DISAPPROVED.code
            else -> false
        }
    }
}
