package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.viewInspectionsPerformedByOrganization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOfInspectionsPerformedViewModel @Inject constructor(private  val repository: ServiceRepository): BaseViewModel() {

    val getListInspectionPerformed = createPager(repository::getListInspectionPerformed).flow.cachedIn(viewModelScope)
    val getWorkshopListInspectionPerformed = createPager(repository::getWorkshopListInspectionPerformed).flow.cachedIn(viewModelScope)
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    /*use in employer inspection */
/*    val mldInspectionInfo = MutableLiveData<Event<InfoInspectionResponse>>()
    fun getInspectionInfo (insuranceCode : String,inspectionCode :String) {
        viewModelScope.launch {
            mldInspectionInfo.postValue(Event(callServiceTest {
                repository.getInspectionInfo(insuranceCode,inspectionCode)
            }))
        }
    }*/
    fun getPerformedInspectionPdf(inspectionNumber: String) {
        viewModelScope.launch {
            mldPdf.postValue(callService {
                repository.getInspectionPdf(inspectionNumber)
            })
        }
    }

}
