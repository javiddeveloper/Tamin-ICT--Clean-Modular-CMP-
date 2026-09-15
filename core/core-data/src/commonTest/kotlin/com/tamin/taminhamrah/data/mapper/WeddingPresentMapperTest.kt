package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentRequestDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeddingPresentMapperTest {

    @Test
    fun infoDto_toDomain_mapsCorrectly() {
        val dto = WeddingPresentInfoDTO(
            risuid = "123",
            nationalCode = "001",
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
            mobileNumber = "0912",
            insuranceTypeDesc = "اجباری",
            insuranceStatusDesc = "فعال",
            bankAccount = "acc",
            bankName = "ملی",
            branchCode = "01",
            branchName = "تهران",
            requestHelpType = "01",
            serviceDateTimeStamp = "1700",
            request = WeddingPresentRequestDTO(id = "req-1", statusId = "2"),
        )

        val domain = dto.toDomain()

        assertEquals("123", domain.risuid)
        assertEquals("علی", domain.insuranceFirstName)
        assertEquals("رضایی", domain.insuranceLastName)
        assertEquals("0912", domain.mobileNumber)
        assertEquals("اجباری", domain.insuranceTypeDesc)
        assertEquals("req-1", domain.request?.id)
        assertEquals("2", domain.request?.statusId)
    }

    @Test
    fun submitRequestDn_toDto_mapsPartnerAndInfo() {
        val domain = WeddingPresentSubmitRequestDN(
            partnerNationalId = "0098765432",
            weddingDateTimeStamp = 1700000000000L,
            info = WeddingPresentInfoDN(
                risuid = "123",
                nationalCode = "001",
                insuranceFirstName = "علی",
                insuranceLastName = "رضایی",
                mobileNumber = "0912",
                branchCode = "01",
                branchName = "تهران",
                requestHelpType = "01",
                serviceDateTimeStamp = "1700",
                request = WeddingPresentRequestDN(id = "req-1"),
            ),
        )

        val dto = domain.toDTO()

        assertEquals("0098765432", dto.partnerNationalId)
        assertEquals(1700000000000L, dto.weddingDateTimeStamp)
        assertEquals("123", dto.shortTermRequest.risuid)
        assertEquals("علی", dto.shortTermRequest.insuranceFirstName)
        assertEquals("رضایی", dto.shortTermRequest.insuranceLastName)
        assertEquals("001", dto.shortTermRequest.nationalCode)
        assertEquals("0912", dto.shortTermRequest.mobileNumber)
        assertEquals("01", dto.shortTermRequest.branchCode)
        assertEquals("req-1", dto.shortTermRequest.request?.id)
        assertNull(dto.shortTermRequest.requestFileList)
    }
}
