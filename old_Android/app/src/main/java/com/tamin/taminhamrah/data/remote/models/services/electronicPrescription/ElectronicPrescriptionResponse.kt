package com.tamin.taminhamrah.data.remote.models.services.electronicPrescription

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPersonInfo
import com.tamin.taminhamrah.data.repository.ai.model.AIClickType
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.ClickableItemModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.randomUUID


class ElectronicPrescriptionResponse : ListDataModel<ElectronicPrescription>()
data class ElectronicPrescription(
    override val id: String = randomUUID(),
    var clinicdocid: Any? = null,
    var docID: String? = null,
    var docName: String? = null,
    var flagSata: String? = null,
    var location: String? = null,
    var noteHeadEprescID: Long? = null,
    var patientID: String? = null,
    var patientName: String? = null,
    var prescDate: String? = null,
    var prescName: String? = null,
    var specDesc: String? = null,
    var prescType: String? = null,
    var trackingCode: Long? = null,
    @Ignore
    var iconRes: Int? = null
) : AiChatModel() {
    fun getPersianDate(timeStamp: Long?): String {
        return timeStamp?.let { ConvertDate.convertTimestampToPersianDate(timeStamp) } ?: ""
    }

    fun getLocalDate(date: String?) = Utility.getDateSeparator(date)

    var isVisible: Boolean = false

    fun createAiKeyValue(): List<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
        val keyValueList = ArrayList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel>()
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("شماره پیگیری", trackingCode.toString()))
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("تاریخ ویزیت", getLocalDate(prescDate)))
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نام پزشک", docName ?: ""))
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("تخصص", specDesc ?: ""))
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("شماره بیمه", patientID ?:"" ))
        keyValueList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نوع نسخه", prescName ?:"" ))
        return keyValueList
    }
    fun createAiClickableItem(): ClickableItemModel = ClickableItemModel(
        title = createAiMessage(),
        customData = this,
        clickType = AIClickType.Prescription,
    )

    fun createAiMessage(): String {
        val message = StringBuilder()
            .append("● ")
            .append("شماره پیگیری")
            .append(": ")
            .append(trackingCode ?: "-")
            .append("\n")
            .append("● ")
            .append("تاریخ ویزیت")
            .append(": ")
            .append(getLocalDate(prescDate))
            .append("\n")
            .append("● ")
            .append("نام پزشک")
            .append(": ")
            .append(docName ?: "-")
            .append("\n")
            .append("● ")
            .append("تخصص")
            .append(": ")
            .append(specDesc ?: "-")
            .append("\n")
            .append("● ")
            .append("شماره بیمه")
            .append(": ")
            .append(patientID ?: "-")
            .append("\n")
            .append("● ")
            .append("نوع نسخه")
            .append(": ")
            .append(prescName ?: "-")
            .append("\n")
        return message.toString()
    }


}

class ElectronicPrescriptionDetailResponse : ListDataModel<ElectronicPrescriptionDetail>()
data class ElectronicPrescriptionDetail(
    var sumPriceItem: Long? = null,
    var ssoPayment: Long? = null,
    @SerializedName("insuPayment")
    var insurancePayment: Long? = null,
    @SerializedName("srvQty")
    var serviceQuantity: Int? = 0,
    var noteHeadEprescID: Long? = null,
    @SerializedName("wsSrvCode")
    var serverCode: String? = null,
    @SerializedName("userName")
    var serverName: String? = null,
    var serviceName: String? = null,
    var dose: Any? = null,
    var drugInst: String? = null,
    @SerializedName("regdate")
    var registerDate: String? = null,
    var drugInstruction: String? = null,
    var deliveredNo: Int? = 0,
    @SerializedName("drugAmnt")
    var drugAmount: String? = null,
    @Ignore
    var prescriptionType: String? = ""
) {

    var isVisible = false

    fun createKeyValue(item: ElectronicPrescriptionDetail): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

//            keyValueList.add(KeyValueModel("نام خدمت / دارو", it.serviceName ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تعداد تجویزی",
                item.serviceQuantity?.toString() ?: "-"
            )
        )
        keyValueList.add(KeyValueModel("دستور مصرف", item.drugInstruction ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ اقدام", item.registerDate ?: "-"))
        keyValueList.add(KeyValueModel("سهم بیمار", item.ssoPayment?.toString() ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "سهم سازمان",
                item.insurancePayment?.toString() ?: "-"
            )
        )
        keyValueList.add(KeyValueModel("جمع کل", item.sumPriceItem?.toString() ?: "-"))
        keyValueList.add(KeyValueModel("تعداد دریافتی", item.deliveredNo?.toString() ?: "-"))
        keyValueList.add(KeyValueModel("داروخانه/پاراکلینیک", item.serverName ?: "-"))

        return keyValueList
    }
}

class ElectronicPrescriptionPriceResponse : ListDataModel<ElectronicPrescriptionPrice>()
data class ElectronicPrescriptionPrice(
    var headInsuPayment: Long? = null,
    var headSsoPayment: Long? = null,
    var noteHeadEprescID: Long? = null,
    var requestPrice: Long? = null
)

class DependantUserUnder18Response : ListDataModel<RelatedPersonInfo>()


fun ElectronicPrescription.createKeyValue(): MutableList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> {
    val finalList: MutableList<com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel> = mutableListOf()

    patientName?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نام بیمار", it)) }
    trackingCode?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("کد پیگیری", it.toString())) }
    prescDate?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("تاریخ نسخه", getLocalDate(it))) }
    prescName?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نام نسخه", it)) }
    docName?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("نام پزشک", it)) }
    specDesc?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("توضیحات", it)) }
    location?.let { finalList.add(com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel("مکان", it)) }

    return finalList
}