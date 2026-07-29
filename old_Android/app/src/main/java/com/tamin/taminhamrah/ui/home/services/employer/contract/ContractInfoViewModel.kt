package com.tamin.taminhamrah.ui.home.services.employer.contract

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedFileModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBase
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBaseSection
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfoNew
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.MafasaHesabRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContractInfoViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val ARG_CONTRACT_SUBJECT_CODE = "contractSubjectCode"
    val ARG_WORKSHOP_ID = "workshopId"
    val ARG_BRANCH_CODE = "branchCode"
    val ARG_PEYMAN_ROW = "peymanRow"
    val ARG_CONTRACT_SEQUENCE = "contractSequence"
    val ARG_CONTRACT_ROW = "contractRow"
    val ARG_CONTRACT = "ARG_CONTRACT"

    val imageFileList by lazy { ArrayList<UploadedImageModel>() }
    val pdfFileList by lazy { ArrayList<UploadedFileModel>() }

    val dataModel: MafasaHesabRequestModel by lazy { MafasaHesabRequestModel() }

    var contractListPager: Pager<Int, ContractInfoNew>? = null
    fun getContractListFlow(
        workshopId: String? = "",
        branchCode: String? = "",
        peymanRow: String? = "",
    ): Flow<PagingData<ContractInfoNew>>? {
        /*  val paramsMap: MutableMap<String, String> = mutableMapOf()
          if (!workshopId.isNullOrEmpty())
              paramsMap["workshopId"] = workshopId
          if (!branchCode.isNullOrEmpty())
              paramsMap["branchCode"] = branchCode
  */

        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!workshopId.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "workshop.workshopId")
            jsonObj.addProperty("value", workshopId)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        if (!branchCode.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "workshop.branchCode")
            jsonObj.addProperty("value", branchCode)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        if (!peymanRow.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "workshop.peymanRow")
            jsonObj.addProperty("value", peymanRow)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()

        return createPager(repository::getContractList, paramsMap = paramsMap).flow.cachedIn(viewModelScope)

    }

     fun getAssignerContractListFlow(
        workshopId: String? = "",
        branchCode: String? = "",
        contractRow: String? = "",
    ): Flow<PagingData<ContractInfo>> {

        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!workshopId.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "workshop.workshopId")
            jsonObj.addProperty("value", workshopId)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        if (!branchCode.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "workshop.branchCode")
            jsonObj.addProperty("value", branchCode)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        if (!contractRow.isNullOrEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "contractRow")
            jsonObj.addProperty("value", contractRow)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()

        return createPager(repository::getAssignerContractList, paramsMap = paramsMap).flow.cachedIn(viewModelScope)
    }

    fun getComputationalBaseListFlow(
        workshopId: String? = "",
        branchCode: String? = "",
        contractRow: String? = "",
        contractSequence: String? = ""
    ): Flow<PagingData<ComputationalBase>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        if (!contractRow.isNullOrEmpty())
            paramsMap["contractRow"] = contractRow
        if (!contractSequence.isNullOrEmpty())
            paramsMap["contractSequence"] = contractSequence


        val result = createPager(repository::getComputationalBaseList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    val mldComputationalBaseList = MutableLiveData<MutableList<ComputationalBaseSection>>()
    fun getComputationalBaseList(item: ComputationalBase?) {
        viewModelScope.launch {
            val itemList = mutableListOf<ComputationalBaseSection>()
            item?.apply {
                itemList.add(
                    ComputationalBaseSection(
                        "جزئیات قرارداد",
                        dataList = createKeyValueContractInfo(item)
                    )
                )
                itemList.add(
                    ComputationalBaseSection(
                        "اعلام مشخصات نامه و ارسال آن",
                        dataList = createKeyValueLetterInfo(item)
                    )
                )
                itemList.add(
                    ComputationalBaseSection(
                        "مدارک تصاویر نامه",
                        attachmentList = dataDetail?.filter { it.documentType == "1" && it.documentCode == "1" })
                )
                itemList.add(
                    ComputationalBaseSection(
                        "مدارک الحاقیه افزایش مدت و یا مبلغ قرارداد",
                        attachmentList = dataDetail?.filter { it.documentCode == "3" })
                )
                itemList.add(
                    ComputationalBaseSection(
                        "مدارک صورت وضعیت قطعی",
                        attachmentList = dataDetail?.filter { it.documentCode == "4" })
                )
                itemList.add(
                    ComputationalBaseSection(
                        "مدارک پیمانکاری فرعی",
                        attachmentList = dataDetail?.filter { it.documentCode == "2" })
                )
                itemList.add(
                    ComputationalBaseSection(
                        item.getConstructionContractTitle(),
                        dataList = createKeyValueConstructionContract(item)
                    )
                )
            }

            itemList.forEach {
                it.attachmentList?.forEach { attachment ->
                    if (attachment.documentType == "1")
                        attachment.documentId?.let {
                            val result = callService {
                                repository.getDocument(it,"")
                            }

                            attachment.attachmentUrl = result.data as? String
                        }
                }
            }

            mldComputationalBaseList.postValue(itemList)
        }
    }

    val mldPdf = MutableLiveData<Resource<String?>>()

    fun downloadComputationalBasePdf(documentId: String, fileName: String) {

        viewModelScope.launch {
            mldPdf.postValue(Resource.loading(null))
            try {
                val result = repository.downloadComputationalBasePdf(documentId, fileName)
                mldPdf.postValue(result)

            } catch (e: Exception) {
                mldPdf.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }
    }


    fun getContractActionListFlow() = createLocalPager(ArrayList<MenuModel>().apply {
        add(MenuModel("مشاهده فرم مبانی محاسباتی", "0", iconRes = R.drawable.ic_list))
        add(MenuModel("مفاصاحساب ماده38", "1", iconRes = R.drawable.ic_list))

    }).flow.cachedIn(viewModelScope)

    fun getAssignerActionListFlow() = createLocalPager(ArrayList<MenuModel>().apply {
        add(MenuModel("مشاهده جزئیات پیمان", "0", iconRes = R.drawable.ic_doc))
        add(MenuModel("مشاهده مبانی محاسباتی", "1", iconRes = R.drawable.ic_doc))
        add(MenuModel("ثبت درخواست صدور مفاصاحساب", "2", iconRes = R.drawable.ic_add_circle))

    }).flow.cachedIn(viewModelScope)

}