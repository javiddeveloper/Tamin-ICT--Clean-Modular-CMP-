package com.tamin.taminhamrah.data.remote.models.employer.employerAgreement

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.utils.Utility

class EmployerAgreementResponse : ListDataModel<EmployerAgreement>()

data class EmployerAgreement(
    var pymseq: Any? = null,
    var regno: Any? = null,
    var firstname: String? = null,
    var emailaddr: String? = null,
    var workshop: WorkshopInfo? = null,
    var nationalno: Any? = null,
    var mobileno: String? = null,
    var startdate: String? = null,
    var mastcusttype: String? = null,
    var createdt: String? = null,
    var masttyp: String? = null,
    var logicalDeleted: Boolean? = false,
    var regemailseq: String? = null,
    var lastname: String? = null,
    var risuid: Any? = null,
    var nationalcode: String? = null,
    var enddate: Any? = null,
    var letDate: String? = null,
    var regdate: Any? = null,
    var roletype: String? = null,
    var dname: Any? = null,
    var letNo: String? = null,
    var createuid: String? = null,
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

    fun getDetailWorkShop() = listOf(
        KeyValueModel(_keyStringResId = R.string.employer_type , _value = workshop?.character?.characterDesc?: "-", _textColor = EnumTextColor.GREEN),
        KeyValueModel(_keyStringResId = R.string.start_activity_date , _value = Utility.getDateSeparator(startdate), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.label_type_activity , _value = workshop?.workshopType?.workshoptypeDesc?: "-"),
        KeyValueModel(_keyStringResId = R.string.label_branch_code , _value = workshop?.branch?.code ?: "-"),
        KeyValueModel(_keyStringResId = R.string.label_branch_name , _value = workshop?.branch?.organizationName ?: "-"),
        KeyValueModel(_keyStringResId = R.string.register_workshop_date , _value =  Utility.getDateSeparator(workshop?.workshopRegisterDate)),
        KeyValueModel(_keyStringResId = R.string.approve_date_workshop , _value =  Utility.getDateSeparator(workshop?.workshopApproveDate))
    )

    fun getTitle(title: String?) = title ?: ""

    fun getLocalDate(date:String?) = Utility.getDateSeparator(date)
}