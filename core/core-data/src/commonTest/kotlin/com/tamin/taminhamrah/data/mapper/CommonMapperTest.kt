package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.UserInsuredInfoDTO
import com.tamin.taminhamrah.model.common.UserType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CommonMapperTest {

    @Test
    fun `toDomain maps a null list to TEMPORARY`() {
        val result = UserInsuredInfoDTO(list = null).toDomain()

        assertEquals(UserType.TEMPORARY, result.userType)
    }

    @Test
    fun `toDomain maps an empty list to INSURED`() {
        val result = UserInsuredInfoDTO(list = emptyList()).toDomain()

        assertEquals(UserType.INSURED, result.userType)
    }

    @Test
    fun `toDomain maps a list whose first entry is 05 to PENSIONER`() {
        val result = UserInsuredInfoDTO(list = listOf("05", "این سرویس برای شما فعال نیست")).toDomain()

        assertEquals(UserType.PENSIONER, result.userType)
        assertEquals("این سرویس برای شما فعال نیست", result.message)
    }

    @Test
    fun `toDomain maps a list whose first entry is not 05 to INSURED`() {
        val result = UserInsuredInfoDTO(list = listOf("01")).toDomain()

        assertEquals(UserType.INSURED, result.userType)
    }

    @Test
    fun `toDomain leaves message null when the list has no second entry`() {
        val result = UserInsuredInfoDTO(list = listOf("05")).toDomain()

        assertNull(result.message)
    }
}
