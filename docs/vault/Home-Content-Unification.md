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

// isOpenable/status/group let the UI gate/dim exactly like the live menu does when it
// renders from this cache instead — see step 5, "the UI actually reads this now".
data class CampaignDN(val flag: FeatureFlag, val title: String, val bannerUrl: String?, val isOpenable: Boolean)
data class QuickAccessDN(val flag: FeatureFlag, val title: String, val iconUrl: String?, val group: HomeQuickAccessGroup, val status: MenuServiceStatusDN?)
data class SpecialServiceDN(val flag: FeatureFlag, val title: String, val iconUrl: String?, val status: MenuServiceStatusDN?)
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
`async { safeCall { ... } }` and falls back to whatever's already cached
(`currentContent?.xxx`) if its own fetch fails — one piece failing never blanks another.
`safeCall` is a private helper (`try { block() } catch (e: CancellationException) { throw e }
catch (e: Exception) { null }`) — **not** `runCatching { }`, which would also swallow
`CancellationException` and let a torn-down scope silently finish writing a stale cache row.

| Piece | Real source today |
|---|---|
| **userInfo** | `UserRepository.getIdentityInfo()` / `getRelationTaminAll()` + `TreatmentRepository` |
| **stories** | `StoryRepository.getChannels()` (real repository; internally a bundled catalogue, see [[Stories]]) |
| **requests** | `UserRequestRepository.refreshUserRequests()` |
| **campaigns** | `CommonRepository.getMainMenu()` (mocked, see [[Feature-Flags]]) + `HomeContentPlaceholders.campaignFlags` |
| **quickAccess** | same menu + `HomeContentPlaceholders.quickAccessGroups` (all 5 «دسترسی سریع» chip sections, not just FREQUENT) |
| **specialServices** | same menu + `HomeContentPlaceholders.specialServiceFlags` |

## 4. The placeholder mechanism — how campaign/quickAccess/specialServices avoid hardcoding

`userInfo`/`stories`/`requests` already have real endpoints (stories' underlying catalogue
is mocked, but the repository/DN shape is real — see [[Stories]]). The other three don't
have an endpoint yet, so instead of writing literal titles/icons in `core-data`, the code
resolves them from the same dynamic menu every other service name in the app already goes
through:

```
HomeContentPlaceholders (core-domain)      — decides WHICH FeatureFlags are featured
        │  campaignFlags / quickAccessGroups (5 sections) / specialServiceFlags
        ▼
HomeServiceMembership (core-domain)        — single source of truth for flag membership
        │  frequent / history / aid / pensioner / employer / featured
        ├──────────────► HomeServiceSection (core-ui) — same lists, home-screen section UI
        │                (HomeQuickAccessGroup, also core-domain, tags each cached
        │                 quick-access row by name-matching HomeServiceSection's 5
        │                 QUICK_ACCESS-placement entries — core-domain can't reference
        │                 core-ui's enum directly)
        ▼
byId[flag.id]  — HomeRepositoryImpl looks up the display title/icon/status by flag.id
        │        in the menu just fetched via CommonRepository.getMainMenu()
        ▼
CampaignEntity(flagId = flag.id, title = <from menu>, bannerUrl = null, isOpenable = <from featureStatusOf>)
```

- [`HomeContentPlaceholders`](../../core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/repository/home/HomeContentPlaceholders.kt)
  holds **no strings** — only `FeatureFlag` lists/maps, plus `HomeQuickAccessGroup`, the
  small enum that tags which of the 5 quick-access chip sections a cached row belongs to.
- [`HomeServiceMembership`](../../core/core-domain/src/commonMain/kotlin/com/tamin/taminhamrah/repository/home/HomeServiceMembership.kt)
  is the one place flag-to-section membership is defined; both `HomeContentPlaceholders`
  (core-data's fetch) and `HomeServiceSection` (core-ui's rendering, see the real
  "خدمات ویژه" section = `featured = [VIEW_TITLE_JOB, OCCURRENCE, REQUEST_FOR_PREGNANCY_PAY]`)
  reference it, so they cannot drift apart.
- `status`/`isOpenable` are read off the same menu row (`MainServiceDN.status`,
  `featureStatusOf(flag).opensSomething`) at cache-write time, so cache-sourced rendering
  can gate/dim exactly like the live menu path did before this existed.
