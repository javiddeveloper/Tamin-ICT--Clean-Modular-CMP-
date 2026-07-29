package com.tamin.taminhamrah.data.remote.models.showRequestInfo

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class ResultFollowUpObjectionNonExitsResponse : ListDataModel<ResultFollowUpObjectionNonExitsModel>()

data class ResultFollowUpObjectionNonExitsModel(
    @SerializedName("reqno")
    val requestNumber: String? = null,
    @SerializedName("reqtype")
    val requestType: String? = null,
    @SerializedName("brchcode")
    val branchId: String? = null,
    val requestDesc: String? = null,
    val cStatusDesc: String? = null,
    val answerTypeDesc: String? = null,
    val resultDesc: String? = null,
    val userDesc: String? = null,
    val requestDate: String? = null,
    val answerDate: String? = null,
    @SerializedName("brchName")
    val branchName: String? = null,
    ){
    fun getDetailInfo()= listOf(
        KeyValueModel(_keyStringResId = R.string.branch, _value = branchName?:"_"),
        KeyValueModel(_keyStringResId = R.string.request_date, _value = Utility.getDateSeparator(requestDate?.substring(0,8)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.desc_request, _value = requestDesc?:"_"),
        KeyValueModel(_keyStringResId = R.string.user_desc, _value = userDesc?:"_"),
        KeyValueModel(_keyStringResId = R.string.processing_date, _value = Utility.getDateSeparator(answerDate?.substring(0,8)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.result_investigation, _value = answerTypeDesc?:"_", _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.expert_explanation, _value = resultDesc?:"_")
        )
}
