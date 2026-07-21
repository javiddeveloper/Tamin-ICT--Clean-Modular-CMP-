package com.tamin.taminhamrah.data.remote.models.employer

import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel

class InsuredDocsResponse : ListDataModel<InsuredDoc>()

data class InsuredDoc(
    var documentFile: DocumentFile? = null,
    var documentType: String? = null,
    var id: Long? = null,
    var personal: Any? = null//Personal Object or personalId
)

data class DocumentFile(
    var createdBy: String? = null,
    var creationTime: Long? = null,
    var id: String? = null,
    var image: String? = null,
    var lastModificationTime: Long? = null,
    var lastModifiedBy: Any? = null
)

fun InsuredDoc.asDomainModel(): UploadedImageModel {
    return UploadedImageModel(
        guid = documentFile?.id,
        imageType = documentType,
        imageName = getDocTitle(documentType),
        /*imageUri=,
        orgUri=,*/
        isSelected = false,
        isBase64 = true,
        Base64Value = documentFile?.image
    )
}


fun List<InsuredDoc>.asDomainModel(): List<UploadedImageModel> {
    return map {
        it.asDomainModel()
    }
}

fun getDocTitle(documentType: String?): String {
    return when (documentType) {
        "01" -> "تصویر پرسنلی"
        "02" -> "تصویر اول اظهارنامه"
        "08" -> "تصویر دوم اظهارنامه"
        "03" -> "تصویر صفحه اول شناسنامه"
        "04" -> "تصویر صفحه دوم شناسنامه"
        "07" -> "تصویر توضیحات شناسنامه"
        "05" -> "تصویر روی کارت ملی"
        "06" -> "تصویر پشت کارت ملی"
        else -> ""
    }
}

