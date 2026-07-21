package com.tamin.taminhamrah.ui.home.services.calculateWageIllDays

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculateWageIllDaysViewModel @Inject constructor(private val repository: ServiceRepository):BaseViewModel(){

    val mldCalculateWageIllDays = MutableLiveData<Resource<List<String?>?>> ()

    fun calculateWageIllDays(StartDateTimeStamp: String, EndDateTimeStamp: String, marital_status: String) {
        viewModelScope.launch {
            mldCalculateWageIllDays.postValue(Resource.loading(null))
            try {
                val result = repository.calculateWageIllDay(StartDateTimeStamp,EndDateTimeStamp,marital_status)
                result.let {
                    mldCalculateWageIllDays.postValue(it)
                }
            } catch (e: Exception) {
                mldCalculateWageIllDays.postValue(Resource.error(MessageModel(e.message?:e.toString(),0)))
            }
        }
    }

    fun getMaritalStatusList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("مجرد", "1"))
        itemList.add(MenuModel("متاهل", "2"))
        return itemList
    }

}