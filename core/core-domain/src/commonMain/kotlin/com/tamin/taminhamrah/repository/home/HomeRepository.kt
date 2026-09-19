package com.tamin.taminhamrah.repository.home

import com.tamin.taminhamrah.model.home.HomeContentDN
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeContent(): Flow<HomeContentDN?>
    suspend fun syncHomeContent()
}
