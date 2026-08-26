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
import com.tamin.taminhamrah.apiService.agent.AgentApiService
import com.tamin.taminhamrah.apiService.agent.createAgentApiService
import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.apiService.contract.ContractsApiService
import com.tamin.taminhamrah.apiService.contract.createContractsApiService
import com.tamin.taminhamrah.apiService.createCommonApiService
import com.tamin.taminhamrah.apiService.createHistoryApiServices
import com.tamin.taminhamrah.apiService.createUserApiService
import com.tamin.taminhamrah.apiService.createWorkShopsApiService
import com.tamin.taminhamrah.apiService.health.HealthApiService
import com.tamin.taminhamrah.apiService.health.createHealthApiService
import com.tamin.taminhamrah.apiService.inbox.PersonalInboxApiService
import com.tamin.taminhamrah.apiService.inbox.createPersonalInboxApiService
import com.tamin.taminhamrah.apiService.orotezProtez.OrotezProtezApiService
import com.tamin.taminhamrah.apiService.orotezProtez.createOrotezProtezApiService
import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.apiService.pension.createPensionApiService
import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.apiService.personal.createPersonalApiService
import com.tamin.taminhamrah.apiService.userRequest.createUserRequestApiService
import com.tamin.taminhamrah.apiService.treatment.TreatmentApiService
import com.tamin.taminhamrah.apiService.treatment.createTreatmentApiService
import com.tamin.taminhamrah.apiService.userRequest.UserRequestApiService
import com.tamin.taminhamrah.apiService.addDependent.AddDependentApiService
import com.tamin.taminhamrah.apiService.addDependent.createAddDependentApiService
import com.tamin.taminhamrah.apiService.calculateWagePension.CalculateWagePensionApiService
import com.tamin.taminhamrah.apiService.calculateWagePension.createCalculateWagePensionApiService
import com.tamin.taminhamrah.apiService.occurrence.OccurrenceApiService
import com.tamin.taminhamrah.apiService.occurrence.createOccurrenceApiService
import com.tamin.taminhamrah.apiService.workersPayment.WorkersPaymentApiService
import com.tamin.taminhamrah.apiService.workersPayment.createWorkersPaymentApiService
import com.tamin.taminhamrah.util.NetworkConstants
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

    // AI Ktorfit instance
    single(named("aiKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("aiHttpClient")))
            .build()
    }

    // Health Ktorfit instance (uses HTTP base IP 172.16.14.115:5700)
    single(named("healthKtorfit")) {
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL_HEALTH_PROFILE)
            .httpClient(get<HttpClient>(named("healthHttpClient")))
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
        ktorfit.createPensionApiService()
    }
    single<PersonalApiService>(named("personalApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createPersonalApiService()
    }

    single<HistoryApiServices> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createHistoryApiServices()
    }

    single<CalculateWagePensionApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createCalculateWagePensionApiService()
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

    single<HealthApiService> {
        val ktorfit: Ktorfit = get(named("healthKtorfit"))
        ktorfit.createHealthApiService()
    }
    single<AddDependentApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createAddDependentApiService()
    }
    single<ContractsApiService>(named("contractsApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createContractsApiService()
    }

    single<AgentApiService>(named("agentApiService")) {
        val ktorfit: Ktorfit = get(named("aiKtorfit"))
        ktorfit.createAgentApiService()
    }

    single<OrotezProtezApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createOrotezProtezApiService()
    }


    single<OccurrenceApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createOccurrenceApiService()
    }

    single<WorkersPaymentApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.createWorkersPaymentApiService()
    }
}
