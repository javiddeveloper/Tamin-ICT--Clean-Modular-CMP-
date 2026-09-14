---
tags: [architecture, convention]
---

# Error Handling

There is **no `Result<T>`/`Resource`/`DataState` sealed wrapper** anywhere in this
codebase. The entire app is consistently exception-based — don't introduce one as an
"improvement."

## The chain

1. **Network boundary** (`core-network`): catch and rethrow as `TaminErrorUriException`
   (carries an `ErrorUri` enum value + optional server message/code). Canonical shape,
   see e.g. `core/core-network/.../dataSource/inbox/PersonalInboxRemoteDataSourceImpl.kt`:
   ```kotlin
   try {
       ...
   } catch (e: TaminErrorUriException) {
       throw errorParser.parseGeneralError(e)
   } catch (e: Exception) {
       throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
   }
   ```
2. **`ErrorParser`/`ErrorParserImpl`** (`core-network/.../tools/errorHandling/`) maps
   `TaminErrorUriException` → localized (Persian) `TaminApiException` (`title`/`subtitle`)
   via a `when` over `ErrorUri`.
3. **ViewModel**: catches the exception and extracts a user-facing message through
   `toSingleLineMessage()` (`core-network/.../tools/errorHandling/TaminException.kt`),
   not raw `e.message`. Preferred pattern (`feature/profile`'s `IdentityInViewModel`):
   ```kotlin
   useCase().map { ... }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }.collect { emit(it) }
   ```
   `feature/my-inbox`'s current `e.message ?: getString(Res.string.error_unknown_fallback)`
   fallback is a known, existing inconsistency — follow `toSingleLineMessage()` for new
   code, don't rewrite my-inbox's existing handling unless asked.

## Response envelope

Ktorfit service methods return `BaseDTO<T>` (`core-network/.../tools/BaseDTO.kt`). Use
`.extractData()` (throws on problems/4xx/5xx/null-data) for the common case, or
`.extractDataOrProblems()` (returns `ApiOutcome<T>`) when the endpoint needs to surface
backend "problems" without collapsing into one exception.

## Before adding a new constant

`TimeoutConstant`/`HeaderConstant` exist duplicated in two packages
(`com.tamin.core.network.constant` — unused — and `com.tamin.taminhamrah.util` —
actually wired up). Search
`core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/util/` before adding a
new timeout/header/network constant; don't create a second definition.

Related: [[Networking]] · [[MVI-Pattern]] · [[Overview]]
