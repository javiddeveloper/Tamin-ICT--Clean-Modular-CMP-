package com.tamin.taminhamrah.data.local.services

import androidx.lifecycle.LiveData
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity
import com.tamin.taminhamrah.data.local.services.entity.ServiceEntity
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionType
import com.tamin.taminhamrah.data.remote.services.ServicesRemoteDataSource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ServiceLocalDataSourceImp @Inject constructor(
    private val remoteDataSource: ServicesRemoteDataSource,
    private val dao: ServiceDao,
    private val pref: PreferenceManager
) : ServiceLocalDataSource {

/* override fun getExploreList(itemLoadedCount: Int =
     dao.getExploreList(1, itemLoadedCount)*/

    override fun getInsuredServiceListFromJsonFile(): List<ServiceEntity> {

        //  return liveData(Dispatchers.IO) {
        return pref.getInsuredServiceListFromJsonFile()

        //   }
    }

    override fun getPensionerServiceListFromJsonFile(): List<ServiceEntity> {
        return pref.getPensionerServiceListFromJsonFile()
    }


//    override fun get(): LiveData<List<ServiceEntity>> {
//
//        return liveData(Dispatchers.IO) {
//            dao.getLastServices()
//        }
//
//    }


//    override fun getDbServices(): LiveData<List<ServiceEntity>> {
//
//        return (Dispatchers.IO).run {
//            dao.getServices()
//        }
//    }

    override fun getUserMode() = pref.getUserMode()

    override fun getAppliedServices(type: Int): LiveData<List<AppliedServiceEntity>> {
        return (Dispatchers.IO).run {
            dao.getAppliedService(type)
        }
    }

    override suspend fun getAllServiceSize(type: Int): Int {
        return (Dispatchers.IO).run {
            dao.getAllServiceSize(type)
        }
    }

    override suspend fun saveAppliedServices(list: List<AppliedServiceEntity>) {
        (Dispatchers.IO).run {
            dao.insertAllServices(list)
        }

    }

    override suspend fun updateAppliedService(id: Int) {
        (Dispatchers.IO).run {
            dao.updateAppliedService(id, System.currentTimeMillis() / 1000)
        }
    }

    override suspend fun getServices(): ServiceResponseModelNew {
        return pref.getServices()
    }


    override fun getAcraConfig() = pref.getAcraConfig()

    override fun saveAcraConfig(acraConfig: AcraConfigResponse) =
        pref.saveAcraConfig(acraConfig)


    override suspend fun checkUpdate(token: String): CheckUpdateResponse {
        return pref.checkUpdate()
    }

    override fun getObjectionTypeList(token: String): ObjectionType {
        return pref.getObjectionTypeList()
    }

    override fun getVersioningInfo(): List<VersionInfoModel> {
        return pref.getVersioningInfo()
    }

   override  fun saveWorkerPayInfo(ticket:String? , info : String?){
       pref.saveWorkerPayInfo(ticket, info)
   }
   override  fun getWorkerPayTicket()  = pref.getWorkerPayTicket()
   override  fun getWorkerPayInfo() = pref.getWorkerPayInfo()

/* override fun getItemsCount(): LiveData<Int> = exploreDao.getItemsCount()


 override fun getPageSize(): Int = Constants.LIST_LIMIT_SIZE

 override fun getLastVenueUpdate() =
     exploreDao.getLastVenueUpdate()

 override suspend fun insertLastVenueUpdate(lastVenueUpdateEntity: LastVenueUpdateEntity) {
     exploreDao.insertLastVenueUpdate(lastVenueUpdateEntity)
 }

 override suspend fun saveAll(exploreList: List<ExploreEntity>?) =
     exploreDao.insertAll(exploreList)*/


}