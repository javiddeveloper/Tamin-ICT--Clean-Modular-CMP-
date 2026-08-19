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
        assertEquals("RIS999", pr.pensionerRisUid)
        assertEquals("TYPE_A", pr.pensionerType)
        assertEquals("Active", pr.statusDesc)
        assertEquals(false, pr.isActive)
    }

    @Test
    fun `toPresentation marks statusDesc 01 as active pensioner`() {
        val dn = PensionInquiryDN(
            fullName = "John Doe",
            paymentAmount = 1500,
            branchCode = "BR01",
            insuranceNumber = "INS123",
            pensionerRisUid = "RIS999",
            pensionerType = "TYPE_A",
            paymentDate = "2023-05-10",
            pensionerBaseDate = "2020-01-01",
            statusDesc = "01",
            sexDesc = "Male",
            branchName = "Central Branch",
            pensionEndDate = "2030-01-01",
            nationalId = "0012345678",
        )

        assertEquals(true, dn.toPresentation().isActive)
    }

    @Test
    fun `toPresentation marks non-01 statusDesc as inactive pensioner`() {
        val dn = PensionInquiryDN(
            fullName = "John Doe",
            paymentAmount = 1500,
            branchCode = "BR01",
            insuranceNumber = "INS123",
            pensionerRisUid = "RIS999",
            pensionerType = "TYPE_A",
            paymentDate = "2023-05-10",
            pensionerBaseDate = "2020-01-01",
            statusDesc = "02",
            sexDesc = "Male",
            branchName = "Central Branch",
            pensionEndDate = "2030-01-01",
            nationalId = "0012345678",
        )

        assertEquals(false, dn.toPresentation().isActive)
    }

    @Test
    fun `toPresentation prefers pensionerId and type description`() {
        val dn = PensionInquiryDN(
            fullName = "Test",
            paymentAmount = null,
            branchCode = null,
            insuranceNumber = "0043007196",
            pensionerRisUid = "0043007196",
            pensionerType = "101",
            paymentDate = null,
            pensionerBaseDate = null,
            statusDesc = null,
            sexDesc = null,
            branchName = null,
            pensionEndDate = null,
            nationalId = "6319889391",
            pensionerId = "1003406938",
            pensionerTypeDesc = "بازنشستگی",
        )

        val pr = dn.toPresentation()

        assertEquals("1003406938", pr.pensionerRisUid)
        assertEquals("بازنشستگی", pr.pensionerType)
    }

    @Test
    fun `toPresentation maps known pensioner type code when description is missing`() {
        val dn = PensionInquiryDN(
            fullName = null,
            paymentAmount = null,
            branchCode = null,
            insuranceNumber = null,
            pensionerRisUid = null,
            pensionerType = "101",
            paymentDate = null,
            pensionerBaseDate = null,
            statusDesc = null,
            sexDesc = null,
            branchName = null,
            pensionEndDate = null,
            nationalId = null,
        )

        val pr = dn.toPresentation()

        assertEquals("101", pr.pensionerType)
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

        assertEquals("-", pr.fullName)
        assertEquals("-", pr.statusDesc)
        assertEquals("-", pr.sexDesc)
        assertEquals("-", pr.pensionerType)
        assertEquals("0", pr.paymentAmount)
        assertEquals("", pr.branchCode)
    }
}
