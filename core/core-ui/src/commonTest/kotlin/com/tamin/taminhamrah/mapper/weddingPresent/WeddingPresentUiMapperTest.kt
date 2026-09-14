package com.tamin.taminhamrah.mapper.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import kotlin.test.Test
import kotlin.test.assertEquals

class WeddingPresentUiMapperTest {

    @Test
    fun infoDn_toPresentation_mapsCorrectly() {
        val presentation = WeddingPresentInfoDN(
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
        ).toPresentation()

        assertEquals("123", presentation.risuid)
        assertEquals("001", presentation.nationalCode)
        assertEquals("علی", presentation.insuranceFirstName)
        assertEquals("رضایی", presentation.insuranceLastName)
        assertEquals("علی رضایی", presentation.fullName)
        assertEquals("0912", presentation.mobileNumber)
        assertEquals("اجباری", presentation.insuranceTypeDesc)
        assertEquals("فعال", presentation.insuranceStatusDesc)
        assertEquals("acc", presentation.bankAccount)
        assertEquals("ملی", presentation.bankName)
        assertEquals("01", presentation.branchCode)
        assertEquals("تهران", presentation.branchName)
        assertEquals("01", presentation.requestHelpType)
        assertEquals("1700", presentation.serviceDateTimeStamp)
    }

    @Test
    fun infoDn_toPresentation_mapsNullToEmpty() {
        val presentation = WeddingPresentInfoDN().toPresentation()

        assertEquals("", presentation.risuid)
        assertEquals("", presentation.fullName)
        assertEquals("", presentation.mobileNumber)
    }
}
