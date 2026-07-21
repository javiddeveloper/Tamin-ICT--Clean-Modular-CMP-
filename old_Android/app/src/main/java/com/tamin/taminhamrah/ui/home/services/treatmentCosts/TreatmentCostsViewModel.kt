package com.tamin.taminhamrah.ui.home.services.treatmentCosts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.SendToInboxTreatmentCosts
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TreatmentCostsViewModel @Inject constructor(private val repository: ServiceRepository): BaseViewModel() {

    val mldTreatmentCostsPDF = MutableLiveData<PdfDownloadResponse>()
    val mldSendToInboxTreatmentCosts = MutableLiveData<SendToInboxTreatmentCosts>()

    val getTreatmentCosts = createPager(repository::getTreatmentCosts,
        Constants.QUERY_PAGE_SIZE_10.toString()).flow.cachedIn(viewModelScope)

    fun getTreatmentCostsPDF(id:String){
        viewModelScope.launch {
            mldTreatmentCostsPDF.postValue(callService {
                repository.getTreatmentCostsPDF(id)
            })
        }
    }
    fun sendToInboxTreatmentCosts(id:String){
        viewModelScope.launch {
            mldSendToInboxTreatmentCosts.postValue(callService { repository.sendToInboxTreatmentCosts(id) })
        }
    }

    fun getMainActions() = listOf(
        MenuModel(titleStringResId = R.string.view_certificate, id = "1"),
        MenuModel(titleStringResId = R.string.label_send_to_inbox, id = "2"),
    )

}