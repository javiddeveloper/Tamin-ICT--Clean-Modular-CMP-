---
tags: [build]
---

# CI/CD — GitLab

فایل: `.gitlab-ci.yml` · ریموت: `https://ci.tamin.ir/base/development/finance/tatmin-kmp.git`
ایمیج: `pull-devops.tamin.ir/devops/android-sdk-image:1.0` · tag رانر: `devops`

## مراحل

| stage | job | trigger | دستور |
|---|---|---|---|
| test | `android-test` | MR, `main`, `develop` | `./gradlew testDebugUnitTest testDirectDebugUnitTest` |
| build | `android-build-debug` | `main`, `develop` | `assembleDirectDebug` + `assembleFlavorTestDebug` |
| release | `android-release` | فقط tag | assemble release چهار flavor |
| release | `create-release` | فقط tag | آپلود APKها به Package Registry + ساخت GitLab Release |

## before_script

- `GRADLE_USER_HOME=$(pwd)/.gradle`
- `key.properties` از متغیرهای CI (`OPERATIONAL_API_KEY`, `TEST_API_KEY`) ساخته می‌شود اگر وجود نداشته باشد.
- `RELEASE_KEYSTORE_BASE64` دیکد و در `release.jks` نوشته می‌شود.
- ⚠️ یک **shim برای `xargs`** ساخته و به ابتدای PATH اضافه می‌شود — چون ایمیج xargs واقعی ندارد. اگر build در CI طوری شکست که با محلی فرق داشت، این یکی از مظنون‌هاست.

## شاخه‌ها

- `main` — شاخه‌ی اصلی (مقصد PR)
- `develop` — شاخه‌ی جاری توسعه؛ فیچرها با الگوی `Feature-EM-<شماره>-<عنوان>` ساخته و در `develop` مرج می‌شوند.

## artifacts

- تست: `**/build/reports/` (۱ هفته)
- debug: `androidApp/build/outputs/apk/*/debug/*.apk` (۱ هفته)
- release: APK هر چهار flavor با نام `TaminX-$CI_COMMIT_TAG`

مرتبط: [[Build-and-Run]]
