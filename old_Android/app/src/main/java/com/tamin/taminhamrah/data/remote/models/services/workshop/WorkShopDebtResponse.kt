package com.tamin.taminhamrah.data.remote.models.services.workshop


import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat


class WorkShopDebtResponse : ListDataModel<WorkShopDebt>()


@Parcelize
data class WorkShopDebt(
    val rowNum: Long? = null,
    val debitNumber: String? = null,
    val orderRecipeDate: String? = null,
    val mastCustomerCode: String? = null,
    val debitAmount: Long? = null,
    val debitRemain: Long? = null,
    val debitStartDate: String? = null,
    val debitEndDate: String? = null,
    val mastCustomerTypeCode: String? = null,
    val peymanSequence: String? = null,
    val debitCreateReasonCode: String? = null,
    val debitCreateReasonDesc: String? = null,
    val debitNumberInstallment: String? = null,
    val mande: String? = null,
    val bimehAmount: String? = null,
    val bikariAmount: String? = null,
    val sayerAmount: String? = null,
    val debitStepCode: String? = null,
    val debitStepDesc: String? = null,
    val debitStatDesc: String? = null,
    val debitStatCode: String? = null,
    val stepCat: String? = null,
    val docDateEblaghEjra: String? = null,
    val badviNo: String? = null,
    val badviDate: String? = null,
    val calculateDate: String? = null,
    val seqNo: Long? = null
) : Parcelable {

    val objectionType2: ObjectionType = ObjectionType.DEFAULT
    var hasPermission: Boolean? = false
    fun createKeyValueMain(list: List<WorkShopDebt>): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        list.forEach {
            createKeyValueMain(it)
        }

        return keyValueList
    }

    fun getExecutiveDate(): String {
        if (docDateEblaghEjra.isNullOrEmpty() || docDateEblaghEjra.length != 8)
            return "-"

        return "${docDateEblaghEjra.substring(0, 4)}/${
            docDateEblaghEjra.substring(
                4,
                6
            )
        }/${docDateEblaghEjra.substring(6, 8)}"
    }

    fun createKeyValueObjectionInfo(item: WorkShopDebt): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
//        keyValueList.add(KeyValueModel("نام و نام خانوادگی کارفرمای حقیقی", name ?: "-"))
//        keyValueList.add(KeyValueModel("آدرس کارگاه", addres ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.peymanSequence ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "دوره بدهی از",
                Utility.getDateSeparator(item.debitStartDate) ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "دوره بدهی تا",
                Utility.getDateSeparator(item.debitEndDate) ?: "-"
            )
        )

        if (item.getObjectionType() == ObjectionType.BADVI) {
            keyValueList.add(KeyValueModel("شماره رای هیات بدوی", badviNo ?: "-"))
            keyValueList.add(
                KeyValueModel(
                    "تاریخ رای هیات بدوی",
                    Utility.getDateSeparator(item.badviDate) ?: "-"
                )
            )
        }
        return keyValueList
    }

    fun getDebtAmount() = "${DecimalFormat("#,###").format(debitAmount)}"
    fun getRemainAmount() = "${DecimalFormat("#,###").format(debitRemain)}"

    fun createKeyValueMain(item: WorkShopDebt): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره بدهی", item.debitNumber ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ ابلاغ",
                Utility.getDateSeparator(item.orderRecipeDate) ?: "-"
            )
        )
        keyValueList.add(KeyValueModel("کد طرف حساب", item.mastCustomerCode ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "مبلغ بدهی",
                Utility.getRialWithSeparator(item.debitAmount),
                _textColor = EnumTextColor.AMBER,
                _isKeyBold = true,
                _isValueBold = true
            )
        )
        return keyValueList
    }

    fun createKeyValueChild(item: WorkShopDebt): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(
            KeyValueModel(
                "مبلغ مانده بدهی",
                Utility.getRialWithSeparator(item.debitRemain)
            )
        )
        keyValueList.add(KeyValueModel("از تاریخ", Utility.getDateSeparator(item.debitStartDate)))
        keyValueList.add(KeyValueModel("تا تاریخ", Utility.getDateSeparator(item.debitEndDate)))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.peymanSequence ?: "-"))

        if (!item.badviNo.isNullOrEmpty()) {
            keyValueList.add(KeyValueModel("شماره رای هیات بدوی", item.badviNo ?: "-"))
            keyValueList.add(
                KeyValueModel(
                    "تاریخ رای هیات بدوی",
                    Utility.getDateSeparator(item.badviDate)
                )
            )
        }
        return keyValueList
    }


    fun createKeyValueObjectionsMain(item: WorkShopDebt): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
