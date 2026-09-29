package com.tamin.taminhamrah.feature.profile.fake

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/** Backs the profile screens' ViewModels: the calls they make are configurable, the rest inert. */
internal class FakeProfileUserRepository : UserRepository {
    var relationError: Throwable? = null
    var sendImageError: Throwable? = null
    var sendImageGate: CompletableDeferred<Unit>? = null
    var sendImageCallCount = 0
    var lastSentBranchCode: String? = null
    var lastSentSerialId: String? = null

    var profileImage = ""

    /** Emitted by the identity call when set; the call completes without emitting when null. */
    var identityInfo: IdentityInfoDN? = null
    var identityError: Throwable? = null
    var activeRelations: List<ActiveRelationDN> = emptyList()

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        relationError?.let { throw it }
        emit(
            TaminRelationDN(
                firstName = "سعید",
                lastName = "نامی",
                nationalId = "0020939111",
                insuranceId = "12345678",
                brhCode = "0010",
            )
        )
    }

    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flowOf(
        SubdominantDN(
            list = listOf(
                SubdominantItemDN(
                    id = 101L,
                    firstName = "زهره",
                    lastName = "تابانی",
                    nationalCode = "0061777943",
                    relationDescription = "همسر",
                ),
                SubdominantItemDN(
                    id = 102L,
                    firstName = "آرش",
                    lastName = "تابانی",
                    nationalCode = "0024551902",
                    relationDescription = "فرزند پسر",
                ),
            ),
            total = "2",
        )
    )

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow {
        sendImageCallCount++
        sendImageGate?.await()
        sendImageError?.let { throw it }
        lastSentBranchCode = branchCode
        lastSentSerialId = serialId
        emit("OK")
    }

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        identityError?.let { throw it }
        identityInfo?.let { emit(it) }
    }

    override suspend fun getUserProfileImage(): Flow<String> = flowOf(profileImage)
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flowOf(activeRelations)

    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {}
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flowOf("")
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): PdfDownloadDN = PdfDownloadDN()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {}
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flowOf(CurrentUserDN())
    override suspend fun registerBankAccount(accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}
