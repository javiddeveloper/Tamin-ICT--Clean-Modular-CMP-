package com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model

import android.net.Uri
import com.tamin.taminhamrah.Constants.DECEASE_CERTIFICATE_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.DECEASE_CHILD_INFO_PAGE_IDENTITY_CARD_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.DECEASE_FIRST_PAGE_IDENTITY_IMAGE_TYPE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.DependencyType
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.PensionRequestDoc
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorModel

class PensionSurvivorDataModel {
    val survivorList by lazy {
        ArrayList<SurvivorModel>()
    }

    var userName: String = ""
    var userNationalId: String = ""
    var genderDesc: String = ""
    var deceasedNationalId: String = ""
    var tempImageType: String = ""
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var branchCode: String = ""
    var deceasedInsuranceId = ""
    var isConfirmRules = false
    var requestId = 0
    var isConfirmDeceasedRules = false
    val deceasedInfoList by lazy { ArrayList<KeyValueModel>() }
    val deceaseDocumentList by lazy { ArrayList<UploadedImageModel>() }
    val deceasedDocsTitle by lazy {
        arrayListOf(MenuModel(titleStringResId = R.string.deathCertificate,
            id = DECEASE_CERTIFICATE_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.first_page_decease_identity_card,
                id = DECEASE_FIRST_PAGE_IDENTITY_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.child_info_decease_identity_card,
                id = DECEASE_CHILD_INFO_PAGE_IDENTITY_CARD_IMAGE_TYPE))
    }

    fun loadImageInfo(guid: String, list: ArrayList<UploadedImageModel>) {
        for (i in 0 until list.size)
            if (list[i].imageType == tempImageType) {
                list.removeAt(i)
                break
            }
        list.add(
            UploadedImageModel(
                guid = guid,
                imageType = tempImageType,
                imageUri = tempImageUri,
                imageName = tempImageName,
                orgUri = tempImageOriginalUri
            )
        )
        tempImageType = ""
        tempImageName = ""
        tempImageUri = null
    }

    fun getSaveSurvivorInfoReqModel(
        address: String,
        mobile: String,
        phone: String,
        relationCode : String,
        imagesList: ArrayList<UploadedImageModel>,
        survivorInfo: SurvivorModel,
    ): SaveSurvivorInfoRequest? {
        val docList = ArrayList<PensionRequestDoc>()
        survivorInfo.userInfo.dependentDocumentList.addAll(imagesList)

        survivorInfo.userInfo.dependentDocumentList.forEach { doc ->
            docList.add(PensionRequestDoc(
                documentType = doc.imageType,
                guid = doc.guid
            ))
        }

        survivorInfo.userInfo.personal?.apply {
            return SaveSurvivorInfoRequest(
                age = age.toString(),
                birthDate = dateOfBirth,
                branchCode = branchCode,
                survivorNationalId = nationalId,
                deathType = "1", //Type of death (1 == insured death, 2 == pensioner death)
                deathDate = null,
                dependencyType = DependencyType(code =relationCode),
                fatherName = fatherName,
                firstName = firstName,
                lastName = lastName,
                gender = gender.genderCode,
                idCardNumber = idCardNumber,
                insuranceNumber = deceasedInsuranceId,
                issuePlace = cityOfIssue,
                deceasedNationalId = this.deceasedNationalID,
                pensionId = "",    //deceased pensioner id
                status = "3", // (3=Initial request,2= non-approval, 1= final approval)
                address = address,
                survivorInsuranceId = survivorInfo.userInfo.insuranceId,
                mobileNumber = mobile,
                phoneNumber = phone,
                pensionRequestDocList = docList,
            )
        }
        return null
    }


}