//        keyValueList.add(KeyValueModel("کد کارگاه", item.workshopId ?: "-"))
        keyValueList.add(KeyValueModel("شماره بدهی", item.debitNumber ?: "-"))
        keyValueList.add(KeyValueModel("شماره اعتراض", item.seqNo?.toString() ?: "-"))
        /*keyValueList.add(
            KeyValueModel(
                "تاریخ اعتراض",
                Utility.getDateSeparator(item.objectionDate)
            )
        )*/

        return keyValueList
    }


    fun createKeyValueObjectionsChild(item: WorkShopDebt): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
//        keyValueList.add(KeyValueModel("شرح اعتراض", item.objectionDesc ?: "-"))
//        keyValueList.add(KeyValueModel("نوع رای", voteType?.voteTypeDesc ?: ""))
//        keyValueList.add(KeyValueModel("وضعیت درخواست", getObjectionStatus(item.status).methodName))
//        keyValueList.add(KeyValueModel("نوع درخواست", getObjectionTypeString(item.objectionTypeString).methodName))

        return keyValueList
    }


    @JvmName("getObjectionType1")
    fun getObjectionType(): ObjectionType {

        return if (seqNo == null) {
            if (debitStepCode == "01" && debitStatCode == "03") {
                ObjectionType.BARAVORDI
            } else if (debitStepCode == "02" && debitStatCode == "03") {
                ObjectionType.BADVI
            } else {
                ObjectionType.DEFAULT
            }
        } else {
            ObjectionType.DEFAULT
        }
    }


    fun getObjectionTypeString(type: String?): ObjectionType {
        return when (type) {
            "1" -> ObjectionType.BARAVORDI
            "2" -> ObjectionType.BADVI
            "3" -> ObjectionType.MADE_16
            else -> ObjectionType.DEFAULT
        }
    }

    fun getObjectionStatus(type: String?): ObjectionStatus {
        return when (type) {
            "1" -> ObjectionStatus.REGISTER
            "2" -> ObjectionStatus.REVIEW
            "3" -> ObjectionStatus.BOARD
            "4" -> ObjectionStatus.RETRY
            "5" -> ObjectionStatus.TIME
            "6" -> ObjectionStatus.CONFIRM
            else -> ObjectionStatus.DEFAULT

        }
    }

    enum class ObjectionStatus constructor(var methodName: String) {
        REGISTER("ثبت درخواست"),
        REVIEW("بازنگري محاسبات"),
        BOARD("طرح در هيئت"),
        RETRY("تجدید محاسبه شده"),
        TIME("تخصیص زمان"),
        CONFIRM("تایید رای"),
        DEFAULT("نامشخص")
    }

    enum class ObjectionType constructor(var methodName: String) {
        BARAVORDI("اعتراض به بدهی برآوردی"),

        //        BADVI("اعتراض به بدهی بدوی"),
//        BADVI_VOTE("اعتراض به رای هیئت بدوی"),
        BADVI("اعتراض به رای هیئت بدوی"),
        MADE_16("درخواست رسیدگی به بدهی قطعی موضوع ماده 16 آیین نامه هیئت ها"),
        DEFAULT("مشاهده اعتراض")

    }

    @Parcelize
    data class VoteType(val voteTypeDesc: String? = null) : Parcelable
}