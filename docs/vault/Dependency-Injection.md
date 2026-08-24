---
tags: [architecture]
---

# Dependency Injection — Koin

Koin `4.1.0`. (`koin-annotations` 2.1.0 is in the catalog, but hand-written DSL is the dominant pattern.)

## Entry point

`shared/src/commonMain/kotlin/com/tamin/taminhamrah/di/Koin.kt`

```kotlin
val sharedModules: List<Module> get() = listOf(
    platformModule, networkModule, datastoreModule, databaseModule,
    ApiClientsModule, remoteModule, domainModule, dataKoinModule, dataModule, pluginModule,
    agentModule, profileModule, pensionInquiryModule, pensionStatusInquiryModule, treatmentModule, cartableModule,
    historyModule, contractsModule, TaminServicesModule, workshopsModule,
    studentInsuranceContractModule, healthProfileModule, changeMobileModule, myInboxModule,
    securityModule, addDependentModule, settingsModule, userRequestModule, orotezProtezModule,
    girlSurvivorModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin { appDeclaration(); modules(sharedModules) }
```

**Adding a feature means adding its Koin module to this list.** Forgetting it produces a runtime failure, not a compile error.

UseCases are not auto-discovered. A new constructor dependency on a ViewModel also needs `factoryOf(::ThatUseCase)` in `domainModule` (`core-domain/.../di/DomainModule.kt`). Missing that yields `InstanceCreationException: Could not create instance for '[Factory: …ViewModel]'`.

## Where each module lives

| Module | File |
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

`domainModule` is provided by core-domain (the UseCases).

## Qualifiers

HTTP clients are distinguished by `named(...)` — see [[Networking]]:

```
"authHttpClient"  "mainHttpClient"  "healthHttpClient"  "uploadHttpClient"  "aiHttpClient"
```

And one qualified ApiService: `get(named("authUserApiService"))`.

Always pass a qualifier when injecting an `HttpClient`; a bare `get<HttpClient>()` is ambiguous.

## In Compose

```kotlin
val viewModel = koinViewModel<XViewModel>()   // koin-compose-viewmodel
```

Related: [[Networking]] · [[Modules]] · [[Adding-a-Feature]]
