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

## Agent-shell Gradle invocations may need a loopback workaround

Running `.\gradlew.bat` from an agent/automated shell on Windows can fail with
`java.io.IOException: Unable to establish loopback connection` (JDK 21's
`Selector.open()` → `PipeImpl` → `UnixDomainSockets.connect0` rejecting the default
temp path). If a `gradlew` invocation fails with that exact error, set this before
retrying (PowerShell):

```powershell
$env:_JAVA_OPTIONS="-Djdk.net.unixdomain.tmpdir=C:\gtmp"
```

`C:\gtmp` (or any short existing path) must exist first. It must be `_JAVA_OPTIONS`,
not `GRADLE_OPTS` — the launcher, the daemon, and forked test workers all need to see
it. This is a machine/environment issue, not a repo one — Android Studio's own Gradle
runs are unaffected.

## No static analysis tooling exists — don't add one silently

There is no detekt, ktlint, or spotless configured anywhere in this repo (no Gradle
plugin, no config file), and CI ([[CI-CD]]) doesn't run any. The only enforcement
mechanism is the bespoke `checkNamingConvention` task ([[Naming-Conventions]], already
wired into `check`/`compileKotlin*`/`assemble*`, but currently a no-op). Don't
introduce detekt/ktlint/spotless as an unrequested "improvement" — that's a real
process decision for the team, not something to add mid-task.

## Testing stack — use what's actually in the project

`kotlin-test`, `kotlinx-coroutines-test`, and `turbine` are available — **no MockK, no
other mocking framework**. Match the existing style:

- `kotlinx.coroutines.test.runTest`
- `app.cash.turbine`'s `.test { awaitItem(); awaitError(); ... }`
- Hand-written `Fake*` classes implementing the relevant interface (see
  `core/core-domain/.../repository/personalInbox/FakePersonalInboxRepository.kt`, or
  the inner `FakeRemoteDataSource`/`FakeDao` classes in
  `core/core-data/.../PersonalInboxRepositoryImplTest.kt`) — not a mocking library.
- Reuse `BaseUseCaseTest` (core-domain, sets `Dispatchers.Main` via
  `StandardTestDispatcher`) and `BaseApiTest` (core-network, `createMockKtorfit(...)`
  via `io.ktor.client.engine.mock.MockEngine`) where applicable instead of duplicating
  setup.

Test coverage today is uneven — only a handful of chains have real tests. Don't assume
every repository/use case you touch already has tests to extend; check first.

## Repo root hygiene

The repo root tends to accumulate untracked scratch files (build logs, one-off
scripts, generated output) and untracked tooling directories from whichever AI/agent
tools were in use at the time. These are workspace artifacts, not repo content:

- Don't delete them without being asked — they may be in-progress work.
- Don't add new scratch/debug files to the repo root as a side effect of your own
  work — write temporary output to a scratch/temp location instead, or clean up after
  yourself.
- Note: a past commit (`33a2c8471`, "remove project automation, AI agent
  configurations, and IDE settings") deliberately stripped `.claude/`, `.obsidian/
  app.json`/workspace state, and `.vscode/settings.json` from git history — this repo
  has an explicit precedent of keeping AI-tool-specific configuration and IDE
  workspace state out of version control, while `docs/vault/*.md` (this vault's actual
  content) and root `CLAUDE.md` are treated as legitimate, trackable project
  documentation. Keep that distinction in mind before committing any new tool-specific
  config directory.

Related: [[CI-CD]] · [[Naming-Conventions]] · [[Tech-Stack]]
