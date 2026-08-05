package com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet

import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.model.IllnessGroupPR

fun List<IllnessGroupPR>.findGroup(
    type: TaminBottomSheetType,
    forFamily: Boolean = false
): IllnessGroupPR? {
    return find { it.groupId == type.groupId && it.forFamily == forFamily }
}
