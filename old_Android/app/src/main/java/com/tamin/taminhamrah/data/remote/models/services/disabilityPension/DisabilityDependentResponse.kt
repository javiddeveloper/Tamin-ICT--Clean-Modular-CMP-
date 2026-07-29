package com.tamin.taminhamrah.data.remote.models.services.disabilityPension

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class DisabilityDependentResponse : ListDataModel<DisabilityDependentModel>()

data class DisabilityDependentModel(
    @SerializedName("relationWithTamin")
    val relationWithTamin: DependentInfo = DependentInfo(),
)

data class DependentInfo(
    val personal: Personal = Personal(),
    @SerializedName("relationWithTamin")
    val tendencyInfo: TendencyInfo? = TendencyInfo(),
){
    fun getPersonalInfo() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.father_name , _value = personal.fatherName?: "_"),
        KeyValueModel(_keyStringResId = R.string.national_code , _value = personal.nationalId?: "_"),
        KeyValueModel(_keyStringResId = R.string.birthdate , _value = if (personal.dateOfBirth != 0L) ConvertDate.convertTimestampToPersianDate(personal.dateOfBirth) else "_"),
        KeyValueModel(_keyStringResId = R.string.relation_type , _value = tendencyInfo?.baseTendency?.tendencyDescription ?: "_"),
    )
}

data class Personal(
    val firstName: String ? = null,
    val lastName: String ? = null,
    val nationalId: String? = null,
    val dateOfBirth: Long = 0,
    val fatherName: String? = null,
    val gender: Gender = Gender(),
    @Ignore
    var relation :String? = null
)

data class Gender(
    val genderCode: String? = null,
    val genderDesc: String? = null,
)

data class TendencyInfo(
    val baseTendency: BaseTendency? = null,
)

data class BaseTendency(
    val tendencyCode: String? = null,
    val tendencyDescription: String? = null,
)