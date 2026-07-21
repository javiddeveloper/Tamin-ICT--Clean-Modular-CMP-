package com.tamin.taminhamrah.ui.home.services.contractList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.user.ContractItem
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContractListViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val ARG_INSURANCE_TYPE = "ARG_NATIONAL_CODE"
    val ARG_CONTRACT_NUMBER = "ARG_INSURANCE_NUMBER"

    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    private fun getInsuranceTypeList(): ArrayList<MenuModel> {
        val list = ArrayList<MenuModel>()
        list.add(MenuModel("حرف و مشاغل آزاد", "01"))
        list.add(MenuModel("اختیاری", "02"))
        list.add(MenuModel("تکمیل سوابق کسری از ماه", "38"))
        return list
    }

    fun getContractInsuranceList(premiumTypeCode: String? = null, contractNumber: String? = null)
            : kotlinx.coroutines.flow.Flow<PagingData<ContractItem>> {
        val array = JsonArray()
        val sort = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!contractNumber.isNullOrBlank()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "contractNumber")
            jsonObj.addProperty("operator", "EQ")
            jsonObj.addProperty("value", contractNumber)
            array.add(jsonObj)
        }
        if (!premiumTypeCode.isNullOrBlank()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "premiumTypeCode")
            jsonObj.addProperty("operator", "EQ")
            jsonObj.addProperty("value", premiumTypeCode)
            array.add(jsonObj)
        }

        //add sort
        val jsonObj = JsonObject()
        jsonObj.addProperty("property", "creatDate")
        jsonObj.addProperty("direction", "DESC")
        sort.add(jsonObj)


        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        paramsMap[Constants.SORT_KEY_FOR_MAP] = sort.toString()
        val result = createPager(repository::getContractInsuranceList, paramsMap = paramsMap)

        /*result {

            mldListStatus.postValue(false)

        }
 */

        return result.flow.cachedIn(viewModelScope)
    }

    fun getActionListFlow(item: ContractItem) =
        createLocalPager(getActionsList(item)).flow.cachedIn(viewModelScope)

    private fun getActionsList(item: ContractItem): ArrayList<MenuModel> {
        //01 --> freelance insurance
        //02--> optional insurance
        //38--> Fraction Contract
        //item.contractStatusObject.selfIsuContStatDode === 1  => active contract

        val list = ArrayList<MenuModel>()

        if (item.contractStatusObject?.selfIsuContStatCode == 1) {
            //38 -->fraction of the month
            //01 --> freelance insurance
            //02--> optional insurance
            //item.contractStatusObject.selfIsuContStatDode === 1  => active contract

            if (item.premiumTypeCode == "38") {
                list.add(MenuModel("مشاهده قرارداد", "2", iconRes = R.drawable.ic_file))
            } else {
                if (item.cntFreeJobCode != Constants.RED_CRESCENT_CODE && item.cntFreeJobCode != Constants.MEDICAL_STUDENT_CODE) {
                    list.add(
                        MenuModel(
                            "پرداخت حق بیمه",
                            "0",
                            iconRes = R.drawable.ic_credit_card
                        )
                    )
                }
                list.add(MenuModel("مشاهده پرداخت ها", "1", iconRes = R.drawable.ic_payments))

                list.add(MenuModel("ویرایش قرارداد", "2", iconRes = R.drawable.ic_file))

                list.add(MenuModel("مشاهده قرارداد", "2", iconRes = R.drawable.ic_file))

                if (item.cntFreeJobCode != Constants.RED_CRESCENT_CODE) {
                    list.add(MenuModel("غیرفعال کردن قرارداد", "3", iconRes = R.drawable.ic_delete))
                }
            }
        }
        return list
    }

    fun downloadContractPdf(isOptionalContract: Boolean) {
        viewModelScope.launch {
            mldPdf.postValue(callService { if (isOptionalContract) repository.downloadOptionalContract() else repository.downloadContractPdf() })
        }
    }

    fun downloadFractionContractPdf() {
        viewModelScope.launch {
            mldPdf.postValue(callService { repository.downloadFractionContract() })
        }
    }

}