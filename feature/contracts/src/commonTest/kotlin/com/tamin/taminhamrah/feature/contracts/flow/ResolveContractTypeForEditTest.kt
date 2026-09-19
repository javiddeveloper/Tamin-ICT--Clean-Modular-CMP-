package com.tamin.taminhamrah.feature.contracts.flow

import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResolveContractTypeForEditTest {

    @Test
    fun `optional premium type maps to OPTIONAL`() {
        assertEquals(
            ContractType.OPTIONAL,
            resolveContractTypeForEdit(ContractPremiumTypeCode.OPTIONAL, freeJobCode = ""),
        )
    }

    @Test
    fun `freelance student job maps to STUDENT`() {
        assertEquals(
            ContractType.STUDENT,
            resolveContractTypeForEdit(
                ContractPremiumTypeCode.FREELANCE,
                ContractFreeJobCode.STUDENT_CONTRACT_CODE,
            ),
        )
    }

    @Test
    fun `freelance housewife job maps to HOUSEWIFE`() {
        assertEquals(
            ContractType.HOUSEWIFE,
            resolveContractTypeForEdit(
                ContractPremiumTypeCode.FREELANCE,
                ContractFreeJobCode.WOMEN_CONTRACT_CODE,
            ),
        )
    }

    @Test
    fun `other freelance job maps to FREELANCE`() {
        assertEquals(
            ContractType.FREELANCE,
            resolveContractTypeForEdit(ContractPremiumTypeCode.FREELANCE, freeJobCode = "110001"),
        )
    }

    @Test
    fun `fraction premium type is unsupported`() {
        assertNull(resolveContractTypeForEdit("38", freeJobCode = ""))
    }
}
