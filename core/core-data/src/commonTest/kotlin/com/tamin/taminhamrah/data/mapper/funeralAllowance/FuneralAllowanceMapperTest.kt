package com.tamin.taminhamrah.data.mapper.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.RequestFuneralDTO
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FuneralAllowanceMapperTest {

    @Test
    fun infoDto_toDomain_mapsFieldsAndRenamesLegacyKeys() {
        val dto = FuneralAllowanceInfoDTO(
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
            risuid = "1234567",
            bankAccount = "0203456789001",
            bankName = "بانک ملت",
            mobileNumber = "09121234567",
            branchName = "شعبه مرکزی",
            branchCode = "10",
            nationalCode = "0012345678",
            partnerNationalId = "0055667788",
            requestHelpType = "07",
            flag = false,
        )

        val domain = dto.toDomain()

        assertEquals("علی", domain.firstName)
        assertEquals("رضایی", domain.lastName)
        assertEquals("1234567", domain.insuranceNumber)
        assertEquals("0203456789001", domain.bankAccount)
        assertEquals("09121234567", domain.mobileNumber)
        assertEquals("10", domain.branchCode)
        assertEquals("0012345678", domain.nationalCode)
        assertEquals("0055667788", domain.deceasedNationalId)
        assertEquals("07", domain.requestHelpType)
        assertEquals(false, domain.hasBankAccountIssue)
        assertNull(domain.registeredRequest)
    }

    @Test
    fun infoDto_toDomain_fillsMissingStringsWithEmptyAndDefaultsHelpType() {
        val domain = FuneralAllowanceInfoDTO().toDomain()

        assertEquals("", domain.firstName)
        assertEquals("", domain.insuranceNumber)
        assertEquals("", domain.deceasedNationalId)
        // blank/absent requestHelpType falls back to the native default "07".
        assertEquals("07", domain.requestHelpType)
    }

    @Test
    fun infoDto_toDomain_blankHelpType_fallsBackToDefault() {
        val domain = FuneralAllowanceInfoDTO(requestHelpType = "   ").toDomain()

        assertEquals("07", domain.requestHelpType)
    }

    @Test
    fun infoDto_toDomain_whenFlagTrue_buildsRegisteredRequestFromRequestBlock() {
        val dto = FuneralAllowanceInfoDTO(
            flag = true,
            partnerNationalId = "0055667788",
            deathTimestamp = 1712000000000L,
            request = RequestFuneralDTO(
                id = 998877L,
                requestDate = 1713000000000L,
                statusName = "در انتظار اصلاح حساب",
            ),
        )

        val domain = dto.toDomain()

        assertEquals(true, domain.hasBankAccountIssue)
        val registered = requireNotNull(domain.registeredRequest)
        assertEquals(998877L, registered.requestId)
        assertEquals("0055667788", registered.deceasedNationalId)
        assertEquals(1712000000000L, registered.deathTimestamp)
        assertEquals(1713000000000L, registered.requestTimestamp)
        assertEquals("در انتظار اصلاح حساب", registered.statusName)
    }

    @Test
    fun infoDto_toDomain_whenFlagTrueButRequestMissing_registeredRequestIdIsZero() {
        val domain = FuneralAllowanceInfoDTO(flag = true, partnerNationalId = "0055667788").toDomain()

        val registered = requireNotNull(domain.registeredRequest)
        assertEquals(0L, registered.requestId)
        assertEquals("", registered.statusName)
        assertNull(registered.requestTimestamp)
    }

    @Test
    fun submitParamsDn_toRequestDTO_mapsIntoNestedShorttermRequest() {
        val params = SubmitFuneralAllowanceParamsDN(
            deceasedNationalId = "0055667788",
            branchCode = "10",
            branchName = "شعبه مرکزی",
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
            mobileNumber = "09121234567",
            nationalCode = "0012345678",
            insuranceNumber = "1234567",
            requestHelpType = "07",
        )

        val dto = params.toRequestDTO()

        assertEquals("0055667788", dto.deadNationalId)
        val shortterm = requireNotNull(dto.shorttermRequest)
        assertEquals("10", shortterm.branchCode)
        assertEquals("شعبه مرکزی", shortterm.branchName)
        assertEquals("علی", shortterm.insuranceFirstName)
        assertEquals("رضایی", shortterm.insuranceLastName)
        assertEquals("09121234567", shortterm.mobileNumber)
        assertEquals("0012345678", shortterm.nationalCode)
        assertEquals("07", shortterm.requestHelpType)
        assertEquals("1234567", shortterm.risuid)
    }
}
