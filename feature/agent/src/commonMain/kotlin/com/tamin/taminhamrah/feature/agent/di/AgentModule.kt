package com.tamin.taminhamrah.feature.agent.di

import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.LawAgentService
import com.tamin.taminhamrah.feature.agent.ui.AgentViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agentModule = module {
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
}
