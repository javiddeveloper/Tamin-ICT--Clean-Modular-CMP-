package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.BaseUrlKey
import kotlinx.coroutines.flow.Flow

interface DeveloperOptionsRepository {
    fun getEffectiveBaseUrl(key: BaseUrlKey): String
    fun observeOverrides(): Flow<Map<BaseUrlKey, String>>
    fun setOverride(key: BaseUrlKey, url: String)
    fun clearOverride(key: BaseUrlKey)
}
