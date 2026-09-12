package com.tamin.taminhamrah.feature.contracts.flow.config

import com.tamin.taminhamrah.feature.contracts.flow.ContractType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the legacy rules-PDF mapping:
 * InsuranceContractFragment → rules.pdf (student / freelance / housewife)
 * OptionalContractFragment → rules2.pdf
 */
class ContractRulesPdfMappingTest {

    @Test
    fun student_freelance_housewife_use_special_insured_rules_pdf() {
        assertEquals(ContractRulesPdf.SPECIAL_INSURED, ContractType.STUDENT.createConfig().rulesPdfPath)
        assertEquals(ContractRulesPdf.SPECIAL_INSURED, ContractType.FREELANCE.createConfig().rulesPdfPath)
        assertEquals(ContractRulesPdf.SPECIAL_INSURED, ContractType.HOUSEWIFE.createConfig().rulesPdfPath)
        assertEquals("rules.pdf", ContractRulesPdf.SPECIAL_INSURED)
    }

    @Test
    fun optional_uses_rules2_pdf() {
        assertEquals(ContractRulesPdf.OPTIONAL, ContractType.OPTIONAL.createConfig().rulesPdfPath)
        assertEquals("rules2.pdf", ContractRulesPdf.OPTIONAL)
    }
}
