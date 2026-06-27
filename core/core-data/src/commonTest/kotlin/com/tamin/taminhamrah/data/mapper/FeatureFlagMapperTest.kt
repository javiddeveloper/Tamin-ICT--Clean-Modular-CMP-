package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.FeaturePlatform
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.FeatureType
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeatureFlagMapperTest {

    @Test
    fun `active boolean maps to Active when statusType absent`() {
        val feature = dto(id = 6, active = true).toDomainOrNull()
        assertEquals(FeatureStatus.Active, feature?.status)
    }

    @Test
    fun `inactive boolean maps to Inactive when statusType absent`() {
        val feature = dto(id = 6, active = false).toDomainOrNull()
        assertEquals(FeatureStatus.Inactive, feature?.status)
    }

    @Test
    fun `statusType overrides the legacy active flag`() {
        val feature = dto(id = 6, active = false, statusType = 0).toDomainOrNull()
        assertEquals(FeatureStatus.Active, feature?.status)
    }

    @Test
    fun `statusType 2 maps to Error with message`() {
        val feature = dto(id = 17, active = true, statusType = 2, statusMessage = "boom").toDomainOrNull()
        assertEquals(FeatureStatus.Error("boom"), feature?.status)
    }

    @Test
    fun `statusType 2 without message falls back to a default message`() {
        val status = dto(id = 17, active = true, statusType = 2).toDomainOrNull()?.status
        assertTrue(status is FeatureStatus.Error)
        assertTrue((status as FeatureStatus.Error).message.isNotBlank())
    }

    @Test
    fun `statusType 3 maps to TemporarilyDisabled`() {
        val status = dto(id = 23, active = true, statusType = 3, statusMessage = "later").toDomainOrNull()?.status
        assertEquals(FeatureStatus.TemporarilyDisabled("later"), status)
    }

    @Test
    fun `web feature maps to Web platform`() {
        val feature = dto(id = 47, active = true, isWeb = true, webUrl = "https://x.ir").toDomainOrNull()
        assertEquals(FeaturePlatform.Web("https://x.ir"), feature?.platform)
    }

    @Test
    fun `isWeb without url falls back to Native`() {
        val feature = dto(id = 47, active = true, isWeb = true, webUrl = null).toDomainOrNull()
        assertEquals(FeaturePlatform.Native, feature?.platform)
    }

    @Test
    fun `type and roles are mapped and unknown roles dropped`() {
        val feature = dto(id = 6, active = true, type = 2, showRole = listOf(1, 2, 99)).toDomainOrNull()
        assertEquals(FeatureType.Pensioner, feature?.type)
        assertEquals(listOf(UserRole.Insured, UserRole.Pensioner), feature?.roles)
    }

    @Test
    fun `null id or name returns null`() {
        assertNull(dto(id = null, active = true).toDomainOrNull())
        assertNull(dto(id = 6, name = null, active = true).toDomainOrNull())
    }

    @Suppress("LongParameterList")
    private fun dto(
        id: Int?,
        active: Boolean?,
        name: String? = "feature",
        type: Int? = 1,
        showRole: List<Int?> = listOf(1),
        statusType: Int? = null,
        statusMessage: String? = null,
        isWeb: Boolean? = null,
        webUrl: String? = null,
    ) = MainServiceDto(
        active = active,
        hiddenForVersions = emptyList(),
        icon = "icon",
        id = id,
        name = name,
        newService = false,
        showRole = showRole,
        sorting = 1,
        subtitle = "",
        type = type,
        statusType = statusType,
        statusMessage = statusMessage,
        webUrl = webUrl,
        isWeb = isWeb,
    )
}
