package com.tamin.taminhamrah.model.workshop

/** One کارکنان row of a workshop. */
data class WorkshopMemberDN(
    val insuranceNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fatherName: String = "",
    val nationalId: String = "",
    val idCardNumber: String = "",
    val nationDescription: String = "",
    val relationTypeDescription: String = "",
    val leavingWorkStatus: String = "",
    val leavingWorkDate: String = "",
) {
    /**
     * Built from whichever halves exist, so a member missing one name does not render the literal
     * `null` the old client showed.
     */
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}
