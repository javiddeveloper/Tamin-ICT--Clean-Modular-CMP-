package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.model.history.UserInfoDN

/**
 * Whether this person can have insurance history at all.
 *
 * «مجموع سوابق» is a بیمه‌شده service — the menu says so itself, `showRole = [1]`. A مستمری‌بگیر or a
 * کارفرما has no insured years, and the two history endpoints answer 500 rather than an empty list
 * for them, so asking at all produces a server error the person can do nothing about. The previous
 * app made the same decision before loading, from `login-services/logininfo`.
 *
 * That endpoint has no counterpart here yet, so the test is the insurance number `userinfos`
 * returns: it is what identifies someone as insured, and a person without one has no years to show.
 *
 * **This is the single place the rule lives.** If the backend later exposes the role directly — the
 * 1/2/3 the menu already speaks — this function is what changes, and nothing above it moves.
 */
fun UserInfoDN.canHaveInsuranceHistory(): Boolean = !insuranceNumber.isNullOrBlank()
