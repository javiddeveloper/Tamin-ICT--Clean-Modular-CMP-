package com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class FamilyRelationShipResponse : ListDataModel<FamilyRelationShipModel>()

data class FamilyRelationShipModel(
    @SerializedName("dependencyCode")
    val relationCode: String?,
    @SerializedName("dependencyDesc")
    val relationDesc: String?,
    val id: Int?,
    @SerializedName("reasonCode")
    val bailCode: String?
)
