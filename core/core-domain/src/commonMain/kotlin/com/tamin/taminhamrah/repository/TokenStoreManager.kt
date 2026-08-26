/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.repository

import kotlinx.coroutines.flow.Flow

interface TokenStoreManager {
    fun saveToken(token: String?)
    fun getToken(): String?
    fun saveRefreshToken(refreshToken: String?)
    fun getRefreshToken(): String?
    fun saveUserId(userId: String?)
    fun getUserId(): String?
    fun saveUserType(userType: String?)
    fun getUserType(): String?
    fun saveCodeVerifier(codeVerifier: String?)
    fun getCodeVerifier(): String?
    fun tokenValidFlow(): Flow<Boolean>
    suspend fun setTokenValid(isValid: Boolean)
    fun isAuthProcessingFlow(): Flow<Boolean>
    fun setAuthProcessing(isProcessing: Boolean)
}
