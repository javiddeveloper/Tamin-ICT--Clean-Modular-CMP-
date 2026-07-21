package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.local.services.ServiceLocalDataSource
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity
import com.tamin.taminhamrah.data.remote.user.UserRemoteDataSource
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import com.tamin.taminhamrah.enums.EnumUserMode
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val pre: PreferenceManager,
    private val localDataSource: ServiceLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource,
) {

    fun hasValidToken(): Boolean {
        val tokenExpireTime = getTokenExpireTime()
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime < tokenExpireTime
    }

    fun getAppliedServices(type: Int) = localDataSource.getAppliedServices(type)
    suspend fun getAllServiceSize(type: Int) = localDataSource.getAllServiceSize(type)
    suspend fun saveService(data: List<AppliedServiceEntity>) {
        localDataSource.saveAppliedServices(data)
    }

    suspend fun updateService(id: Int) {
        localDataSource.updateAppliedService(id)
    }

    fun getTokenExpireTime() = pre.getTokenExpireTime()

    fun logOut() {
        TokenHolder.logOut(pre)
        pre.logOut()
    }

    suspend fun revokeRefreshToken() {
        remoteDataSource.revoke(
            TokenHolder.getAccessToken(pre), TokenHolder.getRefreshToken(pre)
        )
    }

    fun getUserMode(): String? {
        return pre.getUserMode()
    }

    fun getUserModeValue(): Int {

        return when (pre.getUserMode()) {
            EnumUserMode.MODE_EMPLOYER.methodName -> 3
            EnumUserMode.MODE_INSURED.methodName -> 1
            EnumUserMode.MODE_PENSIONER.methodName -> 2
            else -> 4
        }
    }

    fun setUserMode(userMode: String?) {
        return pre.setUserModeMode(userMode)
    }

    fun saveBoolean(key: String, state: Boolean) = pre.saveBoolean(key, state)
    fun loadBoolean(key: String) = pre.loadBoolean(key)
    fun getGender() = pre.getGender()
    fun setGender(gender: String) = pre.setGender(gender)
    fun setUserAvatar(imageUrl: String?) = pre.setUserAvatar(imageUrl)
    fun getUserAvatar() = pre.getUserAvatar()
    fun setApplicationTheme(applicationTheme: ApplicationThemeEnum) =
        pre.setApplicationTheme(applicationTheme)

    fun getApplicationTheme() = pre.getApplicationTheme()
    fun getUserPhoneNumber() = pre.getUserPhoneNumber()
    fun getUserEmail() = pre.getUserEmail()
    fun setPhoneNumber(mobile: String? = "") = pre.setUserPhoneNumber(mobile)

    fun setUserInfo(item: ProfileModel) {

        pre.setUserFullName(item.fullName)
        pre.setUserNationalCode(item.nationalCode)
        pre.setUserEmail(item.email)
        pre.setUserPhoneNumber(item.phonenumber)


    }

    fun getUserInfo() = ProfileModel(
        fullName = pre.getUserFullName(),
        nationalCode = pre.getUserNationalCode(),
        email = pre.getUserEmail(),
        phonenumber = pre.getUserPhoneNumber()
    )

    fun getUserType() = pre.getUserType()

    fun setUserType(userType: String) = pre.setUserType(userType)

    fun getInsuredType() = pre.getInsuredType()

    fun setInsuredType(userType: String) = pre.setInsuredType(userType)
    fun setInsuredMessage(message: String?) = pre.setInsuredMessage(message)
    fun getInsuredMessage() = pre.getInsuredMessage()


    fun getPensionerType() = pre.getPensionerType()

    fun setPensionerType(userType: String) = pre.setPensionerType(userType)

    fun setToken(tokenStr: String) = pre.setToken(tokenStr)
    fun getToken() = TokenHolder.getAccessToken(pre)

    fun getExpiredIn() = pre.getTokenExpireTime()
    fun setFcmToken(token: String) = pre.setFcmToken(token)
    fun getFcmToken() = pre.getFcmToken()
    fun saveSystemType(systemType: String?) = pre.saveSystemType(systemType)
    fun getSystemType() = pre.getSystemType()
    fun saveEmployerDebtSerialNumber(serial: String?) = pre.saveEmployerDebtSerialNumber(serial)
    fun getEmployerDebtSerialNumber() = pre.getEmployerDebtSerialNumber()
    fun setBiometricEnabled(biometricEnabled: Boolean) = pre.setBiometricEnabled(biometricEnabled)
    fun isBiometricEnabled() = pre.isBiometricEnabled()

}