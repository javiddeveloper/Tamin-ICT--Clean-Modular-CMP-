package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.model.history.UserRoleDN

/**
 * Whether this person can have insurance history at all.
 *
 * «مجموع سوابق» is a بیمه‌شده service — the menu says so itself, `showRole = [1]` — but the role
 * picker on the services page is chosen by hand, so a مستمری‌بگیر can still walk in. They have no
 * insured years and the two history endpoints answer 500 rather than an empty list, so the previous
 * app decided this before loading, from `login-services/logininfo`, and so does this one.
 *
 * Only a مستمری‌بگیر is turned away, exactly as before. [UserRoleDN.UNKNOWN] carries on: a gate is
 * here to explain a service someone cannot use, and a person the sign-in service says nothing about
 * is better served by the endpoints' own answer than by a refusal we invented.
 *
 * **This is the single place the rule lives.**
 */
fun UserRoleDN.canHaveInsuranceHistory(): Boolean = this != UserRoleDN.PENSIONER
