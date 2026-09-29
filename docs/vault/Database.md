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

⚠️ Any Entity change generates the next version's JSON in the schema folder, and that file must be committed. The active schema is currently at `TaminXDatabase/6.json` (`TaminXDatabase` `@Database version = 6`; version 5 changed `FeatureFlag` ids, so cached `home_content` rows stored under the old `flagId`s needed to be dropped; version 6 added `construction_file_pages`).

⚠️ **Adding or changing an entity without bumping `version`** rewrites the existing `N.json` in place. Existing installs then crash on open with Room's identity-hash check (destructive fallback does not cover a same-version mismatch). The build also fails in `:core:core-database:copyRoomSchemas` with *"Inconsistency detected exporting Room schema files"* once the debug and release KSP outputs for the same version differ. The fix is to bump `version`, not to delete the build directory. Migration is destructive (`fallbackToDestructiveMigration`), so a version bump wipes the local cache. Do not ignore `core/core-database/schemas/` — Room schema files belong in git.

## What the database is for

Offline cache for: the service menu, identity and personal information, branches and city/province lists, the personal inbox, contracts, version history, and AI assistant chat history.

Related: [[Networking]] · [[Overview]] · [[Naming-Conventions]]
