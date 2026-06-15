package com.tamin.taminhamrah.mapper.pension

import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import kotlin.test.Test
import kotlin.test.assertEquals

class PensionInquiryMapperTest {

    @Test
    fun `toPresentation should map DN to PR correctly`() {
        val dn = PensionInquiryDN(
            fullName = "John Doe",
            paymentAmount = 1500,
            branchCode = "BR01",
            insuranceNumber = "INS123",
            pensionerRisUid = "RIS999",
            pensionerType = "TYPE_A",
            paymentDate = "2023-05-10",
            pensionerBaseDate = "2020-01-01",
            statusDesc = "Active",
            sexDesc = "Male",
            branchName = "Central Branch",
            pensionEndDate = "2030-01-01",
            nationalId = "0012345678"
        )

        val pr = dn.toPresentation()

        assertEquals("John Doe", pr.fullName)
        assertEquals("1500", pr.paymentAmount)
        assertEquals("BR01", pr.branchCode)
        assertEquals("INS123", pr.insuranceNumber)
        assertEquals("Active", pr.statusDesc)
    }

    @Test
    fun `toPresentation should handle nulls with default values`() {
        val dn = PensionInquiryDN(
            fullName = null,
            paymentAmount = null,
            branchCode = null,
            insuranceNumber = null,
            pensionerRisUid = null,
            pensionerType = null,
            paymentDate = null,
            pensionerBaseDate = null,
            statusDesc = null,
            sexDesc = null,
            branchName = null,
            pensionEndDate = null,
            nationalId = null
        )

        val pr = dn.toPresentation()

        assertEquals("نامشخص", pr.fullName)
        assertEquals("0", pr.paymentAmount)
        assertEquals("", pr.branchCode)
    }
}
