package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel


class TitlesJobResponse : ListDataModel<TitlesJobModel>()

data class TitlesJobModel(
    var id: String? = null,
    var rwshId: String? = null,
    var rwshName: String? = null,
    val startDate: String? = null,
    var branchCode: String? = null,
    var jobDesc: String? = null,
    var risuid: String? = null
) {

    fun workStartDate() = if (startDate != null && startDate.length >= 6) "${
        startDate.take(4)
    }/${startDate.substring(4, 6)}" else ""


    fun createKeyValue(): List<KeyValueModel> {
        val keyValueList = mutableListOf<KeyValueModel>()
        jobDesc?.let {keyValueList.add(KeyValueModel("عنوان شغل", it))}
        keyValueList.add(KeyValueModel("تاریخ شروع", workStartDate() ))
        KeyValueModel("----------------", "", null)
        return keyValueList
    }

}
