package com.tamin.taminhamrah.ui.home.services.showAndAddDependent.model

import android.net.Uri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.BailType
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.Dependency
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentType
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DocumentFile
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RegistryDataModel
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestAddDependent
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestFile
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.enums.EnumStepperMode
import com.tamin.taminhamrah.utils.extentions.isNumericString

class AddDependentDataModel {

    var birthDateTimeStamp: String = ""
    var selectedBirthDateGregorian: String = ""
    var selectedBirthDateFormat: String = ""
    var selectedBranch: String = ""
    var dependentNationalId: String = ""
    var selectedDependent: MenuModel? = null      // id = id----  title = dependencyDesc  ----  description = dependencyCode ----- description2 = bailCode
    var dependentUserName: String = ""
    var dependentUserFamily: String = ""
    var dependentFatherName: String = ""
    var educationCode: String = ""
    var universityName: String = ""
    var selectedCityBirthCode: String = ""
    var selectedCityIssuance: String = ""
    var tempImageType: String = ""
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var stepperMode = EnumStepperMode.DEFAULT_MODE
    var needCallInquiryRequest: Boolean = true  //if true => need to reset the stepper and request the information from the registry again
    var needCallInquiryEducationCode: Boolean = true
    var isDisabledIdCard = false //if true => disable upload picture of id card
    var isDisabledMarriageContract = false //if true => disable upload picture of marriage's contract
    val documentList by lazy { ArrayList<UploadedImageModel>() }
    val activeBranchList by lazy { ArrayList<MenuModel>() }
    val dependentList by lazy { ArrayList<MenuModel>() }


    fun resetDependentInfo() {
        dependentUserName = ""
        dependentUserFamily = ""
        needCallInquiryRequest = true
        needCallInquiryEducationCode = true
        selectedCityBirthCode = ""
        selectedCityIssuance = ""
        educationCode = ""
        universityName = ""
        documentList.clear()
    }

    fun getRequestModel(): RequestAddDependent {
        val list = ArrayList<RequestFile>()
        documentList.forEach { image ->
            list.add(RequestFile(
                documentType = image.imageType,
                documentFile = DocumentFile(id = image.guid)))
        }
        val dependentId = if (selectedDependent?.id.toString().isNumericString())
            selectedDependent?.id?.toInt()
        else
            0
        return RequestAddDependent(
            bailType = BailType(selectedDependent?.description2),
            dependency = Dependency(dependentId),
            dependentType = DependentType(code = selectedDependent?.description),
            firstName = dependentUserName,
            lastName = dependentUserFamily,
            dateOfBirth = selectedBirthDateGregorian,
            nationalId = dependentNationalId,
            cityOfBirthId = selectedCityBirthCode,
            cityOfIssueId = selectedCityIssuance,
            branchCode = selectedBranch,
            requestFileList = list)
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

    fun getImageTitleList()=
        ArrayList<MenuModel>().apply {
            if (!isDisabledIdCard) {
                add(MenuModel(titleStringResId = R.string.first_page_identity_card,
                    id=Constants.FIRST_PAGE_IDENTITY_IMAGE_TYPE))
                add(MenuModel(titleStringResId = R.string.spouse_info_identity_card,
                    id= Constants.SPOUSE_INFO_IDENTITY_IMAGE_TYPE))
            }
            if (!isDisabledMarriageContract) {
                add(MenuModel(titleStringResId=R.string.spouses_info_marriage_certificate,
                    id=  Constants.SPOUSES_INFO_MARRIAGE_CERTIFICATE_IMAGE_TYPE))
            }
        }

    fun loadUserInfo(data: RegistryDataModel) {
            dependentUserName = data.firstName
            dependentUserFamily = data.lastName
            dependentFatherName = data.fatherName
    }
}

