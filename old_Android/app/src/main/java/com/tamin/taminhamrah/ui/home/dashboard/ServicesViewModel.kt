package com.tamin.taminhamrah.ui.home.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.profile.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModel
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.getUserType
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.EnumUserMode
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.dashboard.model.ServiceByUserType
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.CheckChatAllowedUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.IsChatAllowedLocalUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel
@Inject constructor(
    private val repository: ServiceRepository,
    private val checkChatAllowedUseCase: CheckChatAllowedUseCase,
    private val isChatAllowedLocalUseCase: IsChatAllowedLocalUseCase
) : BaseViewModel() {

    var isAiIntroPlayed = false
    val mldIsChatAllowed = MutableLiveData<Boolean>()

    fun checkChatAllowed() {
        viewModelScope.launch {
            checkChatAllowedUseCase()
            updateChatAllowedStatus()
        }
    }

    fun updateChatAllowedStatus() {
        mldIsChatAllowed.value = isChatAllowedLocalUseCase()
    }

    var mldFilterType = MutableLiveData<EnumUserMode>()
    val mldServices = MutableLiveData<ServiceResponseModel>()
    val mldDashboardServices = MutableLiveData<ServiceResponseModelNew>()
    val mldAcraConfig = MutableLiveData<AcraConfigResponse>()

    //save the main List
    val mldMainServiceList = MutableLiveData<MutableList<ServiceMainModel>?>()

    val mldSearchService: LiveData<MutableList<ServiceMainModel>?>
        get() = mldMainServiceList

    val mldUserAvatar = MutableLiveData<String?>()
    val mldProfile = MutableLiveData<ProfileModel>()

    fun getUserInfo() {
        viewModelScope.launch {

            val avatarRes = async(Dispatchers.IO) {
                if (getUserAvatar().isNullOrBlank() || getUserAvatar() == "null") {
                    val result = repository.getUserProfileImage()
                    if (result.isSuccess)
                        commonRepository.setUserAvatar(result.data)


                    result

                } else {
                    val result = GeneralStringRes(getUserAvatar())
                    result.baseStatus = BaseStatus(MessageModel(), ServiceStatus.SUCCESS)
                    result
                }
            }

            val profileRes = async(Dispatchers.IO) {
                val userinfo = commonRepository.getUserInfo()

                if (userinfo.fullName.isNullOrBlank()) {
                    val result = repository.getProfileInfo()
                    if (result.isSuccess)
                        commonRepository.setUserInfo(result.asDomainModel())

                    result.asDomainModel()
                } else userinfo
            }

            avatarRes.await().apply {
                mldUserAvatar.postValue(this.data)
            }

            profileRes.await().apply {
                mldProfile.postValue(this)
            }

        }
    }


    fun hasData(): Boolean {
        return mldMainServiceList.value != null && !mldMainServiceList.value.isNullOrEmpty()
    }

    fun getAppliedServices() = commonRepository.getAppliedServices(getSelectedModeValue())

    var tempList = ServiceResponseModel()

    fun getServiceForDashboard() {
        viewModelScope.launch {

            mldDashboardServices.postValue(callService {
                repository.getServices()
            })
        }
    }

    fun getAcraConfig() {
        viewModelScope.launch {
            val localConfig = repository.getLocalAcraConfig()

            val remoteConfig = async(Dispatchers.IO) {
                repository.getRemoteAcraConfig()
            }.await()
            Timber.tag("acraConfigResponse")
                .e("localConfig=${localConfig.data}  remoteConfig=${remoteConfig.isSuccess} remoteConfig=${remoteConfig.data}")




            if (remoteConfig.data != null && remoteConfig.data?.basicAuthLogin != localConfig.data?.basicAuthLogin) {
                // need to change config
                Timber.tag("acraConfigResponse")
                    .e("need to change Config")
                repository.saveAcraConfig(remoteConfig)

            } else {
                // no need to change congif
                Timber.tag("acraConfigResponse")
                    .i("NO need to change Config")
            }
        }
    }

//    fun getServices() {
//
//        viewModelScope.launch {
//            //     mldServices.postValue(Resource.loading(null))
//            //   mldLoadingState.postValue(LoadingState.LOADING)
//
//            val list = callService { repository.getServices() }
//            tempList.data = ServiceData()
//            tempList.data?.menu = ArrayList()
//            tempList.data?.menu?.add(ServiceMenuModel())
//            tempList.baseStatus = BaseStatus(MessageModel("", 0), ServiceStatus.SUCCESS)
//            tempList.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//
//
//            /*     repository.getInsuredServiceListFromJsonFile() as? MutableList<ServiceModel>
//
//
//            list?.addAll(repository.getPensionerServiceListFromJsonFile() as MutableList<ServiceModel>)*/
//
//            // val lastSeen = repository.getLastSeenServices(mldIsEmployer.value!!)
//
//            //  lastSeen.value?.let { list?.addAll(it) }
//
//            // var resultList = ServiceResponseModel()
//            var appliedService: MutableList<AppliedServiceEntity> = ArrayList()
//
//            // resultList.baseStatus = list.baseStatus
//
//            if (list.data?.menu?.get(0)?.groups != null) {
//
//                val groups = list.data!!.menu!![0].groups!!.filter { it.type == getUserModeValue() }
//                    .toMutableList()
//                groups.forEach {
//                    it.serviceList = it.serviceList?.filter { it.active }?.toMutableList()
//
//                    for (service in it.serviceList!!) {
//                        appliedService.add(
//                            AppliedServiceEntity(
//                                service,
//                                it.type ?: 1
//                            )
//                        )
//
//                    }
//                }
//                list.data!!.menu?.get(0)!!.groups = groups.filter {
//                    it.serviceList != null && (it.serviceList?.isNotEmpty() ?: false)
//                }.toMutableList()
//                mldServices.postValue(list)
//
//                initData(list.data?.menu?.get(0)?.groups)
//                if (appliedService.size != commonRepository.getAllServiceSize(getUserModeValue()))
//                    (Dispatchers.IO).run {
//                        commonRepository.saveService(appliedService)
//                    }
//
//
//            }
////            else {
////                initData(list.data?.menu?.get(0)?.groups)
////                mldServices.postValue(list)
////            }
//
//        }
//    }

    private fun initData(list: MutableList<ServiceMainModel>?) {

        mldMainServiceList.value = list
    }


    /*fun searchList(searchString: String?) {
        mldSearchService.postValue(Resource.loading(null))
        try {

            val searchList = ArrayList<ServiceMainModel>()
            if (searchString.isNullOrBlank()) {
                mldSelectedService.value?.data?.serviceList?.let { searchList.addAll(it) }
            } else {
                mldSearchService.value?.data?.let {
                    for (item in it) {
                        if (item.title?.contains(searchString) == true || item.title?.contains(
                                searchString
                            ) == true
                        ) {
                            searchList.add(item)
                        }
                    }
                }
            }


            mldSearchService.postValue(Resource.success(searchList))

        } catch (e: Exception) {
            mldSearchService.postValue(Resource.error(e.toString()))
        }

    }*/

    fun getAllList() {
        mldFilterType.value = (EnumUserMode.TYPE_ALL)
        mldMainServiceList.value?.forEach { it.isSelected = true }
//        val data = ServiceResponseModel()
//        data.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//        data.data?.menu?.get(0)?.groups = mldMainServiceList.value
        tempList.data!!.menu?.get(0)!!.groups = mldMainServiceList.value

        mldServices.postValue(tempList)
    }

    fun getPensionerList() {
        val itemList = ArrayList<ServiceMainModel>()
        mldMainServiceList.value?.forEach {
            if (it.type == 2)
                itemList.add(it)
        }

        mldFilterType.value = (EnumUserMode.MODE_PENSIONER)
//        val data = ServiceResponseModel()
//        data.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//        data.data?.menu?.get(0)?.groups = itemList
        tempList.data!!.menu?.get(0)!!.groups = itemList

        mldServices.postValue(tempList)
    }

    fun getInsuredList() {
        val itemList = ArrayList<ServiceMainModel>()
        mldMainServiceList.value?.forEach {
            if (it.type == 1)
                itemList.add(it)
        }

        mldFilterType.value = (EnumUserMode.MODE_INSURED)
//        val data = ServiceResponseModel()
//        if (data.baseStatus == null)
//            data.baseStatus = BaseStatus(MessageModel("", 0), ServiceStatus.SUCCESS)
//
//        data.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//        data.data?.menu?.get(0)?.groups = itemList
        tempList.data!!.menu?.get(0)!!.groups = itemList

        mldServices.postValue(tempList)
    }

    fun searchList(searchStr: String) {
        val searchList = ArrayList<ServiceMainModel>()
        if (searchStr.isNotBlank()) {
            if (searchStr.length > 2) {

                mldSearchService.value?.forEach { mainService ->

                    if (mainService.title?.contains(searchStr) == true) {
                        searchList.add(mainService)
                    }
                    val item = ServiceMainModel()
                    item.title = mainService.title
                    item.type = mainService.type
                    val itemList = ArrayList<ServiceModel>()
                    mainService.serviceList?.forEach { service ->
                        if (service.name?.contains(searchStr) == true) {
                            itemList.add(service)
                        }
                        item.serviceList = itemList
                    }
                    if (item.serviceList?.isNotEmpty() == true)
                        searchList.add(item)
                }

                mldFilterType.value = (EnumUserMode.TYPE_ALL)
//                val data = ServiceResponseModel()
//                if (data.baseStatus == null)
//                    data.baseStatus = BaseStatus(MessageModel("", 0), ServiceStatus.SUCCESS)
//                data.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//                data.data?.menu?.get(0)?.groups = searchList
                tempList.data!!.menu?.get(0)!!.groups = searchList
                mldServices.postValue(tempList)
            } else {
                getAllList()
            }
        } else {
            getAllList()
        }
    }

    fun filterByTitle() {
        val searchList = ArrayList<ServiceMainModel>()
        mldSearchService.value?.forEach { mainService ->
            if (mainService.isSelected) {
                searchList.add(mainService)
            }
        }
        mldFilterType.value = (EnumUserMode.TYPE_ALL)
//        val data = ServiceResponseModel()
//        if (data.baseStatus == null)
//            data.baseStatus = BaseStatus(MessageModel("", 0), ServiceStatus.SUCCESS)
//        // data.baseStatus?.serviceStatus = ServiceStatus.SUCCESS
//        data.data?.menu?.get(0)?.groups = searchList
        tempList.data!!.menu?.get(0)!!.groups = searchList

        mldServices.postValue(tempList)
    }

    fun getSelectedService(selectedItemTitle: String?, selectedItemType: Int?): ServiceMainModel? {
        mldMainServiceList.value?.forEach {
            if (it.title == selectedItemTitle && it.type == selectedItemType) {
                return it
            }
        }
        return null
    }

    fun getFilterList(): ArrayList<MenuModel> {

        val itemList = ArrayList<MenuModel>()
        mldMainServiceList.value?.forEach { service ->
            itemList.add(MenuModel(title = service.title, isSelected = service.isSelected))
        }
        return itemList
    }

    val mldPensionCheck = MutableLiveData<Event<ServiceByUserType>>()

    /* fun pensionCheck(item: ServiceItem) {
         viewModelScope.launch {
             if (commonRepository.getUserType() == EnumTypeUser.ANONYMOUS.title) {
                 val result = callService { repository.getPensionerIdList(null) }callService { repository.pensionCheck() }
                 var serviceByUserType:ServiceByUserType?=null
                 if (result.isSuccess) {
                     serviceByUserType = ServiceByUserType(list = result.data?.list, serviceItem = item)
                     commonRepository.setUserType(serviceByUserType.typeUser?: ANONYMOUS.toString())
                 }
                 mldPensionCheck.postValue(Event(serviceByUserType))
             } else {
                 val result = CheckPensionerResponse()
                 result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                 result.data = Date(typeUser = commonRepository.getUserType(), serviceItem = item)
                 mldPensionCheck.postValue(Event(result))
             }
         }
     }*/

    fun checkPensionInfo() {
        viewModelScope.launch {
            val result = callService { repository.getPensionerId() }
            if (result.isSuccess && result.data != null) {
                commonRepository.setInsuredType(result.data!!.list.getUserType())
            } else {
                commonRepository.setPensionerType(EnumTypeUser.TEMPORARY.title)
            }
        }
    }

    fun checkInsuredInfo() {
        viewModelScope.launch {
            val result = callService { repository.checkInsuredInfo() }
            if (result.isSuccess && result.data != null) {
                val type = result.data!!.getUserType()
                commonRepository.setInsuredType(type)
                if (type == EnumTypeUser.INSURED.title && !result.data?.list.isNullOrEmpty()) {
                    commonRepository.setInsuredMessage(result.data?.list?.get(1))
                }
            } else {
                commonRepository.setInsuredType(EnumTypeUser.TEMPORARY.title)
            }
        }
    }

    fun getUserType() = commonRepository.getUserType()
    fun getInsuredType() = commonRepository.getInsuredType()
    fun getInsuredMessage() = commonRepository.getInsuredMessage()
    fun getPensionerType() = commonRepository.getPensionerType()

}

