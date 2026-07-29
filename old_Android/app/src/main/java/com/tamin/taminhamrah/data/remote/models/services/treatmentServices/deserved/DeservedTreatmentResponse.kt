package com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved


import android.content.Context
import android.graphics.Bitmap
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.ui.treatment.model.TreatmentCardDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.UiUtils
import saman.zamani.persiandate.PersianDate

class DeservedTreatmentResponse : ListDataModel<DeservedTreatment>()

data class DeservedTreatment(
    var birthDate: String? = null,
    var brhCode: String? = null,
    var brhName: String? = null,
    var dependenceType: String? = null,
    var fatherName: String? = null,
    var feranshiz: String? = null,
    var firstName: String? = null,
    var frisuid: Any? = null,
    var gender: String? = null,
    var healthBookletDate: Long = 0,
    var id: Int? = null,
    var idNumber: String? = null,
    var insuranceType: String? = null,
    var lastBookletDate: String? = null,
    var lastName: String? = null,
    var natCode: String? = null,
    var nationalId: String? = null,
    var parentFirstName: Any? = null,
    var parentFisuid: Any? = null,
    var parentLastName: Any? = null,
    var parentRisuid: String? = null,
    var parentnatCode: Any? = null,
    var pensionerId: Any? = null,
    var provinceCode: String? = null,
    var provinceName: String? = null,
    var regWorkshopId: String? = null,
    var regWorkshopName: String? = null,
    var risuid: String? = null,
    var revokeCode: Any? = null,
    var revokeDate: Any? = null,
    var revokeStatus: Any? = null,
    var message: String? = null,
    var illness: String? = null,
    var trackingCode: String? = null
) {

    fun createKeyValue(): List<KeyValueModel> {

        val string1 = "مجوز برخورداری از حمایت های درمانی تأمین اجتماعی را دارا"
        val string2 = "در تاریخ "
        val string3 = "می باشید"
        val string4 = "نمی باشید"
        val today = PersianDate()
        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام و نام خانوادگی", "$firstName $lastName"))
        keyValueList.add(KeyValueModel("کد ملی", natCode ?: "-"))
        keyValueList.add(KeyValueModel("شماره بیمه", risuid ?: "-"))
        keyValueList.add(KeyValueModel("شعبه بیمه پردازی", brhName ?: "-"))
        keyValueList.add(KeyValueModel("کد شعبه", brhCode ?: "-"))
        keyValueList.add(KeyValueModel("نوع بیمه", insuranceType ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "کد پیگیری",
                trackingCode ?: ""

            )
        )//check status by tracking code
        keyValueList.add(
            KeyValueModel(
                "وضعیت اعتبار",
                _value = if (trackingCode.isNullOrBlank()) "$string2 $today $string1 $string4" else "$string2 $today $string1 $string3",
                _textColor = if (trackingCode.isNullOrBlank()) EnumTextColor.RED else EnumTextColor.GREEN,
                _isValueBold = true,
            _isKeyBold = true
            )
        )



        return keyValueList
    }

    fun isDeserved(): Boolean {
        val currentDateTimeStamp =
            ConvertDate.convertTimestampToPersianDate(System.currentTimeMillis()).replace("/", "")
        return healthBookletDate >= currentDateTimeStamp.toLong()
    }

    fun getFullName(): String {

        return "${firstName ?: "-"} ${lastName ?: "-"}"
    }
    fun treatmentCardsInfo(qrCode:Bitmap?,context: Context) =
        TreatmentCardDataModel(
            name = getFullName(),
            nationalCode = natCode,
            insuranceNumber = risuid,
            isTreatmentSupport = isDeserved(),
            qrCodeFilePath = UiUtils.setQrCodeBitmap(qrCode, context),
        )


}

fun DeservedTreatment.createKeyValueAI(): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
    val keyValueList = mutableListOf<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel>()

    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نام و نام خانوادگی", "$firstName $lastName"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("کد ملی", natCode ?: "-"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("شماره بیمه", risuid ?: "-"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("شعبه بیمه پردازی", brhName ?: "-"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("کد شعبه", brhCode ?: "-"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نوع بیمه", insuranceType ?: "-"))
    keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("کد پیگیری", trackingCode ?: ""))

    val eligibilityMessage = if (isDeserved()) {
        "از حمایت درمان برخوردار می باشید"
    } else {
        "شما از حمایت درمان برخوردار نمی باشید"
    }

    keyValueList.add(
        com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
            "وضعیت استحقاق درمان",
            eligibilityMessage
        )
    )

    if (revokeStatus != "" && revokeStatus != null && revokeStatus != "-") {
        keyValueList.add(
            com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel(
                "وضعیت ابطال",
                "$revokeStatus (تاریخ: ${revokeDate ?: "-"})"
            )
        )
    }

    return keyValueList
}
