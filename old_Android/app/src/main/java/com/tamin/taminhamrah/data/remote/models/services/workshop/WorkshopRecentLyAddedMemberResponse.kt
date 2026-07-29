package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoReq
import com.tamin.taminhamrah.data.remote.models.employer.PersonalInfo
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.HelperDate
import java.util.Date

class WorkshopRecentLyAddedMemberResponse : ListDataModel<WorkshopNewMember>()

data class WorkshopNewMember(
    var id: Long? = null,
    var dateOfStart: Long? = null,
    var endDate: Any? = null,
    var endReasonType: Any? = null,
    var insuranceId: String? = null,
    var personal: Personal? = null,
    var relationWithTamin: Int? = null,
    var organizationId: String? = null,
    var workshopId: String? = null,
    var workshopName: Any? = null,
    var job: String? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null
) {

    fun getTitle(title: String?): String {
        return title ?: "-"
    }

    fun getFullName(): String {
        val name = "${personal?.firstName} ${personal?.lastName}"
        return name.ifBlank { "-" }
    }

    fun getLocalDate(title: Long?): String {
        return ConvertDate.convertTimestampToPersianDate(title ?: 0)
    }

    fun createKeyValue(item: WorkshopNewMember): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(
            KeyValueModel(
                "نام و نام خانوادگی",
                "${item.personal?.firstName} ${item.personal?.lastName}"
            )
        )
        keyValueList.add(KeyValueModel("شماره ملی", item.personal?.nationalId ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ تولد",
                ConvertDate.convertTimestampToPersianDate(item.personal?.dateOfBirth ?: 0) ?: "-"
            )
        )
        keyValueList.add(KeyValueModel("شماره بیمه", item.insuranceId ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "تاریخ ثبت",
                ConvertDate.convertTimestampToPersianDate(item.personal?.request?.creationTime ?: 0)
                    ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "وضعیت نام نویسی",
                item.personal?.request?.status?.requestDesc ?: "-"
            )
        )
        return keyValueList
    }

    fun asDomainModel(): NewInsuredUserInfoReq {

        val dataModel = NewInsuredUserInfoReq(
            PersonalInfo(
                id = personal?.id,
                cityOfBirthId = personal?.cityOfBirthId,
                cityOfIssueId = personal?.cityOfIssueId,
                countryId = personal?.countryId,
                dateOfBirth = if (personal?.dateOfBirth != null)
                    HelperDate.convertServerDateFormatToMobileDateFormat(Date(personal?.dateOfBirth!!))
                else null,
                firstName = personal?.firstName,
                lastName = personal?.lastName,
                nation = personal?.nation,
                nationalId = personal?.nationalId
            ),

            RelationWithTamin2(
                id = id.toString(),
                dateOfStart = if (dateOfStart != null)
                    HelperDate.convertServerDateFormatToMobileDateFormat(Date(dateOfStart!!))
                else null,
                job = job,
                organizationId = organizationId,
                workshopId = workshopId,
                personal = Personal2(personal?.id)
            )
        )
        dataModel.dateOfBirthTimeStamp = personal?.dateOfBirth
        dataModel.localDateOfStartJobTimeStamp = dateOfStart
        return dataModel
    }
}

data class Personal2(
    var id: Long? = null
)

data class Personal(
    var id: Long? = null,
    var nationalId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: Any? = null,
    var idCardSerial1: Any? = null,
    var idCardSerial2: Any? = null,
    var fatherName: Any? = null,
    var dateOfBirth: Long? = null,
    var countryId: String? = null,
    var cityOfBirthId: String? = null,
    var cityOfIssueId: String? = null,
    var foreignId: Any? = null,
    var nation: String? = null,
    var isForien: Any? = null,
    var gender: Any? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: String? = null,
    var lastModifiedBy: Any? = null,
    var refrenceCode: String? = null,
    var request: Request? = null,
    var parentId: Any? = null,
    var dependentType: Any? = null,
    var bailType: Any? = null,
    var accounts: Any? = null,
    var contacts: Any? = null,
    var educations: Any? = null,
    var relationWithTamins: RelationWithTamin2? = null,
    var user: Any? = null,
    var ssn: Any? = null,
    var dependency: Any? = null,
    var portalRequestId: Any? = null,
    var requestFileList: Any? = null,
    var branchCode: Any? = null
)

data class Request(
    var id: Long? = null,
    var createdBy: String? = null,
    var creationTime: Long? = null,
    var lastModifiedBy: String? = null,
    var lastModificationTime: Any? = null,
    var refCode: String? = null,
    var userName: String? = null,
    var status: Status? = null,
    var title: String? = null,
    var comment: Any? = null,
    var template: Any? = null,
    var requestType: RequestType? = null,
    var deliverCode: Any? = null,
    var refrenceid: Any? = null,
    var requestDetails: Any? = null,
    var requestChid: Any? = null,
    var fullName: Any? = null,
    var createByName: Any? = null
)

data class RequestType(
    var createdBy: Any? = null,
    var creationTime: Any? = null,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Any? = null,
    var id: Int? = null,
    var title: String? = null,
    var description: String? = null
)

data class Status(
    var requestCode: String? = null,
    var requestDesc: String? = null
)
data class RelationWithTamin2(
    var id: String = "",
    var personal: Personal2 = Personal2(),
    var organizationId: String? = null,
    var workshopId: String? = null,
    var dateOfStart: String? = null,
    var job: String? = null
)
