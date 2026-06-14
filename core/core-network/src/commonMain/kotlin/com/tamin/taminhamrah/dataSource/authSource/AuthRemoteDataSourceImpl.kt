package com.tamin.taminhamrah.dataSource.authSource

import com.tamin.taminhamrah.model.auth.TokenResponseDto
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.apiService.UserApiService
import co.touchlab.kermit.Logger

internal class AuthRemoteDataSourceImpl(
    private val userApiService: UserApiService,
    private val errorParser: ErrorParser
) : AuthRemoteDataSource {

    private val logger = Logger.withTag("AuthRemoteDataSource")
    override suspend fun exchangeCodeForTokens(
        redirectUri: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        audience: String,
    ): TokenResponseDto {
        return try {
            val url = "${NetworkConstants.BASE_URL_ACCOUNT}server/v2/token"
            userApiService.signIn(
                url = url,
                redirectUrl = redirectUri,
                clientId = clientId,
                grantType = "authorization_code",
                codeFromServer = code,
                codeVerifier = codeVerifier,
                audience = audience,
            )
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
            userApiService.refreshToken(
                url = url,
                grantType = "refresh_token",
                refreshToken = refreshToken,
                clientId = clientId,
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun signOut(token: String): String {
        logger.d { "signOut called with token: ${token.take(10)}..." }
        return try {
            userApiService.signOut(token)
            logger.d { "signOut API call successful" }
            "SUCCESSFUL"
        } catch (e: TaminErrorUriException) {
            logger.e(e) { "signOut failed with TaminErrorUriException" }
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            logger.e(e) { "signOut failed with Exception: ${e.message}" }
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun revokeToken(accessToken: String?, refreshToken: String?): Boolean {
        logger.d { "revokeToken called. AccessToken: ${accessToken?.take(10)}..., RefreshToken: ${refreshToken?.take(10)}..." }
        return try {
            userApiService.revokeToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            logger.d { "revokeToken API call successful" }
            true
        } catch (e: Exception) {
            logger.e(e) { "revokeToken API call failed: ${e.message}" }
            false
        }
    }
}
