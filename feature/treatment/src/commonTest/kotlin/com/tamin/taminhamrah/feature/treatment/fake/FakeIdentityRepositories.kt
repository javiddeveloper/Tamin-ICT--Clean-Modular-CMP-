package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


/**
 * Minimal fake [UserRepository] used only to satisfy `IdentityInfoUseCase` in
 * [com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel]. The identity lookup
 * is only exercised when no user id is stored, so most methods return empty/no-op values.
 */
class FakeUserRepository : UserRepository {
    var identityResult: IdentityInfoDN = IdentityInfoDN(
        cityOfBirthId = null,
        cityOfIssueId = null,
        countryId = null,
        dateOfBirth = null,
        fatherName = null,
        firstName = "کاربر اصلی",
        gender = null,
        id = null,
        idCardNumber = null,
        idCardSerial1 = null,
        idCardSerial2 = null,
        lastName = "کاربر اصلی",
        nationalId = TreatmentTestData.MAIN_NATIONAL_CODE,
        ssn = null
    )

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow { emit(identityResult) }
    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {}
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flow {}
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flowOf("")
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {}
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flowOf("")
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flow {}
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flowOf(
        UserProfileDN(
            entityId = null,
            login = null,
            firstName = "کاربر اصلی",
            lastName = "کاربر اصلی",
            email = null,
            nationalCode = TreatmentTestData.MAIN_NATIONAL_CODE,
            mobile = "09123456789"
        )
    )

    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flowOf(null)
    override suspend fun downloadDocument(url: String): PdfDownloadDN =
        PdfDownloadDN()
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())

}

/** Minimal fake [CityProvinceRepository] dependency of `IdentityInfoUseCase`. */
class FakeCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = flow {}
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {}
    override fun getProvinces(): Flow<List<ProvinceDN>> = flowOf(emptyList())
    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flowOf(emptyList())
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flowOf(CityListResultDN(emptyList()))
}



