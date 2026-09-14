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
import com.tamin.taminhamrah.model.home.*
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.repository.home.HomeRepository
import com.tamin.taminhamrah.repository.stories.StoryRepository
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
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
                    campaigns = it.campaigns?.map { camp -> CampaignDN(camp.id, camp.title, camp.bannerUrl) },
                    quickAccess = it.quickAccess?.map { qa -> QuickAccessDN(qa.id, qa.title, qa.iconUrl) },
                    specialServices = it.specialServices?.map { ss -> SpecialServiceDN(ss.id, ss.title, ss.iconUrl) },
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

        val identity = identityDeferred.await()
        val requests = requestsDeferred.await()
        val activeRelation = activeRelationDeferred.await()
        val stories = storiesDeferred.await()

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

        // Mock data for unimplemented features as requested
        val mockCampaigns = listOf(
            CampaignEntity("1", "کمپین بیمه زنان خانه‌دار", null)
        )
        val mockQuickAccess = listOf(
            QuickAccessEntity("1", "سوابق", null),
            QuickAccessEntity("2", "فیش حقوقی", null)
        )
        val mockSpecialServices = listOf(
            SpecialServiceEntity("1", "کارگران ساختمانی", null),
            SpecialServiceEntity("2", "قراردادهای من", null)
        )

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
}
