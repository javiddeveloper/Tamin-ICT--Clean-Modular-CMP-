---
tags: [architecture]
---

# Database — Room KMP

Room `2.7.0-beta01` + bundled `androidx.sqlite` + KSP. Runs on all three targets (android / iosArm64 / iosSimulatorArm64).

## Files

```
core-database/src/commonMain/kotlin/com/tamin/taminhamrah/data/local/
├── TaminHamrahDatabase.kt        ← @Database
├── LocalDataClearerImpl.kt       ← full wipe (logout)
├── TimeProvider.kt
├── converter/TaminHamrahConverters.kt
├── dao/      AgentChat, Branch, CityProvince, Contract, Health, Menu, Personal,
│             PersonalInbox, Recipient, RegistrationInfo, Treatment, User,
│             UserRequest, VersionHistory, Test
└── entity/   AgentChatEntities, BranchEntity, CityEntity, ContractEntity,
              HealthEntities, IdentityInfoEntity, MenuEntity, PersonalInboxEntity,
              PersonalInfoEntity, ProvinceEntity, RecipientEntity,
              RegistrationInfoEntity, TestEntity, TreatmentEntities,
              UserRequestEntity, VersionHistoryEntity
```

DI: `core-database/.../di/DatabaseModule.kt` → `databaseModule`

## Schemas

`core-database/schemas/` still contains folders named after older database classes:

```
com.tamin.taminhamrah.data.local.TaminXDatabase       ← active
com.tamin.taminhamrah.data.local.TaminHamrahDatabase
com.tamin.taminhamrah.data.local.AppDatabase / MyDatabase / TestDatabase
com.omooooori… / com.riox432…                          ← leftovers from the original template
```

⚠️ Any Entity change generates the next version's JSON in the schema folder, and that file must be committed. The active schema is currently at `TaminXDatabase/2.json`.

⚠️ Inconsistency worth fixing: that path is listed in `.gitignore`, but the file is already tracked — so the ignore rule has no effect and the file keeps showing up as modified. Either drop it from `.gitignore` (schemas should be committed) or untrack it with `git rm --cached`.

## What the database is for

Offline cache for: the service menu, identity and personal information, branches and city/province lists, the personal inbox, contracts, version history, and AI assistant chat history.

Related: [[Networking]] · [[Overview]] · [[Naming-Conventions]]
