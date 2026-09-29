package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The services tab must not repeat a service that already has its own screen in profile or the
 * treatment hub — both are permanent bottom-bar tabs, so listing it again here is the same tap
 * twice, not a second, independent way to reach it.
 */
class GetVisibleServicesUseCaseTest {

    private lateinit var fakeRepository: FakeCommonRepository
    private lateinit var getVisibleServicesUseCase: GetVisibleServicesUseCase

    @BeforeTest
    fun setup() {
        fakeRepository = FakeCommonRepository()
        getVisibleServicesUseCase = GetVisibleServicesUseCase(fakeRepository)
    }

    @Test
    fun `test the services tab drops every profile-owned and treatment-owned row`() = runTest {
        fakeRepository.mainMenuResult = listOf(
            MainServiceDN(id = FeatureFlag.EDIT_IMAGE.id, name = "ویرایش تصویر"),
            MainServiceDN(id = FeatureFlag.IDENTITY_INFO.id, name = "اطلاعات هویتی"),
            MainServiceDN(id = FeatureFlag.DEPENDENTS.id, name = "افراد تبعی"),
            MainServiceDN(id = FeatureFlag.PRESCRIPTION.id, name = "نسخ الکترونیک"),
            MainServiceDN(id = FeatureFlag.DESERVED_TREATMENT_PENSIONER.id, name = "استحقاق درمان"),
            MainServiceDN(id = FeatureFlag.WORKSHOPS.id, name = "کارگاه‌ها"),
        )

        val result = getVisibleServicesUseCase(ServiceCatalogAudience.TAMIN_SERVICES_TAB, "1.0.0", false).first()

        assertEquals(listOf("کارگاه‌ها"), result.map { it.name })
    }

    /** A service the current [FeatureFlag] enum has no entry for at all must still show — never guessed at as a duplicate. */
    @Test
    fun `test a row with no matching FeatureFlag is kept`() = runTest {
        fakeRepository.mainMenuResult = listOf(MainServiceDN(id = 999_999, name = "سرویس ناشناخته"))

        val result = getVisibleServicesUseCase(ServiceCatalogAudience.TAMIN_SERVICES_TAB, "1.0.0", false).first()

        assertEquals(listOf("سرویس ناشناخته"), result.map { it.name })
    }

    /** Excluded regardless of which role's tab is looking — the dedicated screen exists for every role alike. */
    @Test
    fun `test exclusion does not depend on the row's showRole`() = runTest {
        fakeRepository.mainMenuResult = listOf(
            MainServiceDN(id = FeatureFlag.PRESCRIPTION.id, name = "نسخ الکترونیک", showRole = listOf(1, 2)),
        )

        val result = getVisibleServicesUseCase(ServiceCatalogAudience.TAMIN_SERVICES_TAB, "1.0.0", false).first()

        assertTrue(result.isEmpty())
    }
}
