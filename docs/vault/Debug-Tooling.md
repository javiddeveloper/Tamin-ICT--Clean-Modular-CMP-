---
tags: [architecture]
---

# Debug Tooling — token slots and back-to-back login

Debug-only feature living in `:feature:developerOptions`, added so a tester can hold a
client-credentials ("back-to-back") session in parallel with their real user session, without
losing either one. No `old_android` equivalent exists — this is new tooling, not a port.

## `TokenSlot`

`core-domain/.../model/auth/TokenSlot.kt` names where a token is kept:

- `USER` — the real PKCE `authorization_code` login. Keeps the original storage keys
  (`TOKEN`/`REFRESH_TOKEN`), so an install that predates slots still finds its existing session.
- `BACK_TO_BACK` — the debug-only `client_credentials` login, in its own suffixed keys
  (`TOKEN_BACK_TO_BACK`/…). Never active outside a debug build.
- `AGENT` — not a bearer slot. Holds the AI chat token purely so the debug Token Manager screen can
  display/refresh it; `CheckChatAllowedUseCase` writes into it on every successful chat-allowed
  check.

## The `isDebug` gate

`TokenStoreManagerImpl` takes `isDebug: Boolean = AppConfig.isDebug` as a constructor parameter
(same testability pattern as `DeveloperOptionsRepositoryImpl.getEffectiveBaseUrl`) so a release
build — or a unit test — can prove the gate holds regardless of what a previous debug install left
in shared `Settings` storage:

- `getActiveSlot()` / `readActiveSlot()` return `TokenSlot.USER` unconditionally when `!isDebug`,
  no matter what `ACTIVE_TOKEN_SLOT` holds in storage.
- `setActiveSlot()` is a no-op outside a debug build (and also refuses `TokenSlot.AGENT`, which is
  never "active").

Covered by `TokenStoreManagerImplTest` — this is the regression class the feature's own commit
message says it fixed (debug login overwriting the real user session), so it has a direct test
rather than relying on the gate never breaking silently.

## Real login always resets the active slot to `USER`

`TokenStoreManagerImpl.saveToken(token: String?)` — the no-slot overload, used by both
`AuthRepositoryImpl.exchangeCodeForTokens` (real PKCE login) and `logout()` — always clears
`ACTIVE_TOKEN_SLOT` back to `TokenSlot.USER`, whether `token` is a fresh access token or `null`.

Without this, switching to the `BACK_TO_BACK` slot via Token Manager and then logging in for real
without an explicit sign-out first would leave `activeSlot` stuck on `BACK_TO_BACK`: the new user
token would be written correctly, but every request would keep sending the stale debug bearer until
the tester manually re-activated `USER`.

## Screens

- `debugLogin/DebugLoginScreen.kt` — `client_credentials` login form. `BackToBackCredentials`
  hardcodes a real `client_id`/`client_secret` pair; see the KDoc there for the accepted-risk
  trade-off (this module ships unminified in every build).
- `tokens/TokenManagerScreen.kt` — shows all three slots' JWT expiry, and switches the active one
  (`AuthRepository.switchTokenSlot`).

Related: [[Networking]] · [[Dependency-Injection]] · [[Payments]]
