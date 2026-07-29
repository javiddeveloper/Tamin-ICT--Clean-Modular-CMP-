package com.tamin.taminhamrah.data.remote.models.services.edict

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.Constants.BRANCH_TITLE
import com.tamin.taminhamrah.Constants.IMPLEMENT_DATE
import com.tamin.taminhamrah.Constants.PAYABLE_MONTHLY
import com.tamin.taminhamrah.Constants.SERVICE_TITLE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.utils.Utility.getNumberWithSeparatorForStringValue
import com.tamin.taminhamrah.utils.Utility.getRialWithSeparator
import com.tamin.taminhamrah.utils.convertStrToLong
import com.tamin.taminhamrah.utils.extentions.isNumericString
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel as AiKeyModel

class EdictPensionerResponse(val data: EdictPensionerResModel? = null) : BaseResponseNew()
data class EdictPensionerResModel(
    var lastName: String? = null,
    @SerializedName("bazmandeValues")
    val survivorInfo: List<SurvivorInfo>? = null,
    val branchName: String? = null,
    val detail: List<Detail>? = null,
    @SerializedName("hokmValue")
    val edictInfo: EdictInfo? = null,
    val insuranceId: String? = null,
    val pensionerId: Any? = null,
    val pensionerName: Any? = null,
    val title: String? = null,
    @Ignore
    var edictYear: String = "0",
    @Ignore
    var edictMonth: String = "0"
) {


    fun createKeyValue(): MutableList<AiKeyModel> {
        val keyModelList: MutableList<AiKeyModel> = mutableListOf()
        keyModelList.add(
            AiKeyModel(
                _key = "اساس برقراری",
                _value = edictInfo?.basisImplementation ?: "-"
            )
        )
        keyModelList.add(
            AiKeyModel(
                _key = "تاریخ برقراری",
                _value = edictInfo?.pensionStartDate ?: "0"
            )
        )
        keyModelList.add(
            AiKeyModel(
                _key = "سابقه اصلی",
                _value = edictInfo?.originalHistoryYear ?: "0"
            )
        )
        keyModelList.add(
            AiKeyModel(
                _key = "سابقه ارفاقی",
                _value = edictInfo?.additionalYear ?: "0"
            )
        )
        keyModelList.add(
            AiKeyModel(
                _key = "مجموع مستمری و متناسب سازی مراحل اول و دوم قبل از افزایش",
                _value = getRialWithSeparator(
                    convertStrToLong(
                        edictInfo?.totalPensionBeforeIncrease ?: "0"
                    )
                )
            )
        )
        detail?.forEach { detailItem ->
            if (detailItem.packageName?.contains("تاریخ اجرا") == true) {
                keyModelList.add(
                    AiKeyModel(
                        _key = "تاریخ اجرا",
                        _value = fixDate(detailItem.packageName) ?: ""
                    )
                )
                keyModelList.add(
                    AiKeyModel(
                        _key = detailItem.fieldDesc ?: "-",
                        _value = getRialWithSeparator(
                            convertStrToLong(detailItem.fieldValue ?: "0")
                        )
                    )
                )
            }


        }
        return keyModelList
    }

    fun fixDate(text: String): String? {
        val parts = text.split(":")
        if (parts.size != 2) return null

        val dateStr = parts[1].trim()

        val dateParts = dateStr.split("/")
        if (dateParts.size != 3) return null

        val day = dateParts[0].replace(":" ,"" )
        val month = dateParts[1].replace(":" ,"" )
        val year = dateParts[2].replace(":" ,"" )

        if (day.all { it.isDigit() } && month.all { it.isDigit() } && year.all { it.isDigit() }) {
            val newDateFormatted = "$year/$month/$day"
            return newDateFormatted
        }

        return null
    }

    data class Detail(
        val fieldDesc: String? = null,
        val fieldValue: String? = "0",
        val index: String? = null,
        val packageName: String? = null,
    )

    data class EdictInfo(
        val pensionerId: String? = null,
        val nationalCode: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val fatherName: String? = null,
        val birthDate: String? = null,
        val idNumber: String? = null,
        val gender: String? = null,
        @SerializedName("isuType")
        val insuranceType: String? = null,
        @SerializedName("canDateInd")
        val pensionStartDate: String? = null,
        @SerializedName("hisasal")
        val originalHistoryYear: String? = "0",
        @SerializedName("hisamon")
        val originalHistoryMonth: String? = "0",
        @SerializedName("hisaday")
        val originalHistoryDay: String? = "0",
        @SerializedName("hisyere")
        val additionalYear: String? = "0",
        @SerializedName("hismnte")
        val additionalMonth: String? = "0",
        @SerializedName("hisdaye")
        val additionalDay: String? = "0",
        @SerializedName("bexdesc")
        val basisImplementation: String? = null,
        @SerializedName("amt20l")
        val pensionBeforeIncrease: String? = null,
        @SerializedName("amt20")
        val pensionAfterIncrease: String? = null,
        @SerializedName("hokmDesc")
        val edictDescription: String? = null,
        val id: String? = null,
        @SerializedName("mostMot99")
        val firstStageTotalPensionAndProportional: String? = null,
        @SerializedName("mostMot00")
        val totalPensionBeforeIncrease: String? = null,
        @SerializedName("mot99")
        val firstStageTotalProportional: String? = null,
        @SerializedName("sumPay")
        val totalAmount: String? = null,
        @SerializedName("sumPay2")
        val payableMonthly: String? = null,
        @SerializedName("vstramt")
        val lettersPayableMonthly: String? = null,
    )

    data class SurvivorInfo(
        @SerializedName("amt20")
        val pensionAfterIncrease: String? = null,
        @SerializedName("amt20l")
        val previousPension: String? = null,
        @SerializedName("hisaday")
        val originalHistoryDay: String? = null,
        @SerializedName("hisamon")
        val originalHistoryMonth: String? = null,
        @SerializedName("hisasal")
        val originalHistoryYear: String? = null,
        @SerializedName("hisyere")
        val leniencyYear: String? = null,
        @SerializedName("hismnte")
        val leniencyMonth: String? = null,
        @SerializedName("hisdaye")
        val leniencyDay: String? = null,
        @SerializedName("hokmDesc")
        val edictDescription: String? = null,
        val id: String? = null,
        @SerializedName("isuType")
        val insuranceType: String? = null,
        val lastName: String? = null,
        val firstName: String? = null,
        @SerializedName("mostMot99")
        val firstStageTotalPensionAndProportional: String? = null,
        @SerializedName("mot99")
        val firstStageTotalProportional: String? = null,
        @SerializedName("amt33")
        val differenceProportionalityBasedHistory: String? = null,
        val nationalCode: String? = null,
        val pensionerId: String? = null,
        val quota: String = "0",
        @SerializedName("sumPay")
        val totalAmount: String? = null,
    )

    fun getToolbarInfoList(): List<KeyValueModel> {
        val itemList = ArrayList<KeyValueModel>()

        itemList.add(
            KeyValueModel(
                _key = SERVICE_TITLE,
                _valueStringResId = R.string.edict_service_title
            )
        )
        itemList.add(
            KeyValueModel(
                _key = BRANCH_TITLE,
                _value = branchName ?: ""
            )
        )

        detail?.forEach { item ->
            val packageName = item.packageName ?: ""
            if (packageName.contains("تاریخ اجرا")) {
                itemList.add(
                    KeyValueModel(
                        _key = IMPLEMENT_DATE,
                        _value = "$edictMonth ماه $edictYear"
                    )
                )
            }
        }

        itemList.add(
            KeyValueModel(
                _key = PAYABLE_MONTHLY,
                _value = getNumberWithSeparatorForStringValue(
                    edictInfo?.payableMonthly
                        ?: if (survivorInfo?.isNotEmpty() == true) survivorInfo[0].totalAmount
                            ?: "0" else ""
                )
            )
        )

        return itemList
    }

    fun getEdictInfoList(): ArrayList<KeyValueModel> {
        var itemList = ArrayList<KeyValueModel>()

        if (edictInfo?.id != null) {
            val totalBeforeIncrease = edictInfo.totalPensionBeforeIncrease
            itemList = arrayListOf(
                KeyValueModel(
                    _keyStringResId = R.string.basis_implementation,
                    _value = edictInfo.basisImplementation ?: ""
                ),
                KeyValueModel(
                    _keyStringResId = R.string.date_establishment,
                    _value = edictInfo.pensionStartDate ?: "0"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.org_history,
                    _value = "${edictInfo.originalHistoryYear ?: "0"}-${edictInfo.originalHistoryMonth ?: "0"}-${edictInfo.originalHistoryDay ?: "0"} "
                ),
                KeyValueModel(
                    _keyStringResId = R.string.additional_history,
                    _value = "${edictInfo.additionalYear ?: 0}-${edictInfo.additionalMonth ?: 0}-${edictInfo.additionalDay ?: 0} "
                )
            )
            val intYear = edictYear.toIntOrNull()
            if (intYear != null && intYear > 1399) {
                itemList.add(
                    KeyValueModel(
                        _keyStringResId = R.string.title_total_pension_before_increase,
                        _value = getRialWithSeparator(convertStrToLong(totalBeforeIncrease ?: "0"))
                    )
                )
            } else {
                itemList.add(
                    KeyValueModel(
                        _keyStringResId = R.string.before_increase_pension,
                        _value = getRialWithSeparator(
                            convertStrToLong(
                                edictInfo.pensionBeforeIncrease ?: "0"
                            )
                        )
                    )
                )

                itemList.add(
                    KeyValueModel(
                        _keyStringResId = R.string.first_stage_total_proportional,
                        _value = getRialWithSeparator(
                            convertStrToLong(
                                edictInfo.firstStageTotalProportional ?: "0"
                            )
                        )
                    )
                )

                itemList.add(
                    KeyValueModel(
                        _keyStringResId = R.string.edict_total_pension_first_stage,
                        _value = getRialWithSeparator(
                            convertStrToLong(
                                edictInfo.firstStageTotalPensionAndProportional ?: "0"
                            )
                        ),
                        _textColor = EnumTextColor.BLUE,
                        _isValueBold = true
                    )
                )
            }

        } else {
            survivorInfo?.forEach { item ->
                itemList = arrayListOf(
                    KeyValueModel(
                        _keyStringResId = R.string.full_name,
                        _value = "${item.firstName ?: "_"} ${item.lastName ?: "_"}"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.quota,
                        _value = "${item.quota}%"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.previous_pension,
                        _value = getRialWithSeparator(if (item.previousPension?.isNumericString() == true) item.previousPension.toLong() else 0L)
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.pension_after_increase,
                        _value = getRialWithSeparator(if (item.pensionAfterIncrease?.isNumericString() == true) item.pensionAfterIncrease.toLong() else 0L)
                    )
                )
            }
        }
        return itemList

    }

    fun getEdictTypeInfo(): MutableMap<String, ArrayList<KeyValueModel>> {
        val edictType = mutableMapOf<String, ArrayList<KeyValueModel>>()

        val infoEdict = detail ?: emptyList()
        for (i in infoEdict.indices) {
            if (infoEdict[i].fieldValue.equals("0"))
                continue
            var amount = 0L

            val orgAmount = infoEdict[i].fieldValue ?: ""

            if (orgAmount.isNotBlank() && orgAmount.isNumericString()) {
                amount = orgAmount.toLong()
            }
            if (infoEdict[i].index == "1") {
                val detail = KeyValueModel(
                    _key = infoEdict[i].fieldDesc ?: "_",
                    _value = getRialWithSeparator(amount),
                    _type = infoEdict[i].packageName ?: ""
                )
                infoEdict[i].packageName?.let { title ->
                    if (!edictType.containsKey(title)) {
                        edictType[title] = arrayListOf(detail)
                    } else {
                        edictType[title]?.add(detail)
                    }
                }
            }
        }

        return edictType
    }

    fun getDescriptionsEdict(): MutableMap<String, ArrayList<KeyValueModel>> {

        val descEdict = mutableMapOf<String, ArrayList<KeyValueModel>>()

        val infoEdict = detail ?: emptyList()
        for (i in infoEdict.indices) {
            if (infoEdict[i].fieldValue.equals("0"))
                continue
            var amount = 0L
            var orgAmount = infoEdict[i].fieldValue ?: ""

            if (orgAmount.isNotBlank() && orgAmount.isNumericString()) {
                amount = orgAmount.toLong()
            }

            if (infoEdict[i].index != "1") {

                val detail = KeyValueModel(
                    _key = infoEdict[i].fieldDesc ?: "",
                    _value = getRialWithSeparator(amount),
                    _type = infoEdict[i].packageName ?: "",
                    _textColor = if (infoEdict[i].index == "11" || i == infoEdict.size - 1) EnumTextColor.GREEN else EnumTextColor.NORMAL // 11=> sum
                )

                infoEdict[i].packageName?.let { title ->
                    if (!descEdict.containsKey(title)) {
                        descEdict[title] = arrayListOf(detail)
                    } else {
                        descEdict[title]?.add(detail)
                    }
                }
            }
        }

        return descEdict
    }

}
