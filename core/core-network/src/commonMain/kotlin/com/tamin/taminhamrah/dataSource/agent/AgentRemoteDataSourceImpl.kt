package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.apiService.agent.AgentApiService
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException

internal class AgentRemoteDataSourceImpl(
    private val agentApiService: AgentApiService,
    private val errorParser: ErrorParser
) : AgentRemoteDataSource {

    override suspend fun checkChatAllowed(): ChatAllowedDTO {
        return try {
            agentApiService.checkChatAllowed()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendServicePrompt(request: AgentRequestDTO): PollingResponseDTO {
        return try {
            agentApiService.sendServicePrompt(request)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendLawPrompt(request: AgentRequestDTO): PollingResponseDTO {
        return try {
            agentApiService.sendLawPrompt(request)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun trackRequest(requestId: String): PollingResponseDTO {
        return try {
            agentApiService.trackRequest(requestId)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO {
        return try {
            agentApiService.cancelRequest(requestId)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
