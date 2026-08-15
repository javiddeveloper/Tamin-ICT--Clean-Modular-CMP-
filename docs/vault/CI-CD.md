---
tags: [build]
---

# CI/CD — GitLab

File: `.gitlab-ci.yml` · Remote: `https://ci.tamin.ir/base/development/finance/tatmin-kmp.git`
Image: `pull-devops.tamin.ir/devops/android-sdk-image:1.0` · Runner tag: `devops`

## Stages

| Stage | Job | Trigger | Command |
|---|---|---|---|
| test | `android-test` | MR, `main`, `develop` | `./gradlew testDebugUnitTest testDirectDebugUnitTest` |
| build | `android-build-debug` | `main`, `develop` | `assembleDirectDebug` + `assembleFlavorTestDebug` |
| release | `android-release` | tags only | release assemble for all four flavors |
| release | `create-release` | tags only | upload APKs to the Package Registry, create a GitLab Release |

## before_script

- `GRADLE_USER_HOME=$(pwd)/.gradle`
- `key.properties` is generated from the CI variables (`OPERATIONAL_API_KEY`, `TEST_API_KEY`) if it does not exist.
- `RELEASE_KEYSTORE_BASE64` is decoded into `release.jks`.
- ⚠️ A **shim for `xargs`** is written and prepended to PATH, because the image has no real `xargs`. If a CI build fails in a way that does not reproduce locally, this is one of the suspects.

## Branches

- `main` — the main branch (default PR target)
- `develop` — the active integration branch; features use the pattern `Feature-EM-<number>-<title>` and are merged into `develop`.

## Artifacts

- test: `**/build/reports/` (1 week)
- debug: `androidApp/build/outputs/apk/*/debug/*.apk` (1 week)
- release: APKs for all four flavors, named `TaminX-$CI_COMMIT_TAG`

Related: [[Build-and-Run]]
