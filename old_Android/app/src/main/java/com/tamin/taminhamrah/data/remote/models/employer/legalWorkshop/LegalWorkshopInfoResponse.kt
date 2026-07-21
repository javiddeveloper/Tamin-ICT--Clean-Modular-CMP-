package com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class LegalWorkshopInfoResponse(
    var data: LegalWorkshopInfo? = null,
) : BaseResponseNew()

data class LegalWorkshopInfo(
    var id: String? = null,
    var address: String? = null,
    var bankRuptcyDate: Any? = null,
    var branchList: Any? = null,
    var breakUpDate: Any? = null,
    var establishmentDate: String? = null,
    var followUpNo: String? = null,
    var isBankRupt:Boolean? = false,
    var isBranch:Boolean? = false,
    var isBreakUp:Boolean? = false,
    var isSettle:Boolean? = false,
    var lastChangeDate: String? = null,
    var legalPersonType: String? = null,
    var message: Any? = null,
    var name: String? = null,
    var nationalCode: String? = null,
    var parentLegalPerson: Any? = null,
    var postCode: String? = null,
    var registerDate: String? = null,
    var registerNumber: String? = null,
    var registerUnit: Any? = null,
    var residency: Any? = null,
    var settleDate: Any? = null,
    var state: String? = null,
    var successful1:Boolean? = false,
    var unitId: Any? = null,
    var newService:Boolean? = false
)
