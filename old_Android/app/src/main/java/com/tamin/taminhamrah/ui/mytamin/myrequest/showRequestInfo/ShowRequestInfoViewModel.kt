package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.DeferredInstallmentInfoResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.RequestStatusResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ResultFollowUpObjectionNonExitsResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ShortTermRequestInfoResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.shortTerm.model.DocumentTypeEnumClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowRequestInfoViewModel @Inject constructor(val repository: ServiceRepository) :
    BaseViewModel() {

    val mldRequestShortTermStatus = MutableLiveData<RequestStatusResponse>()
    val mldRequestShortTermInfo = MutableLiveData<ShortTermRequestInfoResponse>()
    val mldDownloadDocument = MutableLiveData<List<DownloadFileResponse>>()
    val mldArticle16RequestInfo = MutableLiveData<Article16RequestInfoResponse>()
    val mldLatestInsuranceInfo = MutableLiveData<LatestInsuranceInfoResponse>()
    val mldDeferredInstallmentInfo = MutableLiveData<DeferredInstallmentInfoResponse>()
    val mldFollowUpResultObjectionNonExistsHistory = MutableLiveData<ResultFollowUpObjectionNonExitsResponse>()

    fun getRequestInfo(referenceId: String, requestType: Int? = null) {
        getShortTermRequestInfo(referenceId, requestType)
    }

    private fun getShortTermRequestInfo(referenceId: String, requestType: Int?) {
        viewModelScope.launch {
            val requestStatus = async { repository.getShortTermRequestStatus(referenceId) }
            val requestInfo = async { repository.getShortTermRequestInfo(referenceId) }
            val requestStatusResponse = callService { requestStatus.await() }
            val requestInfoResponse = callService { requestInfo.await() }

            if (requestType == RequestTypeEnumClass.PREGNANCY.serviceId) {
                requestInfoResponse.data?.list?.forEach { request ->
                    val pregnancyStatus = async { repository.getPregnancyStatus() }
                    val pregnancyType = async { repository.getPregnancyType() }
                    val pregnancyStatusResponse = callService { pregnancyStatus.await() }
                    val pregnancyTypeResponse = callService { pregnancyType.await() }
                    if (pregnancyStatusResponse.isSuccess && pregnancyTypeResponse.isSuccess)
                        request.setChildbearingInfo(Pair(pregnancyStatusResponse.data,pregnancyTypeResponse.data))
                }
            }

            mldRequestShortTermStatus.postValue(requestStatusResponse)
            mldRequestShortTermInfo.postValue(requestInfoResponse)
        }
    }


    fun downloadDocument(imageList: List<Article16RequestInfoModel.ObjectionPhoto>?) {
        viewModelScope.launch {

            val listOfDeferred = mutableListOf<Deferred<DownloadFileResponse>>()
            imageList?.forEach { document ->
                if (document.guid != null) {

                    listOfDeferred.add(viewModelScope.async deferredScope@{
                        repository.getDocument(document.guid, getDocumentTitle(document.type))
                    })
                }
            }
            mldLoadingState.postValue(LoadingState.LOADING)

            listOfDeferred.awaitAll().let {
                mldDownloadDocument.postValue(it)
            }

            mldLoadingState.postValue(LoadingState.NOT_LOADING)
        }
    }

    fun getDocumentTitle(docType: String?): String {
        return when (docType) {
            DocumentTypeEnumClass.PRESCRIPTION.typeCode -> {
                DocumentTypeEnumClass.PRESCRIPTION.title
            }
            DocumentTypeEnumClass.PURCHASE_INVOICE.typeCode -> {
                DocumentTypeEnumClass.PURCHASE_INVOICE.title
            }
            DocumentTypeEnumClass.ILL_DAY_TYPE1.typeCode,
            DocumentTypeEnumClass.ILL_DAY_TYPE2.typeCode,
            DocumentTypeEnumClass.ILL_DAY_TYPE3.typeCode,
            DocumentTypeEnumClass.ILL_DAY_TYPE4.typeCode,
            DocumentTypeEnumClass.ILL_DAY_TYPE5.typeCode -> {
                DocumentTypeEnumClass.ILL_DAY_TYPE1.title
            }
            else -> {
                val titleList = getArticle16DocumentTitleList()
                titleList.forEach { titleModel ->
                    if (titleModel.value == docType) {
                        return titleModel.name
                    }
                }
                return "مدرک"
            }
        }

    }

    fun getServiceName(serviceId: Int): Int {
        when (serviceId) {
            RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                return RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceNameRes
            }
            RequestTypeEnumClass.PREGNANCY.serviceId -> {
                return RequestTypeEnumClass.PREGNANCY.serviceNameRes
            }
            RequestTypeEnumClass.ILL_DAY.serviceId -> {
                return RequestTypeEnumClass.ILL_DAY.serviceNameRes
            }
            RequestTypeEnumClass.ARTICLE16.serviceId -> {
                return RequestTypeEnumClass.ARTICLE16.serviceNameRes
            }
            RequestTypeEnumClass.DEFERRED_INSTALLMENT_CERTIFICATE.serviceId -> {
                return RequestTypeEnumClass.DEFERRED_INSTALLMENT_CERTIFICATE.serviceNameRes
            }
        }
        return R.string.show_request
    }

    fun getArticle16RequestInfo(objectionNumber: Long) {
        viewModelScope.launch {
            mldArticle16RequestInfo.postValue(
                callService({
                    repository.getRequestInfoArticle16(objectionNumber = objectionNumber)
                }, startLoading = false, endLoading = false),
            )
        }
    }

    fun getArticle16DocumentTitleList() = repository.getObjectionType().investigationItems

    fun getDeferredInstallmentInfo(requestId:String) {
        viewModelScope.launch {
            mldDeferredInstallmentInfo.postValue(callService { repository.getDeferredInstallmentInfo(requestId = requestId)})
        }
    }

    fun getFollowUpResultObjectionNonExistsHistory(referenceId: String){
        viewModelScope.launch {
            mldFollowUpResultObjectionNonExistsHistory.postValue(repository.getFollowUpResultObjectionNonExistsHistory(referenceId))
        }
    }
}



