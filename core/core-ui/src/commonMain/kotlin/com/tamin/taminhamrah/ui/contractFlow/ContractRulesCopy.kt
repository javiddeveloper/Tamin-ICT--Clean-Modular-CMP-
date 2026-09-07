package com.tamin.taminhamrah.ui.contractFlow

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rules_sheet_freelance_p1
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item1
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item2
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item3
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_item4
import taminx.core.core_ui.contract_rules_sheet_freelance_section1_title
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item1
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item2
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_item3
import taminx.core.core_ui.contract_rules_sheet_freelance_section2_title
import taminx.core.core_ui.contract_rules_sheet_housewife_p1
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item1
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item2
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item3
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_item4
import taminx.core.core_ui.contract_rules_sheet_housewife_section1_title
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item1
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item2
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_item3
import taminx.core.core_ui.contract_rules_sheet_housewife_section2_title
import taminx.core.core_ui.contract_rules_sheet_optional_p1
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item1
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item2
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item3
import taminx.core.core_ui.contract_rules_sheet_optional_section1_item4
import taminx.core.core_ui.contract_rules_sheet_optional_section1_title
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item1
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item2
import taminx.core.core_ui.contract_rules_sheet_optional_section2_item3
import taminx.core.core_ui.contract_rules_sheet_optional_section2_title
import taminx.core.core_ui.contract_rules_sheet_student_p1
import taminx.core.core_ui.contract_rules_sheet_student_section1_item1
import taminx.core.core_ui.contract_rules_sheet_student_section1_item2
import taminx.core.core_ui.contract_rules_sheet_student_section1_item3
import taminx.core.core_ui.contract_rules_sheet_student_section1_item4
import taminx.core.core_ui.contract_rules_sheet_student_section1_title
import taminx.core.core_ui.contract_rules_sheet_student_section2_item1
import taminx.core.core_ui.contract_rules_sheet_student_section2_item2
import taminx.core.core_ui.contract_rules_sheet_student_section2_item3
import taminx.core.core_ui.contract_rules_sheet_student_section2_title
import taminx.core.core_ui.contract_rules_sheet_title_freelance
import taminx.core.core_ui.contract_rules_sheet_title_housewife
import taminx.core.core_ui.contract_rules_sheet_title_optional
import taminx.core.core_ui.contract_rules_sheet_title_student

/**
 * Per-contract-type string resources for [ContractRulesBottomSheet].
 * Each contract flow config supplies the matching copy so the shared sheet never defaults to student text.
 */
data class ContractRulesCopy(
    val titleRes: StringResource,
    val introRes: StringResource,
    val section1TitleRes: StringResource,
    val section1ItemRes: List<StringResource>,
    val section2TitleRes: StringResource,
    val section2ItemRes: List<StringResource>,
)

object ContractRulesCopies {
    val Student = ContractRulesCopy(
        titleRes = Res.string.contract_rules_sheet_title_student,
        introRes = Res.string.contract_rules_sheet_student_p1,
        section1TitleRes = Res.string.contract_rules_sheet_student_section1_title,
        section1ItemRes = listOf(
            Res.string.contract_rules_sheet_student_section1_item1,
            Res.string.contract_rules_sheet_student_section1_item2,
            Res.string.contract_rules_sheet_student_section1_item3,
            Res.string.contract_rules_sheet_student_section1_item4,
        ),
        section2TitleRes = Res.string.contract_rules_sheet_student_section2_title,
        section2ItemRes = listOf(
            Res.string.contract_rules_sheet_student_section2_item1,
            Res.string.contract_rules_sheet_student_section2_item2,
            Res.string.contract_rules_sheet_student_section2_item3,
        ),
    )

    val Freelance = ContractRulesCopy(
        titleRes = Res.string.contract_rules_sheet_title_freelance,
        introRes = Res.string.contract_rules_sheet_freelance_p1,
        section1TitleRes = Res.string.contract_rules_sheet_freelance_section1_title,
        section1ItemRes = listOf(
            Res.string.contract_rules_sheet_freelance_section1_item1,
            Res.string.contract_rules_sheet_freelance_section1_item2,
            Res.string.contract_rules_sheet_freelance_section1_item3,
            Res.string.contract_rules_sheet_freelance_section1_item4,
        ),
        section2TitleRes = Res.string.contract_rules_sheet_freelance_section2_title,
        section2ItemRes = listOf(
            Res.string.contract_rules_sheet_freelance_section2_item1,
            Res.string.contract_rules_sheet_freelance_section2_item2,
            Res.string.contract_rules_sheet_freelance_section2_item3,
        ),
    )

    val Housewife = ContractRulesCopy(
        titleRes = Res.string.contract_rules_sheet_title_housewife,
        introRes = Res.string.contract_rules_sheet_housewife_p1,
        section1TitleRes = Res.string.contract_rules_sheet_housewife_section1_title,
        section1ItemRes = listOf(
            Res.string.contract_rules_sheet_housewife_section1_item1,
            Res.string.contract_rules_sheet_housewife_section1_item2,
            Res.string.contract_rules_sheet_housewife_section1_item3,
            Res.string.contract_rules_sheet_housewife_section1_item4,
        ),
        section2TitleRes = Res.string.contract_rules_sheet_housewife_section2_title,
        section2ItemRes = listOf(
            Res.string.contract_rules_sheet_housewife_section2_item1,
            Res.string.contract_rules_sheet_housewife_section2_item2,
            Res.string.contract_rules_sheet_housewife_section2_item3,
        ),
    )

    val Optional = ContractRulesCopy(
        titleRes = Res.string.contract_rules_sheet_title_optional,
        introRes = Res.string.contract_rules_sheet_optional_p1,
        section1TitleRes = Res.string.contract_rules_sheet_optional_section1_title,
        section1ItemRes = listOf(
            Res.string.contract_rules_sheet_optional_section1_item1,
            Res.string.contract_rules_sheet_optional_section1_item2,
            Res.string.contract_rules_sheet_optional_section1_item3,
            Res.string.contract_rules_sheet_optional_section1_item4,
        ),
        section2TitleRes = Res.string.contract_rules_sheet_optional_section2_title,
        section2ItemRes = listOf(
            Res.string.contract_rules_sheet_optional_section2_item1,
            Res.string.contract_rules_sheet_optional_section2_item2,
            Res.string.contract_rules_sheet_optional_section2_item3,
        ),
    )
}
