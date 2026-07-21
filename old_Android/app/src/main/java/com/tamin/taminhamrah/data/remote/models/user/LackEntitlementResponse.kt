package com.tamin.taminhamrah.data.remote.models.user

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.treatment.model.TreatmentCardDataModel


class LackEntitlementResponse : ListDataModel<LackEntitlement>()

data class LackEntitlement(
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("brhCode") val brhCode: String?,
    @SerializedName("brhName") val brhName: String?,
    @SerializedName("dependenceType") val dependenceType: String?,
    @SerializedName("fatherName") val fatherName: String?,
    @SerializedName("feranshiz") val feranshiz: String?,
    @SerializedName("finalDesc") val finalDesc: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("frisuid") val frisuid: Any?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("healthBookletDate") val healthBookletDate: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("idNumber") val idNumber: String?,
    @SerializedName("illness") val illness: String?,
    @SerializedName("insuranceType") val insuranceType: String?,
    @SerializedName("lastBookletDate") val lastBookletDate: Any?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("natCode") val natCode: String?,
    @SerializedName("nationalId") val nationalId: String?,
    @SerializedName("parentFirstName") val parentFirstName: String?,
    @SerializedName("parentFisuid") val parentFisuid: Any?,
    @SerializedName("parentLastName") val parentLastName: Any?,
    @SerializedName("parentRisuid") val parentRisuid: String?,
    @SerializedName("parentnatCode") val parentnatCode: Any?,
    @SerializedName("pensionerId") val pensionerId: Any?,
    @SerializedName("provinceCode") val provinceCode: String?,
    @SerializedName("provinceName") val provinceName: String?,
    @SerializedName("regWorkshopId") val regWorkshopId: Any?,
    @SerializedName("regWorkshopName") val regWorkshopName: String?,
    @SerializedName("revokeCode") val revokeCode: Any?,
    @SerializedName("revokeDate") val revokeDate: Any?,
    @SerializedName("revokeStatus") val revokeStatus: Any?,
    @SerializedName("risuid") val risuid: String?,
    @SerializedName("trackingCode") val trackingCode: String?
) {

    fun isDeserved(): Boolean = finalDesc.isNullOrBlank() || finalDesc == "null"
    fun fullName() = "${firstName ?: "-"} ${lastName ?: "-"}"

    fun LackEntitlement.toTreatmentCardDataModel(qrCodeFilePath: String?): TreatmentCardDataModel {
        return  TreatmentCardDataModel(
            name = fullName(),
            nationalCode = natCode,
            insuranceNumber = risuid,
            isTreatmentSupport = isDeserved(),
            qrCodeFilePath =qrCodeFilePath,
            treatmentSupportDescription = finalDesc

        )

    }
    fun createAiKeyValue(): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", "$firstName $lastName"))
        keyValueList.add(KeyValueModel("کد ملی", "$natCode"))
        keyValueList.add(KeyValueModel("شماره بیمه", risuid ?: "-"))
        keyValueList.add(KeyValueModel("شعبه بیمه پردازی", brhName ?: "-"))
        keyValueList.add(KeyValueModel("کد شعبه", brhCode ?: "-"))
        keyValueList.add(KeyValueModel("نوع بیمه", insuranceType ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "کد پیگیری",
                trackingCode ?: ""

            )
        )
        keyValueList.add(
            KeyValueModel(
                "استحقاق درمان",
                if (finalDesc.isNullOrBlank())"از حمایت درمان برخوردار میباشید" else finalDesc

            )
        )
        return keyValueList
    }
}

fun LackEntitlement.toTreatmentCardDataModel(qrCodeFilePath: String?): TreatmentCardDataModel {
    return TreatmentCardDataModel(
        name = fullName(),
        nationalCode = natCode,
        insuranceNumber = risuid,
        isTreatmentSupport = isDeserved(),
        qrCodeFilePath = qrCodeFilePath,
        treatmentSupportDescription = finalDesc,
        message = message

    )

}
