package com.tamin.taminhamrah.model.workshop

/** One ذینفع row of a workshop. [birthDate] is epoch millis. */
data class WorkshopStackHolderDN(
    val stackId: Int? = null,
    val nationalId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fatherName: String = "",
    val birthDate: Long? = null,
    val stackType: String = "",
) {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}
