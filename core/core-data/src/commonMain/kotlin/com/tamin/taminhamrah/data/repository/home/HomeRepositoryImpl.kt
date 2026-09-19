package com.tamin.taminhamrah.data.repository.home

import com.tamin.taminhamrah.data.local.dao.HomeContentDao
import com.tamin.taminhamrah.data.local.entity.CampaignEntity
import com.tamin.taminhamrah.data.local.entity.HomeContentEntity
import com.tamin.taminhamrah.data.local.entity.QuickAccessEntity
import com.tamin.taminhamrah.data.local.entity.UserInfoEntity
import com.tamin.taminhamrah.data.local.entity.RequestEntity
import com.tamin.taminhamrah.data.local.entity.SpecialServiceEntity
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.model.home.*
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.home.HomeContentPlaceholders
import com.tamin.taminhamrah.repository.home.HomeRepository
import com.tamin.taminhamrah.repository.stories.StoryRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.model.treatment.toDarmanCoveredOrNull
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.take

class HomeRepositoryImpl(
    private val dao: HomeContentDao,
    private val userRepository: UserRepository,
    private val treatmentRepository: TreatmentRepository,
    private val requestsRepository: UserRequestRepository,
    private val storyRepository: StoryRepository,
    private val commonRepository: CommonRepository,
) : HomeRepository {

    override fun getHomeContent(): Flow<HomeContentDN?> {
        return dao.getHomeContent().map { entity ->
            entity?.let {
                HomeContentDN(
                    userInfo = it.userInfo?.let { user ->
                        UserInfoDN(
                            fullName = user.fullName,
                            hasDarmanCoverage = user.hasDarmanCoverage,
                            hasActiveRelation = user.hasActiveRelation
                        )
                    },
                    stories = it.stories?.map { channel -> channel.toDomain() },
                    campaigns = it.campaigns?.mapNotNull { camp -> camp.toDomain() },
                    quickAccess = it.quickAccess?.mapNotNull { qa -> qa.toDomain() },
                    specialServices = it.specialServices?.mapNotNull { ss -> ss.toDomain() },
                    requests = it.requests?.map { req ->
                        RequestDN(req.id, req.title, req.date, req.status, req.refCode, req.statusCode, req.requestTypeId)
                    }
                )
            }
        }
    }

    override suspend fun syncHomeContent() = coroutineScope {
        // Fetch current cached data to use as a fallback if network fails
        val currentContent = dao.getHomeContent().firstOrNull()

        // Parallel API calls, each shielded by safeCall so one failing piece doesn't cancel the
        // others — but a genuine CancellationException (the scope itself being torn down) still
        // propagates instead of being swallowed. getIdentityInfo/getRelationTaminAll emit a single
        // unconditional network value after any cache emission, so .take(2).lastOrNull() reliably
        // lands on it. getUserRequests and getDeservedTreatment don't have that guarantee (their
        // cache-then-network Flow can legitimately settle on a single emission), so those go
        // through a dedicated one-shot refresh instead of relying on a second Flow emission that
        // might never come.
        val identityDeferred = async { safeCall { userRepository.getIdentityInfo().take(2).lastOrNull() } }
        val requestsDeferred = async { safeCall { requestsRepository.refreshUserRequests() } }
        val activeRelationDeferred = async { safeCall { userRepository.getRelationTaminAll().take(2).lastOrNull() } }
        // StoryRepository loads its (currently bundled) catalogue once and keeps re-emitting it, so
        // a plain first() reliably returns the loaded list without needing the take(2) dance above.
        val storiesDeferred = async { safeCall { storyRepository.getChannels().firstOrNull() } }
        // The menu supplies the display title for the placeholder campaigns/quickAccess/specialServices
        // below — same (currently mocked) source the rest of the app reads service names from.
        val menuDeferred = async {
            safeCall { commonRepository.getMainMenu(AppConfig.versionName, false).firstOrNull() }
        }

        val identity = identityDeferred.await()
        val requests = requestsDeferred.await()
        val activeRelation = activeRelationDeferred.await()
        val stories = storiesDeferred.await()
        val menu = menuDeferred.await()

        val nationalCode = identity?.nationalId
        val darmanCoverage = nationalCode?.let {
            safeCall { treatmentRepository.refreshDeservedTreatment(it) }
        }

        // Generate Request Entities, fallback to cache if network failed
        val requestEntities = requests?.take(3)?.map { req ->
            RequestEntity(
                id = req.id.toString(),
                title = req.title ?: "",
                date = req.creationTime?.toString() ?: "", // Formatted in UI if necessary, or pass raw string
                status = req.status?.requestDesc ?: "",
                refCode = req.refCode ?: "",
                statusCode = req.status?.requestCode ?: "",
                requestTypeId = req.requestType?.id ?: 0L
            )
        } ?: currentContent?.requests

        // Generate UserInfo Entity. A blank/unavailable name stays null here — core-data has no
        // Compose-resources access, so the localized fallback text is resolved by the UI layer
        // instead (see HomeScreen.kt), not typed as a literal here. Only fall back to the cached
        // name when the identity fetch itself failed (identity == null); a *successful* fetch that
        // came back blank is fresher information than the cache and should win, even though it
        // resolves to the same "no name" outcome.
        val fullName = if (identity != null) {
            listOfNotNull(identity.firstName, identity.lastName).joinToString(" ").takeIf { it.isNotBlank() }
        } else {
            currentContent?.userInfo?.fullName
        }

        val hasDarmanCoverage = darmanCoverage?.toDarmanCoveredOrNull()
            ?: currentContent?.userInfo?.hasDarmanCoverage

        val hasActiveRelation = activeRelation?.let {
            it.any { rel -> rel.relationDescription != null }
        } ?: currentContent?.userInfo?.hasActiveRelation

        val userInfoEntity = UserInfoEntity(
            fullName = fullName,
            hasDarmanCoverage = hasDarmanCoverage,
            hasActiveRelation = hasActiveRelation
        )

        // Stories now come from the real (currently bundled-catalogue) StoryRepository; fall back
        // to whatever is already cached if this particular fetch failed.
        val storyEntities = stories?.map { it.toEntity() } ?: currentContent?.stories

        // Placeholder rows for unimplemented features: which flags to show is decided once in
        // HomeContentPlaceholders (core-domain); the title comes from the real menu row for that
        // flag, not from a literal here. A flag the menu doesn't (yet) carry is skipped rather than
        // shown with a blank title. Falls back to whatever is already cached if the menu fetch failed.
        // Each row also carries the menu's current enabled/disabled status so cache-sourced
        // rendering can gate/dim exactly like the live menu does.
        val mockCampaigns = menu?.let { buildCampaignEntities(it) } ?: currentContent?.campaigns
        val mockQuickAccess = menu?.let { buildQuickAccessEntities(it) } ?: currentContent?.quickAccess
        val mockSpecialServices = menu?.let { buildSpecialServiceEntities(it) } ?: currentContent?.specialServices

        val homeContentEntity = HomeContentEntity(
            id = 1,
            userInfo = userInfoEntity,
            stories = storyEntities,
            campaigns = mockCampaigns,
            quickAccess = mockQuickAccess,
            specialServices = mockSpecialServices,
            requests = requestEntities
        )

        // Save everything atomically to Room
        dao.insertOrUpdate(homeContentEntity)
    }

    /**
     * Runs [block], turning a failure into `null` so one piece of the sync doesn't cancel the
     * others — but rethrows [CancellationException] so a real cancellation (the scope this
     * function runs in being torn down) still propagates instead of being swallowed.
     */
    private suspend fun <T> safeCall(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

}

/**
 * Pure, `menu`-in/`entities`-out builders — pulled out of [HomeRepositoryImpl.syncHomeContent] so
 * they're directly unit-testable without needing the whole suspend/coroutine orchestration (and,
 * critically, without needing `AppConfig.versionName`, whose Android `actual` reads a
 * Koin-registered `Context` that isn't available under a plain JVM unit test).
 */
internal fun buildCampaignEntities(menu: List<MainServiceDN>): List<CampaignEntity> {
    val byId = menu.associateBy { it.id }
    return HomeContentPlaceholders.campaignFlags.mapNotNull { flag ->
        byId[flag.id]?.name?.let { title ->
            CampaignEntity(
                flagId = flag.id,
                title = title,
                bannerUrl = null,
                isOpenable = menu.featureStatusOf(flag).opensSomething
            )
        }
    }
}

internal fun buildQuickAccessEntities(menu: List<MainServiceDN>): List<QuickAccessEntity> {
    val byId = menu.associateBy { it.id }
    return HomeContentPlaceholders.quickAccessGroups.flatMap { (group, flags) ->
        flags.mapNotNull { flag ->
            byId[flag.id]?.let { row ->
                row.name?.let { title ->
                    QuickAccessEntity(
                        flagId = flag.id,
                        title = title,
                        iconUrl = row.icon,
                        group = group,
                        status = row.status
                    )
                }
            }
        }
    }
}

internal fun buildSpecialServiceEntities(menu: List<MainServiceDN>): List<SpecialServiceEntity> {
    val byId = menu.associateBy { it.id }
    return HomeContentPlaceholders.specialServiceFlags.mapNotNull { flag ->
        byId[flag.id]?.let { row ->
            row.name?.let { title ->
                SpecialServiceEntity(
                    flagId = flag.id,
                    title = title,
                    iconUrl = row.icon,
                    status = row.status
                )
            }
        }
    }
}
