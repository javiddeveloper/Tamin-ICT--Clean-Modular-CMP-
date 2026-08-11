---
tags: [architecture]
---

# ماژول‌ها

منبع حقیقت: `settings.gradle.kts`

## core

| ماژول | مسیر | مسئولیت | نکته |
|---|---|---|---|
| `:core:core-domain` | `core/core-domain` | مدل `*DN.kt`، اینترفیس Repository، UseCaseها، `NetworkConstants` | ~۲۰ زیرپکیج `useCases/` |
| `:core:core-network` | `core/core-network` | ApiServiceهای Ktorfit، `*RemoteDataSource(+Impl)`، `*Dto.kt`، ساخت HttpClientها | [[Networking]] |
| `:core:core-database` | `core/core-database` | Room: `TaminHamrahDatabase`, DAO، Entity، Converter | [[Database]] |
| `:core:core-data` | `core/core-data` | `*RepositoryImpl`، `data/mapper/*Mapper.kt`، `FeatureManagerImpl` | لایه‌ی چسب |
| `:core:core-datastore` | `core/core-datastore` | `UserPreferencesRepositoryImpl`، `TokenStoreManagerImpl` | multiplatform-settings |
| `:core:core-ui` | `core/core-ui` | دیزاین‌سیستم، `BaseViewModel`، `*PR.kt`، `mapper/*Mapper.kt` | [[MVI-Pattern]] |
| `:core:core-plugin` | `core/core-plugin` | `PluginRegistry`، `ThemePlugin`، `ExportFormatPlugin`، `WorkflowEnginePlugin` | معماری افزونه‌ای |

### داخل core-ui

```
ui/theme/       Color, SemanticColors, Shape, Type, Motion, Shimmer, TaminHamrahTheme
ui/components/  ۵۹ فایل — TaminTopAppBar, TaminText, TaminJalaliDatePicker,
                TaminPdfViewer, LoadingButton, ErrorStateView, SegmentedRadialGauge, …
ui/blur/  ui/image/  ui/motion/  ui/system/  ui/util/
base/           BaseViewModel.kt
model/          مدل‌های *PR به تفکیک دامنه
mapper/         مپرهای DN → PR
```

قبل از ساختن هر کامپوننت جدید، اول `core-ui/ui/components/` را بگرد — احتمالش زیاد است که موجود باشد.

## feature

⚠️ نام پوشه با نام پکیج یکی نیست. جدول تبدیل:

| ماژول Gradle | پوشه | پکیج |
|---|---|---|
| `:feature:profile` | `feature/profile` | `…feature.profile` |
| `:feature:treatment` | `feature/treatment` | `…feature.treatment` |
| `:feature:pensioner` | `feature/pensioner` | `…feature.pensionInquiry` ⚠️ |
| `:feature:cartable` | `feature/cartable` | `…feature.cartable` |
| `:feature:history` | `feature/history` | `…feature.history` |
| `:feature:contracts` | `feature/contracts` | `…feature.contracts` |
| `:feature:workshops` | `feature/workshops` | `…feature.workshops` |
| `:feature:studentInsuranceContract` | `feature/studentInsuranceContract` | `…feature.studentInsuranceContract` |
| `:feature:agent` | `feature/agent` | `…feature.agent` |
| `:feature:healthProfile` | `feature/healthProfile` | `…feature.healthProfile` |
| `:feature:taminServices` | `feature/taminServices` | `…feature.taminServices` |
| `:feature:change-mobile` | `feature/change-mobile` | `…feature.changemobile` ⚠️ |
| `:feature:my-inbox` | `feature/my-inbox` | `…feature.myinbox` ⚠️ |

### ساختار داخلی هر فیچر

الگوی مرجع: `feature/profile`

```
feature/<x>/src/commonMain/kotlin/com/tamin/taminhamrah/feature/<x>/
├── Navigation.kt              ← sealed interface <X>Route + fun NavGraphBuilder.<x>Graph()
├── di/<X>Module.kt            ← val <x>Module = module { … }
└── ui/
    ├── <Screen>Screen.kt
    ├── <Screen>ViewModel.kt
    ├── contract/<Screen>Contract.kt   ← State / PartialState / Event / Intent
    ├── components/…                   ← Composableهای مختصِ همین صفحه
    └── model/…                        ← مدل‌های UI مختصِ همین فیچر
```

هر ماژول فیچر پلاگین `TaminHamrah.kmp.feature` را می‌گیرد که خودکار هر پنج ماژول core + Koin + coroutines + (در تست) turbine را وصل می‌کند — نیازی به اضافه کردن دستی نیست.

## اپ‌ها

- `:androidApp` — `com.tamin.taminhamrah`، مالک `BuildConfig`، flavorها و قابلیت‌های بومی.
- `iosApp/` — پروژه‌ی Xcode (خارج از Gradle)، شامل `TaminHamrahWidget` و پوشه‌های `ML/`، `Services/`.

مرتبط: [[Adding-a-Feature]] · [[Build-and-Run]]
