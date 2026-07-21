package com.tamin.taminhamrah.ui.login.eligibility

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.models.user.EligibilityStatusResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.ConfigApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EligibilityHomeViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val pref: PreferenceManager
) : BaseViewModel() {

    val mldEligibilityResult = MutableLiveData<EligibilityStatusResponse>()

    fun getUserEligibility(nationalCode: String, isForeigner: Boolean) {
        viewModelScope.launch {
            val result =
                callService { loginRepository.getEligibilityTreatment(createUrlRequest(nationalCode)) }

            if (result.isSuccess) {
                var eligibilityResult = EligibilityStatusResponse()
                if (result.data is String) {
                    eligibilityResult.nationalId = nationalCode
                    eligibilityResult.reault = false
                    eligibilityResult.isForeigner = isForeigner
                    mldEligibilityResult.postValue(eligibilityResult)
                } else {
                    try {
                        eligibilityResult = Gson().fromJson(result.data.toString(), EligibilityStatusResponse::class.java)
                        mldEligibilityResult.postValue(eligibilityResult)
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                }

                mldEligibilityResult.postValue(eligibilityResult)
            }
        }
    }

    private fun manageResponse(result: Any?) {

        if (result is String) {

     //      mldEligibilityResult.postValue(Resource.error(MessageModel(result, 1)))
        } else {
            val eligibilityResult =
                Gson().fromJson(result.toString(), EligibilityStatusResponse::class.java)
//            val map: Map<String, Any> = result as Map<String, Any>
//            val obj = mapToObject(map, EligibilityStatusResponse::class)
      //      mldEligibilityResult.postValue(Resource.success((eligibilityResult)))
        }
    }

    private fun createUrlRequest(nationalCode: String): String {
        return Constants.BASE_URL_MEDICAL +
                ConfigApp.PostfixUrlEligibility +
                nationalCode
    }

    fun checkDataForeignNationalsCode(
        foreignNationalsCode: String,
        isNationalCode: Boolean
    ): Boolean {
        if (foreignNationalsCode.length < 10) {
            return !isNationalCode && foreignNationalsCode.isNotEmpty()
        }
        return false
    }

}

