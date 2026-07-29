package com.tamin.taminhamrah.ui.home.services.medicalAuthorities

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MedicalAuthoritiesViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldConfirmationMedicalAuthorities =
        createPager(repository::getConfirmationMedicalAuthorities)
            .flow.cachedIn(viewModelScope)
}