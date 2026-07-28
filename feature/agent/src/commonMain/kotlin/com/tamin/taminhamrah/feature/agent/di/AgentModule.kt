package com.tamin.taminhamrah.feature.agent.di

import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.audio.createVoicePlayer
import com.tamin.taminhamrah.feature.agent.audio.createVoiceRecorder
import com.tamin.taminhamrah.feature.agent.service.impl.AverageWageAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DeepLinkAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DependentsAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.EdictAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.GeneralResponseAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.JobHistoryAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.LawAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.MedicalEntitlementAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PayRollAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PensionInquiryAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.ProfileAgentService
import com.tamin.taminhamrah.feature.agent.ui.AgentViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agentModule = module {
    // Voice engine — new instances per use (recorder/player hold native resources).
    factory { createVoiceRecorder() }
    factory { createVoicePlayer() }

    // 1. Service Registry
    // Koin uses getAll to find all implementations of AgentServiceUseCase
    // registered in other modules with bind.
    single {
        AgentServiceRegistry(
            services = getAll<AgentServiceUseCase>()
        )
    }

    // 2. Action Dispatcher
    singleOf(::AgentActionDispatcher)

    // 3. ViewModel
    viewModelOf(::AgentViewModel)

    // 4. Concrete Service Handlers
    // Using bind identifies this handler as an AgentServiceUseCase
    // so it can be found by the getAll call above.
    singleOf(::DastmozdInfosAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::LawAgentService) { bind<AgentServiceUseCase>() }

    // Ported from old_Android's aiAgent use cases. Data-display services only —
    // the generative-form flows (wedding present, funeral allowance, occurrence
    // report, education inquiry, dependent cancellation, edit phone/bank) still
    // need their own handlers and are intentionally not registered yet.
    singleOf(::GeneralResponseAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::ProfileAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::DependentsAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::PensionInquiryAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::PayRollAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::EdictAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::MedicalEntitlementAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::JobHistoryAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::AverageWageAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::DeepLinkAgentService) { bind<AgentServiceUseCase>() }
}
