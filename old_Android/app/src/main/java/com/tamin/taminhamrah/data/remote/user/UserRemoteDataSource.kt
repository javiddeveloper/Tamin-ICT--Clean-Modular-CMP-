package com.tamin.taminhamrah.data.remote.user


import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPerson
import com.tamin.taminhamrah.data.remote.models.profile.TaminRelationResponse
import com.tamin.taminhamrah.data.remote.models.refreshtoken.RevokeResponse
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.PostLogResponse
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.EditMobileResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxResponseNew
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxType
import com.tamin.taminhamrah.data.remote.models.user.LackEntitlementResponse
import com.tamin.taminhamrah.data.remote.models.user.LoginNewReq
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.data.remote.models.user.MyRequestListResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestErrorResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestTypeResponse
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideResponse
import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.repository.LoginRepository

interface UserRemoteDataSource {
    suspend fun getEligibilityTreatment(eligibilityRequest: String): GeneralRes
    suspend fun getProfileInfo(token: String): ProfileResponse
    suspend fun checkUpdate(token: String): CheckUpdateResponse
    suspend fun postDeviceDataModel(token: String, deviceData: DeviceDetailModel): PostLogResponse
    suspend fun signIn(url: String, loginReq: LoginNewReq): LoginResponse
    suspend fun signOut(token: String): GeneralRes
    suspend fun changeMobile(mobile: String, token: String): EditMobileResponse
    suspend fun fetchTaminRelation(token: String): TaminRelationResponse
    suspend fun sendImageRequest(
        branchCode: String,
        serialNumberStr: String,
        token: String
    ): GeneralRes


    suspend fun testMenu(): String
    suspend fun verifyChangeMobileCode(
        request: VerifyMobileReq,
        token: String
    ): GeneralRes

    suspend fun getMyRequestList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): MyRequestListResponse

    suspend fun getSmartGuideList(
        token: String,
        requestType: Int?,
        requestStatus: String?,
        isPublic: Boolean?
    ): SmartGuideResponse

    suspend fun getRequestTypeList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): RequestTypeResponse

    suspend fun getMyRequestErrorList(
        token: String,
        filter: String
    ): RequestErrorResponse

    // suspend fun getInboxPaging(token: String ,page: String,start: String,pageSize:String): Resource<BaseListResponse<InboxResponse>?>

    suspend fun getInboxNew(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InboxResponseNew

    suspend fun getRelatedPersons(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): RelatedPerson


    suspend fun getInboxSize(token: String): InboxSizeResponse


    suspend fun getMyRequestPDF(
        token: String,
        id: String
    ): InboxPdfItem

    suspend fun deleteMyRequest(
        token: String,
        id: String
    ): DeleteItemResponse


    suspend fun inboxInquiryLicense(
        token: String,
        id: String,
        body: LoginRepository.InboxReqOperation
    ): InquiryLicenseResponse

    suspend fun getInboxSystemList(token: String): Resource<List<InboxType>?>
    suspend fun getInboxSubjectList(token: String): Resource<List<InboxType>?>
    suspend fun getUserProfileImage(token: String): GeneralStringRes
    suspend fun revoke(accessToken: String?, refreshToken: String?): RevokeResponse

    suspend fun getLackEntitlement(
        token: String,
        nationalCode: String
    ): LackEntitlementResponse
}
