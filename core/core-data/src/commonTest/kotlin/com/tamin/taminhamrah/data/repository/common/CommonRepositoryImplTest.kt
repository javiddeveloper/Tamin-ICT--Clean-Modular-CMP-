package com.tamin.taminhamrah.data.repository.common

import app.cash.turbine.test
import com.tamin.core.network.model.common.CityNameDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.data.local.dao.MenuDao
import com.tamin.taminhamrah.data.local.entity.MenuEntity
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.InsuranceTypeDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.common.UserInsuredInfoDTO
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.repository.TokenStoreManager
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommonRepositoryImplTest {

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var tokenStoreManager: FakeTokenStoreManager
    private lateinit var repository: CommonRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        tokenStoreManager = FakeTokenStoreManager()
        repository = CommonRepositoryImpl(remoteDataSource, FakeMenuDao(), tokenStoreManager)
    }

    @Test
    fun `checkUserType uses the cached value and skips the network when a non-anonymous type is cached`() = runTest {
        tokenStoreManager.storedUserType = UserType.PENSIONER.name

        repository.checkUserType().test {
            assertEquals(UserType.PENSIONER, awaitItem().userType)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(0, remoteDataSource.checkInsuredInfoCallCount)
    }

    @Test
    fun `checkUserType fetches and persists when nothing is cached`() = runTest {
        remoteDataSource.checkInsuredInfoResult = UserInsuredInfoDTO(list = emptyList())

        repository.checkUserType().test {
            assertEquals(UserType.INSURED, awaitItem().userType)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, remoteDataSource.checkInsuredInfoCallCount)
        assertEquals(UserType.INSURED.name, tokenStoreManager.storedUserType)
    }

    @Test
    fun `checkUserType re-fetches when the cached value is ANONYMOUS`() = runTest {
        tokenStoreManager.storedUserType = UserType.ANONYMOUS.name
        remoteDataSource.checkInsuredInfoResult = UserInsuredInfoDTO(list = listOf("05"))

        repository.checkUserType().test {
            assertEquals(UserType.PENSIONER, awaitItem().userType)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, remoteDataSource.checkInsuredInfoCallCount)
        assertEquals(UserType.PENSIONER.name, tokenStoreManager.storedUserType)
    }

    @Test
    fun `getMainMenu drops a cached row the server no longer sends`() = runTest {
        val menuDao = FakeMenuDao()
        // Simulate a previous sync that cached a row (MERGE_HISTORY, id 6) the server has since
        // retired — replaceAllMenuItems, not a plain insert, is what has to clear it.
        menuDao.seed(MenuEntity(id = 6, name = "سوابق تلفیقی", subtitle = null, icon = null, active = true, newService = null, sorting = null, url = null, status = null, message = null, showRole = listOf(1), hiddenForVersions = emptyList()))
        remoteDataSource.mainMenuResult = listOf(
            MainServiceDto(id = 8, name = "کلیه سوابق", showRole = listOf(1), status = com.tamin.taminhamrah.model.common.MenuServiceStatus.ACTIVE),
        )
        val repository = CommonRepositoryImpl(remoteDataSource, menuDao, tokenStoreManager)

        repository.getMainMenu("1", false).test {
            assertEquals(listOf(8), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getJobTitlePage fetches and maps to PageDN`() = runTest {
        remoteDataSource.jobTitleResult = ListData(
            list = listOf(
                JobTitleDTO(jobCode = "101", jobDescription = "برنامه‌نویس", status = "1", statusDate = "1402/01/01"),
            ),
            total = 100,
        )

        repository.getJobTitlePage(ApiQueryParamDN(page = 0, limit = 10)).test {
            val page = awaitItem()
            assertEquals(1, page.items.size)
            assertEquals("101", page.items.first().jobCode)
            assertEquals("برنامه‌نویس", page.items.first().jobDescription)
            assertEquals(100, page.total)
            awaitComplete()
        }
    }

    // Fakes
    private class FakeRemoteDataSource : CommonRemoteDataSource {
        var checkInsuredInfoResult = UserInsuredInfoDTO()
        var checkInsuredInfoCallCount = 0

        override suspend fun checkInsuredInfo(): UserInsuredInfoDTO {
            checkInsuredInfoCallCount++
            return checkInsuredInfoResult
        }

        override suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto =
            throw NotImplementedError("not used by these tests")

        override suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto =
            throw NotImplementedError("not used by these tests")

        override suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto =
            throw NotImplementedError("not used by these tests")

        override suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDTO>? =
            throw NotImplementedError("not used by these tests")

        var mainMenuResult: List<MainServiceDto>? = null

        override suspend fun getMainMenu(versionCode: String, forceUpdate: Boolean): List<MainServiceDto> =
            mainMenuResult ?: throw NotImplementedError("not used by these tests")

        override suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO> =
            throw NotImplementedError("not used by these tests")

        override suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO> =
            throw NotImplementedError("not used by these tests")

        override suspend fun getRegistrationDeclarationForm(): HttpStatement =
            throw NotImplementedError("not used by these tests")

        var jobTitleResult: ListData<JobTitleDTO>? = null

        override suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO>? =
            jobTitleResult
    }

    /** Backed by real in-memory state so `replaceAllMenuItems`'s default clear-then-insert body is
     *  exercised for real, not stubbed away. */
    private class FakeMenuDao : MenuDao {
        private val items = MutableStateFlow<List<MenuEntity>>(emptyList())

        fun seed(vararg menuItems: MenuEntity) {
            items.value = menuItems.toList()
        }

        override fun getMenuItems(): Flow<List<MenuEntity>> = items
        override suspend fun insertMenuItems(menuItems: List<MenuEntity>) {
            val byId = menuItems.associateBy { it.id }
            items.value = items.value.filterNot { it.id in byId }.plus(menuItems)
        }
        override suspend fun clearMenu() { items.value = emptyList() }
    }

    private class FakeTokenStoreManager : TokenStoreManager {
        var storedUserType: String? = null
        private val tokenValid = MutableStateFlow(false)
        private val authProcessing = MutableStateFlow(false)

        override fun saveToken(token: String?) = Unit
        override fun getToken(): String? = null
        override fun saveRefreshToken(refreshToken: String?) = Unit
        override fun getRefreshToken(): String? = null
        override fun getToken(slot: TokenSlot): String? = null
        override fun saveToken(slot: TokenSlot, token: String?) = Unit
        override fun getRefreshToken(slot: TokenSlot): String? = null
        override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) = Unit
        override fun getActiveSlot(): TokenSlot = TokenSlot.USER
        override fun activeSlotFlow(): Flow<TokenSlot> = MutableStateFlow(TokenSlot.USER).asStateFlow()
        override suspend fun setActiveSlot(slot: TokenSlot) = Unit
        override fun saveUserId(userId: String?) = Unit
        override fun getUserId(): String? = null
        override fun saveUserType(userType: String?) { storedUserType = userType }
        override fun getUserType(): String? = storedUserType
        override fun saveCodeVerifier(codeVerifier: String?) = Unit
        override fun getCodeVerifier(): String? = null
        override fun tokenValidFlow(): Flow<Boolean> = tokenValid.asStateFlow()
        override suspend fun setTokenValid(isValid: Boolean) { tokenValid.value = isValid }
        override fun isAuthProcessingFlow(): Flow<Boolean> = authProcessing.asStateFlow()
        override fun setAuthProcessing(isProcessing: Boolean) { authProcessing.value = isProcessing }
    }
}
