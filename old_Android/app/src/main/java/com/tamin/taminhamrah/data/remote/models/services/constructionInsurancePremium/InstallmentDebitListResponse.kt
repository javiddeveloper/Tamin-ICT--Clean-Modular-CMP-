package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class InstallmentDebitListResponse : ListDataModel<InstallmentDebitListModel>()

data class InstallmentDebitListModel (
    val workshopId: String? = null,
    val debitNumber: String? = null,
    val debitStepDescription: String? = null,
    val debitStatusDescription: String? = null,
    val debitStartDate: String? = null,
    val debitEndDate: String? = null,
    val remainingAmount: Long? = null){

    fun getDetailInstallmentDebitList() = listOf(
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = (workshopId?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.step , _value = debitStepDescription?:"_"),
        KeyValueModel(_keyStringResId = R.string.label_debit_number , _value = debitNumber?: "_"),
        KeyValueModel(_keyStringResId = R.string.label_start_date , _value =  Utility.getDateSeparator(debitStartDate)),
        KeyValueModel(_keyStringResId = R.string.label_end_date , _value =  Utility.getDateSeparator(debitEndDate)),
        KeyValueModel(_keyStringResId = R.string.label_work_status , _value =  Utility.getDateSeparator(debitStatusDescription)))

}



