---
tags: [architecture, domain, data]
---

# Home Content Unification & Offline-First

How the home screen's six data pieces — identity info, «تازه‌ها» stories, campaigns,
دسترسی سریع (quick access), خدمات ویژه (special services), and requests — were merged
into one offline-first pipeline, and the exact seam to cut when campaigns/quickAccess/
specialServices get real endpoints. Branch `Feature-EM-2642-home-quick-access`.

## Why

Before this work, `HomeRepositoryImpl` built each section from a different shape, and
the campaign/quick-access/special-services placeholder rows were hardcoded literals
sitting in `core-data` — which both violates `core-data` → `core-ui` layering (the
"real" versions of these sections are UI concepts defined in `core-ui`) and hardcodes
copy that has no server source yet. The goal: one cache record for the whole screen,
read instantly (offline-first), refreshed in the background, with zero literal display
strings anywhere in `core-data`.

## 1. One DN, one Entity, one cache row

[`HomeContentDN`](../../core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/model/home/HomeContentDN.kt)
(core-domain) is the single domain object for the whole screen:

```kotlin
data class HomeContentDN(
    val userInfo: UserInfoDN?,
    val stories: List<StoryChannelDN>?,
    val campaigns: List<CampaignDN>?,
    val quickAccess: List<QuickAccessDN>?,
    val specialServices: List<SpecialServiceDN>?,
    val requests: List<RequestDN>?
)

data class CampaignDN(val flag: FeatureFlag, val title: String, val bannerUrl: String?)
data class QuickAccessDN(val flag: FeatureFlag, val title: String, val iconUrl: String?)
data class SpecialServiceDN(val flag: FeatureFlag, val title: String, val iconUrl: String?)
```

`HomeContentEntity` (core-database) mirrors it as **one Room row** (fixed `id = 1`,
single-row cache — same pattern as `PersonalInboxSizeEntity.SINGLE_ROW_ID`, see
[[Data-and-Caching]]). Each field is `@Serializable` and stored as a single JSON TEXT
column via `TaminHamrahConverters`, so changing a nested field's shape (e.g. adding
`flagId` to `CampaignEntity`) does **not** require a Room schema version bump.

`CampaignEntity` / `QuickAccessEntity` / `SpecialServiceEntity` key on `flagId: Int`
(a `FeatureFlag.id`), not a freeform string id — that id is the join key back to real
display data (step 3).

## 2. Read side — cache only, instant

```
GetHomeContentUseCase() → HomeRepository.getHomeContent(): Flow<HomeContentDN?>
```

`HomeRepositoryImpl.getHomeContent()` just reads the one cached Room row and maps every
field to its DN. No network call. `HomeViewModel` collects this directly into
`HomeUiState.homeContent` — whatever was last synced renders immediately, even offline.

## 3. Write side — `syncHomeContent()`, one fetch per piece, all parallel

```
SyncHomeContentUseCase() → HomeRepository.syncHomeContent(): suspend fun
```

Triggered from `HomeViewModel.init` on every `tokenStoreManager.tokenValidFlow()` change
(login/logout/refresh), and swallowed on failure so a sync error never surfaces to the UI
— the cache (or nothing, for a fresh install) is the fallback.

Inside `HomeRepositoryImpl.syncHomeContent()`, each of the six pieces gets its own
`async { runCatching { ... } }` and falls back to whatever's already cached
(`currentContent?.xxx`) if its own fetch fails — one piece failing never blanks another:

| Piece | Real source today |
|---|---|
| **userInfo** | `UserRepository.getIdentityInfo()` / `getRelationTaminAll()` + `TreatmentRepository` |
| **stories** | `StoryRepository.getChannels()` (real repository; internally a bundled catalogue, see [[Stories]]) |
| **requests** | `UserRequestRepository.refreshUserRequests()` |
| **campaigns** | `CommonRepository.getMainMenu()` (mocked, see [[Feature-Flags]]) + `HomeContentPlaceholders.campaignFlags` |
| **quickAccess** | same menu + `HomeContentPlaceholders.quickAccessFlags` |
| **specialServices** | same menu + `HomeContentPlaceholders.specialServiceFlags` |

## 4. The placeholder mechanism — how campaign/quickAccess/specialServices avoid hardcoding

