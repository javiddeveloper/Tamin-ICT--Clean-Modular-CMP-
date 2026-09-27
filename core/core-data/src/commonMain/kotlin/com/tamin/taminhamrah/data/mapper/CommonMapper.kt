package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.JobTitlePageEntity
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.common.UserInsuredInfoDTO
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN

fun JobTitleDTO.toDomain(): JobTitleDN {
    return JobTitleDN(
        jobCode = jobCode ?: "",
        jobDescription = jobDescription ?: "",
        status = status ?: "",
        statusDate = statusDate ?: ""
    )
}

/** [list]'s first entry ("05" = pensioner) is the only signal the server gives for the user's type. */
private const val PENSIONER_INSURANCE_CODE = "05"

fun UserInsuredInfoDTO.toDomain(): UserTypeInfoDN {
    val list = list
    val userType = when {
        list == null -> UserType.TEMPORARY
        list.isEmpty() -> UserType.INSURED
        list.first() == PENSIONER_INSURANCE_CODE -> UserType.PENSIONER
        else -> UserType.INSURED
    }
    return UserTypeInfoDN(userType = userType, message = list?.getOrNull(1))
}

// ---- Offline page cache (job_title_pages) ----

internal fun JobTitleDN.toPageEntity(listKey: String, position: Int) = JobTitlePageEntity(
    listKey = listKey,
    position = position,
    jobCode = jobCode,
    jobDescription = jobDescription,
    status = status,
    statusDate = statusDate,
)

internal fun JobTitlePageEntity.toDomain() = JobTitleDN(
    jobCode = jobCode,
    jobDescription = jobDescription,
    status = status,
    statusDate = statusDate,
)
