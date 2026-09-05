---
tags: [architecture]
---

# Networking

Stack: **Ktor 3.1.3** + **Ktorfit 2.5.2** (annotation-based interfaces via KSP) + kotlinx-serialization.

## core-network layout

```
apiService/     Ktorfit interfaces — Common, User, History, WorkShops, VersionHistory,
                agent/, contract/, health/, inbox/, pension/, personal/, treatment/, userRequest/
dataSource/     <X>RemoteDataSource (interface) + <X>RemoteDataSourceImpl
model/          *DTO.kt — raw JSON shapes
di/             NetworkKoinModule, ApiClientsModule, RemoteModule, ApiQueryBuilderModule
tools/          BaseDTO, NetworkUtils, errorHandling/
constant/       HeaderConstant, TimeoutConstant
```

Every RemoteDataSourceImpl takes an `ErrorParser` (`ErrorParserImpl`, bound in `networkModule`) and converts error responses into internal models. `expectSuccess = false`, so Ktor does not throw on 4xx/5xx — error handling is explicit.

## The five HTTP clients

Defined in `core-network/.../di/NetworkKoinModule.kt`, all behind qualifiers:

| Qualifier | baseUrl | Auth plugin | Timeout |
|---|---|---|---|
| `mainHttpClient` | `NetworkConstants.BASE_URL` | ✅ bearer + refresh | 60 s |
| `authHttpClient` | `BASE_URL` | ❌ (for token endpoints) | 60 s |
| `healthHttpClient` | `BASE_URL_HEALTH_PROFILE` | ❌ | 60 s |
| `uploadHttpClient` | `BASE_URL` | ✅ | 5 min |
| `aiHttpClient` | `AI_BASE_URL` | ✅ + `AiChatTokenPlugin` | 60 s |

## Token flow

```
loadTokens    → authRepository.getAccessToken()
refreshTokens → authRepository.refreshToken() → getAccessToken()
```

⚠️ Noted in the source as well: `BearerAuthProvider` caches its tokens after the first request and never calls `loadTokens` again on its own. That is why `AuthTokenInvalidator` exists:

```kotlin
client.authProviders.filterIsInstance<BearerAuthProvider>().forEach {
    authTokenInvalidator.registerClearAction { it.clearToken() }
}
```

The invalidator must be triggered on login/logout, or the stale token survives.

`refreshToken` is deliberately passed as `""` inside `BearerTokens` — the real refresh token is managed inside `AuthRepository`.

## Endpoints

`core-domain/.../util/NetworkConstants.kt` is the source of truth for shared code:

```kotlin
BASE_URL                = "https://eservices.tamin.ir/api/"
BASE_URL_VIEW           = "https://eservices.tamin.ir/view/"
BASE_URL_ACCOUNT        = "https://account.tamin.ir/auth/"
BASE_URL_HEALTH_PROFILE = "http://172.16.14.115:5700/api/"   // internal IP
AI_BASE_URL             = "https://sw.tamin.ir/api/"
REDIRECT_URI            = "mytamin://login"
DEFAULT_AUDIENCE        = "https://es.tamin.ir,https://eservices.tamin.ir,https://profile-api.tamin.ir"
REQUEST_TIMEOUT_60_SEC = 60_000L   REQUEST_TIMEOUT_5_MIN = 300_000L
```

⚠️ Endpoints are declared in **two places**: this file, and the `buildConfigField` entries in `androidApp/build.gradle.kts`. The `flavorTest` flavor overrides the build-config values but `NetworkConstants` does not see that. When an endpoint changes, check both. See [[Build-and-Run]].

### The `shortterm-request` family

`GET shortterm-request/getRequestInsuredMainInfo[/{type}]` is one backend endpoint shared by
several "shortterm request" features — it returns the caller's insurance identity plus their
`branchWorkshop` list, and the path suffix (or its absence) selects which request type's context
comes back. `:feature:orotez-protez` calls it with `/Orthosis`
(`OrotezProtezApiService.getRequestInsuredMainInfo`); `:feature:pregnancyPay` calls the same path
with **no suffix** (`PregnancyPayApiService.getMainInfo`), matching what `old_android`'s
`ServicesService.getLatestInsuranceInfo` used. Each feature keeps its own DTO/DN copy of this
response (`RequestInsuredMainInfoDTO` vs `PregnancyMainInfoDTO`) rather than sharing one — that
matches this repo's existing precedent of near-duplicate "shortterm request info" models kept in
separate per-domain packages (compare `ShowRequestInfoDTO`). If you're porting another
`old_android` "shortterm" feature (e.g. sick-leave pay / `requestPaymentForillDays`), check
`ServicesService.kt` there first for the exact suffix (or lack of one) before assuming it matches
an existing KMP feature's.

⚠️ `CLIENT_ID` is hardcoded in `NetworkConstants`, while `androidApp` reads it from `key.properties` / the `OPERATIONAL_API_KEY` environment variable.

## Error handling

`tools/BaseDTO.kt` unwraps the standard envelope (`status`, `family`, `reason`, `data`, `problems`):

- `extractData()` — returns `data` or throws `TaminErrorUriException`
- `extractMessage()` — for endpoints that return only a message
- `extractDataOrProblems()` / `extractMessageOrProblems()` — return `ApiOutcome` instead of throwing when the backend sent a `problems` list

HTTP 4xx/5xx is mapped by `HttpStatusErrorMapper` (ported from old_android `BaseRemoteDataSource.handleError`): exact status, then known backend tokens (`sso.to.sa.connection.exception`, missing bank account, open pension request, …), then Arabic-script passthrough via `looksLikeArabicScript()`, then a per-status Persian fallback. 403 also sets `navigateBack` on the exception (VPN/proxy copy). Screens can read it with `Throwable.shouldNavigateBack()`.

401 in this layer maps to `INVALID_AUTH` with login copy. Token refresh is **not** re-implemented here — it already lives in the `mainHttpClient` / `uploadHttpClient` Ktor Auth plugin. A 401 that still reaches `BaseDTO` is the post-refresh failure.

`tools/NetworkUtils.kt` holds `ErrorParser.safeCall(tag) { … }`, which wraps a call and normalizes failures. Its catch-all branch maps every unexpected exception to `NO_CONNECTION_ERROR`.

## Logging

The `Logging` plugin uses Kermit with tags `KtorClient` / `KtorHealthClient`. Log level is driven by `AppConfig.isDebug`, and the `Authorization` header is stripped via `sanitizeHeader`.
`chucker` is also in the catalog (debug build / release no-op) for inspecting Android traffic.

## Testing

`core-network/src/commonTest/resources/mocks/` and `androidUnitTest/resources/mocks/` hold sample JSON (`certificate/`, `pension/`) used with `ktor-client-mock`.

Related: [[Dependency-Injection]] · [[Database]] · [[Overview]]
