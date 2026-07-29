package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model

import android.net.Uri
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16DocumentTitleResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.ObjectionPhoto
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16RequestModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopInfoDebtArticle16Model
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel

class DebtArticle16DataModel {
    var employeeName = ""
    var workshopName = ""
    var workshopId = ""
    var workshopAddress = ""
    var branchCode = ""
    var debtNumber = ""
    var statusCode = ""
    var tempImageName: String = ""
    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null
    var tempImageType: String = ""
    var detailDebtInfo: WorkshopsDebtListModel? = null
    val documentList by lazy { ArrayList<UploadedImageModel>() }
    private val imageTitleList by lazy { ArrayList<MenuModel>() }
    val imageList by lazy { ArrayList<ObjectionPhoto>() }

    fun initialInfo(data: WorkShopInfoDebtArticle16Model) {
        employeeName = data.employerName ?: "_"
        workshopName = data.workshopName ?: "_"
        workshopId = data.workshopId ?: "_"
        workshopAddress = data.lastAddress ?: "_"
    }

    fun loadImageInfo(guid: String) {
        documentList.add(
            UploadedImageModel(
                guid = guid,
                imageType = tempImageType,
                imageUri = tempImageUri,
                imageName = tempImageName,
                orgUri = tempImageOriginalUri
            )
        )
    }

    fun initImageTitleList(data: Article16DocumentTitleResponse) {
        imageTitleList.clear()
        data.investigationItems?.forEach { imageModel ->
            imageTitleList.add(MenuModel(title = imageModel?.name, id = imageModel?.value))
        }
    }

    fun getRequestRegisterModel(): RegisterArticle16RequestModel? {
        detailDebtInfo?.apply {
            return RegisterArticle16RequestModel(
                indebtednessAmount = indebtednessAmount,
                insuranceAmount = insuranceAmount,
                branchCode = branchCode,
                debitEndDate = debitEndDate,
                debitNumber = debitNumber,
                debitStartDate = debitStartDate,
                debitStepCode = debitStepCode,
                dateExecutiveNotification = dateExecutiveNotification,
                fineAmount = fineAmount,
                kindDoc = kindDoc,
                customerTypeCode = customerTypeCode,
                objectionDate = "", //it is set by default to empty on the eservise's site
                objectionPhotos = imageList,
                objectionType = "3", //it is set by default to 3 on the eservise's site
                orderDate = dateImplementation,
                orderNumber =executiveNumber,
                agreementRow=agreementRow,
                otherAmount = otherAmount,
                workshopId=workshopId
                )
        }
        return null
    }

}
