package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A screen that reads a filtered view of the menu through [GetVisibleServicesUseCase]. Add an
 * entry here — and a `when` branch in the use case — whenever a new screen needs its own rule for
 * which menu rows it shows; today only the services tab does.
 */
enum class ServiceCatalogAudience {
    /**
     * «خدمات»'s full catalog. Excludes [DedicatedScreenFlags.all] — a service already has a
     * purpose-built screen in profile or the treatment hub, always reachable from the bottom bar,
     * so listing it again here would just be the same tap twice.
     */
    TAMIN_SERVICES_TAB,
}

/**
 * Every [FeatureFlag] that already has a dedicated, always-reachable screen elsewhere — profile and
 * the treatment hub are both permanent bottom-bar tabs, not something a role or a flag can hide, so
 * a flag with a row here is never worth listing a second time as a generic service card.
 *
 * A flag stays out of both sets when it is used by *both* a dedicated screen and the generic
 * catalog for a genuinely different purpose — that is not the case for anything today, but if it
 * ever is, that flag belongs on neither list and the two screens simply share it.
 *
 * Single source of truth for this one purpose, kept in core-domain rather than re-derived from
 * `ProfileMenuItem`/`TreatmentFeatureFlags` (feature modules), which `feature:taminServices` must
 * not import — see the naming/module-boundary rules in `CLAUDE.md`. Update this list by hand
 * whenever profile or the treatment hub gains or loses a gated row.
 */
object DedicatedScreenFlags {
    /** Every flag [com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem] gates on. */
    val profile: Set<FeatureFlag> = setOf(
        FeatureFlag.EDIT_IMAGE,
        FeatureFlag.IDENTITY_INFO,
        FeatureFlag.ACTIVE_RELATION,
        FeatureFlag.BANK_ACCOUNT_LIST,
        FeatureFlag.DEPENDENTS,
        FeatureFlag.MY_ELECTRONIC_FILE,
        FeatureFlag.CHANGE_MOBILE,
        FeatureFlag.PERSONAL_INBOX,
        FeatureFlag.MY_REQUESTS,
        FeatureFlag.STORIES_AND_SAVE_EVENTS,
    )

    /** Every flag `TreatmentFeatureFlags` gates on. */
    val treatment: Set<FeatureFlag> = setOf(
        FeatureFlag.PRESCRIPTION,
        FeatureFlag.DESERVED_TREATMENT_PENSIONER,
        FeatureFlag.HEALTH_PROFILE,
        FeatureFlag.CONTRACTED_CENTERS,
        FeatureFlag.CURRENT_YEAR_TREATMENT_COSTS,
    )

    val all: Set<FeatureFlag> = profile + treatment
}

/**
 * The menu, filtered for whichever screen is asking. Every screen still reads the same real menu
 * ([CommonRepository.getMainMenu]) — this only decides which of its rows that screen shows, the
 * same rule for every role tab, since a service having its own screen elsewhere is true regardless
 * of who is looking at it.
 */
class GetVisibleServicesUseCase(
    private val commonRepository: CommonRepository,
) {
    operator fun invoke(
        audience: ServiceCatalogAudience,
        versionCode: String,
        forceUpdate: Boolean,
    ): Flow<List<MainServiceDN>> =
        commonRepository.getMainMenu(versionCode, forceUpdate).map { menu ->
            when (audience) {
                ServiceCatalogAudience.TAMIN_SERVICES_TAB -> menu.filterNot { service ->
                    FeatureFlag.fromId(service.id) in DedicatedScreenFlags.all
                }
            }
        }
}
