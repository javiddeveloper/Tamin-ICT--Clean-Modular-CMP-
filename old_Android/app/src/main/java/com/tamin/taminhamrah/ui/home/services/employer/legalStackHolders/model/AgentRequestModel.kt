package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders.model

data class AgentRequestModel(

    var accessCode: String? = null,
    var branchCode: String? = null,
    var nationalCode: String? = null,
    var ticket: String? = null,
    var workshopId: String? = null,
    var contractRows : ArrayList<String?>?=null,
    var special:Boolean?=null
)
