package com.tamin.taminhamrah.feature.retirementPension.fake

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRepository : UserRepository {
    var identityInfoResult: IdentityInfoDN = IdentityInfoDN(
        cityOfBirthId = "1",
        cityOfIssueId = "1",
        countryId = null,
        dateOfBirth = 123456789L,
        fatherName = "حسن",
        firstName = "علی",
        gender = "01",
        id = 1,
        idCardNumber = "1234",
        idCardSerial1 = null,
        idCardSerial2 = null,
        lastName = "محمدی",
        nationalId = "0012345678",
        ssn = "99887766",
    )
    var insuredActiveBranchResult: List<InsuredActiveBranchDN> = listOf(
        InsuredActiveBranchDN(
            branchCode = "0100",
            branchName = "شعبه ۱ تهران",
            workshopCode = "1024300719",
            workshopName = "کارگاه تست",
        ),
    )

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow { emit(identityInfoResult) }
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flow { emit(insuredActiveBranchResult) }

    override suspend fun getUserProfileImage(): Flow<String> = flow { emit("") }
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {}
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow { emit("") }
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {}
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flow { emit("") }
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flow {}
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flow { emit(emptyList()) }
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flow { emit(emptyList()) }
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flow { emit(emptyList()) }
    override suspend fun downloadDocument(url: String): PdfDownloadDN = PdfDownloadDN()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {}
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flow {}
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flow { emit(false) }
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flow { emit(null) }
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flow { emit("") }
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flow { emit("") }
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flow { emit(emptyList()) }
}
