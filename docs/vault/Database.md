---
tags: [architecture]
---

# پایگاه داده — Room KMP

Room `2.7.0-beta01` + `androidx.sqlite` bundled + KSP. کار روی هر سه target (android / iosArm64 / iosSimulatorArm64).

## فایل‌ها

```
core-database/src/commonMain/kotlin/com/tamin/taminhamrah/data/local/
├── TaminHamrahDatabase.kt        ← @Database
├── LocalDataClearerImpl.kt       ← پاک‌سازی کامل (logout)
├── TimeProvider.kt
├── converter/TaminHamrahConverters.kt
├── dao/      AgentChat, Branch, CityProvince, Contract, Health, Menu, Personal,
│             PersonalInbox, Recipient, RegistrationInfo, Treatment, User,
│             UserRequest, VersionHistory, Test
└── entity/   AgentChatEntities, BranchEntity, CityEntity, ContractEntity,
              HealthEntities, IdentityInfoEntity, MenuEntity, PersonalInboxEntity,
              PersonalInfoEntity, ProvinceEntity, RecipientEntity,
              RegistrationInfoEntity, TreatmentEntities, UserRequestEntity,
              VersionHistoryEntity, TestEntity
```

DI: `core-database/.../di/DatabaseModule.kt` → `databaseModule`

## Schemaها

`core-database/schemas/` — چند پوشه با نام کلاس‌های قدیمی هم باقی مانده:

```
com.tamin.taminhamrah.data.local.TaminXDatabase       ← فعال
com.tamin.taminhamrah.data.local.TaminHamrahDatabase
com.tamin.taminhamrah.data.local.AppDatabase / MyDatabase / TestDatabase
com.omooooori… / com.riox432…                          ← بازمانده‌ی template اولیه
```

⚠️ هر تغییری در Entity، فایل JSON نسخه‌ی بعدی را در پوشه‌ی schema تولید می‌کند و باید commit شود. نسخه‌ی فعلی روی `TaminXDatabase/2.json` است.

## نقش دیتابیس

کش آفلاین برای: منوی سرویس‌ها، اطلاعات هویتی/شخصی، شعب و شهر/استان، صندوق پیام، قراردادها، تاریخچه‌ی نسخه، و تاریخچه‌ی چت دستیار هوشمند.

مرتبط: [[Networking]] · [[Overview]] · [[Naming-Conventions]]