`userInfo`/`stories`/`requests` already have real endpoints (stories' underlying catalogue
is mocked, but the repository/DN shape is real — see [[Stories]]). The other three don't
have an endpoint yet, so instead of writing literal titles/icons in `core-data`, the code
resolves them from the same dynamic menu every other service name in the app already goes
through:

```
HomeContentPlaceholders (core-domain)      — decides WHICH FeatureFlags are featured
        │  campaignFlags / quickAccessFlags / specialServiceFlags
        ▼
HomeServiceMembership (core-domain)        — single source of truth for flag membership
        │  frequent / history / aid / pensioner / employer / featured
        ├──────────────► HomeServiceSection (core-ui) — same lists, home-screen section UI
        │
        ▼
menu.titleOf(flag)  — HomeRepositoryImpl looks up the display title by flag.id
        │             in the menu just fetched via CommonRepository.getMainMenu()
        ▼
CampaignEntity(flagId = flag.id, title = <from menu>, bannerUrl = null)
```

- [`HomeContentPlaceholders`](../../core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/repository/home/HomeContentPlaceholders.kt)
  holds **no strings** — only `FeatureFlag` lists.
- [`HomeServiceMembership`](../../core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/repository/home/HomeServiceMembership.kt)
  is the one place flag-to-section membership is defined; both `HomeContentPlaceholders`
  (core-data's fetch) and `HomeServiceSection` (core-ui's rendering, see the real
  "خدمات ویژه" section = `featured = [VIEW_TITLE_JOB, OCCURRENCE, REQUEST_FOR_PREGNANCY_PAY]`)
  reference it, so they cannot drift apart.
- `HomeRepositoryImpl.titleOf(flag)` (private extension on `List<MainServiceDN>`) is the
  only place a menu row's `name` becomes a placeholder row's `title`.

This is why `core-data` never imports anything from `core-ui`, and why no display copy for
these three sections is typed as a literal anywhere in `core-data` or `core-domain`.

## 5. Consumption

`HomeViewModel` depends on `GetHomeContentUseCase`/`SyncHomeContentUseCase`, **not**
`HomeRepository` directly (repository interfaces are injected into use cases only, per
[[MVI-Pattern]] / clean-architecture layering — a ViewModel importing a repository type is
a structural violation).

## 6. How to replace campaigns / quickAccess / specialServices with a real API later

When a real endpoint exists for one of these three, the change is localized to
`HomeRepositoryImpl.syncHomeContent()` — nothing in `core-domain`, `core-ui`, or the
ViewModel/UI needs to change:

1. Add a `XxxRemoteDataSource` call (core-network) for the new endpoint, returning a DTO
   with its own `id`/`title`/`iconUrl`/etc. straight from the wire.
2. In `syncHomeContent()`, replace the `menu?.let { ... HomeContentPlaceholders.xxxFlags ... }`
   block for that piece with `async { runCatching { xxxRemoteDataSource.getXxx() }.getOrNull() }`,
   mapped to the existing `XxxEntity` shape (`flagId` becomes whatever the response's own
   flag/id field is — or drop `flagId` and add real fields directly to the entity/DN if the
   response carries its own display data instead of being flag-keyed).
3. Delete the now-unused entries from `HomeContentPlaceholders` for that piece (leave the
   other two alone if they're still mocked).
4. `HomeContentDN`, `HomeContentEntity`, `HomeMapper`, `GetHomeContentUseCase`,
   `HomeViewModel`, and the UI (`HomeScreenContent`) are untouched — they already consume
   `CampaignDN`/`QuickAccessDN`/`SpecialServiceDN` as domain models, not as menu-derived
   placeholders.
5. If the new endpoint's rows are no longer `FeatureFlag`-keyed (e.g. campaigns get their
   own numeric id space), swap `CampaignEntity.flagId`/`CampaignDN.flag` for whatever key
   the API actually returns, and update `HomeMapper.toDomain()` accordingly — this is the
   only place the flag-based join lives.

`HomeServiceMembership` stays even after all three go live — it's still the source of
truth for which flags render in which **section** of the UI (`HomeServiceSection` in
core-ui); only the placeholder *selection-and-title-lookup* step in `core-data` goes away.

Related: [[Data-and-Caching]] · [[Feature-Flags]] · [[Stories]] · [[MVI-Pattern]] · [[Modules]]
