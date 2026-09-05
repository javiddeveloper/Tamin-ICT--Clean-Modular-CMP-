package com.tamin.taminhamrah.model.userRequest

/**
 * Integer `requestCode` values from the list API, as used by
 * `MyRequestListResponse.getColor()` in my-tamin-droid.
 */
enum class UserRequestWorkflowStatus(val code: Int) {
    PRE_PROCESSING(2),
    SHOW_ERRORS(6),
    BRANCH_DELIVERED(9),
    AWAITING_COMPLETION(14),
    PROCESSING_COMPLETE(16),
    FINAL_APPROVED(18),
    DISAPPROVED(19),
    DOCUMENT_DEFECT(21),
    ARTICLE_SIXTEEN_APPROVED(2602);

    val tabCategory: UserRequestTabCategory
        get() = when (this) {
            AWAITING_COMPLETION,
            DISAPPROVED,
            DOCUMENT_DEFECT,
            -> UserRequestTabCategory.ACTION_REQUIRED

            PROCESSING_COMPLETE,
            FINAL_APPROVED,
            ARTICLE_SIXTEEN_APPROVED,
            -> UserRequestTabCategory.COMPLETED

            else -> UserRequestTabCategory.IN_PROGRESS
        }

    companion object {
        fun fromCode(statusCode: String): UserRequestWorkflowStatus? {
            val parsed = statusCode.toIntOrNull() ?: return null
            return entries.find { it.code == parsed }
        }

        fun tabCategory(statusCode: String): UserRequestTabCategory =
            fromCode(statusCode)?.tabCategory ?: UserRequestTabCategory.IN_PROGRESS
    }
}

enum class UserRequestTabCategory {
    IN_PROGRESS,
    ACTION_REQUIRED,
    COMPLETED,
}
