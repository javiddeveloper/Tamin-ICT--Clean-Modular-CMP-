package com.tamin.taminhamrah.ui.home.services.employer.inspection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedModel
import com.tamin.taminhamrah.data.remote.models.services.performedInspections.InspectionResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformedInspectionViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldPerformedInspection = MutableLiveData<Resource<BaseListResponse<InspectionResponse>?>>()
    val mldPdf = MutableLiveData<Resource<String?>>()


    /*  val listData = Pager(PagingConfig(pageSize = 6)) {
          PostDataSource(repository)
      }.flow.cachedIn(viewModelScope)
  */


    fun getEmployerAgreementInfoList(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<EmployerAgreement>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        return createPager(
            repository::getEmployerAgreementInfoList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }


    fun getPerformedInspectionList(
    ): Flow<PagingData<InspectionPerformedModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        return createPager(
            repository::getWorkshopListInspectionPerformed,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    /*fun getPerformedInspectionList(
        currentPage: Int = 1,
        pageSize: Int = 10,
        startIndex: Int = 0
    ) {

        viewModelScope.launch {


            //   listData.collect {
            //         mainListAdapter.submitData(it)
            //    }


            mldPerformedInspection.postValue(Resource.loading(null))
            try {

                val result =
                    repository.getPerformedInspectionList(currentPage, pageSize, startIndex)
                mldPerformedInspection.postValue(result)

            } catch (e: Exception) {
                mldPerformedInspection.postValue(
                    Resource.error(
                        MessageModel(
                            e.message ?: e.toString(), 0
                        )
                    )
                )
            }
        }
    }*/

    fun getPerformedInspectionPdf(inspectionNumber: String) {

        viewModelScope.launch {
            mldPdf.postValue(Resource.loading(null))
            try {

                val result = repository.getPerformedInspectionPdf(inspectionNumber)
                mldPdf.postValue(result)

            } catch (e: Exception) {
                mldPdf.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }
    }

}


/*
class PostDataSource(private val repository: ServiceRepository) :
    PagingSource<Int, InspectionResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, InspectionResponse> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = repository.getPerformedInspectionList(currentLoadingPageKey, 10, 0)
            val responseData = mutableListOf<InspectionResponse>()
            val data = response.data?.list ?: emptyList()
            responseData.addAll(data)

            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
    @androidx.paging.ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, InspectionResponse>): Int? {
        return 0
    }



}

*/