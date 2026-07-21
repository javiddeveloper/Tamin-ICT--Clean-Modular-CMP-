package com.tamin.taminhamrah.data.remote.models.services
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.utils.ConvertDate

class ViewShortTermRequestResponse  : ListDataModel<ViewShortTermRequestModel>()
data class ViewShortTermRequestModel(

    var risuid: String? = null,
    var payReciever: String? = null,
    var reqHelptype: String? = null,
    var reqDescStep: String? = null,
    var branchName: String? = null,
    var sDate: String? = null,
    var eDate: String? =  null,
    var kind: String? =  null,
    var docNo: String? =  null,
    var docDate: String? =  null,
    var accountNo: String? =  null,
    var bankName: String? =  null,
    var faniConfDesc: String? =  null,
    var acceptDate: String? =  null,
    var sendDate: String? =  null
    ){
    fun getPersianDate(time:String?):String{
        return time?.let { ConvertDate.getTrueFormateOfDate(it) }?:""
    }
}
fun ViewShortTermRequestModel.createKeyValue(): MutableList<KeyValueModel> {
    val finalList: MutableList<KeyValueModel> = mutableListOf()

    // Using getPersianDate helper from the data class for date fields
    reqHelptype?.let{finalList.add(KeyValueModel("نوع درخواست", it))}
    reqDescStep?.let{finalList.add(KeyValueModel("توضیحات مرحله", it))}
    branchName?.let{finalList.add(KeyValueModel("نام شعبه", it))}

    val persianStartDate = getPersianDate(sDate)
    if (persianStartDate.isNotEmpty()) {
        finalList.add(KeyValueModel("تاریخ شروع", persianStartDate))
    }

    val persianEndDate = getPersianDate(eDate)
    if (persianEndDate.isNotEmpty()) {
        finalList.add(KeyValueModel("تاریخ پایان", persianEndDate))
    }

    kind?.let{finalList.add(KeyValueModel("نوع", it))}
    docNo?.let{finalList.add(KeyValueModel("شماره سند", it))}

    val persianDocDate = getPersianDate(docDate)
    if (persianDocDate.isNotEmpty()) {
        finalList.add(KeyValueModel("تاریخ سند", persianDocDate))
    }

    accountNo?.let{finalList.add(KeyValueModel("شماره حساب", it))}
    bankName?.let{finalList.add(KeyValueModel("نام بانک", it))}
    faniConfDesc?.let{finalList.add(KeyValueModel("توضیحات تایید فنی", it))}

    val persianAcceptDate = getPersianDate(acceptDate)
    if (persianAcceptDate.isNotEmpty()) {
        finalList.add(KeyValueModel("تاریخ تایید", persianAcceptDate))
    }

    val persianSendDate = getPersianDate(sendDate)
    if (persianSendDate.isNotEmpty()) {
        finalList.add(KeyValueModel("تاریخ ارسال", persianSendDate))
    }

    return finalList
}