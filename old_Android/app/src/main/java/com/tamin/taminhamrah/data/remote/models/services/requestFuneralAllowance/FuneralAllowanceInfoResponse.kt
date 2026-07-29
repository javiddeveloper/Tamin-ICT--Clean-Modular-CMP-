package com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

class FuneralAllowanceResponse(val data: FuneralAllowanceModel? = null) : BaseResponseNew()

data class FuneralAllowanceModel(
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val branchWorkshop: List<BranchWorkshop>? = null,
    val consequential: Any? = null,
    val flag: Boolean = false,
    val genderCode: String? = null,
    val insuranceFirstName: String? = null,
    val insuranceLastName: String? = null,
    val insuranceStatus: Any? = null,
    val insuranceStatusDesc: Any? = null,
    val insuranceType: Any? = null,
    val insuranceTypeDesc: Any? = null,
    val mobilNumber: String? = null,
    val nationalCode: String? = null,
    val partnerNationalId: String? = null,
    val payDocNo: Any? = null,
    val payment: Any? = null,
    val request: RequestFuneral? = null,
    val requestFileList: List<Any>? = null,
    val requestFileList1: Any? = null,
    val requestHelpType: String? = null,
    val requestHelpTypeDesc: String? = null,
    val requestedBrchName: Any? = null,
    val resultMessage: String? = null,
    val risuid: String? = null,
    val serviceDate: Long? = null,
    val serviceDateTimeStamp: Any? = null,
    val shorttemRequestId: String? = null,
    val stringDocFiles: Any? = null,
    val weddingTimestamp: Long? = null,
    val workshopCode: String? = null,
    val workshopName: Any? = null,
) {
    fun getPersianDate(timeStamp: Long): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp)
    }

    fun getUserInfo() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.full_name,
            _value = "${insuranceFirstName ?: "_"} ${insuranceLastName ?: "_"}"),
        KeyValueModel(_keyStringResId = R.string.insurance_num, _value = risuid ?: "_"),
        KeyValueModel(_keyStringResId = R.string.account_number, _value = bankAccount ?: "_"),
        KeyValueModel(_keyStringResId = R.string.bank_name, _value = bankName ?: "_"),
        KeyValueModel(_keyStringResId = R.string.mobile, _value = mobilNumber ?: "_"),
        KeyValueModel(_keyStringResId = R.string.last_branch, _value = branchName ?: "_"))

    fun getRequestInfo() = InsuranceInfoModel(
        branchCode = branchCode ?: "",
        branchName = branchName,
        insuranceFirstName = insuranceFirstName,
        insuranceLastName = insuranceLastName,
        mobilNumber = mobilNumber,
        nationalCode = nationalCode,
        risuid = risuid,
        requestHelpType = "07")

    fun getRegisterRequestInfo() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.national_id_deceased,
            _value = partnerNationalId ?: "_"),
        KeyValueModel(_keyStringResId = R.string.death_date,
            _value = if (weddingTimestamp != null) getPersianDate(weddingTimestamp) else "_", _textColor = EnumTextColor.AMBER),
        KeyValueModel(_keyStringResId = R.string.request_date , _value = if (request?.requestDate != null) getPersianDate(request.requestDate) else "_"),
        KeyValueModel(_keyStringResId = R.string.request_status , _value =request?.statusName?: "_", _textColor = EnumTextColor.RED)
    )

}

data class RequestFuneral(
    val brchCode: String? = null,
    val editDate: Long? = null,
    val editUser: String? = null,
    val id: Long? = null,
    val refrenceCode: String? = null,
    val requestDate: Long? = null,
    val requestType: RequestType? = null,
    val status: String? = null,
    val statusId: String? = null,
    val statusName: String? = null,
    val systemType: String? = null,
    val userId: String? = null,
)

data class RequestType(
    val form: String? = null,
    val requestTypeCode: String? = null,
    val requestTypeDesc: String? = null,
    val systemId: String? = null,
)

data class BranchWorkshop(
    val branchCode: String? = null,
    val branchName: String? = null,
    val workshopCode: String? = null,
    val workshopName: String? = null,
)

