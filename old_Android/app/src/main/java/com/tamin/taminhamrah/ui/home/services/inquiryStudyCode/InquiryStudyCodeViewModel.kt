package com.tamin.taminhamrah.ui.home.services.inquiryStudyCode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentDataModel
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InquiryStudyCodeViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldCheckRenewCondition = MutableLiveData<DependentInfoResponse>()
    val mldInquiryStudyCode = MutableLiveData<GeneralStringRes>()

    var selectedDependent: String = "1"
    val sonList by lazy {
        ArrayList<DependentDataModel>()
    }
    fun checkRenewCondition() {
        viewModelScope.launch {
            mldCheckRenewCondition.postValue(callService { repository.checkRenewCondition() })
        }
    }

    fun inquiryStudyCodeCertificate(code : String, studyCode : String) {
        viewModelScope.launch {
            mldInquiryStudyCode.postValue(callService { repository.inquiryStudyCodeCertificate(code = code , studyCode = studyCode) })
        }
    }
}