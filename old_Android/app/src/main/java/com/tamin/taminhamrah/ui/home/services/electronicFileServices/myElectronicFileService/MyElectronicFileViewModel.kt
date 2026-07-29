package com.tamin.taminhamrah.ui.home.services.electronicFileServices.myElectronicFileService
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
class MyElectronicFileViewModel @Inject constructor(private val repository: ServiceRepository): BaseViewModel() {

    val getElectronicFile = createPager(repository::getElectronicFile).flow.cachedIn(viewModelScope)

    val mldMyElectronicFileDocumentFullSize = MutableLiveData<PdfDownloadResponse>()

    fun getMyElectronicFileDocumentFullSize(url:String){
        viewModelScope.launch {
            mldMyElectronicFileDocumentFullSize.postValue(callService {
                repository.getMyElectronicFileDocumentFullSize(url)
            })
        }
    }
}