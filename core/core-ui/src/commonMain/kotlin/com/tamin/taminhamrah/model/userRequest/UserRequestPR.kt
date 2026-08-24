package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable

enum class UserRequestStatusTone {
    NEUTRAL,
    ERROR,
    APPROVED,
}

enum class UserRequestProgressPhase {
    IN_PROGRESS,
    ERROR,
    COMPLETED,
}

fun userRequestStatusTone(statusCode: String): UserRequestStatusTone {
    return when (UserRequestWorkflowStatus.fromCode(statusCode)) {
        UserRequestWorkflowStatus.SHOW_ERRORS,
        UserRequestWorkflowStatus.DISAPPROVED,
        UserRequestWorkflowStatus.DOCUMENT_DEFECT,
        -> UserRequestStatusTone.ERROR

        UserRequestWorkflowStatus.PROCESSING_COMPLETE,
        UserRequestWorkflowStatus.FINAL_APPROVED,
        UserRequestWorkflowStatus.ARTICLE16_APPROVED,
        -> UserRequestStatusTone.APPROVED

        else -> UserRequestStatusTone.NEUTRAL
    }
}

fun userRequestProgressPhase(statusCode: String): UserRequestProgressPhase {
    return when (UserRequestWorkflowStatus.fromCode(statusCode)) {
        UserRequestWorkflowStatus.SHOW_ERRORS,
        UserRequestWorkflowStatus.DISAPPROVED,
        UserRequestWorkflowStatus.DOCUMENT_DEFECT,
        -> UserRequestProgressPhase.ERROR

        UserRequestWorkflowStatus.PROCESSING_COMPLETE,
        UserRequestWorkflowStatus.FINAL_APPROVED,
        UserRequestWorkflowStatus.ARTICLE16_APPROVED,
        -> UserRequestProgressPhase.COMPLETED

        else -> UserRequestProgressPhase.IN_PROGRESS
    }
}

@Immutable
data class UserRequestPR(
    val id: Long,
    val refCode: String,
    val title: String,
    val comment: String,
    val creationTime: String,
    val createByName: String,
    val statusDesc: String,
    val statusCode: String,
    val requestTypeId: Long,
    val requestTypeTitle: String,
    val requestDetails: String? = null,
    val details: UserRequestDetailsPR? = null,
    val referenceId: String = "",
    val viewCapability: UserRequestViewCapability =
        UserRequestListPolicy.viewCapability(requestTypeId, statusCode),
    val showErrorsAction: Boolean =
        UserRequestListPolicy.canShowErrors(requestTypeId, statusCode),
    val statusTone: UserRequestStatusTone = userRequestStatusTone(statusCode),
    val progressPhase: UserRequestProgressPhase = userRequestProgressPhase(statusCode),
    val tabCategory: UserRequestTabCategory = UserRequestWorkflowStatus.tabCategory(statusCode),
)
