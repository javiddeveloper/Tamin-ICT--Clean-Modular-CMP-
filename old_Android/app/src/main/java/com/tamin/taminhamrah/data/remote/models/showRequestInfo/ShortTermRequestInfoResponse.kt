package com.tamin.taminhamrah.data.remote.models.showRequestInfo

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListData
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyStatusModel
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyTypesModel
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.RequestTypeEnumClass
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility

class ShortTermRequestInfoResponse : ListDataModel<ShortTermRequestInfoModel>()

data class ShortTermRequestInfoModel(
    val mobile: String? = null,
    @SerializedName("risuFullName")
    val insuredFullName: String? = null,
    @SerializedName("risuid")
    val insuranceNumber: String? = null,
    @SerializedName("shorttermArutz")
    val shortTermOrthotics: List<ShortTermModel?>? = null,
    @SerializedName("consequential")
    val consequential: List<Consequential?>? = null,
    @SerializedName("shorttermIllness")
    val shortTermIllness: List<ShortTermModel?>? = null,
    @SerializedName("shorttermPragnent")
    val shortTermPregnancy: List<ShortTermPregnancy?>? = null
) {
    fun setChildbearingInfo(pregnancyInfo: Pair<ListData<PregnancyStatusModel>?, ListData<PregnancyTypesModel>?>?) {
        shortTermPregnancy?.forEach { requestInfo->
            if (requestInfo!=null) {
                pregnancyInfo?.first?.list?.forEach { status ->
                    if (requestInfo.pregnancyStatusCode == status.code)
                        requestInfo.pregnancyStatusDesc = status.name?:""
                }
                pregnancyInfo?.second?.list?.forEach { type ->
                    if (requestInfo.pregnancyTypeCode == type.code)
                        requestInfo.pregnancyTypeDesc = type.name?:""
                }
            }
          }
    }

    fun getRequestInfo(requestType: Int): List<KeyValueModel> {
        var shortTermInfo: ShortTermRequest? = null
        when (requestType) {
            RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                if (!shortTermOrthotics.isNullOrEmpty())
                    shortTermInfo =shortTermOrthotics[0]?.shortTermRequest
            }
            RequestTypeEnumClass.ILL_DAY.serviceId -> {
                if (!shortTermIllness.isNullOrEmpty())
                    shortTermInfo =shortTermIllness[0]?.shortTermRequest
            }
            RequestTypeEnumClass.PREGNANCY.serviceId -> {
                if (!shortTermPregnancy.isNullOrEmpty())
                    shortTermInfo =shortTermPregnancy[0]?.shortTermRequest
            }
        }

        if (shortTermInfo!=null) {
            return listOf(
                KeyValueModel(
                    _keyStringResId = R.string.label_insurance_code,
                    _value = insuranceNumber ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.full_name,
                    _value = insuredFullName ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.account_number,
                    _value = shortTermInfo.bankAccount ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.bank_name,
                    _value = shortTermInfo.bankName ?: "_"
                ),
                KeyValueModel(_keyStringResId = R.string.mobile, _value = mobile ?: "_")
            )
        }
        return emptyList()
    }

    fun getUserInfo(requestType: Int): List<KeyValueModel> {
        return when (requestType) {
            RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                getOrthoticsInfo()
            }
            RequestTypeEnumClass.ILL_DAY.serviceId -> {
                getIllDaysInfo()
            }
            RequestTypeEnumClass.PREGNANCY.serviceId -> {
                getPregnancyDaysInfo()
            }
            else -> {
                return emptyList()
            }
        }
    }

    fun getDocumentList(requestType: Int): List<RequestFile?> {
        when (requestType) {
            RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                if (!shortTermOrthotics.isNullOrEmpty())
                    return shortTermOrthotics[0]?.shortTermRequest?.requestFileList ?: emptyList()
            }
            RequestTypeEnumClass.ILL_DAY.serviceId -> {
                if (!shortTermIllness.isNullOrEmpty())
                    return shortTermIllness[0]?.shortTermRequest?.requestFileList ?: emptyList()
            }
            RequestTypeEnumClass.PREGNANCY.serviceId -> {
                if (!shortTermPregnancy.isNullOrEmpty())
                    return shortTermPregnancy[0]?.shortTermRequest?.requestFileList ?: emptyList()
            }
        }
        return emptyList<RequestFile>()
    }

    fun getDescAction(requestType: Int): String {
        when (requestType) {
            RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                if (!shortTermOrthotics.isNullOrEmpty()) {
                    return shortTermOrthotics.last()?.shortTermRequest?.resultMessage ?: ""
                }
            }
            RequestTypeEnumClass.ILL_DAY.serviceId -> {
                if (!shortTermIllness.isNullOrEmpty()) {
                    return shortTermIllness.last()?.shortTermRequest?.resultMessage ?: ""
                }
            }

            RequestTypeEnumClass.PREGNANCY.serviceId -> {
                if (!shortTermPregnancy.isNullOrEmpty()) {
                    return shortTermPregnancy.last()?.shortTermRequest?.resultMessage ?: ""
                }
            }
        }
        return ""
    }

    private fun getOrthoticsInfo(): List<KeyValueModel> {
        shortTermOrthotics?.let { list ->
            if (list.isNotEmpty() && !consequential.isNullOrEmpty()) {
                return listOf(
                    KeyValueModel(
                        _keyStringResId = R.string.last_branch,
                        _value = list[0]?.shortTermRequest?.branchName ?: "_"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.insurance_num,
                        _value = consequential[0]?.insuranceNumber ?: "_"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.label_date_prescription,
                        _value = list[0]?.prescriptionDate?.let { timeStamp ->
                            ConvertDate.convertTimestampToPersianDate(timeStamp)
                        } ?: "_",
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.full_name,
                        _value = "${consequential[0]?.insuredLastName} ${consequential[0]?.insuredFirstName}"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.relative,
                        _value = consequential[0]?.relationShip ?: "_"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.identity_number,
                        _value = consequential[0]?.idCardNumber ?: "_"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.label_identity_place,
                        _value = consequential[0]?.cityName ?: "_"
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.birthdate,
                        _value = Utility.getDateSeparator(consequential[0]?.birthDate)
                    ),
                    KeyValueModel(
                        _keyStringResId = R.string.label_expiration_date,
                        _value = consequential[0]?.validityDate ?: "_"
                    ),
                )
            }
        }
        return emptyList()
    }

    private fun getIllDaysInfo(): List<KeyValueModel> {
        val list = shortTermIllness ?: emptyList()
        if (list.isNotEmpty()) {
            return listOf(
                KeyValueModel(
                    _keyStringResId = R.string.last_branch,
                    _value = list[0]?.shortTermRequest?.branchName ?: "_"
                ),

                KeyValueModel(
                    _keyStringResId = R.string.label_start_date_rest,
                    _value = list[0]?.startRestDate?.let { timeStamp ->
                        ConvertDate.convertTimestampToPersianDate(timeStamp)
                    } ?: "_",
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_end_date_rest,
                    _value = list[0]?.endRestDate?.let { timeStamp ->
                        ConvertDate.convertTimestampToPersianDate(timeStamp)
                    } ?: "_",
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_doctor_name,
                    _value = list[0]?.doctorName ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_doctor_id,
                    _value = list[0]?.doctorId ?: "_"
                )
            )

        }
        return emptyList()
    }

    private fun getPregnancyDaysInfo(): List<KeyValueModel> {
        val list = shortTermPregnancy ?: emptyList()
        if (list.isNotEmpty()) {
            return listOf(
                KeyValueModel(
                    _keyStringResId = R.string.last_branch,
                    _value = list[0]?.shortTermRequest?.branchName ?: "_"
                ),

                KeyValueModel(
                    _keyStringResId = R.string.label_start_date_rest,
                    _value = list[0]?.startRestDate?.let { timeStamp ->
                        ConvertDate.convertTimestampToPersianDate(timeStamp)
                    } ?: "_",
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_end_date_rest,
                    _value = list[0]?.endRestDate?.let { timeStamp ->
                        ConvertDate.convertTimestampToPersianDate(timeStamp)
                    } ?: "_",
                ),
                KeyValueModel(
                    _keyStringResId = R.string.date_of_childbearing,
                    _value = list[0]?.childbearingDate?.let { timeStamp ->
                        ConvertDate.convertTimestampToPersianDate(timeStamp)
                    } ?: "_",
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_doctor_name,
                    _value = list[0]?.doctorName ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_doctor_id,
                    _value = list[0]?.doctorMedicalId ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.rest_days,
                    _value = list[0]?.pregnancyRestDays ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_pregnancy_status,
                    _value = list[0]?.pregnancyStatusDesc ?: "_"
                ),
                KeyValueModel(
                    _keyStringResId = R.string.label_type_of_pregnancy,
                    _value = list[0]?.pregnancyTypeDesc ?: "_"
                )
            )

        }
        return emptyList()
    }

}

