package com.tamin.taminhamrah.data.remote.user

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.BaseRemoteDataSource
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPerson
import com.tamin.taminhamrah.data.remote.models.refreshtoken.RevokeResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.EditMobileResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxResponseNew
import com.tamin.taminhamrah.data.remote.models.user.LoginNewReq
import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.repository.LoginRepository
import javax.inject.Inject

class UserRemoteDataSourceImpl
@Inject constructor(private val userService: UserService) : UserRemoteDataSource,
    BaseRemoteDataSource() {

    private fun getRequestPage(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.PAGE) ?: Constants.DEFAULT_QUERY_PAGE_SIZE
    }

    private fun getQueryPageSize(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.QUERY_PAGE_SIZE) ?: Constants.DEFAULT_QUERY_PAGE_SIZE
    }

    private fun getStartIndex(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.START) ?: Constants.DEFAULT_START_INDEX
    }

    override suspend fun getEligibilityTreatment(eligibilityRequest: String): GeneralRes {
        return getResultNew({ userService.getEligibility(eligibilityRequest) })
    }



    override suspend fun testMenu(): String {
        return try {
            val response = userService.testMenu("https://ssodcfs.tamin.ir/Eservices/menu_data.txt")
            response.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun getProfileInfo(token: String) =
        getResultNew({ userService.getProfileInfo(token) })

    override suspend fun checkUpdate(token: String) =
        getResultNew({ userService.checkUpdate(token) })

    override suspend fun postDeviceDataModel(token: String, deviceData: DeviceDetailModel) =
        getResultNew({ userService.postDeviceDataModel(token, deviceData) })

    override suspend fun signIn(url: String, loginReq: LoginNewReq) =
        getResultNew({
            userService.signIn(
                url,
                codeFromServer = loginReq.codeFromServer,
                codeVerifier = loginReq.codeVerifier
            )
        }, false)

    override suspend fun signOut(token: String) =
        getResultNew({
            userService.signOut(
                token
            )
        }, false)

    override suspend fun changeMobile(mobile: String, token: String): EditMobileResponse {
        val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return getResultNew({
            userService.changeMobile(
                finalToken,
                "https://profile.tamin.ir/main/change-phone-number",
                "https://profile-api.tamin.ir/api/user/mobile-change/request",
                mobile
            )
        }, false)
    }


    override suspend fun fetchTaminRelation(token: String) =
        getResultNew({ userService.fetchTaminRelation(token) })

    override suspend fun sendImageRequest(
        branchCode: String,
        serialNumberStr: String,
        token: String
    ) = getResultNew({ userService.sendImageRequest(token, branchCode, serialNumberStr) })

    override suspend fun getInboxNew(
        token: String, paramsMap: MutableMap<String, String>?
    ): InboxResponseNew {
        return getResultNew({
            userService.getInboxNew(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun getRelatedPersons(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): RelatedPerson {
        return getResultNew({
            userService.getRelatedPersons(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun verifyChangeMobileCode(request: VerifyMobileReq, token: String) =
        getResultNew({ userService.verifyChangeMobileCode(
            "Bearer $token",
            "https://profile.tamin.ir/main/change-phone-number",
            "https://profile-api.tamin.ir/api/user/mobile-change/confirm",
            request) }, false)
/*
  override suspend fun verifyChangeMobileCode(verifyCode: VerifyMobileCodeReq, token: String) =
        getResult { userService.verifyChangeMobileCode(token, verifyCode) }
*/

    override suspend fun getMyRequestList(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({

            val array = JsonArray()
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "operation")
            jsonObj1.addProperty("value", "03")
            jsonObj1.addProperty("operator", "EQUAL")
            array.add(jsonObj1)

            if (paramsMap?.containsKey("refCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "refCode")
                jsonObj2.addProperty("value", paramsMap["refCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }

            if (paramsMap?.containsKey("requestType.id") == true) {
                val jsonObj3 = JsonObject()
                jsonObj3.addProperty("property", "requestType.id")
                jsonObj3.addProperty("value", paramsMap["requestType.id"])
                jsonObj3.addProperty("operator", "EQ")
                array.add(jsonObj3)
            }

            val jsonObjSort = JsonObject()
            jsonObjSort.addProperty("property", "refCode")
            jsonObjSort.addProperty("direction", "DESC")

            val arraySort = JsonArray()
            arraySort.add(jsonObjSort)

            userService.getMyRequestList(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString(),
                sort = arraySort.toString()
            )
        })


    //eservices.tamin.ir/api/faq/limitation?requestType=3&requestStatus=0018&isPublic=1
    override suspend fun getSmartGuideList(
        token: String,
        requestType: Int?,
        requestStatus: String?,
        isPublic: Boolean?
    ) =
        getResultNew({
            userService.getSmartGuideList(
                token,
                requestType,
                requestStatus,
                if (isPublic == true) 1 else 0
            )
        })

    override suspend fun getRequestTypeList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        userService.getRequestTypeList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap)

        )
    })

    override suspend fun getMyRequestErrorList(token: String, filter: String) =
        getResultNew({
            userService.getMyRequestErrorList(
                token = token,
                filter = filter

            )
        })

//    override suspend fun getInboxNew(
//        token: String,
//        page: String,
//        start: String,
//        limit: String
//    ) = getListResult { userService.getInboxNew(token, page = page, start = start, limit = limit)  }


    /*
        override suspend fun getInboxPaging(token: String,start:String,pageSize:String, page: String)= getResult { userService.getInbox(token,page = page,start =start ,limit = pageSize) }
    */

    override suspend fun getInboxSize(token: String) =
        getResultNew({ userService.getInboxSize(token) }, false)

    override suspend fun getMyRequestPDF(token: String, id: String): InboxPdfItem =
        getResultNew({ userService.getMyRequestPDF(token, id) })

    override suspend fun deleteMyRequest(token: String, id: String) =
        getResultNew({ userService.deleteMyRequest(token, id) })

    override suspend fun inboxInquiryLicense(
        token: String,
        id: String,
        body: LoginRepository.InboxReqOperation
    ) =
        getResultNew({ userService.inboxInquiryLicense(token, id, body) }, false)

    override suspend fun getInboxSystemList(token: String) =
        getResultList { userService.getInboxSystemList(token) }

    override suspend fun getInboxSubjectList(token: String) =
        getResultList { userService.getInboxSubjectList(token) }

    override suspend fun getUserProfileImage(token: String) =
        getResultNew({ userService.getUserProfileImage(token) })

    override suspend fun getLackEntitlement(
        token: String,
        nationalCode: String
    ) = getResultNew({ userService.getLackEntitlement(token = token, nationalCode = nationalCode) })

    override suspend fun revoke(
        accessToken: String?,
        refreshToken: String?
    ): RevokeResponse {
        return userService.revokeToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

}