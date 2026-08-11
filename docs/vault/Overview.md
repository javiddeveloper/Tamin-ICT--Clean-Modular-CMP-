---
tags: [architecture]
---

# تصویر کلی معماری

`rootProject.name = "TaminX"` — تعریف در `settings.gradle.kts`.
package base در همه‌جا: `com.tamin.taminhamrah`

## لایه‌ها

```
androidApp / iosApp          ← میزبان پلتفرم (Activity، SwiftUI، ویجت)
        ↓
      shared                 ← MainApp، NavGraph، Koin bootstrap، Home
        ↓
     feature:*               ← ۱۳ ماژول فیچر مستقل (UI + ViewModel + Navigation + DI)
        ↓
  core-ui                    ← دیزاین‌سیستم، BaseViewModel، مدل‌های PR، مپرهای PR
  core-data                  ← پیاده‌سازی Repository + مپرهای DTO↔DN↔Entity
  core-domain                ← مدل‌های DN، اینترفیس Repository، UseCaseها، NetworkConstants
  core-network               ← Ktorfit ApiService، RemoteDataSource، DTOها، HttpClientها
  core-database              ← Room (Entity/DAO/Converter)
  core-datastore             ← multiplatform-settings (توکن و ترجیحات)
  core-plugin                ← رجیستری پلاگین (تم، export، workflow)
```

جهت وابستگی همیشه رو به پایین است. `core-domain` به هیچ لایه‌ی بالاتری وابسته نیست.

## جریان یک درخواست

```
Screen (Compose)
  → sendIntent(Intent)            ← BaseViewModel
    → UseCase                     ← core-domain
      → Repository (interface)    ← core-domain
        → RepositoryImpl          ← core-data
          → RemoteDataSource      ← core-network
            → ApiService (Ktorfit)
          → DAO                   ← core-database
        ↑ Mapper: DTO → DN        ← core-data/data/mapper
      ↑ Mapper: DN → PR           ← core-ui/mapper
  → PartialState → reduceState → StateFlow<State>
```

سه خانواده‌ی مدل که هرگز نباید قاطی شوند — رجوع به [[Naming-Conventions]]:

| پسوند | لایه | نقش |
|---|---|---|
| `*Dto.kt` | core-network | شکل دقیق JSON سرور |
| `*DN.kt` | core-domain | مدل دامنه، مستقل از سرور و UI |
| `*PR.kt` | core-ui | مدل presentation، آماده‌ی رندر |
| `*Entity.kt` | core-database | جدول Room |

## نکات مهم

- کل UI در `commonMain` است؛ `androidApp` فقط Activity و چند قابلیت بومی (دوربین، ML Kit، Glance widget، ExoPlayer) دارد.
- ماژول `shared` مالک `TaminHamrahNavGraph.kt`، `MainApp.kt`، `HomeViewModel` و لیست ماژول‌های Koin است.
- targetهای KMP: `androidTarget`، `iosArm64`، `iosSimulatorArm64` (تعریف در `TaminHamrahKmpLibraryPlugin`).

مرتبط: [[Modules]] · [[MVI-Pattern]] · [[Networking]]
