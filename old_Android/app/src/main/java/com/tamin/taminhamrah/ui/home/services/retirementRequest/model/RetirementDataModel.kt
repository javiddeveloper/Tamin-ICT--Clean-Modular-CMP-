package com.tamin.taminhamrah.ui.home.services.retirementRequest.model

import android.net.Uri
import com.tamin.taminhamrah.Constants.RETIREMENT_DESC_PAGE_IDENTITY
import com.tamin.taminhamrah.Constants.RETIREMENT_FIRST_PAGE_IDENTITY_IMAGE_TYPE
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.ConfirmIdentityAndHistoryInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementDocumentModel
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementRequestInfoModel
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.ui.home.services.retirementRequest.RetirementPensionFragment.Companion.IMAGE_DOC_REQUEST_CODE
import com.tamin.taminhamrah.ui.home.services.retirementRequest.RetirementPensionFragment.Companion.IMAGE_RESIGNATION_REQUEST_CODE


class RetirementDataModel {
    var userName: String = ""
    var userNationalId: String = ""
    var genderDesc: String = ""
    var isConfirmRules: Boolean = false
    var sumHistoryDays = "0"
    var historyYears = "0"
    var historyMonths = "0"
    var historyDays = "0"
    var yearsAge = 0
    var monthsAge = "0"
    var daysAge = "0"
    var strAge: String = "0"
    val identityInfo by lazy { ArrayList<KeyValueModel>() }
    val branchInfo by lazy { ArrayList<KeyValueModel>() }
    val documentList by lazy { ArrayList<UploadedImageModel>() }
    var identityInfoNeedVerified : ConfirmIdentityAndHistoryInfoRequest ?= null
    val resignationDocList by lazy { ArrayList<UploadedImageModel>() }
    val retirementRequestInfo by lazy { ArrayList<RetirementRequestInfoModel>() }
    var tempImageType: String = ""
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var requestId: String? = null
    var authenticationsCode : Long?= null
    var currentState = 1
    var identityInfoIsConfirmed = false
    val documentsTitle by lazy {
        arrayListOf(
            MenuModel(titleStringResId = R.string.first_page_identity_card,
                id = RETIREMENT_FIRST_PAGE_IDENTITY_IMAGE_TYPE),
            MenuModel(titleStringResId = R.string.desc_page_identity,
                id = RETIREMENT_DESC_PAGE_IDENTITY))
    }

    fun loadImageInfo(guid: String, requestId: Int = IMAGE_DOC_REQUEST_CODE) {
        if (requestId == IMAGE_DOC_REQUEST_CODE) {
            for (i in 0 until documentList.size)
                if (documentList[i].imageType == tempImageType) {
                    documentList.removeAt(i)
                    break
                }
        }

        val imageModel = UploadedImageModel(
            guid = guid,
            imageType = tempImageType,
            imageUri = tempImageUri,
            imageName = tempImageName,
            orgUri = tempImageOriginalUri
        )

        when (requestId) {
            IMAGE_DOC_REQUEST_CODE -> {
                documentList.add(imageModel)
            }
            IMAGE_RESIGNATION_REQUEST_CODE -> {
                resignationDocList.add(imageModel)
            }
        }
        tempImageType = ""
        tempImageName = ""
        tempImageUri = null
    }


    val requestStatesList by lazy {
        arrayListOf(
            RetirementRequestStateModel(titleStringResId = R.string.authentication_step),
            RetirementRequestStateModel(titleStringResId = R.string.submit_info_step),
            RetirementRequestStateModel(titleStringResId = R.string.upload_identity_doc_step),
            RetirementRequestStateModel(titleStringResId = R.string.need_check_branch),
            RetirementRequestStateModel(titleStringResId = R.string.check_history_of_branch_step),
            RetirementRequestStateModel(titleStringResId = R.string.upload_resignation_step),
            RetirementRequestStateModel(titleStringResId = R.string.check_resignation_of_branch_step),
            RetirementRequestStateModel(titleStringResId = R.string.issuance_edict_step)
        )
    }

    fun getSavedDocumentRequestModel(docType :Int) : RetirementSaveDocumentRequest {
        val list = ArrayList<RetirementDocumentModel>()
        when(docType){
            IMAGE_DOC_REQUEST_CODE ->{
                documentList.forEach { pic ->
                    list.add(RetirementDocumentModel(documentType = pic.imageType, guid = pic.guid))
                }
            }
            IMAGE_RESIGNATION_REQUEST_CODE ->{
                resignationDocList.forEach { pic ->
                    list.add(RetirementDocumentModel(documentType = pic.imageType, guid = pic.guid))
                }
            }
        }
        return RetirementSaveDocumentRequest(status = "4", pensionRequestDocList = if (list.size > 0)
            list
        else
            emptyList())
    }

}

