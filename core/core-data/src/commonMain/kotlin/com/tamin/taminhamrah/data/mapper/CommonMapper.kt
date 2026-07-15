package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleDTO

fun JobTitleDTO.toDomain(): JobTitleDN {
    return JobTitleDN(
        jobCode = jobCode ?: "",
        jobDescription = jobDescription ?: "",
        status = status ?: "",
        statusDate = statusDate ?: ""
    )
}
