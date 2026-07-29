package com.tamin.taminhamrah.ui.base

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.GeneralPagingSource
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.utils.CheckNetwork
import com.tamin.taminhamrah.utils.MultipleLiveData
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass


open class BaseViewModel : ViewModel() {

    @Inject
    lateinit var checkNetwork: CheckNetwork

    @Inject
    lateinit var commonRepository: CommonRepository

    fun isConnected(): Boolean {
        return checkNetwork.isNetworkConnected()
    }

    var recyclerState: HashMap<String, Parcelable?> = HashMap()
    var mldLoadingState = MutableLiveData<LoadingState>()
    var mldErrorState = MultipleLiveData<BaseResponseNew>()

    fun setFcmToken(token: String) = commonRepository.setFcmToken(token)
    fun getFcmToken() =
        commonRepository.getFcmToken()

    fun <T : Any> mapToObject(map: Map<String, Any>, clazz: KClass<T>): T {
        val constructor = clazz.constructors.first()
        val args = constructor
            .parameters.associateWith { map[it.name] }
        return constructor.callBy(args)
    }

    private fun handleError(e: Exception) {
        //if (e is Htt)
    }

    suspend fun updateAppliedService(id: Int) {
        commonRepository.updateService(id)
    }

    fun getUserMode() = commonRepository.getUserMode()
    fun getSelectedModeValue() = commonRepository.getUserModeValue()
    fun setEmployerMode(userMode: String?) = commonRepository.setUserMode(userMode)

    fun loadBoolean(key: String) = commonRepository.loadBoolean(key)

    fun saveBoolean(key: String, state: Boolean) {
        commonRepository.saveBoolean(key, state)
    }

    fun getUserAvatar() = commonRepository.getUserAvatar()


    fun setToken(tokenStr: String) = commonRepository.setToken(tokenStr)

    fun getToken() = commonRepository.getToken()

    fun getNationalCode() = commonRepository.getUserInfo().nationalCode ?: "0"

    fun getExpiredIn() = commonRepository.getTokenExpireTime()

    inline fun <T : BaseResponseNew> callService(call: () -> T): T {
        mldLoadingState.postValue(LoadingState.LOADING)
        val result = call()
        mldLoadingState.postValue(LoadingState.NOT_LOADING)
        Timber.tag("loadingTest").w("callService : ${result.javaClass.simpleName}")
        if (!result.isSuccess)
            mldErrorState.postValue(result)
        return result
    }

    inline fun <T : BaseResponseNew> callService(
        call: () -> T,
        startLoading: Boolean = false,
        endLoading: Boolean = false,
        isBackToPrevious: Boolean = false
    ): T {
        if (startLoading) mldLoadingState.postValue(LoadingState.LOADING)
        val result = call()
        if (endLoading) mldLoadingState.postValue(LoadingState.NOT_LOADING)
        Timber.tag("loadingTest").w("callService : ${result.javaClass.simpleName}")
        result.isBackToPrevious = isBackToPrevious
        if (!result.isSuccess) {
            mldLoadingState.postValue(LoadingState.NOT_LOADING)
            mldErrorState.postValue(result)
        }
        return result
    }


    protected fun <item : Any, list : ListDataModel<item>> createPager(
        function: suspend (MutableMap<String, String>?) -> list,
        queryPageSize: String = Constants.DEFAULT_QUERY_PAGE_SIZE,
        paramsMap: MutableMap<String, String>? = null
    ): Pager<Int, item> {
        return Pager(
            config = PagingConfig(
                pageSize = queryPageSize.toInt(),
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                GeneralPagingSource(
                    function,
                    queryPageSize.toInt(),
                    mldErrorState,
                    paramsMap
                )
            })
    }

    protected fun <item : Any> createLocalPager(
        list: List<item>,
        queryPageSize: Int = Constants.QUERY_PAGE_SIZE_10
    ): Pager<Int, item> {
        return Pager(
            config = PagingConfig(queryPageSize, 2),
            pagingSourceFactory = { LocalPagingSource(list) })
    }

    fun saveSystemType(systemType: String?) {
        commonRepository.saveSystemType(systemType)
    }

    fun getSystemType() = commonRepository.getSystemType()

    fun saveEmployerDebtSerialNumber(serial: String?) {
        commonRepository.saveEmployerDebtSerialNumber(serial)
    }

    fun getEmployerDebtSerialNumber() = commonRepository.getEmployerDebtSerialNumber()

    fun setBiometricEnabled(biometricEnabled: Boolean) =
        commonRepository.setBiometricEnabled(biometricEnabled)

    fun isBiometricEnabled() = commonRepository.isBiometricEnabled()

    fun getApplicationTheme() = commonRepository.getApplicationTheme()
    fun setApplicationTheme(applicationTheme: ApplicationThemeEnum) =
        commonRepository.setApplicationTheme(applicationTheme)
}




