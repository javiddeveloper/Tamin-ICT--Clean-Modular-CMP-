package com.tamin.taminhamrah.data.remote.models.services.retirementPension
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfirmIdentityAndHistoryInfoRequest(
    var activityType: String = "",
    var address: String = "",
    var age: String = "",
    var birthDate: Long = 0,
    var branchCode: String = "" ,
    var fatherName: String = "",
    var firstName: String = "",
    var gender: String = "",
    var idNumber: String = "",
    var insuranceNumber: String = "",
    var issuePlace: String = "",
    var lastName: String = "",
    var managerName: String = "",
    var mobileNumber: String = "",
    var nationalCode: String = "",
    var phoneNumber: String = "",
    var status: String = "0",
    var workshopAddress: String = "",
    var workshopCode: String = "",
    var workshopName: String = ""
) : Parcelable