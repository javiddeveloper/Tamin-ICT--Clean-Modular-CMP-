package com.tamin.taminhamrah.data.local.preference

import EncryptionHelper
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoSavingModel
import com.tamin.taminhamrah.data.local.services.entity.ServiceEntity
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.ai.AiServiceResponse
import com.tamin.taminhamrah.data.remote.models.ai.LawsAiSearchResponse
import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentResponseDTO
import com.tamin.taminhamrah.data.remote.models.ai.agent.ChatAllowedData
import com.tamin.taminhamrah.data.remote.models.ai.agent.PollingResponseDTO
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionType
import com.tamin.taminhamrah.enums.ServiceStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject


class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context,    private val gson: Gson) {


    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("com.tamin.taminhamrah", MODE_PRIVATE)

    private fun <T> readFromAssets(fileName: String, clazz: Class<T>): T {
        val jsonString: String =
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(jsonString, clazz)
    }

    fun setToken(token: String) {
        var finalToken = token
        if (token.isNotEmpty() && !token.startsWith("Bearer")) {
            finalToken = "Bearer $token"
        }
        val encryptedToken = EncryptionHelper.encrypt(context, finalToken)
        sharedPreferences.edit { putString("token", encryptedToken) }
    }

    fun setFcmToken(token: String) = sharedPreferences.edit { putString("fcmToken", token) }
    fun getFcmToken() = sharedPreferences.getString("fcmToken", "") ?: ""


    fun setNationalCode(nationalCode: String?) {
        val valueToSave = if (nationalCode.isNullOrEmpty()) {
            null
        } else {
            EncryptionHelper.encrypt(context, nationalCode)
        }
        sharedPreferences.edit { putString("NationalCode", valueToSave) }
    }

    fun getNationalCode(): String? {
        val encrypted = sharedPreferences.getString("NationalCode", null)
        return if (encrypted != null) EncryptionHelper.decrypt(context, encrypted) else null
    }

    fun getToken(): String {
        val encryptedToken = sharedPreferences.getString("token", "") ?: ""
        val decryptedToken = EncryptionHelper.decrypt(context, encryptedToken)
        return decryptedToken
    }

    fun setTokenExpireTime(expiresIn: Long) =
        sharedPreferences.edit { putLong("expiresIn", expiresIn) }

    fun getTokenExpireTime() = sharedPreferences.getLong("expiresIn", 0)

    /*  fun setUsername(username: String?) =
          sharedPreferences.edit().putString("currentUsername", username).apply()

      fun getUsername() = sharedPreferences.getString("currentUsername", "")*/

