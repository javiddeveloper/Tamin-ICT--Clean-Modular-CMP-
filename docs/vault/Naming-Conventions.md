---
tags: [convention, gotcha]
---

# قواعد نام‌گذاری

فایل: `build-logic/convention/src/main/kotlin/TaminHamrahNamingConventionPlugin.kt`
اعمال‌شده روی: `core-domain`, `core-network`, `core-ui`, `core-database`, `core-data` (با `id("TaminHamrah.naming.convention")` در `build.gradle.kts` هرکدام).

## ⚠️ این چک در عمل کار نمی‌کند (باگ)

پلاگین task ای به نام `checkNamingConvention` می‌سازد و به `check` / `assemble*` / `compileKotlin*` وصلش می‌کند، **ولی همیشه بی‌صدا pass می‌شود**.

علت: داخل بلاک `tasks.register("...") { doLast { ... } }` گیرنده‌ی `path` خودِ **Task** است نه Project. یعنی مقدارش `:core:core-network:checkNamingConvention` است، نه `:core:core-network`. در نتیجه:

```kotlin
path.endsWith("core-network") // → false برای همه‌ی ماژول‌ها
→ conventions = emptyList()
→ return@doLast     // هیچ فایلی بررسی نمی‌شود
```

شاهد تجربی: `core-network/model/` هم‌اکنون ۲۰ فایل دارد که به `Dto.kt` ختم نمی‌شوند (`ErrorDTO.kt`, `ErrorDataDTO.kt`, `ActiveRelationDTO.kt`, …) و با این حال:

```
> Task :core:core-network:checkNamingConvention
BUILD SUCCESSFUL
```

**نتیجه‌ی عملی:** قواعد زیر یک *قرارداد تیمی* هستند، نه چیزی که build تضمینش کند. خودت باید رعایتشان کنی و در review نگاهشان کنی.

**اگر خواستی درستش کنی:** `path` را با `project.path` جایگزین کن. توجه: بعد از این اصلاح، build فوراً روی ده‌ها فایل موجود می‌شکند — چون قرارداد واقعی کدبیس `DTO.kt` (سه‌حرفی بزرگ) است ولی پلاگین `Dto.kt` می‌خواهد. یا پلاگین را با واقعیت هماهنگ کن، یا فایل‌ها را rename کن.

## جدول قواعد

| ماژول | پوشه‌ی تحت نظارت | پسوند اجباری فایل |
|---|---|---|
| `core-domain` | `model/` | `DN.kt` |
| `core-network` | `model/` | `Dto.kt` |
| `core-ui` | `model/` | `PR.kt` |
| `core-ui` | `mapper/` | `Mapper.kt` |
| `core-database` | `data/local/entity/` | `Entity.kt` |
| `core-data` | `data/mapper/` | `Mapper.kt` |

مسیر پایه‌ای که پلاگین می‌گردد:
`src/commonMain/kotlin/com/tamin/taminhamrah/<folder>` و همچنین `…/com/tamin/taminx/<folder>` (نام قدیمی).

مسیر پایه‌ای که پلاگین (در صورت تعمیر) می‌گردد:
`src/commonMain/kotlin/com/tamin/taminhamrah/<folder>` و `…/com/tamin/taminx/<folder>` (نام قدیمی)، به‌صورت `walkTopDown` یعنی شامل همه‌ی زیرپوشه‌ها.

## واقعیت فعلی کدبیس

- DTOها در عمل با **`DTO.kt`** نوشته می‌شوند (`ErrorDTO.kt`, `AddDependentDTO.kt`) نه `Dto.kt`. هنگام افزودن فایل جدید از همسایه‌هایش تقلید کن.
- در `core-ui/model/` بهتر است فقط `*PR.kt` بگذاری؛ ثابت‌ها و helperها جایشان `util/` است.

## سایر قواعدِ غیراجباری ولی رایج

- ViewModel: `<Screen>ViewModel.kt` · Screen: `<Screen>Screen.kt` · Contract: `contract/<Screen>Contract.kt`
- DataSource: `<X>RemoteDataSource.kt` + `<X>RemoteDataSourceImpl.kt`
- Repository: interface در core-domain (`repository/<domain>/`)، پیاده‌سازی `<X>RepositoryImpl` در core-data
- ماژول Koin فیچر: `val <x>Module` — استثنا: `TaminServicesModule` با حرف بزرگ شروع می‌شود
- کامپوننت‌های عمومی core-ui معمولاً پیشوند `Tamin` دارند: `TaminText`, `TaminTopAppBar`, `TaminPdfViewer`

مرتبط: [[Overview]] · [[Adding-a-Feature]] · [[Build-and-Run]]
