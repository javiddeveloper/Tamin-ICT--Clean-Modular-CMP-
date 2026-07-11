package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMainMenuUseCaseTest {

    private lateinit var fakeRepository: FakeCommonRepository
    private lateinit var getMainMenuUseCase: GetMainMenuUseCase

    @BeforeTest
    fun setup() {
        fakeRepository = FakeCommonRepository()
        getMainMenuUseCase = GetMainMenuUseCase(fakeRepository)
    }

    @Test
    fun `test load menu items successfully`() = runTest {
        // Arrange
        val mockItems = listOf(
            MainServiceDN(id = 1, name = "Test Service 1", active = true, status = MenuServiceStatusDN.ACTIVE, showRole = listOf(1)),
            MainServiceDN(id = 2, name = "Test Service 2", active = false, status = MenuServiceStatusDN.DISABLED, showRole = listOf(2))
        )
        fakeRepository.mainMenuResult = mockItems

        // Act
        val result = getMainMenuUseCase("1.0.0", false).first()

        // Assert
        assertEquals(2, result.size)
        assertEquals("Test Service 1", result[0].name)
    }

    @Test
    fun `test categorization (tafkik) by type (role)`() = runTest {
        // Arrange
        val mockItems = listOf(
            MainServiceDN(id = 1, name = "Bime Shode", showRole = listOf(1), active = true, status = MenuServiceStatusDN.ACTIVE),
            MainServiceDN(id = 2, name = "Mostamari Begir", showRole = listOf(2), active = true, status = MenuServiceStatusDN.ACTIVE),
            MainServiceDN(id = 3, name = "Karfarma", showRole = listOf(3), active = true, status = MenuServiceStatusDN.ACTIVE),
            MainServiceDN(id = 4, name = "Shared Service", showRole = listOf(1, 2), active = true, status = MenuServiceStatusDN.ACTIVE)
        )
        fakeRepository.mainMenuResult = mockItems

        // Act
        val result = getMainMenuUseCase("1.0.0", false).first()

        // Assert: Filter logic test (simulation of what happens in UI/Domain)
        val type1Services = result.filter { it.showRole.contains(1) }
        val type2Services = result.filter { it.showRole.contains(2) }
        val type3Services = result.filter { it.showRole.contains(3) }

        assertEquals(2, type1Services.size)
        assertTrue(type1Services.any { it.name == "Bime Shode" })
        assertTrue(type1Services.any { it.name == "Shared Service" })

        assertEquals(2, type2Services.size)
        assertTrue(type2Services.any { it.name == "Mostamari Begir" })
        assertTrue(type2Services.any { it.name == "Shared Service" })

        assertEquals(1, type3Services.size)
        assertEquals("Karfarma", type3Services[0].name)
    }

    @Test
    fun `test active and inactive services`() = runTest {
        // Arrange
        val activeService = MainServiceDN(id = 1, name = "Active", active = true, status = MenuServiceStatusDN.ACTIVE)
        val temporaryDisabledService = MainServiceDN(id = 2, name = "Temp Disabled", active = true, status = MenuServiceStatusDN.TEMPORARY_DISABLED, message = "Under construction")
        val disabledService = MainServiceDN(id = 3, name = "Disabled", active = false, status = MenuServiceStatusDN.DISABLED, message = "Not eligible")

        fakeRepository.mainMenuResult = listOf(activeService, temporaryDisabledService, disabledService)

        // Act
        val result = getMainMenuUseCase("1.0.0", false).first()

        // Assert
        val activeResult = result.find { it.id == 1 }!!
        val tempDisabledResult = result.find { it.id == 2 }!!
        val disabledResult = result.find { it.id == 3 }!!

        assertTrue(activeResult.active == true)
        assertEquals(MenuServiceStatusDN.ACTIVE, activeResult.status)

        assertEquals(MenuServiceStatusDN.TEMPORARY_DISABLED, tempDisabledResult.status)
        assertEquals("Under construction", tempDisabledResult.message)

        assertTrue(disabledResult.active == false)
        assertEquals(MenuServiceStatusDN.DISABLED, disabledResult.status)
        assertEquals("Not eligible", disabledResult.message)
    }
}
