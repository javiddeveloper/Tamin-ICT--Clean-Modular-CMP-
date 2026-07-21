package com.tamin.taminhamrah.data.local.services

import androidx.lifecycle.LiveData
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity
import com.tamin.taminhamrah.data.local.services.entity.ServiceEntity
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionType

interface ServiceLocalDataSource {

    fun getInsuredServiceListFromJsonFile(): List<ServiceEntity>
    fun getPensionerServiceListFromJsonFile(): List<ServiceEntity>
    fun getUserMode(): String?
    fun getAppliedServices(type: Int): LiveData<List<AppliedServiceEntity>>
    suspend fun saveAppliedServices(list: List<AppliedServiceEntity>)
    suspend fun updateAppliedService(id: Int)
    suspend fun getAllServiceSize(type: Int): Int

    suspend fun checkUpdate(token: String): CheckUpdateResponse
    suspend fun getServices(): ServiceResponseModelNew

    fun getAcraConfig(): AcraConfigResponse
    fun saveAcraConfig(acraConfig: AcraConfigResponse)
    fun getObjectionTypeList(token: String): ObjectionType
    fun getVersioningInfo(): List<VersionInfoModel>

    fun saveWorkerPayInfo(ticket:String? , info : String?)
    fun getWorkerPayTicket() : String?
    fun getWorkerPayInfo() :String?

}