# TaminX — تأمین همراه

A Kotlin Multiplatform + Compose Multiplatform app (Android + iOS) for the Iranian Social Security Organization.
`rootProject.name = "TaminX"` · base package: `com.tamin.taminhamrah`

## Read this first

Project knowledge lives in an Obsidian vault: **`docs/vault/`** — start at `docs/vault/Home.md`.
Read the relevant page there before searching the codebase:

| Question | Page |
|---|---|
| How are the layers and data flow organized? | `docs/vault/Overview.md` |
| Which module owns what? | `docs/vault/Modules.md` |
| How is a ViewModel written? | `docs/vault/MVI-Pattern.md` |
| How do I add a screen or feature? | `docs/vault/Adding-a-Feature.md` |
| Persian digits / typography | `docs/vault/Typography.md` |
| Theme tokens (color, size, copy) | `docs/vault/Theme.md` |
| Navigation | `docs/vault/Navigation.md` |
| Scroll-driven collapsing headers (fold/unfold on drag) | `docs/vault/TopArea-System.md` |
| DI and Koin | `docs/vault/Dependency-Injection.md` |
| Networking, tokens, endpoints | `docs/vault/Networking.md` |
| Payments (any feature, and the mock gateway) | `docs/vault/Payments.md` |
| Database and schemas | `docs/vault/Database.md` |
| Build, flavors, JDK | `docs/vault/Build-and-Run.md` |
| CI | `docs/vault/CI-CD.md` |
| Dynamic menu and FeatureFlag | `docs/vault/Feature-Flags.md` |
| AI assistant architecture | `docs/vault/AI-Agent.md` |
| AI assistant JSON contract | `docs/vault/AI-Agent-API-Contract.md` |
| Persian domain term ↔ name in code | `docs/vault/Glossary.md` |

All documentation is written in English. The vault uses Obsidian-style `[[…]]` links, which resolve to the file name without its extension.

## Rules that must not be broken

1. **File naming contract** — `core-domain/model/*DN.kt` · `core-network/model/*DTO.kt` ·
   `core-ui/model/*PR.kt` · `core-ui/mapper/*Mapper.kt` · `core-database/data/local/entity/*Entity.kt` ·
   `core-data/data/mapper/*Mapper.kt`
   ⚠️ `TaminHamrahNamingConventionPlugin` was meant to enforce this but **has a bug and always passes** — details in `docs/vault/Naming-Conventions.md`. Enforcement is manual.
2. Every ViewModel extends `BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>`; input arrives only through `sendIntent`.
3. Each feature's Koin module must be registered in `sharedModules` (`shared/.../di/Koin.kt`).
4. Navigation between two different features goes through a callback, never a direct import.
5. Search `core-ui/ui/components/` before building a new component (59 already exist).
6. **`old_android/` is not part of the project** — it is the legacy native version kept as the reference implementation the KMP rewrite is ported from. Read it to understand expected behaviour, but never edit or commit it (it is ignored). Details: `docs/vault/Reference-old-android.md`

## Common commands

```powershell
.\gradlew.bat :androidApp:assembleDirectDebug
.\gradlew.bat testDebugUnitTest testDirectDebugUnitTest
.\gradlew.bat :feature:<name>:compileDebugKotlinAndroid
```

JDK 17 · compileSdk 36 · minSdk 24 · iOS builds only on macOS.

## Maintaining the vault

When you learn something that was not visible in the code, or that took real time to discover, update the relevant page in `docs/vault/`.
