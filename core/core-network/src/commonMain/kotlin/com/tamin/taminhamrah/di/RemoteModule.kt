/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.apiService.VersionHistoryApiService
import com.tamin.taminhamrah.apiService.VersionHistoryApiServiceImpl
import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSource
import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSourceFakeImpl
import com.tamin.taminhamrah.dataSource.paymentSource.FakePaymentGatewayRemoteDataSource
import com.tamin.taminhamrah.dataSource.paymentSource.PaymentGatewayRemoteDataSource
import com.tamin.taminhamrah.dataSource.paymentSource.PaymentGatewayRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.paymentSource.PaymentGatewayRemoteDataSourceSelector
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSource
import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.contractAffair.ContractAffairRemoteDataSource
import com.tamin.taminhamrah.dataSource.contractAffair.ContractAffairRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.employerInfo.EmployerInfoRemoteDataSource
import com.tamin.taminhamrah.dataSource.employerInfo.EmployerInfoRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSource
import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSource
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSource
import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSource
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.pregnancyPay.PregnancyPayRemoteDataSource
import com.tamin.taminhamrah.dataSource.pregnancyPay.PregnancyPayRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.versionHistory.VersionHistoryRemoteDataSource
import com.tamin.taminhamrah.dataSource.versionHistory.VersionHistoryRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSource
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.contactUs.ContactUsRemoteDataSource
import com.tamin.taminhamrah.dataSource.contactUs.ContactUsRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.requestPaymentForIllDays.RequestPaymentForIllDaysRemoteDataSource
import com.tamin.taminhamrah.dataSource.requestPaymentForIllDays.RequestPaymentForIllDaysRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSourceImpl
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.agentRepository.AgentRepositoryImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.dataSource.fractionContract.FractionContractRemoteDataSource
import com.tamin.taminhamrah.dataSource.fractionContract.FractionContractRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.constructionInsurance.ConstructionInsuranceRemoteDataSource
import com.tamin.taminhamrah.dataSource.constructionInsurance.ConstructionInsuranceRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.inquiryEducation.InquiryEducationRemoteDataSource
import com.tamin.taminhamrah.dataSource.inquiryEducation.InquiryEducationRemoteDataSourceImpl
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
            queryBuilder = get(),
            json = get()
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
            developerOptionsRepository = get()
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
            errorParser = get(),
            developerOptionsRepository = get()
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

    single<ContractAffairRemoteDataSource> {
        ContractAffairRemoteDataSourceImpl(
            contractAffairApiService = get(named("contractAffairApiService")),
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
        AgentRemoteDataSourceFakeImpl(
            json = get()
        )
    }

    single<VersionHistoryApiService> {
        VersionHistoryApiServiceImpl()
    }

    single<VersionHistoryRemoteDataSource> {
        VersionHistoryRemoteDataSourceImpl(
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

    single<EmployerInfoRemoteDataSource> {
        EmployerInfoRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<RequestPaymentForIllDaysRemoteDataSource> {
        RequestPaymentForIllDaysRemoteDataSourceImpl(
            apiService = get(),
            errorParser = get()
        )
    }

    single<PregnancyPayRemoteDataSource> {
        PregnancyPayRemoteDataSourceImpl(
            pregnancyPayApiService = get(),
            errorParser = get()
        )
    }

    single<InquiryEducationRemoteDataSource> {
        InquiryEducationRemoteDataSourceImpl(
            inquiryEducationApiService = get(),
            errorParser = get()
        )
    }

    single<FractionContractRemoteDataSource> {
        FractionContractRemoteDataSourceImpl(
            fractionContractApiService = get(),
            errorParser = get()
        )
    }

    single<ConstructionInsuranceRemoteDataSource> {
        ConstructionInsuranceRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    /**
     * The payment gateway, wrapped so Developer Options can put a fake in front of it.
     *
     * Both fakes are built eagerly and cost nothing until a mode selects one; building them here
     * rather than inside the selector keeps the selector free of construction logic and makes the
     * two mock behaviours visible in the module, which is where a developer looks for them.
     * The selector answers with the real gateway in release builds regardless of what is stored.
     */
    single<PaymentGatewayRemoteDataSource> {
        PaymentGatewayRemoteDataSourceSelector(
            real = PaymentGatewayRemoteDataSourceImpl(
                apiService = get(),
                errorParser = get()
            ),
            successFake = FakePaymentGatewayRemoteDataSource(succeeds = true),
            failureFake = FakePaymentGatewayRemoteDataSource(succeeds = false),
            developerOptionsRepository = get<DeveloperOptionsRepository>()
        )
    }
}
