---
tags: [convention, gotcha]
---

# Naming Conventions

Plugin: `build-logic/convention/src/main/kotlin/TaminHamrahNamingConventionPlugin.kt`
Applied to: `core-domain`, `core-network`, `core-ui`, `core-database`, `core-data` (each declares `id("TaminHamrah.naming.convention")` in its `build.gradle.kts`).

## ⚠️ The check does not actually work

The plugin registers a `checkNamingConvention` task and hooks it to `check`, `assemble*` and `compileKotlin*` — but **it always passes silently**.

Cause: inside `tasks.register("...") { doLast { ... } }` the receiver for `path` is the **Task**, not the Project. So its value is `:core:core-network:checkNamingConvention`, not `:core:core-network`. As a result:

```kotlin
path.endsWith("core-network") // → false for every module
→ conventions = emptyList()
→ return@doLast     // nothing is ever inspected
```

Empirical proof: `core-network/model/` currently holds 20 files that do not end in `Dto.kt` (`ErrorDTO.kt`, `ErrorDataDTO.kt`, `ActiveRelationDTO.kt`, …), and yet:

```
> Task :core:core-network:checkNamingConvention
BUILD SUCCESSFUL
```

**Practical consequence:** the rules below are a *team convention*, not something the build guarantees. Follow them by hand and check them during review.

**If you want to fix it:** replace `path` with `project.path`. Be aware the build will then immediately fail on dozens of existing files, because the codebase's real convention is `DTO.kt` (three capitals) while the plugin expects `Dto.kt`. Either align the plugin with reality or rename the files.

## The rules

| Module | Watched folder | Required suffix |
|---|---|---|
| `core-domain` | `model/` | `DN.kt` |
| `core-network` | `model/` | `Dto.kt` (in practice: `DTO.kt`) |
| `core-ui` | `model/` | `PR.kt` |
| `core-ui` | `mapper/` | `Mapper.kt` |
| `core-database` | `data/local/entity/` | `Entity.kt` |
| `core-data` | `data/mapper/` | `Mapper.kt` |

Base paths the plugin would scan: `src/commonMain/kotlin/com/tamin/taminhamrah/<folder>` and `…/com/tamin/taminx/<folder>` (the old name), walked recursively so subfolders are included.

## What the codebase actually does

- DTOs are written as **`DTO.kt`** (`ErrorDTO.kt`, `AddDependentDTO.kt`), not `Dto.kt`. Match the neighbouring files when adding a new one.
- Keep `core-ui/model/` to `*PR.kt` only; constants and helpers belong in `util/`.

## Other conventions, not enforced but consistent

- ViewModel: `<Screen>ViewModel.kt` · Screen: `<Screen>Screen.kt` · Contract: `contract/<Screen>Contract.kt`
- DataSource: `<X>RemoteDataSource.kt` + `<X>RemoteDataSourceImpl.kt`
- Repository: interface in core-domain (`repository/<domain>/`), implementation `<X>RepositoryImpl` in core-data
- Feature Koin module: `val <x>Module` — one exception, `TaminServicesModule`, starts with a capital
- Shared core-ui components are usually prefixed `Tamin`: `TaminText`, `TaminTopAppBar`, `TaminPdfViewer`

Related: [[Overview]] · [[Adding-a-Feature]] · [[Build-and-Run]]
