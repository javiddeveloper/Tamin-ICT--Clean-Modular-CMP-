/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiQueryParamDN(
    val page: Int = 0,
    val start: Int = 0,
    val limit: Int = 10,
    val filters: List<ApiFilterDN> = emptyList(),
    val sorts: List<ApiSortDN> = emptyList(),
)

@Serializable
enum class SortDirection(val value: String) {
    @SerialName("ASC") ASC("ASC"),
    @SerialName("DESC") DESC("DESC"),
}

@Serializable
data class ApiSortDN(
    val property: String,
    val direction: SortDirection,
)

@Serializable
enum class FilterOperator(val value: String) {
    @SerialName("EQUAL") EQUAL("EQUAL"),
    @SerialName("CONTAINS") CONTAINS("CONTAINS"),
    @SerialName("LIKE") LIKE("LIKE"),
    @SerialName("EQ") EQ("EQ"),
    @SerialName("equal") EQUAL_LOWER("equal")
}

@Serializable
enum class FilterProperty(val key: String) {
    @SerialName("serialId") SERIAL_ID("serialId"),
    @SerialName("mobile") MOBILE("mobile"),
    @SerialName("cityCode") CITY_CODE("cityCode"),
    @SerialName("cityName") CITY_NAME("cityName"),
    @SerialName("provinceCode") PROVINCE_CODE("provinceCode"),
    // Lower-case 'c' on purpose: proxy/models/city names the field `provincecode`, which is also
    // why CityDto's @SerialName is spelled that way. Correcting it stops the filter working.
    @SerialName("provincecode") PROVINCE_CODE_CITY("provincecode"),
    @SerialName("pensionerId") PENSIONER_ID("pensionerId"),
    @SerialName("startDate") START_DATE("startDate"),
    @SerialName("operation") OPERATION("operation"),
    @SerialName("refCode") REF_CODE("refCode"),
    @SerialName("requestType.id") REQUEST_TYPE_ID("requestType.id"),
    @SerialName("workshop.workshopId") WORKSHOP_ID("workshop.workshopId"),
    @SerialName("workshopId.workshopId") WORKSHOPID_ID("workshopId.workshopId"),
    @SerialName("workshop.branchCode") WORKSHOP_BRANCH_CODE("workshop.branchCode"),
    @SerialName("workshopId.branchCode") WORKSHOPID_BRANCH_CODE("workshopId.branchCode"),
    @SerialName("workshop.workshopStatus.workshopStatusCode") WORKSHOP_STATUS_CODE("workshop.workshopStatus.workshopStatusCode"),
    @SerialName("workshopId") PAYMENT_WORKSHOP_ID("workshopId"),
    @SerialName("branchCode") PAYMENT_BRANCH_CODE("branchCode"),
    @SerialName("workshopCode") WORKSHOP_CODE("workshopCode"),
    @SerialName("nationalCode") NATIONAL_CODE("nationalCode"),
    @SerialName("birthDate") BIRTH_DATE("birthDate"),
    @SerialName("payIdFrom") PAY_ID_FROM("payIdFrom"),
    @SerialName("payIdTo") PAY_ID_TO("payIdTo"),
    @SerialName("docDateFrom") DOC_DATE_FROM("docDateFrom"),
    @SerialName("docDateTo") DOC_DATE_TO("docDateTo"),
    @SerialName("debitReason") DEBIT_REASON("debitReason"),
    @SerialName("paymentSheetStatus") PAYMENT_SHEET_STATUS("paymentSheetStatus"),
    @SerialName("premiumTypeCode") PREMIUM_TYPE_CODE("premiumTypeCode"),
    @SerialName("paymentType") PAYMENT_TYPE("paymentType"),
    @SerialName("insuranceNumber") INSURANCE_NUMBER("insuranceNumber"),
    @SerialName("endDate") END_DATE("endDate"),
    @SerialName("recipient") RECIPIENT("recipient"),
    @SerialName("branchName") BRANCH_NAME("branchName"),
    @SerialName("target") TARGET("target"),
    @SerialName("statusCode") STATUS_CODE("statusCode"),
    @SerialName("request.id") REQUEST_ID("request.id"),
    @SerialName("requestType") REQUEST_TYPE("requestType"),
    @SerialName("requestStatus") REQUEST_STATUS("requestStatus"),
    @SerialName("isPublic") IS_PUBLIC("isPublic"),
    @SerialName("dependencyDesc") DEPENDENCY_DESC("dependencyDesc"),

    // Workshop member / stakeholder / absentee-registration lists. Each list addresses the same
    // two people-columns under a different prefix, which is why there is one entry per list
    // rather than a shared "nationalId".
    @SerialName("insurance.id") INSURANCE_ID("insurance.id"),
    @SerialName("insurance.nationalId") INSURANCE_NATIONAL_ID("insurance.nationalId"),
    @SerialName("personal.nationalId") PERSONAL_NATIONAL_ID("personal.nationalId"),
    @SerialName("personal.request.status.requestCode")
    PERSONAL_REQUEST_STATUS_CODE("personal.request.status.requestCode"),

    /** The branch, on the `employers` list only — every other workshop list calls it a branch code. */
    @SerialName("organizationId") ORGANIZATION_ID("organizationId"),

    @SerialName("debitNumber") DEBIT_NUMBER("debitNumber"),
    @SerialName("peymanSequence") PEYMAN_SEQUENCE("peymanSequence"),
}


@Serializable
data class ApiFilterDN(
    val property: FilterProperty,
    val value: String,
    val operator: FilterOperator,
)
