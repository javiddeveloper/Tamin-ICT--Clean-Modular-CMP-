---
tags: [build]
---

# ساخت و اجرا

## پیش‌نیاز

- **JDK 17** — `jvmToolchain(17)`، `JvmTarget.JVM_17`. مسیرِ استفاده‌شده در `build.bat`:
  `%USERPROFILE%\.jdks\corretto-17.0.17`
  (JBR 17 همراه IDE هم جواب می‌دهد.)
- `compileSdk = 36`, `buildToolsVersion = "36.0.0"`, `minSdk = 24`
- مخازن Maven داخلی: `https://nexus.tamin.ir/content/groups/public` و `https://maven.myket.ir/` — بدون دسترسی به شبکه‌ی تأمین، resolve نمی‌شود.
- `android.builder.sdkDownload=false` → SDK باید از قبل نصب باشد.

## دستورها

```powershell
.\gradlew.bat :androidApp:assembleDirectDebug        # ساخت debug (flavor پیش‌فرض کاری)
.\gradlew.bat :androidApp:assembleFlavorTestDebug    # علیه سرورهای تست
.\gradlew.bat testDebugUnitTest testDirectDebugUnitTest   # همان چیزی که CI اجرا می‌کند
.\gradlew.bat :feature:profile:compileDebugKotlinAndroid  # چک سریع یک ماژول
.\gradlew.bat checkNamingConvention                  # فقط بررسی نام‌گذاری
```

`build.bat` میان‌بر است: JAVA_HOME را ست می‌کند و `assembleDebug` می‌زند.

iOS فقط روی macOS با Xcode (`iosApp/iosApp.xcodeproj`) ساخته می‌شود؛ روی این ماشین ویندوزی قابل build نیست.

## flavorها

بُعد: `taminHamrah`

| flavor | applicationId | تفاوت |
|---|---|---|
| `direct` | `com.tamin.taminhamrah` | انتشار مستقیم |
| `caffeBazaar` | همان | کافه‌بازار |
| `myket` | همان | مایکت |
| `flavorTest` | همان | override آدرس‌ها به سرور تست + `TEST_API_KEY` |
| `reporter` | `com.tamin.taminhamrahreporter` | نسخه‌ی «گزارش‌گیری»، ورژن مستقل `1.0.0` |

buildType `debug` پسوند `.debug` به applicationId می‌زند.
نسخه‌ی فعلی اپ: `versionCode = 7`, `versionName = "2.2.0"`.

نام فایل خروجی release:
`Tamin_ICT_<versionCode>_<versionName>-(<flavor>).apk`

## کلیدها و امضا

- `key.properties` در ریشه (در گیت نیست): `OPERATIONAL_API_KEY`, `TEST_API_KEY`. اگر نبود، از متغیر محیطی خوانده می‌شود و در نهایت رشته‌ی خالی.
- امضای release از متغیرهای محیطی: `RELEASE_KEYSTORE`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`. اگر keystore نباشد، release بدون امضا ساخته می‌شود.
- `isMinifyEnabled = false` در release — ProGuard فعال نیست.

## پلاگین‌های convention

`build-logic/convention/src/main/kotlin/`

| پلاگین | id | کارش |
|---|---|---|
| `TaminHamrahKmpLibraryPlugin` | `TaminHamrah.kmp.library` | KMP + android library، JDK 17، targetهای iOS |
| `TaminHamrahKmpComposePlugin` | `TaminHamrah.kmp.compose` | Compose Multiplatform |
| `TaminHamrahKmpFeaturePlugin` | `TaminHamrah.kmp.feature` | library + compose + همه‌ی coreها + Koin + turbine |
| `TaminHamrahAndroidApplicationPlugin` | `TaminHamrah.android.application` | اپ اندروید |
| `TaminHamrahNamingConventionPlugin` | — | [[Naming-Conventions]] |

## نکات

- `gradle.properties`: heap ۴ گیگ، caching و parallel روشن، `kotlin.native.ignoreDisabledTargets=true` (تا روی ویندوز targetهای iOS مانع نشوند).
- پوشه‌ی `build-logic/convention/bin/` کپی کامپایل‌شده‌ی IDE است — منبع حقیقت `src/main/kotlin/` است.
- فایل‌های `hs_err_pid*.log` و `replay_pid*.log` در ریشه، بازمانده‌ی crash قبلی JVM هستند.

مرتبط: [[CI-CD]] · [[Naming-Conventions]] · [[Tech-Stack]]
