package com.tamin.taminhamrah.data.remote.models.services.disabilityPension.medicalCommission

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class RegisteredMedicalCommissionResponse : ListDataModel<RegisteredMedicalCommissionModel>()

data class RegisteredMedicalCommissionModel(
    val demandInfoId: String,
    val demandTypeCode: String,
    val demandSaveDate: Long,
    val commFirstName: String,
    val commLastName: String,
    val commFatherName: String,
    val commBirthDate: Long,
    val commGender: String,
    val commMarriageStatus: String,
    val commIdNumber: String,
    val commExpCityCode: String,
    val commRelationTypeCode: String,
    val commNationalCode: String,
    val guardianNationalCode: String?,
    val commInsuredTypeCode: String,
    val commResidenceCityCode: String,
    val commAddress: String,
    val commMobileNumber: String,
    val commTelephoneNumber: String?,
    val commNationality: String,
    val deadDate: Long?,
    val insuranceNumber: String,
    val pensionerCode: String?,
    val isuTypeCode: String,
    val nationalCode: String,
    val referBadviCode: String?,
    val status: String,
    val referTypeCode: String,
    val lastJobDesc: String?,
    val lastJobCode: String?,
    val jobHistoryDesc: String?,
    val hasDrivingCertificate: String,
    val hasVisitBeforeJob: String?,
    val hasVisitInJob: String,
    val hasHealthyCertificate: String,
    val hasContract: String,
    val hasExpertJob: String,
    val historyConfirm: String?,
    val militaryStatusCode: String,
    val commissionInResidenceCity: String?,
    val dependencyTypeCode: String,
    val branchCode: String,
    val divan: String,
    val isConfirmed: String?,
    val refId: String?,
    val commPostalCode: String?,
    val commCaseTypeCode: String,
    val isuStatusTypeCode: String,
    val demandStage: String,
    val sendCentralCommittee: String?,
    val commissionCentralId: String?,
    val commissionPollDesc: String?,
    val committeeRequestInfoList: List<CommitteeRequestInfo>,
    val committeeDemandInfoDocumentList: List<CommitteeDemandInfoDocument>
) {
    fun getMainInfo() = listOf(
        KeyValueModel(
            _keyStringResId = R.string.request_type,
            _value = gridCellDemandType(),
            _textColor = EnumTextColor.BLUE
        ),
        KeyValueModel(
            _keyStringResId = R.string.refer_type,
            _value = getReferType(),
            _textColor = EnumTextColor.AMBER
        ),
        KeyValueModel(
            _keyStringResId = R.string.nationalId_applicant,
            _value = nationalCode
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_branch_code,
            _value = branchCode
        ),
        KeyValueModel(
            _keyStringResId = R.string.label_status,
            _value = getStatusTranslator(),
            _textColor = getStatusColor()
        ),

        )

    private fun getStatusColor(): EnumTextColor {
        return when (this.status) {
            "01", "07" -> {
                EnumTextColor.BLUE
            }

            "02", "03", "04", "08" -> {
                EnumTextColor.AMBER
            }

            "05", "06", "09", "10" -> {
                EnumTextColor.RED
            }

            else -> {
                EnumTextColor.NORMAL
            }
        }
    }

    fun getPersianDate() = ConvertDate.convertTimestampToPersianDate(this.demandSaveDate)

    private fun gridCellDemandType(): String {
        return when (this.demandTypeCode) {
            "01" -> {
                "از کار افتادگي"
            }

            "02" -> {
                "استراحت پزشکی"
            }

            "04" -> {
                "کار سبک/ تغيير شرايط شغلی"
            }

            "10" -> {
                "بازنشستگی همكاران معلول عادی و ناشی ازکار"
            }

            "11" -> {
                "بازنشستگی پیش از موعد همکاران سازمانی"
            }

            "12" -> {
                "بازنشستگی معلولین عادی بخش عمومی غیر دولتی"
            }

            "13" -> {
                "بازنشستگی معلولین ناشی از کار بخش عمومی غیر دولتی"
            }

            "18" -> {
                "استفاده از تسهيلات موضوع بخشنامه شماره 19/1"
            }

            else -> {
                "نامشخص"
            }
        }
    }

    private fun getReferType(): String {
        return when (this.referTypeCode) {
            "01" -> {
                "بدوی"
            }

            "02" -> {
                "تجدید نظر"
            }

            "03" -> {
                "اعتراض"
            }

            else -> {
                "نامشخص"
            }
        }

    }

    private fun getStatusTranslator(): String {
        return when (this.status) {
            "01" -> {
                "ثبت درخواست"
            }

            "02" -> {
                "در حال بررسی شعبه"
            }

            "03" -> {
                "در حال بررسی کمیسیون پزشکی"
            }

            "04" -> {
                "مختومه رأی نهایی"
            }

            "05" -> {
                "عدم تایید - شعبه"
            }

            "06" -> {
                "عدم تایید - کمیسیون"
            }

            "07" -> {
                "ثبت اعتراض"
            }

            "08" -> {
                "نوبت دهی کمیسیون"
            }

            "09" -> {
                "عدم تایید اعتراض"
            }

            "10" -> {
                "لغو به علت اشتباه در ثبت"
            }

            else -> {
                "نامشخص"
            }
        }
    }
}

data class CommitteeRequestInfo(
    val requestInfoId: String,
    val requestNumber: String,
    val committeeDemandInfo: String,
    val doctorInfoId: String?,
    val requestSaveDate: Long,
    val hasDrugUsage: String,
    val hasSurgery: String,
    val hasHospitalization: String,
    val hasOtherDoctor: String?,
    val hasOtherDarman: String,
    val otherDarmanDesc: String?,
    val hasCommissionOtherOrgan: String,
    val hasCommissionTaminOrgan: String,
    val commissionOtherOrganDesc: String?,
    val hasSupportOrgan: String,
    val supportOrganDesc: String?,
    val bookletTypeCode: String,
    val refrenceReasonCode: String,
    val darmanDocument: String?,
    val illnessDesc: String,
    val mainDoctorFirstName: String,
    val mainDoctorLastName: String,
    val mainDoctorSpeciality: String,
    val hasDrugUsageBoolean: Boolean,
    val hasSurgeryBoolean: Boolean,
    val hasOtherDarmanBoolean: Boolean,
    val committeeRequestInfoDocumentList: List<CommitteeRequestInfoDocument>
)

data class CommitteeRequestInfoDocument(
    val documentId: String,
    val committeeRequestInfo: String,
    val documentTypeId: String,
    val documentFileId: String
)

data class CommitteeDemandInfoDocument(
    val documentId: String,
    val committeeDemandInfo: String,
    val documentTypeId: String,
    val documentFileId: String
)
