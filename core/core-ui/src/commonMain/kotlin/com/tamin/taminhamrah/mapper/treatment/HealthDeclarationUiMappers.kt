package com.tamin.taminhamrah.mapper.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.user.UserProfileDN


fun UserProfileDN.toPresentation() = UserProfilePR(
    entityId = entityId,
    login = login,
    firstName = firstName,
    lastName = lastName,
    email = email,
    nationalCode = nationalCode,
    mobile = mobile
)

fun MedicalAuthoritiesDN.toPresentation() = MedicalAuthoritiesPR(
    supportType = supportType,
    treatmentCenter = treatmentCenter,
    confirmInBranch = confirmInBranch,
    confirmStatus = confirmStatus,
    insuranceNumber = insuranceNumber,
    nationalCode = nationalCode,
    firstName = firstName,
    lastName = lastName,
    outpatientRestStartDate = outpatientRestStartDate,
    outpatientRestEndDate = outpatientRestEndDate,
    numberOfOutpatientDays = numberOfOutpatientDays,
    hospitalizationStartDate = hospitalizationStartDate,
    hospitalizationEndDate = hospitalizationEndDate,
    numberOfHospitalizationDays = numberOfHospitalizationDays,
    description = description,
    branch = branch,
    fromDateNotConfirm = fromDateNotConfirm,
    toDateNotConfirm = toDateNotConfirm
)
