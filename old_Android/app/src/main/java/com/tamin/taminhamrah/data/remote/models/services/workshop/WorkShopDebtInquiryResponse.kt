package com.tamin.taminhamrah.data.remote.models.services.workshop


import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.Utility

data class WorkShopDebtInquiryResponse(
    var data: WorkShopDebtInquiry? = null
) : BaseResponseNew()


data class WorkShopDebtInquiry(
    @SerializedName("amount1")
    val definitiveDebt: String? = null,
    @SerializedName("amount2")
    val divisibleDebt: String? = null,
    @SerializedName("amount3")
    val inDivisibleDebt: String? = null,
    val branchCode: Int? = null,
    val result: String? = null,
    val sDate: String? = null,
    val workshopId: String? = null,
    val workshopName: String? = null
) {

    fun createKeyValue(list: List<WorkShopDebtInquiryResponse>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        repeat(list.size) {
            keyValueList.addAll(createKeyValue())
        }

        return keyValueList
    }

    fun createKeyValue(): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(
            KeyValueModel(
                "وضعیت بدهی", result ?: "-",
                _textColor = EnumTextColor.NORMAL,
                _isKeyBold = true,
                _isValueBold = true
            )
        )
        keyValueList.add(KeyValueModel("تاریخ", sDate ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "بدهی قطعی",
                Utility.getRialWithSeparator(definitiveDebt?.toLong())
            )
        )
        keyValueList.add(
            KeyValueModel(
                "قابل تقسیط",
                Utility.getRialWithSeparator(divisibleDebt?.toLong())
            )
        )
        keyValueList.add(
            KeyValueModel(
                "غیر قابل تقسیط",
                Utility.getRialWithSeparator(inDivisibleDebt?.toLong())
            )
        )

        return keyValueList
    }
}