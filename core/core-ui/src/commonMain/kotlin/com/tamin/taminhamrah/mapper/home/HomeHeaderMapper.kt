package com.tamin.taminhamrah.mapper.home

import com.tamin.taminhamrah.mapper.activeRelation.toUiModelList
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN

// toDarmanCoveredOrNull() moved to core-domain's
// com.tamin.taminhamrah.model.treatment.TreatmentCoverageMapper.kt, so both core-data
// (HomeRepositoryImpl, building the offline cache) and core-ui (this module) can call the same
// implementation — core-data cannot depend on core-ui.

/** True when the user has at least one active شعبه/کارگاه relation, for the «ارتباط فعال» chip. */
fun List<ActiveRelationDN>.hasActiveRelation(): Boolean =
    toUiModelList().any { it.isActive }
