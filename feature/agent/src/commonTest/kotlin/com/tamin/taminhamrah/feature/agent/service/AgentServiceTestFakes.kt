package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

/**
 * Labels in tests are the resource keys themselves (with arguments after a colon), so an
 * assertion reads which label was used without loading platform resources.
 */
val testStrings = AgentStrings { resource, args ->
    if (args.isEmpty()) resource.key else resource.key + ":" + args.joinToString(",")
}

fun agentParams(
    key: AgentActionKey,
    message: String? = "عنوان",
    filters: List<String> = emptyList(),
    rawData: JsonElement? = null,
) = AgentServiceParams(
    payload = if (filters.isEmpty()) null else buildJsonObject {
        putJsonArray("filter") { filters.forEach { add(JsonPrimitive(it)) } }
        put("itemType", 1)
    },
    rawData = rawData,
    message = message,
    sessionContext = AgentSessionContext(),
    requestedKey = key,
)

/** A wage-history year; [months] are (days, wage) for فروردین onwards. */
fun wageYear(year: String, vararg months: Pair<Int, Long>, workshop: String = "کارگاه $year") = DastmozdInfoItemDN(
    wageDetails = List(12) { index ->
        months.getOrNull(index)?.let { (days, wage) -> WageDetailDN(days.toString(), wage.toString()) }
            ?: WageDetailDN("0", "0")
    },
    hisyear = year,
    id = null, risufname = null, risubirthdate = null, risuidserial2 = null, risuidserial1 = null,
    rwshname = workshop, expcitycode = null, brhcode = null, risuidno = null, risudname = null,
    risuid = null, risulname = null, risunatcode = null, brhname = "شعبه", historytypedesc = "عادی", rwshid = null,
)

/** Only the pension calls the agent services make are implemented; the rest fail loudly. */
class FakeAgentPensionRepository(
    var pensionerId: String? = "P1",
    var inquiry: List<PensionInquiryDN> = emptyList(),
    /** Payslip rows per `YYYYMM` start date. */
    var payRolls: Map<String, List<PayRollDN>> = emptyMap(),
    var edict: EdictPensionerDN? = null,
) : PensionRepository {
    val payRollDates = mutableListOf<String>()
    val edictDates = mutableListOf<String>()

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> = flowOf(inquiry)

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flowOf(listOfNotNull(pensionerId?.let(::PensionIdDN)))

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> {
        val date = filters.first { it.property == FilterProperty.START_DATE }.value
        payRollDates += date
        return flowOf(payRolls[date].orEmpty())
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> {
        edictDates += query.filters.first { it.property == FilterProperty.START_DATE }.value
        return flowOf(edict)
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> = unused()
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = unused()
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = unused()
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = unused()
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = unused()
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> = unused()
    override suspend fun createRetirementRequest(authenticationsCode: Long, form: RetirementRequestFormDN): Flow<RetirementRequestCreatedDN> = unused()
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = unused()
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> = unused()
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> = unused()
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> = unused()
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> = unused()
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> = unused()
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> = unused()
    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> = unused()
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmDN): Flow<DisabilityRequestRefDN?> = unused()
    override suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentDN): Flow<String?> = unused()
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> = unused()
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>): Flow<List<RegisteredMedicalCommissionDN>> = unused()

    private fun unused(): Nothing = error("not used by the agent service under test")
}

/** Serves dependents; every other call fails loudly. */
class FakeDependentsUserRepository(private val dependents: SubdominantDN) : UserRepository {
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flowOf(dependents)

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = unused()
    override suspend fun getUserProfileImage(): Flow<String> = unused()
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = unused()
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = unused()
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = unused()
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = unused()
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = unused()
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = unused()
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = unused()
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = unused()
    override suspend fun downloadDocument(url: String): PdfDownloadDN = unused()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = unused()
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = unused()
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = unused()
    override suspend fun registerBankAccount(accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long): Flow<String?> = unused()
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = unused()
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = unused()
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = unused()

    private fun unused(): Nothing = error("not used by the agent service under test")
}
