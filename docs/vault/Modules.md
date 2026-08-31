---
tags: [architecture]
---

# Modules

Source of truth: `settings.gradle.kts`

## core

| Module | Path | Owns | Notes |
|---|---|---|---|
| `:core:core-domain` | `core/core-domain` | `*DN.kt` models, Repository interfaces, UseCases, `NetworkConstants` | ~20 sub-packages under `useCases/` |
| `:core:core-network` | `core/core-network` | Ktorfit ApiServices, `*RemoteDataSource(+Impl)`, `*DTO.kt`, HTTP client construction | [[Networking]] |
| `:core:core-database` | `core/core-database` | Room: `TaminHamrahDatabase`, DAOs, Entities, Converters | [[Database]] |
| `:core:core-data` | `core/core-data` | `*RepositoryImpl`, `data/mapper/*Mapper.kt`, `FeatureManagerImpl` | the glue layer |
| `:core:core-datastore` | `core/core-datastore` | `UserPreferencesRepositoryImpl`, `TokenStoreManagerImpl` | multiplatform-settings |
| `:core:core-ui` | `core/core-ui` | design system, `BaseViewModel`, `*PR.kt`, `mapper/*Mapper.kt` | [[MVI-Pattern]] |
| `:core:core-plugin` | `core/core-plugin` | `PluginRegistry`, `ThemePlugin`, `ExportFormatPlugin`, `WorkflowEnginePlugin` | plugin architecture |

### Inside core-ui

```
ui/theme/       Color, SemanticColors, Shape, Type, Motion, Shimmer, TaminHamrahTheme
ui/components/  59 files — TaminTopAppBar, TaminText, TaminJalaliDatePicker,
                TaminPdfViewer, LoadingButton, ErrorStateView, SegmentedRadialGauge, …
ui/blur/  ui/image/  ui/motion/  ui/system/  ui/util/
base/           BaseViewModel.kt
model/          *PR models, grouped by domain
mapper/         DN → PR mappers
```

Search `core-ui/ui/components/` before building any new component — the odds are good it already exists.

Typography / Persian digits: [[Typography]] — theme `ss01` is visual; `toPersianDigits()` changes the string.

Colors, spacing, radius: [[Theme]] — never hardcode `Color`, `.dp`, or UI copy in features.

## feature

⚠️ Folder names do not match package names. Translation table:

| Gradle module | Folder | Package |
|---|---|---|
| `:feature:profile` | `feature/profile` | `…feature.profile` |
| `:feature:treatment` | `feature/treatment` | `…feature.treatment` |
| `:feature:pensioner` | `feature/pensioner` | `…feature.pensionInquiry` ⚠️ |
| `:feature:cartable` | `feature/cartable` | `…feature.cartable` |
| `:feature:history` | `feature/history` | `…feature.history` |
| `:feature:contracts` | `feature/contracts` | `…feature.contracts` |
| `:feature:contractFlow` | `feature/contractFlow` | `…feature.contractFlow` — shared insurance contract wizard |
| `:feature:studentContract` | `feature/studentContract` | `…feature.studentContract` |
| `:feature:housewifeContract` | `feature/housewifeContract` | `…feature.housewifeContract` |
| `:feature:freelanceContract` | `feature/freelanceContract` | `…feature.freelanceContract` |
| `:feature:optionalContract` | `feature/optionalContract` | `…feature.optionalContract` |
| `:feature:workshops` | `feature/workshops` | `…feature.workshops` |
| `:feature:agent` | `feature/agent` | `…feature.agent` |
| `:feature:healthProfile` | `feature/healthProfile` | `…feature.healthProfile` |
| `:feature:taminServices` | `feature/taminServices` | `…feature.taminServices` |
| `:feature:change-mobile` | `feature/change-mobile` | `…feature.changemobile` ⚠️ |
| `:feature:my-inbox` | `feature/my-inbox` | `…feature.myinbox` ⚠️ |
| `:feature:addDependent` | `feature/addDependent` | `…feature.addDependent` |
| `:feature:orotez-protez` | `feature/orotez-protez` | `…feature.orotezprotez` |
| `:feature:deferredInstallment` | `feature/deferredInstallment` | `…feature.deferredInstallment` |
| `:feature:pensionStatusInquiry` | `feature/pensionStatusInquiry` | `…feature.pensionStatusInquiry` |
| `:feature:girlSurvivor` | `feature/girlSurvivor` | `…feature.girlSurvivor` |
| `:feature:pensionSurvivor` | `feature/pensionSurvivor` | `…feature.pensionSurvivor` |

### Layout of a feature module

Reference implementation: `feature/profile`

```
feature/<x>/src/commonMain/kotlin/com/tamin/taminhamrah/feature/<x>/
├── Navigation.kt              ← sealed interface <X>Route + fun NavGraphBuilder.<x>Graph()
├── di/<X>Module.kt            ← val <x>Module = module { … }
└── ui/
    ├── <Screen>Screen.kt
    ├── <Screen>ViewModel.kt
    ├── contract/<Screen>Contract.kt   ← State / PartialState / Event / Intent
    ├── components/…                   ← Composables specific to this screen
    └── model/…                        ← UI models specific to this feature
```

Every feature module applies the `TaminHamrah.kmp.feature` plugin, which wires in all five core modules plus Koin, coroutines and (for tests) turbine automatically — do not add them by hand.

## Apps

- `:androidApp` — `com.tamin.taminhamrah`, owns `BuildConfig`, product flavors and native capabilities.
- `iosApp/` — Xcode project (outside Gradle), including `TaminHamrahWidget` and the `ML/` and `Services/` folders.

Related: [[Adding-a-Feature]] · [[Build-and-Run]]
