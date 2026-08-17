---
tags: [reference]
---

# Tech Stack

Source of truth for versions: `gradle/libs.versions.toml`. Only the essentials are repeated here.

## Core

| | Version |
|---|---|
| Kotlin | 2.1.20 |
| AGP | 8.9.3 |
| Compose Multiplatform | 1.10.3 |
| Compose Material3 | 1.7.3 |
| KSP | 2.1.20-2.0.1 |
| JDK / target | 17 |
| compileSdk / minSdk | 36 / 24 |

## Main libraries

| Area | Library |
|---|---|
| Networking | Ktor 3.1.3, Ktorfit 2.5.2, kotlinx-serialization 1.8.1 |
| DI | Koin 4.1.0 (+ koin-annotations 2.1.0) |
| Database | Room 2.7.0-beta01, bundled androidx-sqlite |
| Preferences | multiplatform-settings 1.3.0 |
| Navigation | jetbrains navigation-compose 2.9.0-beta03 (+ navigation3 1.0.0 in androidApp) |
| Lifecycle/VM | androidx-lifecycle 2.9.0, jetbrains lifecycle 2.9.1 |
| Images | Coil 3.2.0 (compose, ktor, svg) |
| Logging | Kermit 2.1.0 |
| iOS interop | SKIE 0.10.11 |
| Firebase | GitLive 2.1.0 (analytics, crashlytics, performance) |
| Testing | kotlin-test, coroutines-test, Turbine 1.2.0, ktor-client-mock |

## Supporting libraries

`filekit` (file picking), `qrose` + zxing + ML Kit barcode (QR), `calf-permissions` and `moko-permissions`, `connectivity` (network state), `haze` (blur), `constraintlayout-compose-multiplatform`, `material3-window-size-class-multiplatform`, `aboutlibraries`, `chucker` (debug network inspection), `media3` (ExoPlayer), `glance` (Android widget), `onnxruntime-android` behind `AppConfig.FEATURE_SIMILARITY_SEARCH` (semantic search, currently `false`), `wire` (protobuf).

## A note on the version catalog

The catalog contains many duplicate entries (both `room-runtime` and `androidx-room-runtime`, both `coil-compose` and `coil-kt-compose`, and so on). When adding a dependency, check which alias the neighbouring modules already use and reuse that one.

Related: [[Build-and-Run]] · [[Overview]]
