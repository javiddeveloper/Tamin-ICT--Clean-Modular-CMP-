---
tags: [architecture]
---

# Architecture Overview

`rootProject.name = "TaminX"` — defined in `settings.gradle.kts`.
Base package everywhere: `com.tamin.taminhamrah`

## Layers

```
androidApp / iosApp          ← platform hosts (Activity, SwiftUI, widgets)
        ↓
      shared                 ← MainApp, NavGraph, Koin bootstrap, Home
        ↓
     feature:*               ← 13 independent feature modules (UI + ViewModel + Navigation + DI)
        ↓
  core-ui                    ← design system, BaseViewModel, PR models, DN→PR mappers
  core-data                  ← Repository implementations, DTO↔DN↔Entity mappers
  core-domain                ← DN models, Repository interfaces, UseCases, NetworkConstants
  core-network               ← Ktorfit ApiServices, RemoteDataSources, DTOs, HTTP clients
  core-database              ← Room (Entity/DAO/Converter)
  core-datastore             ← multiplatform-settings (tokens and preferences)
  core-plugin                ← plugin registry (theme, export, workflow)
```

Dependencies always point downward. `core-domain` depends on nothing above it.

## How a request flows

```
Screen (Compose)
  → sendIntent(Intent)            ← BaseViewModel
    → UseCase                     ← core-domain
      → Repository (interface)    ← core-domain
        → RepositoryImpl          ← core-data
          → RemoteDataSource      ← core-network
            → ApiService (Ktorfit)
          → DAO                   ← core-database
        ↑ Mapper: DTO → DN        ← core-data/data/mapper
      ↑ Mapper: DN → PR           ← core-ui/mapper
  → PartialState → reduceState → StateFlow<State>
```

Four model families that must never be mixed — see [[Naming-Conventions]]:

| Suffix | Layer | Role |
|---|---|---|
| `*DTO.kt` | core-network | exact shape of the server JSON |
| `*DN.kt` | core-domain | domain model, independent of server and UI |
| `*PR.kt` | core-ui | presentation model, ready to render |
| `*Entity.kt` | core-database | Room table |

## Things worth knowing up front

- All UI lives in `commonMain`. `androidApp` only holds the Activity plus a few native capabilities (camera, ML Kit, Glance widget, ExoPlayer).
- The `shared` module owns `TaminHamrahNavGraph.kt`, `MainApp.kt`, `HomeViewModel` and the list of Koin modules.
- KMP targets: `androidTarget`, `iosArm64`, `iosSimulatorArm64` — declared in `TaminHamrahKmpLibraryPlugin`.

Related: [[Modules]] · [[MVI-Pattern]] · [[Networking]]
