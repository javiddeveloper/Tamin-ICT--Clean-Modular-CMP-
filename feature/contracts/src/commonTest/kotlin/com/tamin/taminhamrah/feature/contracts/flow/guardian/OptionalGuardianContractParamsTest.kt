package com.tamin.taminhamrah.feature.contracts.flow.guardian

import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OptionalGuardianContractParamsTest {

    @Test
    fun `builds guardian params with protector details instead of personal contract payload`() {
        val params = buildOptionalContractByGuardianParams(
            selectedSalary = 362_592_593L,
            branch = validBranch(),
            treatmentSupportCode = "1",
            premiumRateCode = "",
            guardianForm = validGuardianForm(),
            wardNationalId = "3860387200",
            documentDescription = "تصویر قیم نامه",
        )

        assertNotNull(params)
        assertEquals(362_592_593L, params.selectedSalary)
        assertEquals("0360", params.contract.brchCodeNew)
        assertEquals("2442", params.contract.cityCode)
        assertEquals("1", params.contract.cntDrmn)
        assertEquals("", params.contract.premiumRateCode)
        assertEquals("33", params.contract.provinceCode)

        assertEquals("3860387200", params.protector.proCode)
        assertEquals("guid-abc", params.protector.guid)
        assertEquals("تصویر قیم نامه", params.protector.guidName)
        assertEquals("0083834001", params.protector.nid)
        assertEquals("رضا نادری", params.protector.fullName)
        assertEquals("2222222222", params.protector.protectorLetterNo)
        assertEquals(true, params.protector.protectorLetterDate.isNotBlank())
    }

    @Test
    fun `returns null when guardian form is incomplete`() {
        val params = buildOptionalContractByGuardianParams(
            selectedSalary = 100L,
            branch = validBranch(),
            treatmentSupportCode = "1",
            premiumRateCode = "",
            guardianForm = validGuardianForm(letterNumber = "12"),
            wardNationalId = "3860387200",
            documentDescription = "تصویر قیم نامه",
        )
        assertNull(params)
    }

    @Test
    fun `returns null when document guid is missing`() {
        val params = buildOptionalContractByGuardianParams(
            selectedSalary = 100L,
            branch = validBranch(),
            treatmentSupportCode = "1",
            premiumRateCode = "",
            guardianForm = validGuardianForm().copy(
                documentGuid = null,
                documentPreviewBytes = byteArrayOf(1),
            ),
            wardNationalId = "3860387200",
            documentDescription = "تصویر قیم نامه",
        )
        assertNull(params)
    }

    @Test
    fun `returns null when ward national id is blank`() {
        val params = buildOptionalContractByGuardianParams(
            selectedSalary = 100L,
            branch = validBranch(),
            treatmentSupportCode = "1",
            premiumRateCode = "",
            guardianForm = validGuardianForm(),
            wardNationalId = "",
            documentDescription = "تصویر قیم نامه",
        )
        assertNull(params)
    }

    @Test
    fun `returns null when branch is incomplete`() {
        val params = buildOptionalContractByGuardianParams(
            selectedSalary = 100L,
            branch = BranchSelectionFormPR(provinceCode = "33"),
            treatmentSupportCode = "1",
            premiumRateCode = "",
            guardianForm = validGuardianForm(),
            wardNationalId = "3860387200",
            documentDescription = "تصویر قیم نامه",
        )
        assertNull(params)
    }

    private fun validBranch() = BranchSelectionFormPR(
        provinceCode = "33",
        cityCode = "2442",
        branchCode = "0360",
    )

    private fun validGuardianForm(
        letterNumber: String = "2222222222",
    ) = GuardianFormPR(
        nationalId = "0083834001",
        letterNumber = letterNumber,
        fullName = "رضا نادری",
        letterDateFormatted = "1401/06/27",
        letterDateEpoch = 1_663_527_000_000L,
        documentGuid = "guid-abc",
        documentName = "guardian.jpg",
    )
}
