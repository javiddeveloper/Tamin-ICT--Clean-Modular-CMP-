package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contracts.SaveContactPersonalDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO

internal fun SaveContactRequestDN.toDto(): SaveContactRequestDTO =
    SaveContactRequestDTO(
        address = address,
        mobile = mobile,
        personal = SaveContactPersonalDTO(ssn = ssn),
        phoneNumber = phoneNumber,
        zipCode = zipCode,
    )
