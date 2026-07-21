package com.tamin.taminhamrah.data.remote.models.services.disabilityPension

data class DisabilitySaveInfoRequest(
    var activityType: String? = null,
    var address: String? = null,
    var age: String? = null,
    var birthDate: Long? = null,
    var branchCode: String? = null,
    var fatherName: String? = null,
    var firstName: String? = null,
    var gender: String? = null,
    var idNumber: String? = null,
    var insuranceNumber: String? = null,
    var issuePlace: String? = null,
    var lastName: String? = null,
    var managerName: String? = null,
    var mobileNumber: String? = null,
    var nationalCode: String? = null,
    var pensionRequestDocList: List<PensionRequestDoc?>? = null,
    var phoneNumber: String? = null,
    var status: String? = null, //Status 0 initial registration, 1 final approval, 2 disapproval
    var workshopAddress: String? = null,
    var workshopCode: String? = null,
    var workshopName: String? = null
)

data class PensionRequestDoc(
    val documentType: String? = null,
    val guid: String? = null,
    val id: Any? = null,
    val pensionRequest: Any? = null
)