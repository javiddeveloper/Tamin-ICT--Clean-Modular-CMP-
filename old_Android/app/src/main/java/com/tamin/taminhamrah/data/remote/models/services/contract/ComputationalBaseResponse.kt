package com.tamin.taminhamrah.data.remote.models.services.contract

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class ComputationalBaseResponse : ListDataModel<ComputationalBase>()

@Parcelize
class ComputationalBase(
    var contract: Contract? = null,
    var sequenceNumber: String? = null,
    var letno: String? = null,
    var letimage: String? = null,
    var letimage1: String? = null,
    var letimage2: String? = null,
    var letimage3: String? = null,
    var letimage4: String? = null,
    var cntamount: Long? = null,
    var cntamountcurrency: Long? = null,
    var cntamountcurrencyToR: Long? = null,
    var cntamounttotal: Long? = null,
    var subcontractor: String? = null,
    var subcontractorimage: String? = null,
    var subcontractorimage1: String? = null,
    var subcontractorimage2: String? = null,
    var subcontractorimage3: String? = null,
    var subcontractorimage4: String? = null,
    var supplementimage: String? = null,
    var supplementimage1: String? = null,
    var supplementimage2: String? = null,
    var supplementimage3: String? = null,
    var supplementimage4: String? = null,
    var statusreportimage: String? = null,
    var statusreportimage1: String? = null,
    var statusreportimage2: String? = null,
    var statusreportimage3: String? = null,
    var statusreportimage4: String? = null,
    var contractsubjectcode: String? = null,
    var subjectOwner: String? = null,
    var subjecttext1: String? = null,
    var subjecttext2: String? = null,
    var subjectimage: String? = null,
    var subjectamount1: Long? = null,
    var subjectamount2: Long? = null,
    var subjectamount3: Long? = null,
    var subjectamount4: Long? = null,
    var createui: String? = null,
    var createdate: String? = null,
    var confirmui: String? = null,
    var confirmdate: String? = null,
    var natcodecontract: String? = null,
    var natcodeassign: @RawValue Any? = null,
    var status: String? = null,
    var dbtno1: String? = null,
    var dbtno2: String? = null,
    var ordno1: String? = null,
    var ordno2: String? = null,
    var descriptions: String? = null,
    var editui: @RawValue Any? = null,
    var editdate: String? = null,
    var senddate: Long? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var letdate: Long? = null,
    var refCode: String? = null,
    var converted: String? = null,
    var dataDetail: List<DataDetail>? = null,
    var hasLetImage: Boolean? = null

) : Parcelable {

    fun createKeyValue(item: ComputationalBase): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
//        keyValueList.add(KeyValueModel("ردیف پیمان", item.contract?.contractRow ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "مبلغ ناخالص کارکرد",
                Utility.getRialWithSeparator(item.cntamount),_textColor = EnumTextColor.GREEN
            )
        )
        keyValueList.add(
            KeyValueModel(
                "تاریخ ارسال مبانی محاسباتی",
                ConvertDate.convertTimestampToPersianDate(item.senddate ?: 0)
            )
        )
        keyValueList.add(KeyValueModel("شماره برگه پرداخت بدهی قطعی", item.ordno1 ?: "-"))
        keyValueList.add(KeyValueModel("شماره برگه پرداخت بدهی برآوردی", item.ordno2 ?: "-"))
        keyValueList.add(KeyValueModel("وضعیت", statusDescription(), _isValueBold = true ))
        return keyValueList
    }

    fun createKeyValueContractInfo(item: ComputationalBase?): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره قرارداد", item?.contract?.contractNumber ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ قرارداد",
                Utility.getDateSeparator(item?.contract?.contractDate)
            )
        )
        keyValueList.add(KeyValueModel("موضوع قرارداد", item?.contract?.contractSubject ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "نام پیمانکار",
                item?.contract?.workshop?.workshopName ?: "-"
            )
        )
       /* keyValueList.add(
            KeyValueModel(
                "کد کارگاه پیمانکار",
                item?.contract?.workshop?.workshopId ?: "-"
            )
        )*/
