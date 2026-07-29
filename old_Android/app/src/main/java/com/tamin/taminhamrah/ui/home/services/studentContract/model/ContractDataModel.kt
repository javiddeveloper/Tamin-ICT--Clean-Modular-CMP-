package com.tamin.taminhamrah.ui.home.services.studentContract.model

import android.net.Uri
import com.google.gson.Gson
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CreateContractModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.PremiumOptionsModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.Protector
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.HelperDate
import timber.log.Timber
import java.util.Date

data class ContractDataModel(
    var brchCodeNew: String? = null,
    var cityCode: String? = null,
    var cntDrmn: String? = null,
    var cntFreeJobCode: String = "",
    var guid: String = "00",
    //   var guidName: String = "00",
    var premiumRateCode: String = "00",
    var provinceCode: String? = null,
    var selectedSalary: Int = -1,
    var selectedPercentDesc: String = "",
    var usersCity: String = "",
    var usersAddress: String = "",
    var usersZipCode: String = "",
    var usersMobile: String = "",
    var usersPhoneNumber: String = "",
    var insuranceId: String = "",
    var havePreviousPayment: Boolean = false,
    var selectedValueSeekbar: Double = 0.0,
    var eligibilityStatus: Int = -1,
) {

    var tempAddress = ""
    var tempZipCode = ""
    var tempPhoneNumber = ""
    var mobileExist = false
    fun loadAgeAndHistoryData(checkAgeAndHistoryModel: CheckAgeAndHistoryModel?, isOptional: Boolean = false) {

        checkAgeAndHistoryModel?.apply {

            havePreviousPayment = previousPayment ?: false
            this@ContractDataModel.provinceCode = provinceCode ?: ""
            this@ContractDataModel.provinceName = provinceName ?: ""
            cityCode = contract?.cityCode
            cityNameOfBranch = city ?: ""
            Timber.tag("debugUpdateUser").i("cityNameOfBranch=$cityNameOfBranch")

            brchCodeNew = contract?.branchCode
            branchAddress = organizationAddress ?: ""
            cntFreeJobCode = contract?.cntFreeJobCode ?: ""
            jobDesc = contract?.freeJob?.discrioption ?: ""

            cntDrmn = contract?.cntDrmn
            agreement = if (cntDrmn == "1")
                true
            else {
                cntDrmn = "2"
                false
            }
            selectedSalary = contract?.salary ?: 0
            Timber.tag("EditDebugOpt").d("premiumRateCode=$premiumRateCode ")

            selectedValueSeekbar = (premiumRateCode ?: 0).toDouble()
            if (isOptional)
                selectedValueSeekbar = ((selectedSalary / 100) * 27).toDouble()

            branchName = organizationAddress ?: ""
            this@ContractDataModel.premiumRateCode = contract?.premiumRate?.spcrateCode ?: "00"
            tempImageName = contract?.guidName ?: ""
            guid = contract?.guid ?: "00"
            selectedPercentDesc = "${contract?.premiumRate?.insurDpercent} درصد" ?: ""


            if (protector is Boolean) {
                Timber.tag("EditDebugOpt").i("protector is Boolean")
                guardianShip = GuardianType.FOR_ITSELF
            } else {
                Timber.tag("EditDebugOpt").i("protector NOT  Boolean")

                val protectorStr = Gson().toJson(protector).toString()
                // has guardian ship
                val model = Gson().fromJson(protectorStr, Protector::class.java)
                model?.apply {
                    guardianName = fullName ?: ""
                    guardianNationalId = nid ?: ""
                    guardianNumber = protectorLetterNo ?: ""
                    guardianDate = ConvertDate.convertTimestampToPersianDate(createDate.toString())
                    val date = Date((createDate ?: 0) + 70200000)
                    guardianDateFormatted =
                        HelperDate.convertServerDateFormatToMobileDateFormat(date)
                    guardianGuid = guid ?: "00"
                    guardianImageName = guidName ?: ""
                    guardianShip = GuardianType.FOR_GUARDIAN
                }
            }
        }
    }

    fun loadRegistrationInfo(result: CreateContractModel) {
        //  usersCity = result.lastContact?.city?:""
        usersAddress = result.lastContact?.address ?: ""
        usersZipCode = result.lastContact?.zipCode ?: ""
        usersMobile = result.lastContact?.mobile ?: ""
        mobileExist = usersMobile.isNotBlank()
        usersPhoneNumber = result.lastContact?.phoneNumber ?: ""
        firstName = result.personalInfo?.firstName ?: ""
        lastName = result.personalInfo?.lastName ?: ""
        nationalId = result.personalInfo?.nationalId ?: ""
        ssn = result.personalInfo?.ssn ?: ""
        genderCode = result.personalInfo?.gender?.genderCode ?: ""
        genderDesc = result.personalInfo?.gender?.genderDesc ?: ""
        insuranceId = result.insuranceId


    }

    var firstName = ""
    var lastName = ""
    var finalText = ""
    var nationalId = ""
    var ssn: String = ""
    var branchName: String = ""
    var branchAddress: String = ""
    var provinceName = ""
    var cityNameOfBranch = ""
    var needToCalculate = true
    var jobDesc = ""
    var premiumOptionList: MutableList<PremiumOptionsModel> = ArrayList()
    var paymentTabayi: Int? = null
    var isConfirmRules = false
    var imageFile: UploadedImageModel? = null
    var agreement = false
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var minPremiumRate: Double = -1.0
    var maxPremiumRate: Double = -1.0
    var changePremiumRateCode = false
    var guardianShip: GuardianType? = null
    var receivedBranchInfo = false
    var guardianshipImage: UploadedImageModel? = null
    var guardianImageName: String = ""
    var guardianImageUri: Uri? = null
    var guardianName = ""
    var guardianDate = ""
    var guardianDateFormatted = ""
    var guardianNumber = ""
    var guardianNationalId = ""
    var guardianGuid = "00"
    var genderCode = "*"
    var genderDesc = "*"

    fun getFullName() = "$firstName $lastName"

    fun isMan() = genderCode == "01" || genderDesc == "مرد"
}

enum class GuardianType {
    FOR_ITSELF, FOR_GUARDIAN
}

