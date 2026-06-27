package com.tamin.taminhamrah.data.repository.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureFlagRepositoryImplTest {

    private val dataSource = FakeMenuLocalDataSource()
    private val repository = FeatureFlagRepositoryImpl(dataSource)

    @Test
    fun `offline menu is mapped to domain features and malformed rows are dropped`() = runTest {
        dataSource.menu = listOf(
            dto(id = 6, active = true),
            dto(id = 17, active = true, statusType = 2, statusMessage = "boom"),
            dto(id = null, active = true), // dropped by mapper
        )

        repository.getFeatures().test {
            val features = awaitItem()
            assertEquals(setOf(6, 17), features.map { it.id }.toSet())
            assertTrue(features.first { it.id == 17 }.status is FeatureStatus.Error)
            awaitComplete()
        }
    }

    @Test
    fun `empty offline menu emits empty list`() = runTest {
        dataSource.menu = emptyList()

        repository.getFeatures().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Suppress("LongParameterList")
    private fun dto(
        id: Int?,
        active: Boolean?,
        statusType: Int? = null,
        statusMessage: String? = null,
    ) = MainServiceDto(
        active = active,
        hiddenForVersions = emptyList(),
        icon = "icon",
        id = id,
        name = "feature-$id",
        newService = false,
        showRole = listOf(1),
        sorting = 1,
        subtitle = "",
        type = 1,
        statusType = statusType,
        statusMessage = statusMessage,
        webUrl = null,
        isWeb = null,
    )
}