//        keyValueList.add(KeyValueModel("ردیف پیمان", item?.contract?.contractRow ?: "-"))
        return keyValueList
    }

    fun createKeyValueLetterInfo(item: ComputationalBase?): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("شماره نامه", item?.letno ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ نامه",
                ConvertDate.convertTimestampToPersianDate(item?.letdate ?: 0)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "تاریخ شروع قرارداد",
                ConvertDate.convertTimestampToPersianDate(item?.startDate ?: 0)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "تاریخ خاتمه عملیات اجرایی پیمان",
                ConvertDate.convertTimestampToPersianDate(item?.endDate ?: 0)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مقاطعه کار جهت اجرای پروژه از پیمانکاران فرعی استفاده نموده است؟",
                if (item?.subcontractor == "0") "خیر" else "بلی"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مبلغ ناخالص کارکرد (قطعی، تعدیل، ما به التفاوت مصالح و...)",
                Utility.getRialWithSeparator(item?.cntamount)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مبلغ ارزی",
                Utility.getRialWithSeparator(item?.cntamountcurrency)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "معادل ریالی مبلغ ارزی",
                Utility.getRialWithSeparator(item?.cntamountcurrencyToR)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "مجموع کل ناخالص کارکرد",
                Utility.getRialWithSeparator(item?.cntamounttotal)
            )
        )
        keyValueList.add(KeyValueModel("توضیحات", item?.descriptions ?: "-"))
        return keyValueList
    }

    fun createKeyValueConstructionContract(item: ComputationalBase?): List<KeyValueModel> {
        val keyValueList = ArrayList<KeyValueModel>()

        when (contractsubjectcode) {
            "01" -> {
                keyValueList.add(
                    KeyValueModel(
                        "انعقاد قرارداد بر اساس فهرست بهاء پایه سازمان برنامه و بودجه (تهیه مصالح به عهده:)",
                        constructorSubject()
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "اعتبار طرح از محل اعتبارات طرح تملک دارایی های سرمایه ای دولت به شماره طرح",
                        item?.subjecttext1 ?: "0"
                    )
                )
                keyValueList.add(KeyValueModel("ردیف بودجه", item?.subjecttext2 ?: "0"))
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ حق بیمه پرداخت شده",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
            }
            "02" -> {
                keyValueList.add(
                    KeyValueModel(
                        "تهیه و تأمین مصالح مصرفی به عهده",
                        constructorSubject()
                    )
                )
                if (item?.subjectOwner == "3")
                    keyValueList.add(
                        KeyValueModel(
                            "ارزش مصالح واگذارنده",
                            Utility.getRialWithSeparator(item.subjectamount1)
                        )
                    )


            }
            "03" -> {
                keyValueList.add(KeyValueModel("درصد مکانیکی", item?.subjecttext1 ?: "-"))
                keyValueList.add(KeyValueModel("درصد دستی", item?.subjecttext2 ?: "-"))
            }
            "29" -> {
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ ارزی تجهیزات خارجی",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "معادل ریالی",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
            }
            "04" -> {
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان در اختیار پیمانکار",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان خود مالک",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
            }
            "05" -> {
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان در اختیار پیمانکار",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان خود مالک",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
            }
            "06" -> {
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان در اختیار پیمانکار",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "مبلغ کارکرد انجام شده توسط رانندگان خود مالک",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
            }
            "07" -> {
                keyValueList.add(
                    KeyValueModel(
                        "هزینه خرید تجهیزات غیر از مواد مصرف",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "هزینه خدمات، اجرا و مصالح مصرفی",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
            }
            "11" -> {
                keyValueList.add(
                    KeyValueModel(
                        "آدرس کارگاه ثابت پیمانکار",
                        item?.contract?.workshop?.lastAddress ?: "-"
                    )
                )
                keyValueList.add(KeyValueModel("شناسه ملی/کد ملی"))
                keyValueList.add(
                    KeyValueModel(
                        "هزینه ساخت",
                        Utility.getRialWithSeparator(item?.subjectamount1)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "هزینه حمل",
                        Utility.getRialWithSeparator(item?.subjectamount2)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "هزینه نصب",
                        Utility.getRialWithSeparator(item?.subjectamount3)
                    )
                )
                keyValueList.add(
                    KeyValueModel(
                        "هزینه اجرا",
                        Utility.getRialWithSeparator(item?.subjectamount4)
                    )
                )
            }
            "13" -> {
                keyValueList.add(
                    KeyValueModel(
                        "عملیات پیمان توسط کارکنان دفتر مرکزی شرکت انجام شده است؟",
                        item?.subjectOwner ?: "-"
                    )
                )
            }
            "" -> {
            }
            "" -> {
            }
        }


        return keyValueList
    }

    private fun constructorSubject(): String {
        return when (contractsubjectcode) {
            "01" -> when (subjectOwner) {
                "1" -> "پیمانکار"
                "2" -> "کارفرما"
                "3" -> "انعقاد قرارداد بر اساس ضوابط تيپ سازمان برنامه و بودجه"
                else -> "-"
            }
            "02" -> when (subjectOwner) {
                "1" -> "مقاطعه کار"
                "2" -> "واگذارنده"
                "3" -> "قسمتي از مصالح توسط پيمانکار و قسمتي توسط واگذارنده کار تامين شده است"
                else -> "-"
            }
            /* "11" -> {
                 this.theForm.get("employerAddress").setValue(this.gSes("employerAddress"))
                 this.theForm.get("employerNationalId").setValue(this.gSes("employerNationalId"))
             }*/
            "13" -> when (subjectOwner) {
                "0" -> "خیر"
                "1" -> "بله"
                else -> "-"
            }
            else -> "-"
        }
    }

    private fun statusDescription(): String {
        return when (status) {
            "01" -> "ارسال پيامک"
            "02" -> "ثبت مباني محاسباتي توسط واگذارنده"
            "03" -> "تاييد و ثبت در اطلاعات تکميلي پيمان"
            "04" -> "عدم تاييد"
            "05" -> "تعيين ضريب"
            "06" -> "محاسبه پيمان"
            "07" -> "صدور فرم يک تبصره الحاقي"
            "08" -> "ابلاغ فرم يک تبصره الحاقي"
            "09" -> "صدور فرم دو تبصره الحاقي"
            "10" -> "ابلاغ فرم دو تبصره الحاقي"
            "11" -> "صدور برگه پرداخت بدهي قطعي"
            "12" -> "صدور برگه پرداخت بدهي برآوردي"
            "13" -> "وصول بدهي قطعي"
            "14" -> "وصول بدهي برآوردي"
            "15" -> "وصول بدهي قطعي و برآوردي"
            "16" -> "صدور مفاصاحساب"
            "17" -> "ابلاغ مفاصاحساب"
            else -> "-"
        }
    }

    fun getConstructionContractTitle(): String {
        return when (contractsubjectcode) {
            "01" -> {
                "قرادادهای عمرانی"
            }
            "02" -> {
                "قرادادهای غیرعمرانی"
            }
            "03" -> {
                "موضوع قرارداد صرفا خدمات می باشد و هزینه تهیه و تأمین ماشین آلات به عهده پیمانکار بوده"
            }
            "04" -> {
                "قراردادهای غیرعمرانی حمل و نقل مواد نفتی"
            }
            "05" -> {
                "قراردادهای حمل و نقل بار و کالای بین شهری"
            }
            "06" -> {
                "قرارداد جابه جایی مسافر بین شهری و درون شهری"
            }
            "07" -> {
                "قرارداد کلید در دست"
            }
            "11" -> {
                "پیمانکار تمام یا بخشی از کار را در کارگاه صنعتی و خدمات تولیدی یا فنی مهندسی ثابت خود انجام داده است"
            }
            "13" -> {
                "قراردادهای فناوری اطلاعات انفورماتیک"
            }
            "29" -> {
                "قراردادهای دارای تجهیزات خریداری شده از خارج کشور بر اساس گشایش اعتبار اسنادی"
            }
            else -> "-"

        }
    }

    @Parcelize
    class Contract(
        var workshop: Workshop? = null,
        var assignersWorkshop: @RawValue Any? = null,
        var contractRow: String? = null,
        var contractSequence: String? = null,
        var contractNumber: String? = null,
        var contractDate: String? = null,
        var contractStartDate: String? = null,
        var contractEndDate: String? = null,
        var contractSubject: String? = null,
        var contractAddress: String? = null,
        var contractAssignName: String? = null,
        var contractAmount: String? = null,
        var isNew: Boolean? = null,
        var assignName: String? = null,
        var branch: @RawValue Any? = null
    ) : Parcelable

    @Parcelize
    class DataDetail(
        var id: Int? = null,
        var documentType: String? = null,
        var documentId: String? = null,
        var documentCode: String? = null
    ) : Parcelable{
        var attachmentUrl:String?=""
    }

    @Parcelize
    class Workshop(
        var workshopId: String? = null,
        var branchCode: String? = null,
        var workshopName: String? = null,
        var workshopApproveDate: String? = null,
        var actitvityCode: String? = null,
        var character: String? = null,
        var sendListPeriod: @RawValue Any? = null,
        var workshopUnemployedStat: String? = null,
        var inclusionDate: String? = null,
        var brhCode: String? = null,
        var workshopRegisterDate: String? = null,
        var userId: String? = null,
        var createDate: String? = null,
        var claimOpDate: Long? = null,
        var claimUserId: String? = null,
        var incomOpDate: Long? = null,
        var incomUserId: String? = null,
        var status: Int? = null,
        var workshopKhalaf: String? = null,
        var fromOtherBranch: @RawValue Any? = null,
        var workshopStatus: String? = null,
        var webServiceResultStatus: String? = null,
        var sswn: String? = null,
        var isNew: Boolean? = null,
        var parentWorkshop: Workshop? = null,
        var activityName: String? = null,
        var lastAddress: String? = null,
        var employerName: String? = null,
        var decodedCreateDate: String? = null,
    ) : Parcelable

}

//ui class
data class ComputationalBaseSection(
    var title:String,
    var dataList:List<KeyValueModel>?=null,
    var attachmentList:List<ComputationalBase.DataDetail>?=null
)


