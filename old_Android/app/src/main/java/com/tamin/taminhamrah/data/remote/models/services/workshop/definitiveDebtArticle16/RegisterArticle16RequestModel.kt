package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16


import com.google.gson.annotations.SerializedName

data class RegisterArticle16RequestModel(
    @SerializedName("bikariAmount")
    val indebtednessAmount: Int? = null,
    @SerializedName("bimehAmount")
    val insuranceAmount: Int? = null,
    val branchCode: String? = null,
    val debitEndDate: String? = null,
    val debitNumber: String? = null,
    val debitStartDate: String? = null,
    val debitStepCode: String? = null,
    @SerializedName("eblaghDate")
    val dateExecutiveNotification: String? = null,
    @SerializedName("jarimehAmount")
    val fineAmount: Int? = null,
    val kindDoc: String? = null,
    @SerializedName("mastCustomerTypeCode")
    val customerTypeCode: String? = null,
    val objectionDate: String? = null,
    val objectionPhotos: List<ObjectionPhoto?>? = null,
    val objectionType: String? = null,
    val orderDate: String? = null,
    val orderNumber: String? = null,
    @SerializedName("peymanSequence")
    val agreementRow: String? = null,
    @SerializedName("sayerAmount")
    val otherAmount: Int? = null,
    val workshopId: String? = null
)

data class ObjectionPhoto(
    val guid: String? = null,
    val type: String? = null
)