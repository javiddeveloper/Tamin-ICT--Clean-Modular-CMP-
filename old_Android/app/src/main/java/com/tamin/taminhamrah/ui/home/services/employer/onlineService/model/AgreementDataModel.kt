package com.tamin.taminhamrah.ui.home.services.employer.onlineService.model

import com.google.gson.annotations.SerializedName

data class AgreementDataModel(
    @SerializedName("mobileNo")
    var mobileNew: String? = null,
    @SerializedName("email")
    var emailNew: String? = null,
    @SerializedName("ticketCode")
    var verificationCode: String? = null,
){

    @Transient
    var mobilePrev:String?=null
    @Transient
    var emailPrev:String?=null
    @Transient
    var fullName:String?=null
    @Transient
    var nationalCode:String?=null

}