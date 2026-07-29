package com.tamin.taminhamrah.ui.home.services.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankAccountDeclarationViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldAccountTypeList = createLocalPager(getAccountTypeList()).flow.cachedIn(viewModelScope)
    val mldBanKList = createLocalPager(getBanKList()).flow.cachedIn(viewModelScope)
    var mldAccountResult = MutableLiveData<GeneralRes>()

    fun sendBankAccountInfo(
        accountNumberStr: String,
        accountTypeId: String,
        bankId: String,
        startDate: String
    ) {
        viewModelScope.launch {

            mldAccountResult.postValue(callService {
                repository.sendBankAccountInfo(
                    accountNumberStr,
                    accountTypeId,
                    bankId,
                    startDate
                )
            })
        }
    }

/*
    this.banks = [
    {name: 'بانک رفاه', value: '01'},
    {name: 'بانک ملی ایران', value: '02'},
    {name: 'بانک ملت', value: '03'},
    {name: 'بانک تجارت', value: '04'},
    {name: 'بانک سپه', value: '07'},
    {name: 'بانک صادرات', value: '05'}
    ];
*/

    fun getBanKList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("بانک رفاه", "01"))
        itemList.add(MenuModel("بانک ملی ایران", "02"))
        itemList.add(MenuModel("بانک ملت", "03"))
        itemList.add(MenuModel("بانک تجارت", "04"))
        itemList.add(MenuModel("بانک سپه", "07"))
        itemList.add(MenuModel("بانک صادرات", "05"))

        return itemList
    }

/*    this.accountTypes = [
    {name: 'قرض الحسنه', value: '01'},
    {name: 'پس انداز عادی', value: '02'},
    {name: 'پس انداز همراه', value: '03'},
    {name: 'جاری عادی', value: '04'},
    {name: 'جاری همراه', value: '05'}
    ];
}*/

    fun getAccountTypeList(): List<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("قرض الحسنه", "01"))
        itemList.add(MenuModel("پس انداز عادی", "02"))
        itemList.add(MenuModel("پس انداز همراه", "03"))
        itemList.add(MenuModel("جاری عادی", "04"))
        itemList.add(MenuModel("جاری همراه", "05"))

        return itemList
    }

}

