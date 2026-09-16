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
    /** Set once the person is on file, so the service updates that person instead of adding one. */
    val personalId: Long? = null,
)

/** What the service answers a create or update with; [personalId] is what documents are filed against. */
data class NewMemberRegistrationResultDN(val personalId: Long? = null)

