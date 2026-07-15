/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.apiService.CommonApiService
import com.tamin.taminhamrah.apiService.HistoryApiServices
import com.tamin.taminhamrah.apiService.UserApiService
import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.apiService.contract.ContractsApiService
import com.tamin.taminhamrah.apiService.contract.createContractsApiService
import com.tamin.taminhamrah.apiService.createCommonApiService
import com.tamin.taminhamrah.apiService.createHistoryApiServices
import com.tamin.taminhamrah.apiService.createUserApiService
import com.tamin.taminhamrah.apiService.createWorkShopsApiService
import com.tamin.taminhamrah.apiService.inbox.PersonalInboxApiService
import com.tamin.taminhamrah.apiService.inbox.createPersonalInboxApiService
import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.apiService.userRequest.createUserRequestApiService
import com.tamin.taminhamrah.apiService.treatment.TreatmentApiService
import com.tamin.taminhamrah.apiService.treatment.createTreatmentApiService
import com.tamin.taminhamrah.apiService.userRequest.UserRequestApiService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


val ApiClientsModule = module {
    // Main Ktorfit instance
    single(named("mainKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("mainHttpClient")))
            .build()
    }

    // Auth Ktorfit instance (uses authHttpClient without Auth plugin)
    single(named("authKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("authHttpClient")))
            .build()
    }

    // API Services
    single<UserApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createUserApiService()
    }

    single<UserApiService>(named("authUserApiService")) {
        val ktorfit: Ktorfit = get(named("authKtorfit"))
        ktorfit.createUserApiService()
    }

    single<CommonApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createCommonApiService()
    }

    single<TreatmentApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createTreatmentApiService()
    }

    single<PensionApiService>(named("pensionApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }
    single<PersonalApiService>(named("personalApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }

    single<HistoryApiServices> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createHistoryApiServices()
    }

    single<WorkShopsApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createWorkShopsApiService()
    }

    single<UserRequestApiService>(named("requestApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createUserRequestApiService()
    }

    single<PersonalInboxApiService>(named("personalInboxApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createPersonalInboxApiService()
    }

    single<ContractsApiService>(named("contractsApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createContractsApiService()
    }

}
