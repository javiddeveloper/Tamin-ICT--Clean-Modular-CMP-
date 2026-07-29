package com.tamin.taminhamrah.data.entity

import com.tamin.taminhamrah.data.remote.models.services.inquirePensionStatus.InquirePensionStatusModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel

data class PensionInquiryModel(
    var branchCode: String? = null,
    var insuranceNumber: String? = null,
    var pensionerRisuid: String? = null,
    var pensionerType: String? = null,
    var paymentDate: String? = null,
    var pensionerBaseDate: String? = null,
    var fullName: String? = null,
    var statusDesc: String? = null,
    var sexDesc: String? = null,
    var branchName: String? = null,
    var pensionEndDate: String? = null,
    var nationalId: String? = null,
    var paymentAmount: Int? = null
) {
    companion object {
        fun createKeyValue(list: List<InquirePensionStatusModel>): List<KeyValueModel> {

            val keyValueList: MutableList<KeyValueModel> = mutableListOf()
            list.forEach {
                keyValueList.add(KeyValueModel("نام واحد سازمانی", it.branchName ?: "-"))
                keyValueList.add(KeyValueModel("نام و نام خانوادگی", it.fullName ?: "-"))
                keyValueList.add(KeyValueModel("شماره مستمری", it.pensionerRisuid ?: "-"))
                keyValueList.add(KeyValueModel("شماره بیمه", it.insuranceNumber ?: "-"))
                keyValueList.add(KeyValueModel("کد ملی", it.nationalId ?: "-"))
                keyValueList.add(KeyValueModel("نوع حکم", it.pensionerType ?: "-"))
                keyValueList.add(KeyValueModel("تاریخ پرداخت", it.paymentDate ?: "-"))
                keyValueList.add(KeyValueModel("تاریخ برقراری مستمری", it.pensionerBaseDate ?: "-"))
                keyValueList.add(KeyValueModel("وضعیت", it.statusDesc ?: "-"))
                keyValueList.add(KeyValueModel("کد سازمان", it.brchCode ?: "-"))
                keyValueList.add(KeyValueModel("جنسیت", it.sexDesc ?: "-"))
                keyValueList.add(KeyValueModel("تاریخ پایان مستمری", it.pensionEndDate ?: "-"))
                keyValueList.add(KeyValueModel("میزان پرداختی", it.paymentAmount.toString()))
            }

            return keyValueList
        }
    }

}




