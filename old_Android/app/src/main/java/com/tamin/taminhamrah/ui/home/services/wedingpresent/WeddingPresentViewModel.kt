package com.tamin.taminhamrah.ui.home.services.wedingpresent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.responses.CalculateMarriageResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ShorttremMariageReq
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentModel
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentResponse
import com.tamin.taminhamrah.data.remote.models.services.asRequestInput
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeddingPresentViewModel @Inject constructor(
    private val repository: ServiceRepository,
) : BaseViewModel() {

    val mldUserInfo = MutableLiveData<WeddingPresentResponse>()
    val mldRequestGift = MutableLiveData<GeneralRes>()

    //  val mldRecipient = MutableLiveData<Resource<MutableList<MenuModel>?>>()
    //  val mldValidateMarriageGift = MutableLiveData<Resource<String?>>()
    var selectedDatetimeStamp: Long? = 0L
    var isConfirmRules = false
    lateinit var userInfo: WeddingPresentModel

    fun getWeddingPresentInfo() {
        viewModelScope.launch {
            mldUserInfo.postValue(callService {
                repository.getWeddingPresent()
            })
        }
    }
/*

    fun getReceiverList() {
        viewModelScope.launch {
            mldRecipient.postValue(Resource.loading(null))
            try {

                */
/*val result = repository.getRecipientList()
                if (result.status != Resource.Status.ERROR) {
                    val itemList = mutableListOf<MenuModel>()

                    result.data?.forEach {
                        itemList.add(it.asDomainModel())
                    }
                    mldRecipient.postValue(Resource.success(itemList))
                } else {
                    mldRecipient.postValue(Resource.error(result.message))
                }*//*

            } catch (e: Exception) {
                mldRecipient.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }
    }
*/

    /* fun validateMarriageGift(date: String, nationalCode: String) {
         viewModelScope.launch {
             mldValidateMarriageGift.postValue(Resource.loading(null))
             try {
                 val result = repository.validateMarriageGift(date, nationalCode)
                 Log.i("validateMarriageGift", "validateMarriageGift response=$result: ")
                 mldValidateMarriageGift.postValue(result)
             } catch (e: Exception) {
                 mldValidateMarriageGift.postValue(
                     Resource.error(
                         MessageModel(
                             e.message ?: e.toString(), 0
                         )
                     )
                 )
             }
         }
     }
 */
    fun postRequest(nationalCode: String, timeStamp: Long, userInfo: WeddingPresentModel) {
        viewModelScope.launch {
            mldRequestGift.postValue(callService {
                repository.marriageGiftRequest(
                    ShorttremMariageReq(
                        nationalCode,
                        userInfo.asRequestInput(),
                        timeStamp
                    )
                )
            }
            )
        }
    }

    fun createKeyValue(item: WeddingPresentModel): ArrayList<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()

        keyValueList.add(KeyValueModel("شماره بیمه", getValue(item.risuid)))
        keyValueList.add(
            KeyValueModel(
                "نام و نام خانوادگی",
                getValue("${item.insuranceFirstName} ${item.insuranceLastName}")
            )
        )
        keyValueList.add(KeyValueModel("نوع بیمه", getValue(item.insuranceTypeDesc)))
        keyValueList.add(KeyValueModel("وضعیت بیمه", getValue(item.insuranceStatusDesc)))
        keyValueList.add(KeyValueModel("شماره حساب", getValue(item.bankAccount)))
        keyValueList.add(KeyValueModel("نام بانک", getValue(item.bankName)))
        keyValueList.add(KeyValueModel("آخرین شعبه بیمه پردازی", getValue(item.branchName)))
        keyValueList.add(KeyValueModel("شماره همراه", getValue(item.mobilNumber)))
        return keyValueList
    }

    private fun getValue(title: String?): String {
        return if (!title.isNullOrBlank()) {
            title
        } else {
            "-"
        }
    }

    val mldCalculateMarriageAllowance = MutableLiveData<CalculateMarriageResponse> ()

    fun calculateMarriageAllowance(timeStamp: String) {
        viewModelScope.launch {
            mldCalculateMarriageAllowance.postValue(callService {
                repository.calculateMarriageAllowance(timeStamp)
            })
//            try {
//                val result = repository.calculateMarriageAllowance(timeStamp)
//                result.let {
//                    mldCalculateMarriageAllowance.postValue(it)
//                }
//            } catch (e: Exception) {
//                mldCalculateMarriageAllowance.postValue(Resource.error(MessageModel(e.message?:e.toString(),0)))
//            }
        }
    }
}

