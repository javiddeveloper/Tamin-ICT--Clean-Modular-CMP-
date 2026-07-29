package com.tamin.taminhamrah.data.remote.models.profile

import android.content.Context
import android.graphics.Bitmap
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.ui.treatment.model.TreatmentCardDataModel
import com.tamin.taminhamrah.utils.UiUtils.setQrCodeBitmap

class RelatedPerson : ListDataModel<RelatedPersonInfo>()

data class RelatedPersonInfo(
    var request: Any,
    var relationWithTamin: RelationWithTamin?,
    var id: Long
){
    fun getDependantList(): ArrayList<MenuModel> {
        val itemList = java.util.ArrayList<MenuModel>()
        relationWithTamin?.personal?.apply {
            itemList.add(MenuModel("$firstName $lastName - $nationalId",id="$nationalId"))
        }

        return itemList
    }
}

data class RelationWithTamin(
    var request: Request? = null,
    var varcreationTime: Long? = null,
    var endDate: Long? = null,
    var lastModificationTime: Long? = null,
    var logicalControlStatus: Any? = null,
    var endConfirmed: Boolean? = null,
    var confirmed: Boolean? = null,
    var branch: Any? = null,
    var organizationId: String? = null,
    var recognizeDate: Any? = null,
    var recognizeMethod: Any? = null,
    var dateOfStart: Long? = null,
    var endReasonType: Any? = null,
    var insuranceId: String? = null,
    var id2: Int? = null,
    var id: Long? = null,
    var newInsuranceId: Boolean? = null,
    var subDominant: SubDominant? = null,
    var edited: Boolean? = null,
    var work: Any? = null,
    var lastModifiedBy: String? = null,
    var isFirst: Boolean? = null,
    var personal: Personal? = null,
    var printed: Boolean? = null,
    var deleted: Boolean? = null,
    var createdBy: String? = null,
    var relationWithTamin: RelationTaminModel? = null
) {
    fun treatmentCardsInfo(qrCode: Bitmap?, context: Context, treatmentSupport: Boolean?) =
        TreatmentCardDataModel(
            name = "${personal?.firstName} ${personal?.lastName}",
            nationalCode = personal?.nationalId,
            insuranceNumber = insuranceId,
            isTreatmentSupport = treatmentSupport,
            isMainUser = false,
            qrCodeFilePath = setQrCodeBitmap(qrCode,context),
        )
}
data class RelationTaminModel (
    var relationDescription: String? = null,
    var baseRelationType: BaseRelationType? = null,
    var baseTendency: BaseTendency? = null,
    var baseAudienceType: BaseAudienceType? = null,
    var createdBy: String? = null,
    var id: Int? = null,
    var status: String? = null
)

class BaseTendency {
    var statusDate: Any? = null
    var tendencyCode: String? = null
    var creationTime: Any? = null
    var lastModificationTime: Any? = null
    var createdBy: Any? = null
    var lastModifiedBy: Any? = null
    var id: String? = null
    var tendencyDescription: String? = null
    var status: String? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}


data class BailType(
    var statusDate: String? = null,
    var code: String? = null,
    var description: String? = null,
    var status: String? = null

)

data class BaseAudienceType(

    var statusDate: Any? = null,
    var audienceTypeDescription: String? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null,
    var audienceTypeCode: String? = null,
    var id: String? = null,
    var status: Any? = null

)


data class BaseRelationType(

    var statusDate: Any? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null,
    var relationTypeCode: String? = null,
    var relationTypeDescription: String? = null,
    var id: String? = null,
    var status: String? = null
)

data class Gender(

    var statusDate: Long? = null,
    var genderCode: String? = null,
    var genderDesc: Any? = null,
    var status: Long? = null

)

data class Nation(

    var statusDate: String? = null,
    var nationDesc: String? = null,
    var nationCode: String? = null,
    var status: Any? = null,

    )

data class Personal(

    var lastName: String? = null,
    var fatherName: String? = null,
    var country: Any? = null,
    var cityOfIssue: Any? = null,
    var idCardSerial1: String? = null,
    var gender: Gender? = null,
    var creationTime: Long? = null,
    var idCardSerial2: String? = null,
    var nation: Nation? = null,
    var lastModificationTime: Long? = null,
    var militaryService: Any? = null,
    var saveMethod: Int? = null,
    var educations: List<Any>? = null,
    var baseBloadGroup: Any? = null,
    var confirmed: Boolean? = null,
    var ssn: String? = null,
    var cityOfBirth: Any? = null,
    var marriage: Any? = null,
    var id: Long? = null,
    var forienRisuid: Any? = null,
    var personalLogs: List<PersonalLog>? = null,
    var dateOfDead: Any? = null,
    var languages: List<Any>? = null,
    var medicalExamination: List<Any>? = null,
    var lastModifiedBy: String? = null,
    var dateOfBirth: Long? = null,
    var relatives: List<Any>? = null,
    var firstName: String? = null,
    var foreignId: Any? = null,
    var nationalId: String? = null,
    var createdBy: String? = null,
    var idCardNumber: String? = null,
    var relationWithTamins: List<Any>? = null,
    var accounts: List<Any>? = null,
    var contacts: List<Any>? = null,
    var isForien: Any? = null


)
data class PersonalLog(
    var lastName: String? = null,
    var fatherName: String? = null,
    var country: Any? = null,
    var cityOfIssue: Any? = null,
    var cityOfBirthId: String? = null,
    var idCardSerial1: String? = null,
    var gender: Gender? = null,
    var creationTime: Long? = null,
    var idCardSerial2: Long? = null,
    var nation: Nation? = null,
    var lastModificationTime: Long? = null,
    var confirmed: Boolean? = null,
    var countryId: String? = null,
    var ssn: String? = null,
    var organizationId: String? = null,
    var cityOfBirth: Any? = null,
    var marriage: Any? = null,
    var lastModifiedUser: Any? = null,
    var id: Long? = null,
    var forienRisuid: Any? = null,
    var createdUser: Any? = null,
    var dateOfDead: Any? = null,
    var lastModifiedBy: String? = null,
    var personal: Int? = null,
    var dateOfBirth: Long? = null,
    var firstName: String? = null,
    var foreignId: Any? = null,
    var nationalId: String? = null,
    var createdBy: String? = null,
    var cityOfIssueId: String? = null,
    var idCardNumber: String? = null,
    var isForien: Any? = null,
    )


