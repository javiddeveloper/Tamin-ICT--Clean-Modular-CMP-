package com.tamin.taminhamrah.data.remote.models.electronicFile.myElectronicFile

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class ElectronicFileResponse :ListDataModel<ElectronicFileModel>()

data class ElectronicFileModel(

    val addUser: Any? = null,
    val categoryName: String? = null,
    val contentServer: String? = null,
    val countNumger: Int? = null,
    val docDate: Any? = null,
    val filter: Any? = null,
    val id: String? = null,
    val isStagnant: Any? = null,
    val name: String? = null,
    val recType: Any? = null,
    val rowNumber: Int? = null,
    val thumb: String? = null,
    val type: String? = null,
)