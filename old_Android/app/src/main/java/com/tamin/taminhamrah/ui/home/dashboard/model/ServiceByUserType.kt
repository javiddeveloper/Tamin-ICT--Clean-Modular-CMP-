package com.tamin.taminhamrah.ui.home.dashboard.model

import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.PensionIdModel
import com.tamin.taminhamrah.data.remote.models.services.ServiceItem

class ServiceByUserType(
    var serviceItem: ServiceItem? = null,
    var typeUser: String? = EnumTypeUser.ANONYMOUS.title
) {

    fun getUserType(list: List<PensionIdModel>?) {
        typeUser = if (list?.isNotEmpty() == true)
            EnumTypeUser.PENSIONER.title
        else
            EnumTypeUser.INSURED.title
    }
}

enum class EnumTypeUser {
    PENSIONER, INSURED, ANONYMOUS
}
