package com.tamin.taminhamrah.ui.components.bottomsheet

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.*

data class TaminBottomSheetConfig(
    val title: String = "",
    val subtitle: String? = null,
    val subtitleRes: StringResource? = null,
    val description: String? = null,
    val showSearchInput: Boolean = false,
    val searchInputHint: String? = null,
    val submitText: String? = null,
    val cancelText: String? = null,
    val type: TaminBottomSheetType = TaminBottomSheetType.CUSTOM,
    val items: List<TaminBottomSheetItem> = emptyList(),
    val singleSelection: Boolean = false,
    val showWarning: Boolean = false,
    val warningText: String? = null,
    val isLoading: Boolean = false
)

data class TaminBottomSheetItem(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)

enum class TaminBottomSheetType(
    val groupId: Int,
    val titleRes: StringResource? = null,
    val isSingleSelect: Boolean = false,
    val showSearch: Boolean = false
) {
    RISK_FACTOR(groupId = 1, titleRes = Res.string.bs_risk_factor, isSingleSelect = false),
    ILLNESS_HISTORY(groupId = 2, titleRes = Res.string.bs_illness_history, isSingleSelect = false),
    MENTAL(groupId = 3, titleRes = Res.string.bs_mental, isSingleSelect = false),
    CANCER(groupId = 4, titleRes = Res.string.bs_cancer, isSingleSelect = false),
    FAMILY_DISEASES(groupId = 5, titleRes = Res.string.bs_family_diseases, isSingleSelect = false),
    FAMILY_CANCER(groupId = 6, titleRes = Res.string.bs_family_cancer, isSingleSelect = false),
    PROVINCE(groupId = 9, titleRes = Res.string.bs_province, isSingleSelect = true, showSearch = true),
    CITY(groupId = 8, titleRes = Res.string.bs_city, isSingleSelect = true, showSearch = true),
    BLOOD_GROUP(groupId = 101, titleRes = Res.string.bs_blood_group, isSingleSelect = true),
    SMOKING_ADDICTION(groupId = 201, titleRes = Res.string.bs_smoking_addiction, isSingleSelect = false),
    DRUG_ADDICTION(groupId = 202, titleRes = Res.string.bs_drug_addiction, isSingleSelect = false),
    ALCOHOL_ADDICTION(groupId = 203, titleRes = Res.string.bs_alcohol_addiction, isSingleSelect = false),
    EXERCISE(groupId = 204, titleRes = Res.string.bs_exercise, isSingleSelect = false),
    MARITAL_STATUS(groupId = 10, titleRes = Res.string.bs_marital_status, isSingleSelect = true),
    RELATION_TYPE(groupId = 11, titleRes = Res.string.bs_relation_type, isSingleSelect = true),
    EMERGENCY_CONTACT(groupId = 12, titleRes = Res.string.bs_emergency_contact, isSingleSelect = true),
    CUSTOM(groupId = 999, titleRes = null);

    companion object {
        fun fromGroupId(groupId: Int): TaminBottomSheetType? =
            entries.firstOrNull { it.groupId == groupId }
    }
}

data class TaminBottomSheetResult(
    val type: TaminBottomSheetType,
    val selectedItemIds: List<Int>,
    val text: String? = null,
    val description: String? = null
)
