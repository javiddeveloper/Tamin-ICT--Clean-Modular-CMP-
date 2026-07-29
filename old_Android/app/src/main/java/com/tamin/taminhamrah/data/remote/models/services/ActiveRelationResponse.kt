package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class ActiveRelationResponse : ListDataModel<ActiveRelation>()

@Parcelize
data class ActiveRelation (
    var lastName: String? = null,
    var relationWithTaminId: Int? = null,
    var endDate: String? = null,
    var workshopJob: @RawValue Any? = null,
    var dateinmohlat:  @RawValue Any? = null,
    var workshopName: String? = null,
    var paymentSequence:  @RawValue Any? = null,
    var dependentTypeId:  @RawValue Any? = null,
    var parentIsPensioner:  @RawValue Any? = null,
    var organizationId: String? = null,
    var dateinmohlatTime:  @RawValue Any? = null,
    var insuranceId: String? = null,
    var workshopHelp:  @RawValue Any? = null,
    var pensionerId:  @RawValue Any? = null,
    var id: Int? = null,
    var birthDateTime:  @RawValue Any? = null,
    var mohlatDateTime:  @RawValue Any? = null,
    var dependentType:  @RawValue Any? = null,
    var endDateTime:  @RawValue Any? = null,
    var birthDate: String? = null,
    var parentId: String? = null,
    var firstName: String? = null,
    var mohlatDate:  @RawValue Any? = null,
    var startDateTime:  @RawValue Any? = null,
    var nationalId: String? = null,
    var unemploymentAmount:  @RawValue Any? = null,
    var organization: @RawValue Organization? = null,
    @SerializedName("relationWithTamin")
    var relationWithTamin: @RawValue RelationWithTaminInfo? = null,
    var healthDateTime:  @RawValue Any? = null,
    var workshopId: String? = null,
    var ageFlag: Int? = null,
    var parentNationalId:  @RawValue Any? = null,
    var healthDate:  @RawValue Any? = null,
    var startDate: String? = null

) :Parcelable{

    fun createKeyValue(): List<KeyValueModel> {
        return buildList {
            organization?.organizationName?.let { add(KeyValueModel("نام واحد سازمانی", it)) }

            add(KeyValueModel("نام و نام خانوادگی", getFullName()))

            insuranceId?.let { add(KeyValueModel("شماره بیمه", it)) }
            startDate?.let { add(KeyValueModel("از تاریخ", it)) }
            startDateTime?.let { add(KeyValueModel("تاریخ و زمان شروع", getLocalDate(it.toString()))) }
            endDate?.let { add(KeyValueModel("تا تاریخ", getLocalDate(it))) }
            endDateTime?.let { add(KeyValueModel("تاریخ و زمان پایان", getLocalDate(it.toString()))) }

//            parentId?.let { add(KeyValueModel("شماره بیمه شده اصلی", it)) }
//            parentNationalId?.let { add(KeyValueModel("کد ملی سرپرست", it.toString())) }
//            parentIsPensioner?.let { add(KeyValueModel("سرپرست بازنشسته است", it.toString())) }

//            workshopId?.let { add(KeyValueModel("شماره کارگاه", it)) }
//            workshopName?.let { add(KeyValueModel("نام کارگاه", it)) }
//            workshopJob?.let { add(KeyValueModel("شغل کارگاه", it.toString())) }
//            workshopHelp?.let { add(KeyValueModel("کمک کارگاه", it.toString())) }

//            organizationId?.let { add(KeyValueModel("شناسه سازمان", it)) }

//            id?.let { add(KeyValueModel("شناسه", it.toString())) }
//            nationalId?.let { add(KeyValueModel("کد ملی", it)) }
//            ageFlag?.let { add(KeyValueModel("پرچم سن", it.toString())) }

//            dependentTypeId?.let { add(KeyValueModel("شناسه نوع وابستگی", it.toString())) }
//            dependentType?.let { add(KeyValueModel("نوع وابستگی", it.toString())) }
//
//            pensionerId?.let { add(KeyValueModel("شناسه بازنشسته", it.toString())) }
//            unemploymentAmount?.let { add(KeyValueModel("مبلغ بیکاری", it.toString())) }

            birthDate?.let { add(KeyValueModel("تاریخ تولد", getLocalDate(it))) }
            birthDateTime?.let { add(KeyValueModel("تاریخ و زمان تولد", getLocalDate(it.toString()))) }

//            dateinmohlat?.let { add(KeyValueModel("تاریخ انقضا/مهلت", it.toString())) }
//            dateinmohlatTime?.let { add(KeyValueModel("زمان انقضا/مهلت", it.toString())) }
//            mohlatDate?.let { add(KeyValueModel("تاریخ مهلت", it.toString())) }
//            mohlatDateTime?.let { add(KeyValueModel("زمان مهلت", it.toString())) }

//            relationWithTaminId?.let { add(KeyValueModel("شناسه ارتباط با تأمین", it.toString())) }
//            add(KeyValueModel("ارتباط با تأمین (نام از تابع)", getRelationName()))

            relationWithTamin?.let { info ->
                info.relationDescription?.let {
                    add(KeyValueModel("توضیحات ارتباط با تأمین", it))
                }
//                info.id?.let { add(KeyValueModel("شناسه ارتباط (درون)", it.toString())) }
//                info.status?.let { add(KeyValueModel("وضعیت ارتباط", it)) }
//                info.createdBy?.let { add(KeyValueModel("ایجاد شده توسط", it)) }

                info.statusDate?.let { add(KeyValueModel("تاریخ وضعیت", getLocalDate(it.toString()))) }
                info.creationTime?.let { add(KeyValueModel("زمان ایجاد", it.toString())) }
                info.lastModificationTime?.let { add(KeyValueModel("زمان آخرین ویرایش", it.toString())) }
                info.lastModifiedBy?.let { add(KeyValueModel("آخرین ویرایشگر", it.toString())) }
            }

            healthDate?.let { add(KeyValueModel("تاریخ وضعیت سلامت", getLocalDate(it.toString()))) }
            healthDateTime?.let { add(KeyValueModel("تاریخ و زمان وضعیت سلامت", it.toString())) }

            paymentSequence?.let { add(KeyValueModel("توالی پرداخت", it.toString())) }
        }
    }

    fun hasRelation(): Boolean {
        return !relationWithTamin?.relationDescription.isNullOrBlank()
    }

    fun getFullName(): String {
        return "$firstName $lastName"
    }
    fun getRelationName(): String {
        return relationWithTamin?.relationDescription?:"فاقد ارتباط فعال"
    }

    fun getDate():String{
        return Utility.getDateSeparator(startDate)
    }

    fun getOrganizationName():String{
        return organization?.organizationName?:"-"
    }

    fun getLocalDate(date: String?) = Utility.getDateSeparator(date)

}