    fun saveBoolean(key: String, state: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, state)
        }
    }

    fun loadBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun setRefreshToken(refreshToken: String) {
        val encrypted = EncryptionHelper.encrypt(context, refreshToken)
        sharedPreferences.edit { putString("refreshToken", encrypted) }
    }

    fun getRefreshToken(): String? {
        val encrypted = sharedPreferences.getString("refreshToken", null) ?: return null
        val decrypted = EncryptionHelper.decrypt(context, encrypted)
        return decrypted.ifEmpty { null }
    }

    fun setGender(gender: String) =
        sharedPreferences.edit { putString("currentUserGender", gender) }

    fun getGender() = sharedPreferences.getString("currentUserGender", "")


    fun getVersioningInfo(): List<VersionInfoModel> {
        val jsonString: String =
            context.assets
                .open("versioningInfo.json")
                .bufferedReader()
                .use { it.readText() }


        val model = Gson().fromJson(jsonString, VersionInfoSavingModel::class.java)
        return model.list

    }

    fun getServices(): ServiceResponseModelNew {

        val jsonString: String =
            context.assets
                .open("menu.json")
                .bufferedReader()
                .use { it.readText() }

        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject

        val result =
            Gson().fromJson(jsonObject, ServiceResponseModelNew::class.java)
        result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
        return result
    }

    fun getLaws(): LawsAiSearchResponse {

        val jsonString: String =
            context.assets
                .open("law.json")
                .bufferedReader()
                .use { it.readText() }

        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, LawsAiSearchResponse::class.java)
        return result
    }

    fun getAiHistoryServices(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_history.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAiLastPay(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_last_pay.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAgentAllServices(): AgentResponseDTO {
        return readFromAssets("agent_response.json", AgentResponseDTO::class.java)
    }

    fun getAgentOneServices(): PollingResponseDTO {
        return readFromAssets("agent_one_response.json", PollingResponseDTO::class.java)
    }

    fun getInquiryEducationServices(): PollingResponseDTO {
        return readFromAssets("agent_inquiry_education_response.json", PollingResponseDTO::class.java)
    }
    fun getAgentDependentServices(): AgentResponseDTO {
        return readFromAssets("agent_response_dependent.json", AgentResponseDTO::class.java)
    }
    fun getAgentAppointmentServices(): AgentResponseDTO {
        return readFromAssets("agent_response_appointment.json", AgentResponseDTO::class.java)
    }

    // hemayat darman
    fun getAiBookletServices(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_booklet.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }


    fun getAiLastTrackingCodeServices(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_last-tracking-code.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAiAllServices(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAiSchedulePatient(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_schedule_patient.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAiVoiceServices(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_voice.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun getAiPatient(): AiServiceResponse {
        val jsonString: String =
            context.assets
                .open("search_service_patient.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result =
            Gson().fromJson(jsonObject, AiServiceResponse::class.java)
        return result
    }

    fun setBiometricEnabled(biometricEnabled: Boolean) = sharedPreferences.edit { putBoolean(Constants.BIOMETRIC_ENABLED, biometricEnabled) }

    fun isBiometricEnabled() = sharedPreferences.getBoolean(Constants.BIOMETRIC_ENABLED, false)

    //for pension Check
    fun setUserType(userType: String?) =
        sharedPreferences.edit { putString(Constants.USER_TYPE, userType) }

    fun getUserType() =
        sharedPreferences.getString(Constants.USER_TYPE, EnumTypeUser.ANONYMOUS.title)

    //for set insured type
    fun setInsuredType(userType: String?) =
        sharedPreferences.edit { putString(Constants.INSURED_TYPE, userType) }

    fun getInsuredType() =
        sharedPreferences.getString(Constants.INSURED_TYPE, EnumTypeUser.ANONYMOUS.title)

    //for set pensioner type
    fun setPensionerType(userType: String?) =
        sharedPreferences.edit { putString(Constants.PENSIONER_TYPE, userType) }

    fun getPensionerType() =
        sharedPreferences.getString(Constants.PENSIONER_TYPE, EnumTypeUser.ANONYMOUS.title)
    /*fun setPassword(password: String) =
        sharedPreferences.edit().putString("currentPassword", password).apply()
    fun getPassword() = sharedPreferences.getString("currentPassword", "")*/

    fun getUserMode(): String? {
        return sharedPreferences.getString("currentUserMode", "")
    }

    fun getApplicationTheme(): Int {
        return sharedPreferences.getInt(
            Constants.APPLICATION_THEME,
            ApplicationThemeEnum.FOLLOW_SYSTEM.state
        )
    }

    fun setApplicationTheme(applicationTheme: ApplicationThemeEnum) {
        sharedPreferences.edit { putInt(Constants.APPLICATION_THEME, applicationTheme.state) }
    }

    fun setUserModeMode(userMode: String?) =
        sharedPreferences.edit { putString("currentUserMode", userMode) }

    fun setUserAvatar(imageUrl: String?) =
        sharedPreferences.edit { putString("USER_AVATAR", imageUrl) }

    fun getUserAvatar() = sharedPreferences.getString("USER_AVATAR", "")

    fun saveLoginInfo(

    ): Boolean {
        setGender("")
        setUserType(null)
        return true
    }

    fun logOut() {
        setGender("")
        setUserModeMode("")
        setUserAvatar("")
        setUserFullName("")
        setUserNationalCode("")
        setUserEmail("")
        setUserPhoneNumber("")
        setBiometricEnabled(false)
        setUserType(EnumTypeUser.ANONYMOUS.title)
        setCodeVerifier("")
        setInsuredType(EnumTypeUser.ANONYMOUS.title)
        setInsuredMessage(null)
        setPensionerType(EnumTypeUser.ANONYMOUS.title)
        saveSystemType("")
        setFcmToken("")
        EncryptionHelper.clearKey(context)
    }

    fun getInsuredServiceListFromJsonFile(): List<ServiceEntity> {

        /*  val jsonString: String =
              context.assets
                  .open("services_insured")
                  .bufferedReader()
                  .use { it.readText() }

          val jsonParser = JsonParser().parse(jsonString)
          val jsonObject = jsonParser.asJsonObject

          val items = jsonObject.getAsJsonArray("items")
  */
        val itemList = ArrayList<ServiceEntity>()
        /*for (item in items) {
            itemList.add(
                createNewServiceItem(
                    item,
                    isPensionerService = false,
                    isInsuredService = true
                )
            )
        }*/

        return itemList

    }

    fun getPensionerServiceListFromJsonFile(): List<ServiceEntity> {

        /* val jsonString: String =
             context.assets
                 .open("services_pensioner")
                 .bufferedReader()
                 .use { it.readText() }

         val jsonParser = JsonParser().parse(jsonString)
         val jsonObject = jsonParser.asJsonObject*/

        //   val items = jsonObject.getAsJsonArray("items")

        val itemList = ArrayList<ServiceEntity>()
        /* for (item in items) {
             itemList.add(
                 createNewServiceItem(
                     item,
                     isPensionerService = true,
                     isInsuredService = false
                 )
             )
         }*/

        return itemList

    }


    fun checkUpdate(): CheckUpdateResponse {
        val jsonString = context.assets.open("update.txt").bufferedReader().use { it.readText() }
        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject
        val result = Gson().fromJson(jsonObject, CheckUpdateResponse::class.java)
        result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
        return result

    }

    fun saveWorkerPayInfo(ticket: String?, info: String?) {
        sharedPreferences.edit {
            putString(Constants.WORKERS_PAY_TICKET, ticket)
                .putString(Constants.WORKERS_PAY_INFO, info)
        }
    }

    fun getWorkerPayTicket(): String? =
        sharedPreferences.getString(Constants.WORKERS_PAY_TICKET, null)

    fun getWorkerPayInfo(): String? = sharedPreferences.getString(Constants.WORKERS_PAY_INFO, null)

    fun saveAcraConfig(acraConfigResponse: AcraConfigResponse) {
        val json = Gson().toJson(acraConfigResponse)
        sharedPreferences.edit { putString(Constants.ACRA_CONFIG_TAG, json) }
    }

    fun getAcraConfig(): AcraConfigResponse {
        val prefConfig = sharedPreferences.getString(
            Constants.ACRA_CONFIG_TAG, context.assets
                .open("acra_config.txt")
                .bufferedReader()
                .use { it.readText() })

        val json = JsonParser().parse(prefConfig).asJsonObject

        val result = Gson().fromJson(json, AcraConfigResponse::class.java)
        result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
        return result


    }


    fun getObjectionTypeList(): ObjectionType {

        val jsonString: String =
            context.assets
                .open("objection-type.json")
                .bufferedReader()
                .use { it.readText() }

        val jsonParser = JsonParser().parse(jsonString)
        val jsonObject = jsonParser.asJsonObject


        val list = Gson().fromJson(jsonObject, ObjectionType::class.java)

        return list

    }

    fun setUserFullName(fullName: String?) {
        Timber.tag("loginRepository").i("setUserFullName: fullName=%s", fullName)
        sharedPreferences.edit { putString("USER_FULL_NAME", fullName) }
    }

    fun getUserFullName(): String? {
        val fullName = sharedPreferences.getString("USER_FULL_NAME", "")
        Timber.tag("loginRepository").i("getUserFullName: fullName=$fullName")
        return fullName
    }


    fun setUserNationalCode(nationalCode: String?) {
        val valueToSave = if (nationalCode.isNullOrEmpty()) "" else EncryptionHelper.encrypt(
            context,
            nationalCode
        )
        sharedPreferences.edit { putString("USER_NATIONAL_CODE", valueToSave) }
    }

    fun getUserNationalCode(): String? {
        val encrypted = sharedPreferences.getString("USER_NATIONAL_CODE", "") ?: ""
        return if (encrypted.isNotEmpty()) EncryptionHelper.decrypt(context, encrypted) else ""
    }

    fun setUserEmail(email: String?) {
        val valueToSave =
            if (email.isNullOrEmpty()) "" else EncryptionHelper.encrypt(context, email)
        sharedPreferences.edit { putString("USER_EMAIL", valueToSave) }
    }

    fun getUserEmail(): String? {
        val encrypted = sharedPreferences.getString("USER_EMAIL", "") ?: ""
        return if (encrypted.isNotEmpty()) EncryptionHelper.decrypt(context, encrypted) else ""
    }

    fun setUserPhoneNumber(phoneNumber: String?) {
        val valueToSave =
            if (phoneNumber.isNullOrEmpty()) "" else EncryptionHelper.encrypt(context, phoneNumber)
        sharedPreferences.edit { putString("USER_PHONE_NUMBER", valueToSave) }
    }

    fun getUserPhoneNumber(): String? {
        val encrypted = sharedPreferences.getString("USER_PHONE_NUMBER", "") ?: ""
        return if (encrypted.isNotEmpty()) EncryptionHelper.decrypt(context, encrypted) else ""
    }

    fun setCodeVerifier(codeVerifier: String?) {
        sharedPreferences.edit { putString("CODE_VERIFIER", codeVerifier) }
    }

    fun getCodeVerifier(): String? {
        return sharedPreferences.getString("CODE_VERIFIER", "")
    }

    fun saveSystemType(systemType: String?) {
        sharedPreferences.edit { putString("PAYMENT_SYSTEM_TYPE", systemType) }
    }

    fun getSystemType() = sharedPreferences.getString("PAYMENT_SYSTEM_TYPE", "")

    fun saveEmployerDebtSerialNumber(serial: String?) {
        sharedPreferences.edit { putString("EMPLOYER_DEBT_SERIALNUMBER", serial) }
    }

    fun getEmployerDebtSerialNumber() =
        sharedPreferences.getString("EMPLOYER_DEBT_SERIALNUMBER", "")

    fun setInsuredMessage(message: String?) = sharedPreferences.edit {
        putString(
            "INSURED_MESSAGE",
            message
        )
    }

    fun getInsuredMessage() = sharedPreferences.getString("INSURED_MESSAGE", null)
    fun saveSentryConfig(sentryConfig: SentryConfig) {
        val json = Gson().toJson(sentryConfig)
        sharedPreferences.edit { putString("SENTRY_CONFIG", json) }
    }

    fun getSentryConfig(): SentryConfig? {
        val json = sharedPreferences.getString("SENTRY_CONFIG", null)
        return if (json != null) {
            try {
             Gson().fromJson(json, SentryConfig::class.java)
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse Sentry config from preferences")
                null
            }
        } else {
            null
        }
    }

    fun getChatToken(): String {
        return getChatAllowedData()?.chatToken ?: ""
    }

    fun saveChatAllowedData(data: ChatAllowedData) {
        val ttlInSeconds = data.ttl ?: 0
        data.expiresAt = System.currentTimeMillis() + (ttlInSeconds * 1000L)
        val json = gson.toJson(data)
        sharedPreferences.edit { putString("CHAT_ALLOWED_DATA", json) }
    }

    fun getChatAllowedData(): ChatAllowedData? {
        val json = sharedPreferences.getString("CHAT_ALLOWED_DATA", null) ?: return null
        return try {
            gson.fromJson(json, ChatAllowedData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isChatAllowed(): Boolean {
        val data = getChatAllowedData() ?: return false
        return data.canStartChat == true
    }

    /*  fun setTempTokenInfo(result: LoginResponse?) {
          val currentTime = System.currentTimeMillis()/1000
          val expireTime = currentTime + (result?.expiresIn?:0)
          result?.expiresIn = expireTime
          sharedPreferences.edit().putString("TEMP_TOKEN_INFO", Gson().toJson(result)).apply()
      }

      fun getTempTokenInfo():LoginResponse?{
          val temp = sharedPreferences.getString("TEMP_TOKEN_INFO", null)
          return Gson().fromJson(temp, LoginResponse::class.java)
      }
  */

    /*private fun createNewServiceItem(
        item: JsonElement,
        isPensionerService: Boolean,
        isInsuredService: Boolean
    ): ServiceEntity {

        val service = Gson().fromJson(item, ServiceEntity::class.java)
        if (!service.items.is-OrEmpty()) service.isParent = true
        service.isEmployerServices = isEmployerMode()
        service.isInsuredService = isInsuredService
        service.isPensionerService = isPensionerService

        return service
    }*/

}