data class ProcessStatus(

    var statusDesc: String? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null,
    var id: Int? = null,


    )

data class Request(

    var deadDate: Any? = null,
    var requestType: RequestType? = null,
    var processStatus: ProcessStatus? = null,
    var creationTime: Long? = null,
    var lastModificationTime: Long? = null,
    var createdByUser: Any? = null,
    var lastModifiedBy: String? = null,
    var personal: Int? = null,
    var dateOfBirth: Any? = null,
    var requestGroup: Any? = null,
    var portalRequestId: Any? = null,
    var ssn: String? = null,
    var foreignId: Any? = null,
    var deleted: Boolean? = null,
    var nationalId: String? = null,
    var isSendToBranch: Boolean? = null,
    var processId: Any? = null,
    var createdBy: String? = null,
    var organization: Any? = null,
    var modifiedByUser: Any? = null,
    var id: Long? = null,
    var forienRisuid: Any? = null,
    var mainOrganization: String? = null,
    var requestFrom: RequestFrom? = null,


    )

data class RequestFrom(

    var requestFromDesc: String? = null,
    var requestFromId: Int? = null,
    var requestFromCode: String? = null
)

data class RequestType(

    var requestTypeDesc: String? = null,
    var requestTypeId: Int? = null,
    var requestTypeCode: String? = null

)

data class SubDominant(

    var howManyChildrenBefore: Any? = null,
    var bailExpireControl: Any? = null,
    var creationTime: Long? = null,
    var lastModificationTime: Long? = null,
    var parentInsuranceId: String? = null,
    var dependentType: Any? = null,
    var lastModifiedBy: String? = null,
    var bailType: BailType? = null,
    var createdBy: String? = null,
    var relationWithTamin: Int? = null,
    var personalOfMainAudience: Any? = null,
    var dateOfExpire: Any? = null

)


/*
fun RelatedPerson.asDomainModel(): RelatedPersonModel {
    return RelatedPersonModel(
        id = relationWithTamin.id,
        fullname ="${relationWithTamin.personal?.firstName} ${relationWithTamin.personal?.lastName}" ,
        relation = getRelationTitle(relationWithTamin.relationWithTamin?.baseTendency?.tendencyCode, relationWithTamin.personal?.gender?.genderCode),
        nationalCode = relationWithTamin.personal?.nationalId
    )
}

fun List<RelatedPerson>.asDomainModel(): List<RelatedPersonModel> {
    return map {
        it.asDomainModel()
    }
}
*/

fun getRelationTitle(tendencyCode: String?, gendercode: String?): String {
    var relation = ""
    when (tendencyCode) {
        "100" -> {
            relation = "همسر"

        }
        "101" -> {
            relation = "فرزند پسر"

        }
        "102" -> {
            relation = "دختر"

        }
        "103" -> {
            relation = "همسر"

        }
        "104" -> {
            relation = "فرزند پسر"

        }
        "105" -> {
            relation = "دختر"

        }
        "106" -> {
            if (gendercode === "02") {
                relation = "مادر"
            } else if (gendercode === "01") {
                relation = "پدر"
            } else {
                relation = "والدین"
            }

        }
        "107" -> {
            relation = "همسر"

        }
        "108" -> {
            relation = "همسر"

        }
        "109" -> {
            relation = "همسر"

        }
        "110" -> {
            if (gendercode === "02") {
                relation = "مادر"
            } else if (gendercode === "01") {
                relation = "پدر"
            } else {
                relation = "والدین"
            }

        }
        "111" -> {
            if (gendercode === "01") {
                relation = "فرزند پسر"
            } else if (gendercode === "02") {
                relation = "فرزند دختر"
            } else {
                relation = "فرزند"
            }

        }
        "112" -> {
            if (gendercode === "01") {
                relation = "فرزند پسر"
            } else if (gendercode === "02") {
                relation = "فرزند دختر"
            } else {
                relation = "فرزند"
            }

        }
        "117" -> {
            if (gendercode === "01") {
                relation = "فرزند پسر"
            } else if (gendercode === "02") {
                relation = "فرزند دختر"
            } else {
                relation = "فرزند"
            }

        }
        "123" -> {
            relation = "فرزند خوانده"

        }
        "124" -> {
            relation = "بازمانده"

        }
        "118" -> {
            relation = "فرزند خوانده"

        }
        "133" -> {
            relation = "فرزند خوانده"

        }
    }

    return relation
}


