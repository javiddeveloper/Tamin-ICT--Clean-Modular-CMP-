---
tags: [build]
---

# Build and Run

## Prerequisites

- **JDK 17** — `jvmToolchain(17)`, `JvmTarget.JVM_17`. The path used by `build.bat`:
  `%USERPROFILE%\.jdks\corretto-17.0.17` (the IDE's bundled JBR 17 also works).
- `compileSdk = 36`, `buildToolsVersion = "36.0.0"`, `minSdk = 24`
- Internal Maven repositories: `https://nexus.tamin.ir/content/groups/public` and `https://maven.myket.ir/` — dependency resolution fails without access to the Tamin network.
- `android.builder.sdkDownload=false` → the Android SDK must already be installed.

## Commands

```powershell
.\gradlew.bat :androidApp:assembleDirectDebug        # debug build, default working flavor
.\gradlew.bat :androidApp:assembleFlavorTestDebug    # against the test servers
.\gradlew.bat testDebugUnitTest testDirectDebugUnitTest   # what CI runs
.\gradlew.bat :feature:profile:compileDebugKotlinAndroid  # quick single-module check
.\gradlew.bat checkNamingConvention                  # naming check (currently a no-op, see [[Naming-Conventions]])
```

`build.bat` is a shortcut: it sets JAVA_HOME and runs `assembleDebug`.

iOS is built only on macOS with Xcode (`iosApp/iosApp.xcodeproj`); it cannot be built on this Windows machine.

## Flavors

Dimension: `taminHamrah`

| Flavor | applicationId | Difference |
|---|---|---|
| `direct` | `com.tamin.taminhamrah` | direct distribution |
| `caffeBazaar` | same | Cafe Bazaar |
| `myket` | same | Myket |
| `flavorTest` | same | overrides endpoints to the test servers, uses `TEST_API_KEY` |
| `reporter` | `com.tamin.taminhamrahreporter` | "reporting" build, independent version `1.0.0` |

The `debug` build type appends `.debug` to the applicationId.
Current app version: `versionCode = 7`, `versionName = "2.2.0"`.

Release output file name:
`Tamin_ICT_<versionCode>_<versionName>-(<flavor>).apk`

## Keys and signing

- `key.properties` at the repo root (not in git): `OPERATIONAL_API_KEY`, `TEST_API_KEY`. If missing, values fall back to environment variables and finally to an empty string.
- Release signing comes from environment variables: `RELEASE_KEYSTORE`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`. Without a keystore the release build is produced unsigned.
- `isMinifyEnabled = false` in release — ProGuard is not active.

## Convention plugins

`build-logic/convention/src/main/kotlin/`

| Plugin | id | What it does |
|---|---|---|
| `TaminHamrahKmpLibraryPlugin` | `TaminHamrah.kmp.library` | KMP + android library, JDK 17, iOS targets |
| `TaminHamrahKmpComposePlugin` | `TaminHamrah.kmp.compose` | Compose Multiplatform |
| `TaminHamrahKmpFeaturePlugin` | `TaminHamrah.kmp.feature` | library + compose + all core modules + Koin + turbine |
| `TaminHamrahAndroidApplicationPlugin` | `TaminHamrah.android.application` | the Android app |
| `TaminHamrahNamingConventionPlugin` | `TaminHamrah.naming.convention` | [[Naming-Conventions]] — currently a no-op |

## Notes

- `gradle.properties`: 4 GB heap, caching and parallel builds on, `kotlin.native.ignoreDisabledTargets=true` so the iOS targets do not block Windows builds.
- `build-logic/convention/bin/` is the IDE's compiled copy — the source of truth is `src/main/kotlin/`.
- `hs_err_pid*.log` and `replay_pid*.log` at the repo root are leftovers from an earlier JVM crash.

Related: [[CI-CD]] · [[Naming-Conventions]] · [[Tech-Stack]]
