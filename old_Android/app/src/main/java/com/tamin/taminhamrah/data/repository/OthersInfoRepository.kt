package com.tamin.taminhamrah.data.repository

import android.content.Context
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.local.services.ServiceLocalDataSource
import com.tamin.taminhamrah.data.remote.services.ServicesRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OthersInfoRepository @Inject constructor(
    private val remoteDataSource: ServicesRemoteDataSource,
    private val localDataSource: ServiceLocalDataSource,
    private val pre: PreferenceManager,
    @ApplicationContext val context: Context
) {


    fun getVersioningInfo() = localDataSource.getVersioningInfo()


}

