package com.tamin.taminhamrah.data.remote.models.employer

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class LetterSubjectResponse : ListDataModel<LetterSubject>()

data class LetterSubject(
    var code: Long? = null,
    var contract: String? = null,
    var createdBy: Any? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var lastModifiedBy: Any? = null,
    var status: String? = null,
    var subjectCode: Int? = null,
    var subjectDesc: String? = null,

    )
