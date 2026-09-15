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
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.home.*
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.home.HomeContentPlaceholders
import com.tamin.taminhamrah.repository.home.HomeRepository
import com.tamin.taminhamrah.repository.stories.StoryRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.util.AppConfig
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
                    requests = it.requests?.map { req -> RequestDN(req.id, req.title, req.date, req.status,req.refCode) }
                )
            }
        }
    }

    override suspend fun syncHomeContent() = coroutineScope {
        // Fetch current cached data to use as a fallback if network fails
        val currentContent = dao.getHomeContent().firstOrNull()

        // Parallel API calls with runCatching to avoid cancelling the scope on failure.
        // getIdentityInfo/getRelationTaminAll emit a single unconditional network value after any
        // cache emission, so .take(2).lastOrNull() reliably lands on it. getUserRequests and
        // getDeservedTreatment don't have that guarantee (their cache-then-network Flow can
        // legitimately settle on a single emission), so those go through a dedicated one-shot
        // refresh instead of relying on a second Flow emission that might never come.
        val identityDeferred = async { runCatching { userRepository.getIdentityInfo().take(2).lastOrNull() }.getOrNull() }
        val requestsDeferred = async { runCatching { requestsRepository.refreshUserRequests() }.getOrNull() }
        val activeRelationDeferred = async { runCatching { userRepository.getRelationTaminAll().take(2).lastOrNull() }.getOrNull() }
        // StoryRepository loads its (currently bundled) catalogue once and keeps re-emitting it, so
        // a plain first() reliably returns the loaded list without needing the take(2) dance above.
        val storiesDeferred = async { runCatching { storyRepository.getChannels().firstOrNull() }.getOrNull() }
        // The menu supplies the display title for the placeholder campaigns/quickAccess/specialServices
        // below — same (currently mocked) source the rest of the app reads service names from.
        val menuDeferred = async {
            runCatching { commonRepository.getMainMenu(AppConfig.versionName, false).firstOrNull() }.getOrNull()
        }

        val identity = identityDeferred.await()
        val requests = requestsDeferred.await()
        val activeRelation = activeRelationDeferred.await()
        val stories = storiesDeferred.await()
        val menu = menuDeferred.await()

        val nationalCode = identity?.nationalId
        val darmanCoverage = nationalCode?.let {
            runCatching { treatmentRepository.refreshDeservedTreatment(it) }.getOrNull()
        }

        // Generate Request Entities, fallback to cache if network failed
        val requestEntities = requests?.take(3)?.map { req ->
            RequestEntity(
                id = req.id.toString(),
                title = req.title ?: "",
                date = req.creationTime?.toString() ?: "", // Formatted in UI if necessary, or pass raw string
                status = req.status?.requestDesc ?: "",
                refCode = req.refCode?:""
            )
        } ?: currentContent?.requests

        // Generate UserInfo Entity, fallback to cache if network failed
        val fullName = identity?.let {
            listOfNotNull(it.firstName, it.lastName).joinToString(" ").takeIf { str -> str.isNotBlank() } ?: "کاربر تامین"
        } ?: currentContent?.userInfo?.fullName ?: "کاربر تامین"

        val hasDarmanCoverage = darmanCoverage?.firstOrNull()?.let { main ->
            val refusal = main.finalDesc?.takeIf { it.isNotBlank() } ?: main.message?.takeIf { it.contains("عدم استحقاق") }
            refusal == null
        } ?: currentContent?.userInfo?.hasDarmanCoverage

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
        val mockCampaigns = menu?.let { m ->
            HomeContentPlaceholders.campaignFlags.mapNotNull { flag ->
                m.titleOf(flag)?.let { title -> CampaignEntity(flagId = flag.id, title = title, bannerUrl = null) }
            }
        } ?: currentContent?.campaigns
        val mockQuickAccess = menu?.let { m ->
            HomeContentPlaceholders.quickAccessFlags.mapNotNull { flag ->
                m.titleOf(flag)?.let { title -> QuickAccessEntity(flagId = flag.id, title = title, iconUrl = null) }
            }
        } ?: currentContent?.quickAccess
        val mockSpecialServices = menu?.let { m ->
            HomeContentPlaceholders.specialServiceFlags.mapNotNull { flag ->
                m.titleOf(flag)?.let { title -> SpecialServiceEntity(flagId = flag.id, title = title, iconUrl = null) }
            }
        } ?: currentContent?.specialServices

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

    private fun List<MainServiceDN>.titleOf(flag: FeatureFlag): String? = find { it.id == flag.id }?.name
}
