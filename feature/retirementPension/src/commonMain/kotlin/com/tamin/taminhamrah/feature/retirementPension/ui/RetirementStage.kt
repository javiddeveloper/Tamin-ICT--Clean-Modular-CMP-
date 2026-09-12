package com.tamin.taminhamrah.feature.retirementPension.ui

/**
 * The eight stages the track screen lists, and the server status code that puts a request on each.
 *
 * The codes are the legacy app's `Constants` (`RETIREMENT_*_STATUS`) — four-digit, unordered, and
 * not derivable from anything, which is why they live in one table beside the stage they name
 * rather than spread through a `when`.
 *
 * Two stages carry no code of their own: [Authentication] is where a request that does not exist
 * yet sits, and [SubmitInfo] is passed through the moment the form is posted, so the server never
 * reports it.
 *
 * One correction against the legacy app: it handles `0015` by moving to
 * `STEP_UPLOAD_RESIGNATION_DOC` while showing "please upload your identity documents". The message
 * is right and the step is a copy-paste slip, so `0015` maps to [UploadIdentityDocuments] here.
 */
enum class RetirementStage(val statusCode: String?) {
    Authentication(null),
    SubmitInfo(null),
    UploadIdentityDocuments("0015"),
    BranchReviewNeeded("0017"),
    BranchHistoryReview("0045"),
    UploadQuitLetter("0046"),
    BranchQuitLetterReview("0048"),
    IssueEdict("0047"),
    ;

    companion object {
        /** The code that means the edict is issued — past the last stage, not on it. */
        const val ISSUED_STATUS_CODE = "0018"

        /**
         * Zero-based index of the stage a request with this status code is sitting on.
         *
         * An unknown or absent code means the request has not started, so nothing is marked done —
         * better than the mock's hardcoded index 4, which claims progress the server never
         * reported. An issued edict returns the last index, with every earlier stage complete.
         */
        fun indexOf(statusCode: String?): Int {
            if (statusCode.isNullOrBlank()) return Authentication.ordinal
            if (statusCode == ISSUED_STATUS_CODE) return IssueEdict.ordinal
            return entries
                .indexOfFirst { it.statusCode == statusCode }
                .takeIf { it >= 0 }
                ?: Authentication.ordinal
        }

        /** True once the branch has issued the edict and there is nothing left to wait for. */
        fun isComplete(statusCode: String?): Boolean = statusCode == ISSUED_STATUS_CODE
    }
}
