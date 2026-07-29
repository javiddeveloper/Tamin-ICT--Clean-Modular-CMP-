package com.tamin.taminhamrah.data.remote.models.employer.employerAgreement

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class WorkshopInfoWithoutContractResponse : ListDataModel<WorkshopInfoWithoutContract>()

data class WorkshopInfoWithoutContract(
    var workshopName: String? = null,
    var nationalId: String? = null,
    @SerializedName("wokshopId")
    var workshopId: String? = null,
    var branchCode: String? = null,
    var contractRow: Any? = null,
    var organization: Organization? = null,
    var postalCode: String? = null,
    var tel: String? = null,
    var address: String? = null
) {
    fun createKeyValue(list: List<EmployerAgreement>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            keyValueList.add(KeyValueModel("کد کارگاه", it.workshop?.workshopId ?: "-"))
            keyValueList.add(KeyValueModel("نام کارگاه", it.workshop?.workshopName ?: "-"))
//            keyValueList.add(KeyValueModel("فعالیت", it.workshop?.activityName ?: "-"))
            keyValueList.add(KeyValueModel("کد شعبه", it.workshop?.branch?.code ?: "-"))
            keyValueList.add(
                KeyValueModel(
                    "نام شعبه",
                    it.workshop?.branch?.organizationName ?: "-"
                )
            )
            keyValueList.add(KeyValueModel("تاریخ تعهد", it.letDate ?: "-"))
            keyValueList.add(KeyValueModel("پست الکترونیک", it.emailaddr ?: "-"))
            keyValueList.add(KeyValueModel("تلفن همراه", it.mobileno ?: "-"))
            keyValueList.add(KeyValueModel("محل اقامت", it.workshop?.lastAddress ?: "-"))
        }

        return keyValueList
    }

    fun getTitle(title: String?) = title ?: "-"

    fun getLocalDate(date: String?) = Utility.getDateSeparator(date)
}

data class Organization(
    var entityId: String? = null,
    var organizationName: String? = null,
    var code: String? = null,
    var type: String? = null,
    var actKey: Long? = null,
    var parent: Parent? = null,
    var children: Any? = null
)

data class Parent(
    var entityId: String? = null,
    var organizationName: String? = null,
    var code: String? = null,
    var type: String? = null,
    var actKey: Long? = null,
    var parent: Parent? = null,
    var children: Any? = null
)
