package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.BRANCH_ID
import com.tamin.taminhamrah.Constants.WORKSHOP_ID
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16RequestModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopInfoDebtArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopListDefinitiveArticle16Model
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.ActionDebtEnumClass
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.DebtArticle16DataModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.FilterRequestTypeEnumClass
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.FilterWorkshopEnumClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DefinitiveDebtArticle16ViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldDebtListList = MutableLiveData<WorkshopsDebtListResponse>()
    val mldWorkshopInfo = MutableLiveData<WorkShopInfoDebtArticle16Response>()
    val workShopList = ArrayList<WorkShopListDefinitiveArticle16Model>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    val mldRegisterRequest = MutableLiveData<RegisterArticle16Response>()
    val mldExpertsMessageArticle16 = MutableLiveData<Article16RequestInfoResponse>()
    val debtList = ArrayList<WorkshopsDebtListModel>()
    val dataModel by lazy { DebtArticle16DataModel() }

    //val menuUrl = "https://eservices.tamin.ir/view/assets/data/objection-type.json"
  //  val menuUrl = "http://172.16.13.248:4200/assets/data/objection-type.json"

    fun createWorkShopPaging() = createLocalPager(workShopList).flow.cachedIn(viewModelScope)
    fun createDebtListPaging() = createLocalPager(debtList).flow.cachedIn(viewModelScope)

    fun getWorkshopListDefinitiveDebt(
        workshopId: String? = "",
        branchCode: String? = "",
    ): Flow<PagingData<WorkShopListDefinitiveArticle16Model>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        return createPager(
            repository::getWorkshopListDefinitiveDebt,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun getWorkshopsDebtsListPaging(
        workshopId: String,
        branchId: String,
        debtNumber: String? = "",
        agreementRow: String? = "",
    ): Flow<PagingData<WorkshopsDebtListModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!debtNumber.isNullOrEmpty())
            paramsMap[Constants.DEBIT_NUMBER] = debtNumber
        if (!agreementRow.isNullOrEmpty())
            paramsMap[Constants.AGREEMENT_ROW] = agreementRow

        paramsMap["workshopId"] = workshopId
        paramsMap["branchId"] = branchId
        return createPager(
            repository::getWorkshopsDebtsList,
            paramsMap = paramsMap,
        ).flow.cachedIn(viewModelScope)
    }

    fun getWorkshopsDebtsList(workshopId: String, branchCode: String,debtNumber: String? = "", agreementRow: String? = "",) {
        viewModelScope.launch {
            val paramsMap: MutableMap<String, String> = mutableMapOf()

            if (!debtNumber.isNullOrEmpty())
                paramsMap[Constants.DEBIT_NUMBER] = debtNumber
            if (!agreementRow.isNullOrEmpty())
                paramsMap[Constants.AGREEMENT_ROW] = agreementRow

            paramsMap[WORKSHOP_ID] = workshopId
            paramsMap[BRANCH_ID] = branchCode
            mldDebtListList.postValue(callService { repository.getWorkshopsDebtsList(paramsMap) })
        }
    }

    fun getFilterWorkshop() = listOf(
        MenuModel(titleStringResId = FilterWorkshopEnumClass.DELETE_FILTER.title, id = FilterWorkshopEnumClass.DELETE_FILTER.id),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.ACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.ACTIVE_WORKSHOP.id),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.SEMI_ACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.SEMI_ACTIVE_WORKSHOP.id),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.INACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.INACTIVE_WORKSHOP.id),
    )

    fun getFilterRequestType() = listOf(
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.DELETE_FILTER.title, id = FilterRequestTypeEnumClass.DELETE_FILTER.id),
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.NEW_REQUEST.title, id = FilterRequestTypeEnumClass.NEW_REQUEST.id),
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.DOC_VIOLATION_STATE.title, id = FilterRequestTypeEnumClass.DOC_VIOLATION_STATE.id),
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.REJECT_REQUEST_STATE.title, id = FilterRequestTypeEnumClass.REJECT_REQUEST_STATE.id),
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.CONFIRM_REQUEST_STATE.title, id = FilterRequestTypeEnumClass.CONFIRM_REQUEST_STATE.id),
        MenuModel(titleStringResId = FilterRequestTypeEnumClass.UNKNOWN_STATE.title, id = FilterRequestTypeEnumClass.UNKNOWN_STATE.id),
    )

    fun getActionList(status: String): ArrayList<MenuModel> {
        val list = ArrayList<MenuModel>()
        when (status) {
            "0" -> {
                list.add(MenuModel(titleStringResId = ActionDebtEnumClass.INVESTIGATION_DEBTS.title, id = ActionDebtEnumClass.INVESTIGATION_DEBTS.id))
            }
            "7" -> {
                list.add(MenuModel(titleStringResId = ActionDebtEnumClass.EXPERT_MESSAGE.title, id = ActionDebtEnumClass.EXPERT_MESSAGE.id))
                list.add(MenuModel(titleStringResId = ActionDebtEnumClass.MODIFY_REQUEST.title, id = ActionDebtEnumClass.MODIFY_REQUEST.id))
            }
            else -> {
                list.add(MenuModel(titleStringResId = ActionDebtEnumClass.SHOW_REQUEST.title, id = ActionDebtEnumClass.SHOW_REQUEST.id))
            }
        }
        return list
    }

    fun getWorkshopInfoDebtArticle16(workshopId: String, branchId: String) {
        viewModelScope.launch {
            val paramsMap: MutableMap<String, String> = mutableMapOf()
            paramsMap[WORKSHOP_ID] = workshopId
            paramsMap[BRANCH_ID] = branchId
            mldWorkshopInfo.postValue(callService { repository.getWorkshopInfoDebtArticle16(paramsMap) })
        }
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    fun getDebitObjectionPdf(seqNo: Long) {
        viewModelScope.launch {
            mldPdf.postValue(callService { repository.downloadDebitObjectionReportPDF(seqNo) })
        }
    }

    fun getDocumentTitleList()= createLocalPager(repository.getObjectionType().investigationItems).flow.cachedIn(viewModelScope)

    fun registrationRequestArticle16(body:RegisterArticle16RequestModel) {
        viewModelScope.launch {
            mldRegisterRequest.postValue(callService { repository.registrationRequestArticle16(body) })
        }
    }

    fun getExpertsMessageArticle16(objectionNumber:Long){
        viewModelScope.launch {
            mldExpertsMessageArticle16.postValue(callService { repository.getRequestInfoArticle16(objectionNumber=objectionNumber) })
        }
    }
}