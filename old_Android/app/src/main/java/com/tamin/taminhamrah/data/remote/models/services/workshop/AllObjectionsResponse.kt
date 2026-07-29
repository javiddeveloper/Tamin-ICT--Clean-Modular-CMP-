package com.tamin.taminhamrah.data.remote.models.services.workshop

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class AllObjectionsResponse : ListDataModel<WorkShopObjection>()

@Parcelize
data class WorkShopObjection(
    val seqNo: Long? = null,
    val debitNumber: String? = null,
    val workshopId: String? = null,
    val peymanSequence: @RawValue Any? = null,
    val objectionType: String? = null,
    val objectionDate: String? = null,
    val objectionDesc: String? = null,
    val defectDesc: @RawValue Any? = null,
    val defectFlag: @RawValue Any? = null,
    val createUserId: String? = null,
    val createDate: @RawValue Any? = null,
    val confirmDate: @RawValue Any? = null,
    val type1: String? = null,
    val type2: String? = null,
    val type3: String? = null,
    val type4: String? = null,
    val type5: String? = null,
    val type6: String? = null,
    val type7: String? = null,
    val type8: String? = null,
    val type9: String? = null,
    val type10: String? = null,
    val type11: String? = null,
    val type12: String? = null,
    val type13: String? = null,
    val type14: String? = null,
    val type15: String? = null,
    val type16: String? = null,
    val type17: String? = null,
    val type18: String? = null,
    val type19: String? = null,
    val branchCode: String? = null,
    val status: String? = null,
    val debitStepCode: String? = null,
    val debitStatCode: String? = null,
    val badviNo: @RawValue Any? = null,
    val badviDate: @RawValue Any? = null,
    val orderNumber: @RawValue Any? = null,
    val identifier: @RawValue Any? = null,
    val seporde: String? = null,
    val refId: String? = null,
    val payment: @RawValue Any? = null,
    val heyatDate: @RawValue Any? = null,
    val heyatTime: @RawValue Any? = null,
    val voteType: @RawValue VoteType? = null,
    val objectionPhotos: @RawValue Any? = null,
    val confirmUserId: @RawValue Any? = null,
    val action: @RawValue Any? = null
) : Parcelable {


    fun createKeyValueMain(list: List<WorkShopObjection>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValueMain(it)
        }

        return keyValueList
    }

    fun createKeyValueMain(item: WorkShopObjection): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("کد کارگاه", item.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("شماره بدهی", item.debitNumber ?: "-"))
        keyValueList.add(KeyValueModel("شماره اعتراض", item.seqNo?.toString() ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ اعتراض", Utility.getDateSeparator(item.objectionDate)))

        return keyValueList
    }
    fun createKeyValueChild(item: WorkShopObjection): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شرح اعتراض", item.objectionDesc ?: "-"))
        keyValueList.add(KeyValueModel("نوع رای ", item.voteType?.voteTypeDesc ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "وضعیت درخواست",
                setStatusTranslator(item.status),
                _textColor =gridStatusTypeColor(item.status)
            )
        )
        keyValueList.add(KeyValueModel("نوع درخواست", gridObjectionTypeTranslator(item.objectionType)))
        return keyValueList
    }

    data class VoteType(
        var voteTypeCode: String? = null,
        var voteTypeDesc: String? = null
    )


    fun setStatusTranslator(item: String?): String {
        return when (item) {
            "1" -> "ثبت درخواست"
            "2" -> "بازنگري محاسبات"
            "3" -> "طرح در هيئت"
            "4" -> "تجدید محاسبه شده"
            "5" -> "تخصیص زمان"
            "6" -> "تایید رای"
            else -> "نامشخص";
        }
    }

    fun gridStatusTypeColor(item: String?): EnumTextColor {
        return when (item) {
            "1" -> EnumTextColor.NORMAL
            "2" -> EnumTextColor.RED
            "3" -> EnumTextColor.BLUE
            "4" -> EnumTextColor.AMBER
            else -> EnumTextColor.NORMAL
        }
    }

    fun gridObjectionTypeTranslator(item: String?): String {
        return when (item) {
            "1" -> "اعتراض به بدهی برآوردی"
            "2" -> "اعتراض به رای هیئت بدوی"
            "3" -> "درخواست رسیدگی به بدهی قطعی موضوع ماده 16 آیین نامه هیئت ها"
            else -> "-"
        }
    }
}