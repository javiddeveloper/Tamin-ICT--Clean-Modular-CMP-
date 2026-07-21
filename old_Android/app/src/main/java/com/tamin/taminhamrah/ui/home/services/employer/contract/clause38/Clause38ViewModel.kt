package com.tamin.taminhamrah.ui.home.services.employer.contract.clause38

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38DetailResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38Info
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class Clause38ViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    var MafasaHesabListPager: Pager<Int, Clause38Info>? = null

    fun getClause38List(
        workshopCode: String? = "",
        branchCode: String? = "",
        contractRow: String? = "",
        mafasaStatus: Int? = null,
        contractNumber: String? = ""

    ): Flow<PagingData<Clause38Info>>? {

        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!workshopCode.isNullOrEmpty())
            paramsMap["workshopCode"] = workshopCode

        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        if (!contractRow.isNullOrEmpty())
            paramsMap["contractRow"] = contractRow

        if (mafasaStatus != null)
            paramsMap["mafasaStatus"] = mafasaStatus.toString()

        if (!contractNumber.isNullOrEmpty())
            paramsMap["contractNumber"] = contractNumber.replace('/', 'L')

        if (MafasaHesabListPager == null)
            MafasaHesabListPager = createPager(repository::getClause38List, paramsMap = paramsMap)
        return MafasaHesabListPager?.flow?.cachedIn(viewModelScope)
    }

    var mldClause38Detail = MutableLiveData<Clause38DetailResponse>()
    fun getClause38Detail(workshopCode: String? = "",
                          branchCode: String? = "",
                          contractRow: String? = "",
                          mafasaSerialNo: String? = ""){

        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!workshopCode.isNullOrEmpty())
            paramsMap["workshopCode"] = workshopCode

        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        if (!contractRow.isNullOrEmpty())
            paramsMap["contractRow"] = contractRow

        if (!mafasaSerialNo.isNullOrEmpty())
            paramsMap["mafasaSerialNo"] = mafasaSerialNo

        viewModelScope.launch {
            supervisorScope {

                mldClause38Detail.postValue(callService {
                    repository.getClause38Detail(paramsMap) })
            }
        }
    }

}
