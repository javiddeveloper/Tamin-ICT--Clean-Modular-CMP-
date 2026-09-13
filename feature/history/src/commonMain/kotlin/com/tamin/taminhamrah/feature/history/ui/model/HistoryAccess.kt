package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN

/**
 * Whether this person can have insurance history at all.
 *
 * «کلیه سوابق» is a بیمه‌شده service — the menu says so itself, `showRole = [1]` — but the role
 * picker on the services page is chosen by hand, so a مستمری‌بگیر or a کارفرما can still walk in.
 * Neither has insured years, and the history endpoints reject them rather than answering with an
 * empty list, so this is decided before loading.
 *
 * **Two signals, because they answer different questions.** `login-services/logininfo` says whether
 * this is a مستمری‌بگیر — that is the previous app's own test and the only thing it checked. It has
 * no code for an employer, so it cannot recognize one: a کارفرما reads back as ordinary and would
 * be let through. The insurance number from `history-services/userinfos` closes exactly that gap —
 * it is what identifies someone as insured at all, and a person who never was has none.
 *
 * Both are needed. Either alone lets one of the two roles reach an endpoint that will refuse it.
 *
 * A [UserRoleDN.UNKNOWN] role still carries on when the insurance number is there: a gate exists to
 * explain a service someone cannot use, not to lock out someone the sign-in service happens to have
 * nothing to say about.
 */
fun canHaveInsuranceHistory(role: UserRoleDN, userInfo: UserInfoDN?): Boolean = when {
    role == UserRoleDN.PENSIONER -> false
    // Only when the record actually arrived. A failed lookup is not evidence of anything, and
    // refusing on it would turn an outage into "you are not insured".
    userInfo != null && userInfo.insuranceNumber.isNullOrBlank() -> false
    else -> true
}
