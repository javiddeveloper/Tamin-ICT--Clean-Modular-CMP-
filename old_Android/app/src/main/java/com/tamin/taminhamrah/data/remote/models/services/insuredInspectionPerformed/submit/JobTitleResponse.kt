package com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class JobTitleResponse : ListDataModel<JobTitleModel>()

data class JobTitleModel (
    val jobCode: String? = null,
    val jobDescription: String? = null,
    val status: String? = null,
    val statusDate: String? = null
)