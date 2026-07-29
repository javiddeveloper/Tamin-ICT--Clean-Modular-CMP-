package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import kotlinx.parcelize.Parcelize

class SurvivorResponse : ListDataModel<SurvivorModel>()
@Parcelize
data class SurvivorModel(
    @SerializedName("relationWithTamin")
    val userInfo: UserInfo = UserInfo(),
) : Parcelable {

    @Parcelize
    data class UserInfo(
        var personal: Personal?=Personal() ,
        val insuranceId:String = "",
        @SerializedName("relationWithTamin")
        val relation: Relation = Relation(),
        @Ignore
        var isCompleted: Boolean = false,
        @Ignore
        val dependentDocumentList : ArrayList<UploadedImageModel> = arrayListOf()
    ): Parcelable {
        fun getSurvivorIdentityInfo() =
            arrayListOf(KeyValueModel(_keyStringResId = R.string.full_name, _value = "${personal?.firstName} ${personal?.lastName}"),
                KeyValueModel(_keyStringResId = R.string.national_code, _value = personal?.nationalId?:"_"),
                KeyValueModel(_keyStringResId = R.string.father_name, _value = personal?.fatherName?:"_"),
                KeyValueModel(_keyStringResId = R.string.identity_number, _value = personal?.idCardNumber?:"_"),
                KeyValueModel(_keyStringResId = R.string.relationship_with_main_insured, _value = personal?.relationShip?.ifBlank { "_" } ?:"_" ),
                KeyValueModel(_keyStringResId = R.string.birthdate, _value = ConvertDate.convertTimestampToPersianDate(personal?.dateOfBirth?:0)),
                KeyValueModel(_keyStringResId = R.string.age, _value = personal?.age.toString()),
                KeyValueModel(_keyStringResId = R.string.issue_city, _value = personal?.cityOfIssue?:"")
            )
    }

    @Parcelize
    data class Personal(
        val firstName: String?="" ,
        val lastName: String? ="",
        val nationalId: String?="",
        val fatherName: String?="",
        val idCardNumber: String?="",
        val cityOfIssue: String?="",
        val gender: Gender = Gender(),
        val dateOfBirth: Long = 0L,
        @Ignore
        var relationShip :String ="",
        @Ignore
        var age : Int = 0,
        @Ignore
        var branchCode : String = "",
        @Ignore
        var deceasedInsuranceId : String ="",
        @Ignore
        var deceasedNationalID : String = ""
    ):Parcelable

    @Parcelize
    data class Relation(
        @SerializedName("baseTendency")
        val tendency: BaseTendency? = BaseTendency(),
    ):Parcelable

    @Parcelize
    data class BaseTendency(
        val tendencyCode: String? = null,
    ):Parcelable

    @Parcelize
    data class Gender(
        val genderCode: String? = null,
        val genderDesc: String? = null,
    ):Parcelable

}