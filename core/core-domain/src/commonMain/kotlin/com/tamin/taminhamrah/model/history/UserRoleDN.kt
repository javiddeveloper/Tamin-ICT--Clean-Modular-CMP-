package com.tamin.taminhamrah.model.history

/**
 * What the sign-in service says this person is.
 *
 * The wire carries relation codes, not a role — the translation lives in the data layer's mapper,
 * and everything above it reads this instead.
 *
 * [UNKNOWN] is a real answer, not a failure: the service returns no codes at all for a
 * newly-registered person. Callers gating on a role must treat it as "carry on", the way the
 * previous app did — a gate exists to explain a service someone cannot use, never to lock out
 * someone the service simply has nothing to say about yet.
 */
enum class UserRoleDN {
    INSURED,
    PENSIONER,
    UNKNOWN,
}
