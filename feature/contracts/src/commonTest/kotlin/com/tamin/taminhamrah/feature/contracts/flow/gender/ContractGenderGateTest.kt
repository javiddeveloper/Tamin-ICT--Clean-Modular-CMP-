package com.tamin.taminhamrah.feature.contracts.flow.gender

import com.tamin.taminhamrah.feature.contracts.flow.config.FreelanceContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.HousewifeContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContractGenderGateTest {

    @Test
    fun `housewife config requires female gender`() {
        assertTrue(HousewifeContractFlowConfig().requiresFemaleGender)
        assertFalse(StudentContractFlowConfig().requiresFemaleGender)
        assertFalse(FreelanceContractFlowConfig().requiresFemaleGender)
    }

    @Test
    fun `blocks male registrant under female-only config`() {
        assertTrue(
            isFemaleOnlyServiceBlocked(
                requiresFemaleGender = true,
                isFemale = false,
            ),
        )
    }

    @Test
    fun `allows female registrant under female-only config`() {
        assertFalse(
            isFemaleOnlyServiceBlocked(
                requiresFemaleGender = true,
                isFemale = true,
            ),
        )
    }

    @Test
    fun `does not block when config is not female-only`() {
        assertFalse(
            isFemaleOnlyServiceBlocked(
                requiresFemaleGender = false,
                isFemale = false,
            ),
        )
    }
}
