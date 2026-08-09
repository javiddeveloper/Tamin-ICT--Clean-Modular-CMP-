package com.tamin.taminhamrah.model.health

/**
 * One business-level problem returned by the backend's `problems` envelope,
 * e.g. {"error_Code":9001,"error_Msg":"..."}. Mirrors ProblemDTO in core-network,
 * mapped by HealthDeclarationMappers.toDomain() in core-data.
 */
data class HealthProblemDN(
    val code: Int?,
    val message: String
)

/**
 * Result of a health mutation call (updatePatient, addSelfDeclarative, etc.).
 * [data] is non-null on success. When the backend responds with structured
 * business problems (invalid record id, duplicate declaration, etc.) [data] is
 * null and [problems] carries the details instead of the call throwing a
 * generic exception - callers (ultimately HealthProfileViewModel) decide how
 * to surface them.
 */
data class HealthMutationResult<out T>(
    val data: T?,
    val problems: List<HealthProblemDN> = emptyList()
) {
    val isSuccess: Boolean get() = data != null && problems.isEmpty()
}
