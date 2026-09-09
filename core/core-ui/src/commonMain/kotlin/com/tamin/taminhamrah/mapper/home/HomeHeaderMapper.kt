package com.tamin.taminhamrah.mapper.home

import com.tamin.taminhamrah.mapper.activeRelation.toUiModelList
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN

/**
 * The phrase the treatment-entitlement endpoint uses when it refuses. Matched as a whole phrase,
 * not on «عدم» alone — that fragment also sits inside ordinary words. Mirrors the rule in
 * `feature/treatment/.../ui/model/CoverageStatus.kt`, which can't be imported here (feature
 * modules aren't on the dependency graph of `core-ui`).
 */
private const val NOT_ENTITLED_PHRASE = "عدم استحقاق"

/**
 * Whether the insured person is entitled to treatment support, for the home header chip.
 *
 * `null` means the answer isn't known yet — the entitlement list came back empty, so the chip is
 * hidden rather than shown as a guess. Otherwise the main (first) record decides: it is a refusal
 * when `finalDesc` is worded, or when `message` carries [NOT_ENTITLED_PHRASE]; anything else is
 * read as covered (an entitled record still carries branch / booklet data).
 */
fun List<DeservedTreatmentDN>.toDarmanCoveredOrNull(): Boolean? {
    val main = firstOrNull() ?: return null
    val refusal = main.finalDesc?.takeIf { it.isNotBlank() }
        ?: main.message?.takeIf { it.contains(NOT_ENTITLED_PHRASE) }
    return refusal == null
}

/** True when the user has at least one active شعبه/کارگاه relation, for the «ارتباط فعال» chip. */
fun List<ActiveRelationDN>.hasActiveRelation(): Boolean =
    toUiModelList().any { it.isActive }
