---
tags: [reference]
---

# استک فنی

منبع حقیقت نسخه‌ها: `gradle/libs.versions.toml`. اینجا فقط موارد کلیدی.

## هسته

| | نسخه |
|---|---|
| Kotlin | 2.1.20 |
| AGP | 8.9.3 |
| Compose Multiplatform | 1.10.3 |
| Compose Material3 | 1.7.3 |
| KSP | 2.1.20-2.0.1 |
| JDK / target | 17 |
| compileSdk / minSdk | 36 / 24 |

## کتابخانه‌های اصلی

| حوزه | کتابخانه |
|---|---|
| شبکه | Ktor 3.1.3، Ktorfit 2.5.2، kotlinx-serialization 1.8.1 |
| DI | Koin 4.1.0 (+ koin-annotations 2.1.0) |
| دیتابیس | Room 2.7.0-beta01، androidx-sqlite bundled |
| ذخیره‌سازی | multiplatform-settings 1.3.0 |
| ناوبری | jetbrains navigation-compose 2.9.0-beta03 (+ navigation3 1.0.0 در androidApp) |
| Lifecycle/VM | androidx-lifecycle 2.9.0، jetbrains lifecycle 2.9.1 |
| تصویر | Coil 3.2.0 (compose، ktor، svg) |
| لاگ | Kermit 2.1.0 |
| iOS interop | SKIE 0.10.11 |
| Firebase | GitLive 2.1.0 (analytics، crashlytics، performance) |
| تست | kotlin-test، coroutines-test، Turbine 1.2.0، ktor-client-mock |

## ابزارهای جانبی

`filekit` (انتخاب فایل)، `qrose` + zxing + ML Kit barcode (QR)، `calf-permissions` و `moko-permissions`، `connectivity` (وضعیت شبکه)، `haze` (بلور)، `constraintlayout-compose-multiplatform`، `material3-window-size-class-multiplatform`، `aboutlibraries`، `chucker` (بازرسی شبکه debug)، `media3` (ExoPlayer)، `glance` (ویجت اندروید)، `onnxruntime-android` + `AppConfig.FEATURE_SIMILARITY_SEARCH` (جست‌وجوی معنایی، فعلاً `false`)، `wire` (protobuf).

## نکته درباره‌ی version catalog

مدخل‌های تکراری زیادی وجود دارد (مثلاً هم `room-runtime` هم `androidx-room-runtime`، هم `coil-compose` هم `coil-kt-compose`). هنگام افزودن dependency، اول ببین کدام alias را ماژول‌های همسایه استفاده می‌کنند و همان را بردار.

مرتبط: [[Build-and-Run]] · [[Overview]]
