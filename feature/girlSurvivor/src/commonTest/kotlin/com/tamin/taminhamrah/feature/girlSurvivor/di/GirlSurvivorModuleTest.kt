package com.tamin.taminhamrah.feature.girlSurvivor.di

import com.tamin.taminhamrah.feature.girlSurvivor.fake.FakePersonalRepository
import com.tamin.taminhamrah.feature.girlSurvivor.ui.GirlSurvivorViewModel
import com.tamin.taminhamrah.useCases.personal.CheckGirlSurvivorConditionsUseCase
import com.tamin.taminhamrah.useCases.personal.ConfirmGirlSurvivorUseCase
import com.tamin.taminhamrah.useCases.personal.GetGirlSurvivorReportUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Regression for the crash on opening the screen: `viewModelOf(::GirlSurvivorViewModel)` also
 * tried to resolve the default `resolveString` parameter, which nothing registers.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GirlSurvivorModuleTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun girlSurvivorModule_resolvesViewModel() {
        val repository = FakePersonalRepository()
        val koin = koinApplication {
            modules(
                girlSurvivorModule,
                module {
                    single { GetPersonalInfoUseCase(repository) }
                    single { CheckGirlSurvivorConditionsUseCase(repository) }
                    single { GetGirlSurvivorReportUseCase(repository) }
                    single { ConfirmGirlSurvivorUseCase(repository) }
                },
            )
        }.koin

        assertNotNull(koin.get<GirlSurvivorViewModel>())
    }
}
