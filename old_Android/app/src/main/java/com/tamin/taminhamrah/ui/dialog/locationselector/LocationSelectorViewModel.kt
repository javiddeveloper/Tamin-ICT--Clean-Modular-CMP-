package com.tamin.taminhamrah.ui.dialog.locationselector

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.CityModel
import com.tamin.taminhamrah.data.remote.models.services.ProvinceModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LocationSelectorViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    //Provinces
    fun getProvincesList(
        provinceName: String? = null
    ): Flow<PagingData<ProvinceModel>> {

        val array = JsonArray()

        if (!provinceName.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "provinceName")
                addProperty("operator", "LIKE")
                addProperty("value", "*$provinceName*")
            //    [{"property":"provinceName","value":"*ته*%","operator":"LIKE"}]
                array.add(this)
            }
        }

        return createPager(
            repository::getProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )
    }

    //Cities
    fun getCitiesList(
        provinceCode: String? = null,
        cityName: String? =null
    ): Flow<PagingData<CityModel>> {
        val array = JsonArray()
        if (!cityName.isNullOrEmpty()) {
            JsonObject().apply {
                addProperty("property", "cityName")
                addProperty("operator", "LIKE")
//                addProperty("value", "*$cityName*")
                addProperty("value", "*$cityName*")
                array.add(this)
            }
        }
        provinceCode?.let {
            JsonObject().apply {
                addProperty("property", "provincecode")
                addProperty("operator", "EQ")
                addProperty("value", it)
                array.add(this)
            }
        }

        return createPager(
            repository::getCitiesOfProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(viewModelScope)

    }


    // BRANCH
    fun getBranchList(
        cityCode: String? = null,
        branchName: String? = null
    ): Flow<PagingData<BranchesInfoListModel>> {
        val array = JsonArray()

        if (!branchName.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "name")
                addProperty("value", "*$branchName*")
                addProperty("operator", "LIKE")
                array.add(this)
            }
        }

        if (!cityCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "cityCode")
                addProperty("operator", "EQ")
                addProperty("value", cityCode)
                array.add(this)
            }
        }
        return createPager(
            repository::getInfoBranch,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )

    }

}