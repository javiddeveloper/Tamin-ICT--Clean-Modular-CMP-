---
tags: [architecture]
---

# شبکه

استک: **Ktor 3.1.3** + **Ktorfit 2.5.2** (interfaceهای annotation-based با KSP) + kotlinx-serialization.

## ساختار core-network

```
apiService/     interfaceهای Ktorfit — Common, User, History, WorkShops, VersionHistory,
                agent/, contract/, health/, inbox/, pension/, personal/, treatment/, userRequest/
dataSource/     <X>RemoteDataSource (interface) + <X>RemoteDataSourceImpl
model/          *Dto.kt — شکل خام JSON
di/             NetworkKoinModule, ApiClientsModule, RemoteModule, ApiQueryBuilderModule
constant/       HeaderConstant, TimeoutConstant
```

هر RemoteDataSourceImpl یک `ErrorParser` می‌گیرد (`ErrorParserImpl` در `networkModule`) و پاسخ خطا را به مدل داخلی تبدیل می‌کند. `expectSuccess = false` است، یعنی Ktor روی 4xx/5xx exception پرت نمی‌کند و مدیریت خطا دستی است.

## پنج HttpClient

تعریف در `core-network/.../di/NetworkKoinModule.kt`، همه با qualifier:

| qualifier | baseUrl | Auth plugin | timeout |
|---|---|---|---|
| `mainHttpClient` | `NetworkConstants.BASE_URL` | ✅ bearer + refresh | ۶۰ ثانیه |
| `authHttpClient` | `BASE_URL` | ❌ (برای endpointهای توکن) | ۶۰ ثانیه |
| `healthHttpClient` | `BASE_URL_HEALTH_PROFILE` | ❌ | ۶۰ ثانیه |
| `uploadHttpClient` | `BASE_URL` | ✅ | ۵ دقیقه |
| `aiHttpClient` | `AI_BASE_URL` | ✅ + `AiChatTokenPlugin` | ۶۰ ثانیه |

## جریان توکن

```
loadTokens    → authRepository.getAccessToken()
refreshTokens → authRepository.refreshToken() → getAccessToken()
```

⚠️ نکته‌ای که در کد کامنت هم شده: `BearerAuthProvider` بعد از اولین درخواست، توکن را cache می‌کند و دیگر `loadTokens` را صدا نمی‌زند. به همین دلیل `AuthTokenInvalidator` وجود دارد:

```kotlin
client.authProviders.filterIsInstance<BearerAuthProvider>().forEach {
    authTokenInvalidator.registerClearAction { it.clearToken() }
}
```

هنگام login/logout باید invalidator صدا زده شود، وگرنه توکن قدیمی می‌ماند.

`refreshToken` در `BearerTokens` عمداً `""` پاس می‌شود — مدیریت واقعی refresh token داخل `AuthRepository` است.

## آدرس‌ها

`core-domain/.../util/NetworkConstants.kt` — منبع حقیقت برای کد مشترک:

```kotlin
BASE_URL                = "https://eservices.tamin.ir/api/"
BASE_URL_VIEW           = "https://eservices.tamin.ir/view/"
BASE_URL_ACCOUNT        = "https://account.tamin.ir/auth/"
BASE_URL_HEALTH_PROFILE = "http://172.16.14.115:5700/api/"   // IP داخلی
AI_BASE_URL             = "https://sw.tamin.ir/api/"
REDIRECT_URI            = "mytamin://login"
DEFAULT_AUDIENCE        = "https://es.tamin.ir,https://eservices.tamin.ir"
REQUEST_TIMEOUT_60_SEC = 60_000L   REQUEST_TIMEOUT_5_MIN = 300_000L
```

⚠️ آدرس‌ها **دو جا** تعریف شده‌اند: همین فایل، و `buildConfigField`های `androidApp/build.gradle.kts`. flavor `flavorTest` آدرس‌های تست را override می‌کند ولی `NetworkConstants` این را نمی‌بیند. اگر آدرسی عوض شد، هر دو جا را بررسی کن. رجوع به [[Build-and-Run]].

⚠️ `CLIENT_ID` در `NetworkConstants` هاردکد شده و در `androidApp` از `key.properties` / متغیر محیطی `OPERATIONAL_API_KEY` می‌آید.

## لاگ

`Logging` plugin با Kermit، تگ `KtorClient` / `KtorHealthClient`. سطح لاگ با `AppConfig.isDebug` کنترل می‌شود و هدر `Authorization` با `sanitizeHeader` پاک می‌شود.
`chucker` هم در version catalog هست (debug/release no-op) برای بازرسی ترافیک اندروید.

## تست

`core-network/src/commonTest/resources/mocks/` و `androidUnitTest/resources/mocks/` فایل‌های JSON نمونه دارند (`certificate/`، `pension/`) — با `ktor-client-mock` استفاده می‌شوند.

مرتبط: [[Dependency-Injection]] · [[Database]] · [[Overview]]
