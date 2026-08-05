package com.tamin.taminhamrah.ui.components.bottomsheet

data class TaminBottomSheetConfig(
    val title: String = "",
    val subtitle: String? = "",
    val description: String? = null,
    val showSearchInput: Boolean = false,
    val searchInputHint: String? = null,
    val submitText: String = "ثبت",
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
    val title: String? = null,
    val isSingleSelect: Boolean = false,
    val showSearch: Boolean = false
) {
    RISK_FACTOR(groupId = 1, title = "آیا سابقه بالا بودن هر یک از موارد زیر را دارید؟", isSingleSelect = false),
    ILLNESS_HISTORY(groupId = 2, title = "آیا سابقه ابتلا به بیماری دارید؟", isSingleSelect = false),
    MENTAL(groupId = 3, title = "آیا بیماری اعصاب و روان دارید؟", isSingleSelect = false),
    CANCER(groupId = 4, title = "ایا سابقه ابتلا به سرطان دارید ؟", isSingleSelect = false),
    FAMILY_DISEASES(groupId = 5, title = "آیا در بین اعضای خانواده سابقه ابتلا به موارد زیر وجود دارد؟", isSingleSelect = false),
    FAMILY_CANCER(groupId = 6, title = "سابقه ابتلا به سرطان در خانواده", isSingleSelect = false),
    PROVINCE(groupId = 9, title = "استان", isSingleSelect = true, showSearch = true),
    CITY(groupId = 8, title = "شهر", isSingleSelect = true, showSearch = true),
    BLOOD_GROUP(groupId = 101, title = "گروه خونی", isSingleSelect = true),
    SMOKING_ADDICTION(groupId = 201, title = "آیا از دخانیات استفاده می\u200Cکنید؟", isSingleSelect = false),
    DRUG_ADDICTION(groupId = 202, title = "آیا اعتیاد دارید؟", isSingleSelect = false),
    ALCOHOL_ADDICTION(groupId = 203, title = "آیا الکل مصرف دارید؟", isSingleSelect = false),
    EXERCISE(groupId = 204, title = "آیا ورزش می کنید؟", isSingleSelect = false),
    MARITAL_STATUS(groupId = 10, title = "وضعیت تاهل", isSingleSelect = true),
    EMERGENCY_CONTACT(groupId = 11, title = "تماس اضطراری", isSingleSelect = true),
    CUSTOM(groupId = 999, title = null);

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
