package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * One نام نویسی غیر حضوری بیمه شده row.
 *
 * [isDraft] is what the row's actions branch on — confirm/edit/delete while drafted, follow-up once
 * submitted — and [canConfirm] is the narrower question of whether the confirm call can be made at
 * all. The ids stay raw: they address the confirm and delete calls.
 */
@Immutable
data class WorkshopNewMemberPR(
    val personalId: Long? = null,
    val requestId: Long? = null,
    val fullName: String = "",
    val nationalId: String = "",
    val birthDate: String = "",
    val insuranceNumber: String = "",
    val registerDate: String = "",
    /** The tracking code a submitted registration is followed by. Blank while it is a draft. */
    val referenceCode: String = "",
    val statusLabel: String = "",
    val isDraft: Boolean = true,
    val canConfirm: Boolean = false,
    /**
     * What re-opening this draft needs, kept raw.
     *
     * The names are held apart because the form edits them apart, and the two city codes and the
     * job code are what the pickers are re-seeded from — [fullName] and the formatted dates are
     * for reading, not for filling a form back in.
     */
    val firstName: String = "",
    val lastName: String = "",
    val cityOfBirthId: String = "",
    val cityOfIssueId: String = "",
    val jobCode: String = "",
    val startDate: String = "",
)
