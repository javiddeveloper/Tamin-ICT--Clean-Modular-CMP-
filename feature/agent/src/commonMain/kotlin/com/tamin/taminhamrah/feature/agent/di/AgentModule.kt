package com.tamin.taminhamrah.feature.agent.di

import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.audio.createVoicePlayer
import com.tamin.taminhamrah.feature.agent.audio.createVoiceRecorder
import com.tamin.taminhamrah.feature.agent.service.impl.AppointmentAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.AverageWageAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DeepLinkAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.DependentsAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.EdictAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.GeneralResponseAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.IllnessAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.JobHistoryAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.LawAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.MedicalEntitlementAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PatientHistoryAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PayRollAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.PensionInquiryAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.ProfileAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.ShowcaseAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.TrackingAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.TreatmentCostAgentService
import com.tamin.taminhamrah.feature.agent.ui.AgentViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agentModule = module {
    // Voice engine — new instances per use (recorder/player hold native resources).
    // One coordinator per app: it is what keeps voice and video from talking over
    // each other, so every player must see the same instance.
    single { com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator() }
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

    // ── Wage / History ────────────────────────────────────────────────────────
    singleOf(::DastmozdInfosAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::AverageWageAgentService)   { bind<AgentServiceUseCase>() }
    singleOf(::JobHistoryAgentService)    { bind<AgentServiceUseCase>() }

    // ── Pension ───────────────────────────────────────────────────────────────
    singleOf(::PensionInquiryAgentService) { bind<AgentServiceUseCase>() }

    // ── Pay / Payslip ─────────────────────────────────────────────────────────
    singleOf(::PayRollAgentService) { bind<AgentServiceUseCase>() }

    // ── Edict / Decree ────────────────────────────────────────────────────────
    singleOf(::EdictAgentService) { bind<AgentServiceUseCase>() }

    // ── Tracking ──────────────────────────────────────────────────────────────
    singleOf(::TrackingAgentService) { bind<AgentServiceUseCase>() }

    // ── Health / Treatment ────────────────────────────────────────────────────
    singleOf(::PatientHistoryAgentService) { bind<AgentServiceUseCase>() }
    singleOf(::TreatmentCostAgentService)  { bind<AgentServiceUseCase>() }
    singleOf(::MedicalEntitlementAgentService) { bind<AgentServiceUseCase>() }

    // ── Illness (placeholder — routes to dedicated screens) ───────────────────
    singleOf(::IllnessAgentService) { bind<AgentServiceUseCase>() }

    // ── Appointment ───────────────────────────────────────────────────────────
    singleOf(::AppointmentAgentService) { bind<AgentServiceUseCase>() }

    // ── Profile / Identity ────────────────────────────────────────────────────
    singleOf(::ProfileAgentService)   { bind<AgentServiceUseCase>() }
    singleOf(::DependentsAgentService) { bind<AgentServiceUseCase>() }

    // ── Law ───────────────────────────────────────────────────────────────────
    singleOf(::LawAgentService) { bind<AgentServiceUseCase>() }

    // ── General / Misc ────────────────────────────────────────────────────────
    singleOf(::GeneralResponseAgentService) { bind<AgentServiceUseCase>() }

    // ── Deep Link (form flows & screens not yet in KMP) ───────────────────────
    singleOf(::DeepLinkAgentService) { bind<AgentServiceUseCase>() }

    // Demo-only, paired with the showcase fixture; remove with AgentActionKey.SHOWCASE.
    singleOf(::ShowcaseAgentService) { bind<AgentServiceUseCase>() }
}
