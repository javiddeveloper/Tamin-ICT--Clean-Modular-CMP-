package com.tamin.taminhamrah.data.repository.base

import com.tamin.taminhamrah.utils.ErrorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A base repository class to handle safe API calls and error handling centrally.
 * This reduces boilerplate code (try-catch blocks) in individual repositories.
 */
abstract class BaseRepository {

    /**
     * Executes a suspend function (API call) safely within a try-catch block.
     * It handles exceptions using [ErrorUtils] and returns a [Result] wrapper.
     *
     * @param apiCall A suspend lambda representing the API call.
     * @return [Result] containing either the data or an exception with a friendly message.
     */
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                Result.success(response)
            } catch (e: Exception) {
                // Log the original exception here if needed (e.g., Timber.e(e))
                Result.failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }
    }
}
