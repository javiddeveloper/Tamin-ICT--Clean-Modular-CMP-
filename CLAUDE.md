# TaminX — تأمین همراه

اپ Kotlin Multiplatform + Compose Multiplatform (اندروید + iOS) برای سازمان تأمین اجتماعی.
`rootProject.name = "TaminX"` · package base: `com.tamin.taminhamrah`

## اول اینجا را بخوان

دانش پروژه در یک vault ابسیدین جمع شده است: **`docs/vault/`** — نقطه‌ی شروع `docs/vault/Home.md`.
به‌جای گشتن در کدبیس، اول صفحه‌ی مرتبط را از آنجا بخوان:

| سؤال | صفحه |
|---|---|
| لایه‌ها و جریان داده چطور است؟ | `docs/vault/Overview.md` |
| کدام ماژول مسئول چیست؟ | `docs/vault/Modules.md` |
| ViewModel چطور نوشته می‌شود؟ | `docs/vault/MVI-Pattern.md` |
| صفحه/فیچر جدید چطور اضافه کنم؟ | `docs/vault/Adding-a-Feature.md` |
| ناوبری | `docs/vault/Navigation.md` |
| DI و Koin | `docs/vault/Dependency-Injection.md` |
| شبکه، توکن، آدرس سرورها | `docs/vault/Networking.md` |
| دیتابیس و schema | `docs/vault/Database.md` |
| build، flavor، JDK | `docs/vault/Build-and-Run.md` |
| CI | `docs/vault/CI-CD.md` |
| منوی داینامیک و FeatureFlag | `docs/vault/Feature-Flags.md` |
| اصطلاح فارسی ↔ نام در کد | `docs/vault/Glossary.md` |

اسناد قدیمی‌تر و مفصل‌تر: `documents/features.md`، `documents/agent.md`، `documents/agent-api-contract.md`

## Code Review

هر درخواست review روی یک merge request، از فرایند **`review/README.md`** پیروی می‌کند — قبل از شروع بخوانش.
خلاصه: گزارش در `review/MR-<id>.md` نوشته و روی **branch مبدأ همان MR** commit می‌شود؛ توسعه‌دهنده اصلاح می‌کند؛ همان فایل بند به بند به‌روز می‌شود؛ در پایان فایل حذف می‌شود.
پنج قاعده: (۱) **گزارش review کاملاً به انگلیسی نوشته می‌شود**؛ (۲) working tree کاربر دست نمی‌خورد — از `git worktree` استفاده کن نه `git checkout`؛ (۳) commit بررسی‌شده در header گزارش ثبت شود؛ (۴) `✅ fixed` فقط بعد از خواندن کد؛ (۵) **merge به `develop` کار reviewer نیست.**

## قواعدی که نباید نقض شوند

1. **قرارداد نام‌گذاری فایل** — `core-domain/model/*DN.kt` · `core-network/model/*DTO.kt` ·
   `core-ui/model/*PR.kt` · `core-ui/mapper/*Mapper.kt` · `core-database/data/local/entity/*Entity.kt` ·
   `core-data/data/mapper/*Mapper.kt`
   ⚠️ `TaminHamrahNamingConventionPlugin` قرار بوده این را اجبار کند ولی **باگ دارد و همیشه pass می‌شود** — جزئیات در `docs/vault/Naming-Conventions.md`. پس رعایتش دستی است.
2. هر ViewModel از `BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>` ارث می‌برد؛ ورودی فقط از `sendIntent`.
3. ماژول Koin هر فیچر باید در `sharedModules` (فایل `shared/.../di/Koin.kt`) ثبت شود.
4. ناوبری بین دو فیچر مختلف با callback انجام می‌شود، نه import مستقیم.
5. قبل از ساخت کامپوننت جدید، `core-ui/ui/components/` را بگرد (۵۹ کامپوننت آماده).
6. **`old_android/` جزئی از پروژه نیست** — نسخه‌ی native قدیمی است که به‌عنوان پیاده‌سازی مرجع نگه داشته شده و KMP از رویش port می‌شود. برای فهمیدن رفتار مورد انتظار بخوانش، ولی ویرایش و commit نکن (ignore شده). جزئیات: `docs/vault/Reference-old-android.md`

## دستورهای رایج

```powershell
.\gradlew.bat :androidApp:assembleDirectDebug
.\gradlew.bat testDebugUnitTest testDirectDebugUnitTest
.\gradlew.bat :feature:<name>:compileDebugKotlinAndroid
```

JDK 17 · compileSdk 36 · minSdk 24 · iOS فقط روی macOS ساخته می‌شود.

## نگه‌داری vault

وقتی چیزی یاد گرفتی که در کد پیدا نبود یا وقت زیادی برای کشفش صرف شد، صفحه‌ی مربوطه در `docs/vault/` را به‌روز کن. لینک‌های `[[…]]` سبک ابسیدین‌اند و با نام فایل بدون پسوند کار می‌کنند.
