package com.tamin.taminhamrah.ui.home.services.accountlist

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BankAccountListViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldBankAccountList = createPager(repository::getBankAccountList).flow.cachedIn(viewModelScope)

}

