package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.mafasahesab

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedFileModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.mafasaHesab.MafasaHesabContractSubject
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.MafasaHesabRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MafasaHesabInfoViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val ARG_CONTRACT = "ARG_CONTRACT"

    val image_request_code_term_1 = 1234

    val imageFileList by lazy { ArrayList<UploadedImageModel>() }

    val pdfFileList by lazy { ArrayList<UploadedFileModel>() }

    val dataModel: MafasaHesabRequestModel by lazy { MafasaHesabRequestModel() }

    val mldPdf = MutableLiveData<Resource<String?>>()

    val mldUploadPdfFile = MutableLiveData<UploadedFileModel>()

    val mldUploadImage = MutableLiveData<UploadedImageModel>()

    val mldRegister = MutableLiveData<GeneralRes?>()

    private var contractorSubjectFlow: Pager<Int, MafasaHesabContractSubject>? = null

    fun getImageTitleFlow() = createLocalPager(getDocTitleList()).flow.cachedIn(viewModelScope)

    fun getDocTitleList() = ArrayList<MenuModel>().apply {
        add(MenuModel("مستندات نامه", "1", tag = 1001))
        add(MenuModel("مستندات مدارک الحاقیه", "3", tag = 1002))
        if (dataModel.SubContractor == "1")
            add(MenuModel("مستندات لیست فهرست", "2", tag = 1004))
        add(MenuModel("مستندات صورت وضعیت قطعی", "4", tag = 1003))
    }

    private fun getDocId(requestCode: Int): String? {
        return if (requestCode == image_request_code_term_1)
            requestCode.toString()
        else getDocTitleList().filter { it.tag == requestCode }[0].id

    }

    private fun getDocTitle(requestCode: Int, fileName: String): String {
        return if (requestCode == image_request_code_term_1)
            fileName
        else {
            val list = getDocTitleList().filter { it.tag == requestCode }
            if (list.isNotEmpty())
                "${list[0].title} _ $fileName"
            else fileName

        }

    }

    fun uploadImage(
        image: MultipartBody.Part,
        requestCode: Int,
        imageUri: Uri,
        fileName: String
    ) {
        viewModelScope.launch {
            val response = callService {
                repository.uploadImage(image)
            }

            if (response.isSuccess) {
                mldUploadImage.postValue(
                    UploadedImageModel(
                        response.guid,
                        imageType = getDocId(requestCode),
                        imageName = getDocTitle(requestCode, fileName),
                        imageUri
                    )
                )
            }
        }
    }


    fun uploadPdfFile(
        image: MultipartBody.Part,
        requestCode: Int,
        fileUri: Uri,
        fileName: String
    ) {
        viewModelScope.launch {
            val response = callService {
                repository.uploadPdfFile(image)
            }
            if (response.isSuccess) {
                mldUploadPdfFile.postValue(
                    UploadedFileModel(
                        response.data,
                        fileType = getDocId(requestCode),
                        fileName = getDocTitle(requestCode, fileName),
                        fileUri = fileUri
                    )
                )
            }
        }
    }


    fun getMafasaHesabContractSubjects(): Flow<PagingData<MafasaHesabContractSubject>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (contractorSubjectFlow == null)
            contractorSubjectFlow = createPager(
                repository::getMafasaHesabContractSubjects,
                paramsMap = paramsMap
            )

        return contractorSubjectFlow!!.flow.cachedIn(viewModelScope)
    }

    fun getContractTermStep1Flow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("پیمانکار", "1"))
            add(MenuModel("کارفرما", "2"))
            add(MenuModel("انعقاد قرارداد بر اساس ضوابط تيپ سازمان برنامه و بودجه", "3"))

        }
    ).flow.cachedIn(viewModelScope)

    fun getContractTemStep2Flow() = createLocalPager(
        ArrayList<MenuModel>().apply {
            add(MenuModel("مقاطعه کار", "1"))
            add(MenuModel("واگذارنده", "2"))
            add(
                MenuModel(
                    "قسمتي از مصالح توسط پيمانکار و قسمتي توسط واگذارنده کار تامين شده است",
                    "3"
                )
            )

        }
    ).flow.cachedIn(viewModelScope)

    fun sendRegisterRequest(
        workshopId: String?,
        contractRow: String?,
        branchCode: String?,
        contactSequence: String?
    ) {

        //0082810145TT02100001TT0210TT01TT01


        viewModelScope.launch {
            val id =
                "${workshopId}TT${contractRow}TT${branchCode}TT${contactSequence}TT${dataModel.contractsubjectcode}"
            mldRegister.postValue(callService { repository.sendMafasaHesabRequest(id, dataModel) })
        }
    }

}