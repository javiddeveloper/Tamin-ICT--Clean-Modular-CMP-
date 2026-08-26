/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSourceImpl
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.agentRepository.AgentRepositoryImpl
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSource
import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSource
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSource
import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSourceImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSource
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.contactUs.ContactUsRemoteDataSource
import com.tamin.taminhamrah.dataSource.contactUs.ContactUsRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSource
import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSource
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.pregnancyPay.PregnancyPayRemoteDataSource
import com.tamin.taminhamrah.dataSource.pregnancyPay.PregnancyPayRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSource
import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteModule = module {

    includes(ApiQueryBuilderModule)

    // Error handling
    singleOf(::ErrorParserImpl) { bind<ErrorParser>() }

    // Remote data sources
    single<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(
            userApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            errorParser = get(),
            queryBuilder = get()
        )
    }

    single<CommonRemoteDataSource> {
        CommonRemoteDataSourceImpl(
            commonApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            userApiService = get(named("authUserApiService")),
            errorParser = get(),
        )
    }

    single<TreatmentRemoteDataSource> {
        TreatmentRemoteDataSourceImpl(
            apiService = get(),
            errorParser = get()
        )
    }

    single<PensionRemoteDataSource> {
        PensionRemoteDataSourceImpl(
            pensionApiService = get(named("pensionApiService")),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<WorkShopsRemoteDataSource> {
        WorkShopsRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<PersonalRemoteDataSource> {
        PersonalRemoteDataSourceImpl(
            personalApiService = get(named("personalApiService")),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<HistoryRemoteDataSource> {
        HistoryRemoteDataSourceImpl(
            apiServices = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<CalculateWagePensionRemoteDataSource> {
        CalculateWagePensionRemoteDataSourceImpl(
            apiService = get(),
            errorParser = get()
        )
    }

    single<UserRequestRemoteDataSource> {
        UserRequestRemoteDataSourceImpl(
            requestApiService = get(named("requestApiService")),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<HistoryObjectionRemoteDataSource> {
        HistoryObjectionRemoteDataSourceImpl(
            historyObjectionApiService = get(),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<PersonalInboxRemoteDataSource> {
        PersonalInboxRemoteDataSourceImpl(
            personalInboxApiService = get(named("personalInboxApiService")),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<HealthRemoteDataSource> {
        HealthRemoteDataSourceImpl(
            apiService = get(),
            errorParser = get()
        )
    }

    single<AddDependentRemoteDataSource> {
        AddDependentRemoteDataSourceImpl(
            apiService = get(),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<ContractsRemoteDataSource> {
        ContractsRemoteDataSourceImpl(
            contractsApiService = get(named("contractsApiService")),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<ContactUsRemoteDataSource> {
        ContactUsRemoteDataSourceImpl()
    }

    single<OccurrenceRemoteDataSource> {
        OccurrenceRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<AgentRemoteDataSource> {
        // Fake agent responses while the real API is being finished.
        // Swap to the AgentRemoteDataSourceImpl below to hit the live service:
        //   AgentRemoteDataSourceImpl(
        //       agentApiService = get(named("agentApiService")),
        //       errorParser = get(),
        //       json = get()
        //   )
        com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSourceFakeImpl(
            json = get()
        )
    }

    single<com.tamin.taminhamrah.apiService.VersionHistoryApiService> {
        com.tamin.taminhamrah.apiService.VersionHistoryApiServiceImpl()
    }

    single<com.tamin.taminhamrah.dataSource.versionHistory.VersionHistoryRemoteDataSource> {
        com.tamin.taminhamrah.dataSource.versionHistory.VersionHistoryRemoteDataSourceImpl(
            apiService = get()
        )
    }

    single<AgentRepository> {
        AgentRepositoryImpl(
            remoteDataSource = get()
        )
    }

    single<OrotezProtezRemoteDataSource> {
        OrotezProtezRemoteDataSourceImpl(
            orotezProtezApiService = get(),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<InspectionRemoteDataSource> {
        InspectionRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<PregnancyPayRemoteDataSource> {
        PregnancyPayRemoteDataSourceImpl(
            pregnancyPayApiService = get(),
            errorParser = get()
        )
    }
}
