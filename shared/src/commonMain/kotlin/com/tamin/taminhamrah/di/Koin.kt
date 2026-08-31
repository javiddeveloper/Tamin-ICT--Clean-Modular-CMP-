package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.core.datastore.di.datastoreModule
import com.tamin.taminhamrah.data.di.dataKoinModule
import com.tamin.taminhamrah.feature.history.di.historyModule
import com.tamin.taminhamrah.feature.contracts.di.contractsModule
import com.tamin.taminhamrah.feature.taminServices.di.TaminServicesModule
import com.tamin.taminhamrah.feature.cartable.di.cartableModule
import com.tamin.taminhamrah.feature.pensionInquiry.di.pensionInquiryModule
import com.tamin.taminhamrah.feature.agent.di.agentModule
import com.tamin.taminhamrah.feature.healthProfile.di.healthProfileModule
import com.tamin.taminhamrah.feature.myinbox.di.myInboxModule
import com.tamin.taminhamrah.feature.profile.di.profileModule
import com.tamin.taminhamrah.feature.treatment.di.treatmentModule
import com.tamin.taminhamrah.feature.workshops.di.workshopsModule
import com.tamin.taminhamrah.feature.studentInsuranceContract.di.studentInsuranceContractModule
import com.tamin.taminhamrah.feature.changemobile.di.changeMobileModule
import com.tamin.taminhamrah.feature.security.di.securityModule
import com.tamin.taminhamrah.feature.settings.di.settingsModule
import com.tamin.taminhamrah.feature.addDependent.di.addDependentModule
import com.tamin.taminhamrah.feature.pensionStatusInquiry.di.pensionStatusInquiryModule
import com.tamin.taminhamrah.feature.userRequest.di.userRequestModule
import com.tamin.taminhamrah.feature.historyobjection.di.historyObjectionModule
import com.tamin.taminhamrah.feature.orotezprotez.di.orotezProtezModule
import com.tamin.taminhamrah.feature.girlSurvivor.di.girlSurvivorModule
import com.tamin.taminhamrah.feature.pensionSurvivor.di.pensionSurvivorModule
import com.tamin.taminhamrah.feature.deferredInstallment.di.deferredInstallmentModule
import com.tamin.taminhamrah.feature.inquiryEducation.di.inquiryEducationModule
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.di.requestPaymentForIllDaysModule
import com.tamin.taminhamrah.feature.pregnancyPay.di.pregnancyPayModule
import com.tamin.taminhamrah.plugin.di.pluginModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

val sharedModules: List<Module>
    get() = listOf(
        platformModule,
        networkModule,
        datastoreModule,
        databaseModule,
        ApiClientsModule,
        remoteModule,
        domainModule,
        dataKoinModule,
        dataModule,
        pluginModule,
        agentModule,
        profileModule,
        pensionInquiryModule,
        treatmentModule,
        cartableModule,
        historyModule,
        contractsModule,
        TaminServicesModule,
        workshopsModule,
        studentInsuranceContractModule,
        healthProfileModule,
        changeMobileModule,
        myInboxModule,
        securityModule,
        addDependentModule,
        pensionStatusInquiryModule,
        settingsModule,
        userRequestModule,
        orotezProtezModule,
        girlSurvivorModule,
        deferredInstallmentModule,
        historyObjectionModule,
        requestPaymentForIllDaysModule,
        pensionSurvivorModule,
        pregnancyPayModule,
        inquiryEducationModule,
    )

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModules)
    }
}