- The three `menu -> entities` builders (`buildCampaignEntities`/`buildQuickAccessEntities`/
  `buildSpecialServiceEntities`) are `internal` top-level functions in `HomeRepositoryImpl.kt`,
  not private lambdas inside `syncHomeContent()` — pulled out specifically so tests can call them
  directly with a hand-built `menu` list. Going through `syncHomeContent()` itself to exercise
  this logic doesn't work in a plain JVM unit test: it needs `AppConfig.versionName`, whose
  Android `actual` reads a Koin-registered `Context` (`GlobalContext.get().get<Context>()`,
  unguarded) that a plain unit test has no way to provide short of a Robolectric+Koin bootstrap.
  Keep new `menu`-derived logic in these functions (or ones like them) rather than back inline.

This is why `core-data` never imports anything from `core-ui`, and why no display copy for
these three sections — nor the header's fallback name — is typed as a literal anywhere in
`core-data` or `core-domain`. `UserInfoDN.fullName`/`UserInfoEntity.fullName` are `String?`:
`HomeRepositoryImpl` leaves it `null` when the identity fetch/cache genuinely has no name,
rather than injecting a literal `core-data` can't localize (it has no Compose-resources plugin,
so it cannot reach `Res.string.*`). `HomeScreen.kt` resolves the localized fallback
(`Res.string.home_header_fallback_name`) at the point it builds `HomeHeader`'s `fullName` prop
— careful to only do this once `homeContent` itself is non-null, since `HomeHeader` treats a
`null` `fullName` as "still loading" (shows a shimmer); a blank *name* is a different state
from *no data yet* and must not collapse into the same shimmer.

## 5. Consumption — the UI actually reads this now

`HomeViewModel` depends on `GetHomeContentUseCase`/`SyncHomeContentUseCase`, **not**
`HomeRepository` directly (repository interfaces are injected into use cases only, per
[[MVI-Pattern]] / clean-architecture layering — a ViewModel importing a repository type is
a structural violation).

`HomeScreen.kt` renders campaigns/quickAccess/specialServices **from `uiState.homeContent`**,
via three core-ui mapper functions that reconstruct what the live-menu path used to build
directly:

- `List<CampaignDN>.toCampaignKinds()` (`mapper/campaign/CampaignMapper.kt`) — filters
  `isOpenable`, matches each row back to a `CampaignKind` by `.flag`.
- `List<QuickAccessDN>.toHomeSections()` (`mapper/home/HomeServiceSectionMapper.kt`) —
  groups by `HomeQuickAccessGroup`, matched to `HomeServiceSection` by enum-entry name,
  reconstructing a minimal `MainServiceDN` per row (only `id`/`name`/`icon`/`status` — a tap
  only ever needs `id`, the rest of the gating is re-resolved live via `FeatureManager`).
- `List<SpecialServiceDN>.toMainServices()` — same reconstruction, no grouping.

There is no second, independent `getMainMenuUseCase()` call for these sections anymore —
that was the original bug this section describes fixing: the sync computed and cached this
data, but the UI rendered from a separate live menu fetch instead, so the cache was dead
weight and the menu was fetched twice per home load. `HomeIntent.LoadMenu`/`MenuLoaded` and
the `menuItems`/`campaigns` `HomeUiState` fields were removed along with it; the empty-state
retry button now sends `HomeIntent.Retry`, which re-triggers `SyncHomeContentUseCase`.

## 6. How to replace campaigns / quickAccess / specialServices with a real API later

When a real endpoint exists for one of these three, the change is localized to
`HomeRepositoryImpl.syncHomeContent()` — nothing in `core-domain`, `core-ui`, or the
ViewModel/UI needs to change:

1. Add a `XxxRemoteDataSource` call (core-network) for the new endpoint, returning a DTO
   with its own `id`/`title`/`iconUrl`/etc. straight from the wire.
2. In `syncHomeContent()`, replace the `menu?.let { ... HomeContentPlaceholders.xxxFlags ... }`
   block for that piece with `async { safeCall { xxxRemoteDataSource.getXxx() } }`, mapped to
   the existing `XxxEntity` shape (`flagId` becomes whatever the response's own flag/id field
   is — or drop `flagId` and add real fields directly to the entity/DN if the response carries
   its own display data instead of being flag-keyed).
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
