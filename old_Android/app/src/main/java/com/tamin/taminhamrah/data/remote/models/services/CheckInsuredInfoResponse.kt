package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class CheckInsuredInfoResponse(
    var data: Date? = null
) : BaseResponseNew()

data class Date(
    var total: Int = 0,
    var list: List<String>? = null,
    var typeUser: String? = EnumTypeUser.ANONYMOUS.title,
) {

    fun getUserType(): String {

        typeUser = when {
            list == null -> EnumTypeUser.TEMPORARY.title // Handle null explicitly first
            list!!.isEmpty() -> EnumTypeUser.INSURED.title // Handle empty list
            list!!.first() == "05" -> EnumTypeUser.PENSIONER.title
            else -> EnumTypeUser.INSURED.title
        }
        return typeUser!!
    }


}

enum class EnumTypeUser(val id: Int, val title: String) {
    PENSIONER(id = 1, "PENSIONER"),
    INSURED(id = 2, "INSURED"),
    ANONYMOUS(id = 4, "ANONYMOUS"),
    TEMPORARY(id = 5, "temporary")
}

