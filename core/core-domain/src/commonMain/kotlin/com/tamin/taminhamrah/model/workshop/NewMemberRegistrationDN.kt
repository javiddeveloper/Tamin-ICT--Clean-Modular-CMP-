package com.tamin.taminhamrah.model.workshop

/**
 * Everything نام‌نویسی غیرحضوری collects before it can create a registration.
 *
 * The three steps of the form fill this in order: who the person is, where they were born and
 * what they will do, then the documents that evidence it.
 */
data class NewMemberRegistrationDN(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    /** Jalali `yyyy/MM/dd`, as the picker produces and the service expects. */
    val dateOfBirth: String,
    val cityOfBirthId: String,
    val cityOfIssueId: String,
    val jobCode: String,
    val startDate: String,
    val workshopId: String,
    val branchCode: String,
    /** Set when the person already exists, so the service updates rather than duplicates. */
    val personalId: Long? = null,
)

/** What the service answers a create with; [personalId] is what documents are filed against. */
data class NewMemberRegistrationResultDN(
    val requestId: Long? = null,
    val personalId: Long? = null,
)

/** Whether a national id is someone the organisation has never registered. */
data class NewMemberIsNewDN(
    val isNew: Boolean = true,
    val personalId: Long? = null,
)
