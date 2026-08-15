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

⚠️ `CLIENT_ID` is hardcoded in `NetworkConstants`, while `androidApp` reads it from `key.properties` / the `OPERATIONAL_API_KEY` environment variable.

## Error handling

`tools/BaseDTO.kt` unwraps the standard envelope (`status`, `family`, `reason`, `data`, `problems`):

- `extractData()` — returns `data` or throws `TaminErrorUriException`
- `extractMessage()` — for endpoints that return only a message
- `extractDataOrProblems()` / `extractMessageOrProblems()` — return `ApiOutcome` instead of throwing when the backend sent a `problems` list

`tools/NetworkUtils.kt` holds `ErrorParser.safeCall(tag) { … }`, which wraps a call and normalizes failures. Note that its catch-all branch maps every unexpected exception to `NO_CONNECTION_ERROR`, and that `ErrorUri.fromString("CLIENT_ERROR: $reason")` never matches an enum name, so it always resolves to `UNKNOWN`.

## Logging

The `Logging` plugin uses Kermit with tags `KtorClient` / `KtorHealthClient`. Log level is driven by `AppConfig.isDebug`, and the `Authorization` header is stripped via `sanitizeHeader`.
`chucker` is also in the catalog (debug build / release no-op) for inspecting Android traffic.

## Testing

`core-network/src/commonTest/resources/mocks/` and `androidUnitTest/resources/mocks/` hold sample JSON (`certificate/`, `pension/`) used with `ktor-client-mock`.

Related: [[Dependency-Injection]] · [[Database]] · [[Overview]]
