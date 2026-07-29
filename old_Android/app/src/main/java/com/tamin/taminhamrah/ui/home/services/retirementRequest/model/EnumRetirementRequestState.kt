package com.tamin.taminhamrah.ui.home.services.retirementRequest.model

enum class EnumRetirementRequestState constructor(var index: Int) {
    STEP_AUTHENTICATION(0),
    STEP_SUBMIT_INFO( 1),
    STEP_UPLOAD_IDENTITY_DOC( 2),
    STEP_CHECK_BRANCH( 3),
    STEP_CHECK_HISTORY_BY_BRANCH( 4),
    STEP_UPLOAD_RESIGNATION_DOC( 5),
    STEP_RESIGNATION_DOC_CHECK_BY_BRANCH( 6),
    STEP_ISSUANCE_EDICT( 7)
}