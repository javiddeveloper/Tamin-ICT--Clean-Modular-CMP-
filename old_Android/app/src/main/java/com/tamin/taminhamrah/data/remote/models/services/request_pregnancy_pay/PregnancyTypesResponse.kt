package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class PregnancyTypesResponse : ListDataModel<PregnancyTypesModel>()

    data class PregnancyTypesModel(
        val code: String?,
        val name: String?
    )
