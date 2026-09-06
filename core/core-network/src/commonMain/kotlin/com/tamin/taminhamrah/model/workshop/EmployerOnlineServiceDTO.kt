package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Wire models for خدمات غیرحضوری کارفرما (Employer → Online Services), the flow the legacy app
 * calls `employerEservicesAgreement`.
 *
 * The flow is a three-step stepper:
 *   1. کارفرما موبایل و ایمیل جدید را وارد می‌کند و یک کد تایید (تیکت) درخواست می‌کند
 *      -> [WorkShopsApiService.requestEmployerAgreementTicket]
 *   2. کد تایید را وارد می‌کند و مشخصات هویتی کارفرما برگردانده می‌شود
 *      -> [WorkShopsApiService.getEmployerAgreementUserInfo]
 *      همزمان لیست کارگاه‌های بدون قرارداد نمایش داده می‌شود
 *      -> [WorkShopsApiService.getEmployerWorkshopsWithoutContract]
 *   3. کارفرما قوانین را می‌پذیرد و تعهد نهایی ثبت می‌شود
 *      -> [WorkShopsApiService.submitEmployerAgreement]
 *
 * The management side (لیست تعهدهای ثبت‌شده) reuses the existing
 * `workshop-services/employer/get-all-employer-agreement-by-national-id` endpoint
 * ([WorkShopsApiService.getAllEmployerAgreementByNationalId] / [EmployerAgreementDTO]); drilling
 * into one agreement opens the contract rows and the per-workshop agreement list:
 *   -> [WorkShopsApiService.getEmployerWorkshopContractList]
 *
 * Only the columns a screen can actually show are modelled; `ignoreUnknownKeys` drops the rest.
 * Lower-case wire names (`emailaddr`, `mobileno`, `startdate`, `wokshopId`) are the server's own
 * spelling — correcting them breaks deserialisation.
 */

/**
 * `data` of `workshop-services/employer-info/{verificationCode}` — step 2 of the stepper.
 *
 * Returned after the user submits the verification ticket; used to pre-fill and confirm the
 * employer's identity block (نام و نام خانوادگی، کد ملی، موبایل/ایمیل فعلی) before they accept
 * the agreement rules in step 3.
 */
@Serializable
data class EmployerCommitmentInfoDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    /** The employer's *current* registered mobile, shown next to the newly requested one. */
    @SerialName("mobile") val mobile: String? = null,
    /** The employer's *current* registered email, shown next to the newly requested one. */
    @SerialName("email") val email: String? = null,
)

/**
 * One row of `workshop-services/employer-workshops-info-with-out-contract` — step 2 of the stepper.
 *
 * The کارگاه‌های بدون قرارداد list the employer sees while confirming the agreement; tapping a row
 * navigates to its contract list ([WorkShopsApiService.getEmployerWorkshopContractList]).
 */
@Serializable
data class WorkshopWithoutContractDTO(
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    /** Server typo kept verbatim: the key is `wokshopId`, not `workshopId`. */
    @SerialName("wokshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("tel") val tel: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("organization") val organization: WorkshopOrganizationDTO? = null,
)

/** The شعبه / سازمان block nested in [WorkshopWithoutContractDTO]. */
@Serializable
data class WorkshopOrganizationDTO(
    @SerialName("organizationName") val organizationName: String? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("type") val type: String? = null,
)

/**
 * One row of
 * `workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/{workshopId}/{branchCode}`.
 *
 * The پیمانکاران / قراردادهای کارگاه list shown when the employer drills into a workshop from either
 * the without-contract list (step 2) or a registered agreement (management side).
 */
@Serializable
data class WorkshopContractRowDTO(
    @SerialName("contractRow") val contractRow: String? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("endDate") val endDate: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("mobileNo") val mobile: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("tel") val tel: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("workshop") val workshop: EmployerWorkshopDTO? = null,
)

/**
 * One row of
 * `workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}`.
 *
 * The تعهدهای ثبت‌شده‌ی یک کارگاه list — every employer-agreement already registered against a single
 * workshop, opened from the management side (لیست تعهدها -> انتخاب کارگاه).
 */
@Serializable
data class EmployerAgreementByWorkshopDTO(
    @SerialName("pymseq") val paymentSequence: String? = null,
    @SerialName("startdate") val startDate: String? = null,
    @SerialName("letDate") val commitmentDate: String? = null,
    @SerialName("emailaddr") val email: String? = null,
    @SerialName("mobileno") val mobile: String? = null,
    @SerialName("workshop") val workshop: EmployerWorkshopDTO? = null,
)

/**
 * Body of `POST workshop-services/employer-agreement` — step 3 of the stepper.
 *
 * Submitted once the employer accepts the قوانین خدمات غیرحضوری checkbox. [ticketCode] is the
 * verification code entered in step 2; [mobile]/[email] are the newly requested contact details.
 * The legacy request names the mobile key `mobileNo`.
 */
@Serializable
data class EmployerAgreementSubmitRequestDTO(
    @SerialName("mobileNo") val mobile: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("ticketCode") val ticketCode: String? = null,
)
