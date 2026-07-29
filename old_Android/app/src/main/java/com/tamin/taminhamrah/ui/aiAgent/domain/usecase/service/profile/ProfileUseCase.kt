package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile

import android.content.Context
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoModelRes
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.utils.ConvertDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {
    @Inject
    lateinit var commonRepository: CommonRepository
    override val serviceName: ServiceNameEnum = ServiceNameEnum.PROFILE_INFO

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val serviceResponse = getAllProfileData(params)
            ServiceResult.Success(listOf(serviceResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private suspend fun getAllProfileData(params: ServiceParams): ServiceResponse = coroutineScope {

        val identityRequest = async { repository.getIdentityInfo() }
        val personalInfoRequest = async { repository.getPersonalInfo() }

        val identityResponse = identityRequest.await()
        val personalInfoResponse = personalInfoRequest.await()



        return@coroutineScope if (identityResponse.isSuccess && personalInfoResponse.isSuccess) {

            val identityData = identityResponse.data
            val personalInfoData = personalInfoResponse.data

            val birthCityRequest = async { getCityById(identityData?.cityOfBirthId) }
            val issueCityRequest = async { getCityById(identityData?.cityOfIssueId) }

            val birthCity = birthCityRequest.await()
            val issueCity = issueCityRequest.await()

            val keyValueList = createKeyValue(
                identityData,
                personalInfoData,
                birthCity,
                issueCity
            )
            ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.KeyValueMessage(keyValueList)
            )
        } else {
            ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.StringMessage("متاسفانه مشکلی در دریافت اطلاعات پیش آمده است!")
            )
        }

    }


    private suspend fun getCityById(cityId: String?): String {
        if (cityId.isNullOrEmpty()) {
            return "-"
        }
        val map = HashMap<String, String>()
        map["cityCode"] = cityId
        return repository.getCityName(map).data?.list?.firstOrNull()?.cityName ?: "-"
    }

    private fun createKeyValue(
        identity: IdentityInfoResponse.IdentityInfo?,
        personalInfo: PersonalInfoModelRes?,
        birthCity: String?,
        cityOfIssue: String?
    ): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("نام", identity?.firstName ?: "-"))
        keyValueList.add(KeyValueModel("نام خانوادگی", identity?.lastName ?: "-"))
        keyValueList.add(KeyValueModel("نام پدر", identity?.fatherName ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "شماره تامین اجتماعی",
                personalInfo?.organizationId ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "شماره تلفن همراه",
                personalInfo?.mobileNumber ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "جنسیت",
                if (identity?.gender == "01") "مرد" else "زن"
            )
        )
        keyValueList.add(KeyValueModel("کد ملی", identity?.nationalId ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "شماره شناسنامه",
                identity?.idCardNumber ?: "0"
            )
        )
        keyValueList.add(KeyValueModel("سری شناسنامه", identity?.idCardSerial1 ?: "-"))
        keyValueList.add(
            KeyValueModel(
                "سریال شناسنامه",
                identity?.idCardSerial2 ?: "-"
            )
        )
        keyValueList.add(
            KeyValueModel(
                "تاریخ تولد",
                ConvertDate.convertTimestampToPersianDate(identity?.dateOfBirth ?: 0)
            )
        )
        keyValueList.add(
            KeyValueModel(
                "ملیت",
                if (identity?.countryId == "0001") "ایرانی" else "غیر ایرانی"
            )
        )
        keyValueList.add(KeyValueModel("شهر محل تولد", birthCity ?: "-"))
        keyValueList.add(KeyValueModel("شهر محل صدور", cityOfIssue ?: "-"))

        return keyValueList
    }
}