data class Organization(
    var parent:  @RawValue Any? = null,
    var actKey: Int? = null,
    var code: String? = null,
    var organizationName: String? = null,
    var children: List<Any>? = null,
    var entityId: String? = null,
    var type: String? = null
)

data class RelationWithTaminInfo(

    var statusDate:  @RawValue Any? = null,
    var creationTime:  @RawValue Any? = null,
    var relationDescription: String? = null,
    var lastModificationTime:  @RawValue Any? = null,
    var lastModifiedBy:  @RawValue Any? = null,
    var baseRelationType: BaseType? = null,
    var baseServiceType: BaseType? = null,
    var baseTendency: BaseType? = null,
    var baseAudienceType: BaseType? = null,
    var createdBy: String? = null,
    var id: Int? = null,
    var status: String? = null,
    var baseInsuranceType: BaseType? = null
)

data class BaseType(
    var statusDate:  @RawValue Any? = null,
    var creationTime:  @RawValue Any? = null,
    var lastModificationTime:  @RawValue Any? = null,
    var createdBy:  @RawValue Any? = null,
    var lastModifiedBy:  @RawValue Any? = null,
    var relationTypeCode: String? = null,
    var relationTypeDescription: String? = null,
    var id: String? = null,
    var status: String? = null,
    var serviceTypeDescription: String? = null,
    var serviceTypeCode: String? = null,
    var audienceTypeDescription: String? = null,
    var audienceTypeCode: String? = null,
    var tendencyCode: String? = null,
    var tendencyDescription: String? = null
)




