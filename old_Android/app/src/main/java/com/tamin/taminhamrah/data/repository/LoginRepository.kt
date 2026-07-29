package com.tamin.taminhamrah.data.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.profile.TaminRelationResponse
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.remote.models.user.LoginNewReq
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestErrorResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestTypeResponse
import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.remote.models.user.asDomainModel
import com.tamin.taminhamrah.data.remote.user.UserRemoteDataSource
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: PreferenceManager
) {

    fun hasValidToken(): Boolean {
        val tokenExpireTime = getTokenExpireTime()
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime < tokenExpireTime
    }

    suspend fun getEligibilityTreatment(eligibilityRequest: String) =
        remoteDataSource.getEligibilityTreatment(eligibilityRequest)


    suspend fun checkUpdate(accessToken: String) =
        remoteDataSource.checkUpdate(
            if (accessToken.isBlank()) TokenHolder.getAccessToken(
                localDataSource
            ) else "Bearer $accessToken"
        )

    suspend fun postDeviceDataModel(deviceData: DeviceDetailModel) =
        remoteDataSource.postDeviceDataModel(
            TokenHolder.getAccessToken(localDataSource),
            deviceData
        )

    suspend fun signIn(url: String, codeFromServer: String, codeVerifier: String): LoginResponse {

        val req = LoginNewReq(codeFromServer = codeFromServer, codeVerifier = codeVerifier)

        val result = remoteDataSource.signIn(url, req)

        return result

    }

    suspend fun getInbox(paramsMap: MutableMap<String, String>?) = remoteDataSource.getInboxNew(
        TokenHolder.getAccessToken(localDataSource), paramsMap
    )


    suspend fun testMenu(): String {
        return remoteDataSource.testMenu()
    }

    //  remoteDataSource.login(LoginReq(username, password, hashCode, appId, code))


    fun saveLoginInfo(
        accessToken: String,
        expiresIn: Long,
        refreshToken: String
    ) {
        TokenHolder.updateTokens(localDataSource, accessToken, refreshToken, expiresIn)
        localDataSource.saveLoginInfo()

    }


    fun getNationalCode() = localDataSource.getNationalCode()
    fun setNationalCode(nationalCode: String) = localDataSource.setNationalCode(nationalCode)
    fun getToken() = TokenHolder.getAccessToken(localDataSource)
    fun getTokenExpireTime() = localDataSource.getTokenExpireTime()

    suspend fun getProfileInfo(
        tempToken: String? = null
    ): ProfileResponse {


        val result =
            remoteDataSource.getProfileInfo(
                if (tempToken.isNullOrBlank()) {
                    TokenHolder.getAccessToken(localDataSource)
                } else {
                    "Bearer $tempToken"
                }
            )

        Timber.tag("debugServiceMultiple").i("result:" + result.data.toString() + " ")
        return result
//        return when (result.status) {
//
//            Resource.Status.SUCCESS -> {
//                result?.data?.gender?.let {
//                    localDataSource.setGender(it)
//                    Log.i("getProfileInfo", " +Login Repository :" + it)
//                }
//                Resource.success(result.data?.asDomainModel())
//            }
//
//            Resource.Status.LOADING -> {
//                Resource.loading()
//            }
//
//            Resource.Status.ERROR -> {
//                Resource.error(result.message)
//            }
//
//            Resource.Status.NEED_REFRESH_TOKEN -> {
//                Resource.needRefreshToken(result.message)
//            }
//            Resource.Status.NEED_NETWORK -> {
//                Resource.needNetwork()
//            }
//        }

    }

    suspend fun editMobile(mobile: String) =
        remoteDataSource.changeMobile(mobile, TokenHolder.getAccessToken(localDataSource))


    suspend fun fetchTaminRelation(): TaminRelationResponse {
        return remoteDataSource.fetchTaminRelation(TokenHolder.getAccessToken(localDataSource))
    }

    suspend fun signOut(): GeneralRes {
        return remoteDataSource.signOut(TokenHolder.getAccessToken(localDataSource))
    }

    suspend fun sendImageRequest(branchCode: String, serialNumberStr: String): GeneralRes {

        val jsonObj2 = JsonObject()
        jsonObj2.addProperty("property", "serialId")
        jsonObj2.addProperty("value", serialNumberStr)
        jsonObj2.addProperty("operator", "EQ")
        val array = JsonArray()
        array.add(jsonObj2)

        return remoteDataSource.sendImageRequest(
            branchCode,
            array.toString(),
            TokenHolder.getAccessToken(localDataSource)
        )
    }

    suspend fun getRelatedPersons(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getRelatedPersons(TokenHolder.getAccessToken(localDataSource), paramsMap)


    suspend fun verifyChangeMobileCode(request: VerifyMobileReq) =
        remoteDataSource.verifyChangeMobileCode(
            request,
            TokenHolder.getAccessToken(localDataSource)
        )

    /*suspend fun verifyChangeMobileCode(mobile: String, verifyCode: String): Resource<Boolean?> {
        val request = VerifyMobileCodeReq("", mobile, verifyCode)

        return remoteDataSource.verifyChangeMobileCode(request, localDataSource.getToken())
    }
*/
    /*    fun logOut() {
            localDataSource.logOut()
        }*/
    /*
        fun isEmployerMode(): Boolean {
            return localDataSource.isEmployerMode()
        }*/

    suspend fun getMyRequestList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getMyRequestList(TokenHolder.getAccessToken(localDataSource), paramsMap)

    suspend fun getRequestTypeList(paramsMap: MutableMap<String, String>?): RequestTypeResponse {

        return remoteDataSource.getRequestTypeList(
            TokenHolder.getAccessToken(localDataSource),
            paramsMap
        )

        /* return when (result.status) {

             Resource.Status.SUCCESS -> {
                 Resource.success(result.data?.asDomainModel() as? ArrayList)
             }

             Resource.Status.LOADING -> {
                 Resource.loading()
             }

             Resource.Status.ERROR -> {
                 Resource.error(result.message)
             }

             Resource.Status.NEED_REFRESH_TOKEN -> {
                 Resource.needRefreshToken(result.message)
             }
             Resource.Status.NEED_NETWORK -> {
                 Resource.needNetwork()
             }
         }*/
    }

    suspend fun getMyRequestErrorList(
        requestId: Long?
    ): RequestErrorResponse {

        val jsonObj2 = JsonObject()
        jsonObj2.addProperty("property", "request.id")
        jsonObj2.addProperty("value", requestId)
        jsonObj2.addProperty("operator", "EQUAL")
        val array = JsonArray()
        array.add(jsonObj2)

        return remoteDataSource.getMyRequestErrorList(
            TokenHolder.getAccessToken(localDataSource),
            array.toString()
        )
    }

    suspend fun getSmartGuideList(
        requestType: Int?,
        requestStatus: String?,
        isPublic: Boolean?
    ) = remoteDataSource.getSmartGuideList(
        TokenHolder.getAccessToken(localDataSource),
        requestType,
        requestStatus,
        isPublic
    )

    suspend fun getMyRequestPDF(requestId: Int?): InboxPdfItem {

        return remoteDataSource.getMyRequestPDF(
            TokenHolder.getAccessToken(localDataSource),
            requestId.toString()
        )
    }

    suspend fun deleteMyRequest(requestId: Int?): DeleteItemResponse {

        return remoteDataSource.deleteMyRequest(
            TokenHolder.getAccessToken(localDataSource),
            requestId.toString()
        )

//        return when (result.status) {
//
//            Resource.Status.SUCCESS -> {
//                Resource.success(requestId)
//            }
//
//            Resource.Status.LOADING -> {
//                Resource.loading()
//            }
//
//            Resource.Status.ERROR -> {
//                Resource.error(result.message)
//            }
//
//            Resource.Status.NEED_REFRESH_TOKEN -> {
//                Resource.needRefreshToken(result.message)
//            }
//            Resource.Status.NEED_NETWORK -> {
//                Resource.needNetwork()
//            }
//        }


    }


    data class InboxReqOperation(val operation: String, val permission: InboxReqPermission? = null)
    data class InboxReqPermission(val operation: String)

    suspend fun inboxInquiryLicense(requestId: String, inquiryId: String?): InquiryLicenseResponse {

        return remoteDataSource.inboxInquiryLicense(
            TokenHolder.getAccessToken(localDataSource), requestId,
            if (inquiryId.isNullOrBlank()) InboxReqOperation("cancel") else InboxReqOperation(
                "ok",
                InboxReqPermission(inquiryId)
            )
        )

//        return when (result.status) {
//
//            Resource.Status.SUCCESS -> {
//                result.message?.message = "با موفقیت انجام شد"
//                result
//            }
//
//            Resource.Status.LOADING -> {
//                Resource.loading()
//            }
//
//            Resource.Status.ERROR -> {
//                Resource.error(result.message)
//            }
//
//            Resource.Status.NEED_REFRESH_TOKEN -> {
//                Resource.needRefreshToken(result.message)
//            }
//            Resource.Status.NEED_NETWORK -> {
//                Resource.needNetwork()
//            }
//        }

    }


    /*      suspend fun getInboxPaging(): LiveData<PagingData<Resource<List<InboxResponse>?>>> {

          return Pager(
              config = PagingConfig(
                  pageSize = Constants.QUERY_PAGE_SIZE_10,
                  enablePlaceholders = false,
                  prefetchDistance = 1,
                  initialLoadSize  = 10
              ),
              pagingSourceFactory = { InboxPagingSource(remoteDataSource,TokenHolder.getAccessToken(localDataSource))
              }
          ).liveData
      }
  */
    suspend fun getInboxSize(): InboxSizeResponse {
        return remoteDataSource.getInboxSize(TokenHolder.getAccessToken(localDataSource))

    }

    suspend fun getInboxSystemList(): Resource<List<MenuModel>?> {

        val result =
            remoteDataSource.getInboxSystemList(TokenHolder.getAccessToken(localDataSource))

        return when (result.status) {

            Resource.Status.SUCCESS -> {
                Resource.success(result.data?.asDomainModel())
            }

            Resource.Status.LOADING -> {
                Resource.loading()
            }

            Resource.Status.ERROR -> {
                Resource.error(result.message)
            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                Resource.needRefreshToken(result.message)
            }

            Resource.Status.NEED_NETWORK -> {
                Resource.needNetwork()
            }
        }

    }

    suspend fun getInboxSubjectList(): Resource<List<MenuModel>?> {


        val result =
            remoteDataSource.getInboxSubjectList(TokenHolder.getAccessToken(localDataSource))

        return when (result.status) {

            Resource.Status.SUCCESS -> {
                Resource.success(result.data?.asDomainModel())
            }

            Resource.Status.LOADING -> {
                Resource.loading()
            }

            Resource.Status.ERROR -> {
                Resource.error(result.message)
            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                Resource.needRefreshToken(result.message)
            }

            Resource.Status.NEED_NETWORK -> {
                Resource.needNetwork()
            }
        }
    }

    suspend fun getUserProfileImage(tempToken: String?) =
        remoteDataSource.getUserProfileImage(
            if (tempToken.isNullOrBlank()) TokenHolder.getAccessToken(
                localDataSource
            ) else "Bearer $tempToken"
        )

    fun setCodeVerifier(codeVerifier: String?) {
        localDataSource.setCodeVerifier(codeVerifier)
    }

    fun getCodeVerifier(): String? {
        return localDataSource.getCodeVerifier()
    }

    /*
        fun setTempTokenInfo(result: LoginResponse?) {
            localDataSource.setTempTokenInfo(result)
        }

        fun getTempTokenInfo() = localDataSource.getTempTokenInfo()

    */

}

