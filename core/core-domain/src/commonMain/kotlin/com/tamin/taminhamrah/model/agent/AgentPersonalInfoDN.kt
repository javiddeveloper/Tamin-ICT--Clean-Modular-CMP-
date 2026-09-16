package com.tamin.taminhamrah.model.agent

/**
 * Who is asking, sent with every prompt so the server can answer personal questions without
 * looking the user up. [pensionerId] is null for anyone who is not a pensioner.
 */
data class AgentPersonalInfoDN(
    val nationalId: String,
    val pensionerId: String?,
    val firstName: String?,
    val lastName: String?,
)
