---
tags: [architecture]
---

# تزریق وابستگی — Koin

نسخه: Koin `4.1.0` (+ `koin-annotations` 2.1.0 در catalog موجود است ولی الگوی غالب DSL دستی است).

## نقطه‌ی شروع

`shared/src/commonMain/kotlin/com/tamin/taminhamrah/di/Koin.kt`

```kotlin
val sharedModules: List<Module> get() = listOf(
    platformModule, networkModule, datastoreModule, databaseModule,
    ApiClientsModule, remoteModule, domainModule, dataKoinModule, dataModule, pluginModule,
    agentModule, profileModule, pensionInquiryModule, treatmentModule, cartableModule,
    historyModule, contractsModule, TaminServicesModule, workshopsModule,
    studentInsuranceContractModule, healthProfileModule, changeMobileModule, myInboxModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin { appDeclaration(); modules(sharedModules) }
```

**افزودن فیچر جدید = افزودن ماژول Koin آن به همین لیست.** فراموش کردنش خطای runtime می‌دهد نه compile.

## ماژول‌ها و محل تعریفشان

| ماژول | فایل |
|---|---|
| `platformModule` | `shared/.../di/PlatformModule.kt` (expect/actual) |
| `dataModule` | `shared/.../di/DataModule.kt` |
| `networkModule` | `core-network/.../di/NetworkKoinModule.kt` |
| `ApiClientsModule` | `core-network/.../di/ApiClientsModule.kt` |
| `remoteModule` | `core-network/.../di/RemoteModule.kt` |
| (query builder) | `core-network/.../di/ApiQueryBuilderModule.kt` |
| `databaseModule` | `core-database/.../di/DatabaseModule.kt` |
| `datastoreModule` | `core-datastore/.../di/DataStoreModule.kt` |
| `dataKoinModule` | `core-data/.../data/di/DataKoinModule.kt` |
| `pluginModule` | `core-plugin/.../plugin/di/PluginModule.kt` |
| `<x>Module` | `feature/<x>/.../di/<X>Module.kt` |

`domainModule` توسط core-domain تأمین می‌شود (UseCaseها).

## qualifierها

HttpClientها با `named(...)` از هم جدا می‌شوند — رجوع به [[Networking]]:

```
"authHttpClient"  "mainHttpClient"  "healthHttpClient"  "uploadHttpClient"  "aiHttpClient"
```

و یک ApiService با qualifier: `get(named("authUserApiService"))`.

هنگام تزریق HttpClient حتماً qualifier بده؛ `get<HttpClient>()` بدون نام قابل ابهام است.

## در Compose

```kotlin
val viewModel = koinViewModel<XViewModel>()   // koin-compose-viewmodel
```

مرتبط: [[Networking]] · [[Modules]] · [[Adding-a-Feature]]
