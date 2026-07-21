package com.tamin.taminhamrah.ui.home.services.disabilityPension.model

import android.net.Uri
import com.tamin.taminhamrah.Constants.FINAL_OPINIONS_JUDICIAL_AUTHORITIES_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.INCIDENT_REPORT_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.THEORY_APPEAL_MEDICAL_COMMISSION_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.THEORY_EARLY_MEDICAL_COMMISSION_IMAGE_TYPE
import com.tamin.taminhamrah.Constants.WORK_INSPECTION_REPORT_IMAGE_TYPE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityDependentModel
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.PensionRequestDoc

class DisabilityPensionDataModel {

    var userNationalId: String = ""
    var genderDesc: String = ""
    var userName: String = ""
    var historyYear = ""
    var historyMonth = ""
    var historyDay = ""
    var totalHistory = ""
    var tempImageType: String = ""
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var requestId: Long = 0
    val dependentsList by lazy { ArrayList<DisabilityDependentModel>() }
    val identityInfo by lazy { ArrayList<KeyValueModel>() }
    val branchInfo by lazy { ArrayList<KeyValueModel>() }
    val documentList by lazy { ArrayList<UploadedImageModel>() }
    val saveInfoRequestModel by lazy { DisabilitySaveInfoRequest() }

    fun loadRequestInfo(data: DisabilityPersonalInfoResponse.DisabilityPersonalInfoDataModel) {
        saveInfoRequestModel.apply {
            firstName = data.personal.firstName
            lastName = data.personal.lastName
            fatherName = data.personal.fatherName
            mobileNumber = data.mobileNumber
            branchCode = data.branch
            birthDate = data.personal.dateOfBirth
            gender = data.personal.gender.genderCode
            idNumber = data.personal.idCardNumber
            nationalCode = data.personal.nationalId
            insuranceNumber = data.insuranceId
            issuePlace = data.personal.cityOfIssue.description
            status = "3" //Status 0 initial registration, 1 final approval, 2 disapproval
            workshopCode = data.work?.workshopId
            age = data.strAge
            pensionRequestDocList = emptyList()
        }
    }

    fun loadImageInfo(guid: String) {
        for (i in 0 until documentList.size)
            if (documentList[i].imageType == tempImageType) {
                documentList.removeAt(i)
                break
            }
        documentList.add(
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

    val documentsTitle by lazy {
        arrayListOf(MenuModel(titleStringResId = R.string.theory_early_medical_commission_title,
            descStringResId = R.string.theory_early_medical_commission_desc,
            id = THEORY_EARLY_MEDICAL_COMMISSION_IMAGE_TYPE,
            showDesc = true),
            MenuModel(titleStringResId = R.string.theory_appeal_medical_commission_title,
                descStringResId = R.string.theory_appeal_medical_commission_desc,
                showDesc = true,
                id = THEORY_APPEAL_MEDICAL_COMMISSION_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.incident_report_title,
                descStringResId = R.string.incident_report_desc,
                showDesc = true,
                id = INCIDENT_REPORT_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.work_inspection_report_title,
                descStringResId = R.string.work_inspection_report_desc,
                showDesc = true,
                id = WORK_INSPECTION_REPORT_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.final_opinions_judicial_authorities_title,
                descStringResId = R.string.final_opinions_judicial_authorities_desc,
                showDesc = true,
                id = FINAL_OPINIONS_JUDICIAL_AUTHORITIES_IMAGE_TYPE))
    }

    fun getSaveDocumentRequestModel() : DisabilitySaveDocumentRequest {
        val list = ArrayList<PensionRequestDoc>()
        documentList.forEach { pic ->
            list.add(PensionRequestDoc(documentType = pic.imageType, guid = pic.guid))
        }
        return DisabilitySaveDocumentRequest(status = "4", pensionRequestDocList = if (list.size > 0)
            list
        else
            emptyList())

    }
}