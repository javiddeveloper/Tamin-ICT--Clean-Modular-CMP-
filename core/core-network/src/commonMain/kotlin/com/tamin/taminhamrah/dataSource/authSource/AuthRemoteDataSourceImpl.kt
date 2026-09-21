package com.tamin.taminhamrah.dataSource.authSource

import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.auth.TokenResponseDto
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.apiService.UserApiService
import co.touchlab.kermit.Logger

internal class AuthRemoteDataSourceImpl(
    private val userApiService: UserApiService,
    private val errorParser: ErrorParser,
    private val developerOptionsRepository: DeveloperOptionsRepository
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
            val url = "${developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)}server/v2/token"
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
            val url = "${developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)}server/v2/token"
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

    override suspend fun debugClientCredentialsLogin(
        clientId: String,
        clientSecret: String,
    ): TokenResponseDto {
        // Uses the same ACCOUNT override as the PKCE flow (Developer Options ->
        // "سرویس احراز هویت") rather than its own URL field, so switching that one entry to a
        // pilot/test environment routes both login methods there together.
        val url = "${developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)}server/token"
        return userApiService.debugClientCredentialsLogin(
            url = url,
            clientId = clientId,
            clientSecret = clientSecret
        )
    }

    override suspend fun signOut(token: String): String {
        logger.d { "signOut called with token: ${token.take(10)}..." }
        return try {
            userApiService.signOut(
                token = token,
                url = "${developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)}signout"
            )
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
                url = "${developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.ACCOUNT)}revoke",
                accessToken = accessToken?.let { "${com.tamin.taminhamrah.util.HeaderConstant.AUTHORIZATION_TYPE}$it" },
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
