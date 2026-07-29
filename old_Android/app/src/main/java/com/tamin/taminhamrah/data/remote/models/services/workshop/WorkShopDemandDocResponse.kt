package com.tamin.taminhamrah.data.remote.models.services.workshop


import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class WorkShopDemandDocResponse : ListDataModel<WorkShopDemandDoc>()

data class WorkShopDemandDoc(
    @SerializedName("claimSequens")
    val claimSequence: String? = null,
    @SerializedName("claimdescription")
    val claimDescription: String? = null,
    val debitNumber: String? = null,
    @SerializedName("debitcrtreasoncode")
    val debitCreationCode: Int? = null,

    @SerializedName("debitstatedisc")
    val debitStateDesc: String? = null,
    @SerializedName("debitcrtreasondesc")
    val debitCreationDesc: String? = null,
    val docDate: String? = null,
    val docNumber: String? = null,
    val docTypeCode: String? = null,
    val docTypeDescription: String? = null,
    @SerializedName("debitstepdesc")
    val debitStepDesc: String? = null
) {
    fun createKeyValue(list: List<WorkShopDemandDoc>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValue(it)
        }

        return keyValueList
    }

    fun createKeyValue(item: WorkShopDemandDoc): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(KeyValueModel("شماره سند", item.docNumber ?: "-", _isValueBold = true))
        keyValueList.add(KeyValueModel("تاریخ", Utility.getDateSeparator(item.docDate)))
        keyValueList.add(KeyValueModel("شرح نوع سند", item.docTypeDescription ?: "-"))
        keyValueList.add(KeyValueModel("مرحله", item.debitStepDesc ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت", item.debitStateDesc ?: "-"))

        return keyValueList
    }

    fun hasDocNumber(): Boolean {
        return !docNumber.isNullOrBlank()
    }


}