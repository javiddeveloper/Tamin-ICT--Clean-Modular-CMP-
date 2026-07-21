package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class PregnancyStatusResponse :ListDataModel<PregnancyStatusModel>()

        data class PregnancyStatusModel(
            val code: String?,
            val name: String?
        )