data class Consequential(
    @SerializedName("risuId")
    val insuranceNumber: String? = null,
    @SerializedName("bletenddate")
    val validityDate: String? = null,
    @SerializedName("brithDate")
    val birthDate: String? = null,
    @SerializedName("relationShip")
    val relationShip: String? = null,
    @SerializedName("risuLName")
    val insuredFirstName: String = "",
    @SerializedName("risuFname")
    val insuredLastName: String = "",
    @SerializedName("cityName")
    val cityName: String? = null,
    @SerializedName("risuIdNo")
    val idCardNumber: String? = null,
)

data class RequestType(
    val requestTypeDesc: String? = null,
)

data class ShortTermModel(
    @SerializedName("shorttermRequest")
    val shortTermRequest: ShortTermRequest? = null,
    @SerializedName("useTaj")
    val prescriptionDate: Long? = null,
    @SerializedName("bimSDate")
    val startRestDate: Long? = null,
    @SerializedName("bimEdate")
    val endRestDate: Long? = null,
    @SerializedName("bimDrname")
    val doctorName: String? = null,
    @SerializedName("bimDrid")
    val doctorId: String? = null,
)

data class ShortTermRequest(
    val bankAccount: String? = null,
    val bankName: String? = null,
    val request: Request? = null,
    val branchName: String? = null,
    val requestFileList: List<RequestFile?>? = null,
    val resultMessage: String? = null
)

data class Request(
    @SerializedName("requestType")
    val requestType: RequestType? = null,
)

data class RequestFile(
    @SerializedName("documentFile")
    val documentFile: String? = null,
    @SerializedName("documentType")
    val documentType: String? = null,

    )

data class ShortTermPregnancy(
    @SerializedName("barDRid")
    val doctorMedicalId: String? = null,
    @SerializedName("barDd")
    val pregnancyRestDays: String? = null,
    @SerializedName("barDemDat")
    val childbearingDate: Long? = null,
    @SerializedName("barDrname")
    val doctorName: String? = null,
    @SerializedName("barSDate")
    val startRestDate: Long? = null,
    @SerializedName("barEDate")
    val endRestDate: Long? = null,
    @SerializedName("barType")
    val pregnancyStatusCode: String? = null,
    @Ignore
    var pregnancyStatusDesc: String? = null,
    @SerializedName("barChild")
    val pregnancyTypeCode: String? = null,
    @Ignore
    var pregnancyTypeDesc: String? = null,
    @SerializedName("shorttermRequest")
    val shortTermRequest: ShortTermRequest? = null
)
