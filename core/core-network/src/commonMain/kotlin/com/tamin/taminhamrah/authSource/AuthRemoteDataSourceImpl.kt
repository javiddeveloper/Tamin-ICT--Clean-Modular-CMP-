package com.tamin.taminhamrah.authSource

import com.tamin.core.network.datasource.authSource.AuthRemoteDataSource
import com.tamin.core.network.model.auth.TokenResponseDto
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.core.network.tools.errorHandling.ErrorUri
import com.tamin.core.network.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.utils.NetworkConstants
import com.tamin.taminhamrah.apiService.UserApiService

internal class AuthRemoteDataSourceImpl(
    private val userApiService: UserApiService,
    private val errorParser: ErrorParser
) : AuthRemoteDataSource {
    override suspend fun exchangeCodeForTokens(
        redirectUri: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        audience: String,
    ): TokenResponseDto {
        return try {
            val url = "${NetworkConstants.BASE_URL_ACCOUNT}server/v2/token"
            val response = userApiService.signIn(
                url = url,
                redirectUrl = redirectUri,
                clientId = clientId,
                grantType = "authorization_code",
                codeFromServer = code,
                codeVerifier = codeVerifier,
                audience = audience,
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun refreshTokens(
        refreshToken: String,
        clientId: String,
    ): TokenResponseDto {
        return try {
            val url = "${NetworkConstants.BASE_URL_ACCOUNT}server/v2/token"
            val response = userApiService.refreshToken(
                url = url,
                grantType = "refresh_token",
                refreshToken = refreshToken,
                clientId = clientId,
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
