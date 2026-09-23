---
tags: [architecture, domain]
---

# Feature Flags and the Dynamic Menu

How services are fetched, categorized, gated and routed. Absorbed from the former `documents/features.md`.

## 1. Menu data — `MainServiceDto`

The home screen's services arrive dynamically from a web service in the data layer (currently via `MockMenuData`) as `MainServiceDto`. Key fields:

| Field | Meaning |
|---|---|
| `id` | unique service id (e.g. 35 for contract affairs) |
| `name` | display name |
| `showRole` | array of role numbers (e.g. `[1, 2]`) controlling who sees the service |
| `status` | current service state (active, disabled, webview, …) |
| `message` | error message to display when the service is unavailable |
| `url` | target link when the service is a webview |

`menu.json` at the repo root is a local sample of this structure.

## 2. User roles via `showRole`

| Value | Audience | Examples |
|---|---|---|
| `1` | insured persons | merged history, contract affairs, e-prescription |
| `2` | pensioners | payslip, pension status inquiry, e-prescription |
| `3` | employers | workshops, inspections |

In the UI these are filtered by a dropdown at the top of `HomeScreen`. If the selected group's number appears in a service's `showRole`, that service is shown. This lets shared services (like e-prescription) appear for several roles without duplicating the id server-side.

## 3. Service states (`MenuServiceStatus` / `FeatureStatus`)

| State | UI behaviour |
|---|---|
| `ACTIVE` | fully enabled; tapping opens the native screen |
| `TEMPORARY_DISABLED` | temporarily down; card is dimmed to 50% alpha, tap is blocked, reason shown under the name |
| `DISABLED` / `COMPLETELY_DISABLED` | disabled entirely or for this specific user (e.g. not eligible for the marriage grant); looks the same as temporarily disabled |
| `ENABLED_WITH_ERROR` | opens normally and is not dimmed, but a red server warning is shown under the name |
| `WEB_VIEW` | no native screen; the `url` opens in an external browser or an in-app webview |

## 4. Server id → client enum (`FeatureFlag`)

The UI layer must not depend on hardcoded numeric ids, so every server id is mapped to a dedicated enum in `FeatureFlag.kt`.

- `FeatureFlag.fromId(id)` converts a server number into the enum.
- Using an enum instead of a raw number prevents human error and keeps `when` expressions exhaustive.

## 5. Routing and `FeatureManager`

What happens when a user taps a service (for example "housewives' insurance"):

1. **Compose UI** — the user taps a service card.
2. **ViewModel** — the tap is delivered as an `Intent` (e.g. `OnServiceClick`) to the relevant ViewModel (`HomeViewModel`, `ContractsViewModel`, …).
3. **FeatureManager** — the ViewModel converts `service.id` into a `FeatureFlag`, then `FeatureManager` evaluates that flag's `FeatureStatus`.
4. **Events** — if the state allows entry (`Enabled` or `EnabledWithError`), the ViewModel emits a `NavigateToService(flag)` event back to the UI. For `WebView` it emits `NavigateToWeb`.
5. **NavGraph** — `TaminHamrahNavGraph.kt` decides, based on the `FeatureFlag`, which Compose Navigation call to make (e.g. `navController.navigateToHousewifeInsuranceContract()`), via `FeatureNavigation.kt`.

### Links use the same gate

A deep link, a story call-to-action or an assistant button never navigates on its own: `ResolveDeepLinkUseCase` reads the same `FeatureStatus` and blocks a disabled, temporarily disabled or missing service (and blocks when the menu cannot be read). Details in [[Deep-Links]].

`AGENT(2000)` is two-stage everywhere: the flag **and** the server's cached chat permission (`ObserveAgentAvailabilityUseCase` for the orb, `ResolveDeepLinkUseCase` for links). See [[AI-Agent]].

### Why this is data-driven

- Menus stay fully dynamic.
- Adding a service later needs no sweeping UI logic changes.
- Availability and error messages take effect from the server without shipping a new app version.
- Routing errors and view handling are centralized in `FeatureManager`.

## 5a. `STACK_HOLDER_LIST(1005)` — legal representative introduction, not `workshopStackholders`

`STACK_HOLDER_LIST(1005)` (in the employer `1001`–`1012` range) routes to
`feature:workshops`' `ui/legalRepresentative/**` flow — "معرفی نماینده اشخاص
حقوقی" (introducing a representative for a legal-entity employer). Wired via
`navigateToLegalRepresentativeWorkshops()` in `FeatureNavigation.kt`.

⚠️ Do not confuse this with `feature:workshops/ui/workshopStackholders`
(`GetWorkshopStackHoldersUseCase`, endpoint `workshop-services/workshop-stackholders/get-all`)
— that is a **separate, pre-existing, read-only** feature (a simple
nationalId/mobile list) with no relation to legal representatives, no OTP, and
no add/edit/delete. The two happen to share the word "stakeholder/stack
holder" in their naming, purely coincidentally (`WorkshopStackHolderDN` came
first); nothing currently links them and none of the flag id `1005` overlaps
with the flag that fronts `workshopStackholders` — searching for "stackholder"
across the module will surface both, so check which feature you actually mean
before touching either.

The legal-representative flow's own API family (`legal-stakeholders`,
`v.1/legal-stakeholders/units`, `legal-ticket*`) is a distinct, OTP-gated
contract ported from `old_android`'s `ui.home.services.employer.legalStackHolders`
package — see that package for the original behavior if the contract needs
re-verifying against a live backend.

## 5b. Beyond the services page — profile, treatment and the home dashboard

`feature:taminServices` (the «خدمات» tab) was, for a long time, the only screen that actually read
`FeatureManager`/the menu per row. Home already gated its own service cards; profile and treatment
did not gate anything except one tap in `TreatmentViewModel.openRecords`. All three now follow the
same shape:

- **One lookup, many flags.** `FeatureManager.observeFeatureStatuses(flags: Set<FeatureFlag>)` reads
  the menu once and resolves every flag in the set from it (`FeatureManagerImpl` overrides it to do
  one `getMainMenu` call; the interface default just `combine`s `getFeatureStatus` per flag, which is
  what `FakeFeatureManager`-based tests fall back to). A menu that cannot be read leaves every flag
  `Enabled` — a failed lookup must never lock someone out of a feature the server never said was off.
- **`FeatureStatus.toGate(): FeatureGate`** (`core-domain/model/common/FeatureStatus.kt`) is the one
  decision every gated tap now goes through: `Open`, `OpenWithWarning(message)`, `Blocked(message)`,
  `OpenWeb(url)`. `FeatureStatus.serverMessage` pulls the same string out of `Disabled` /
  `TemporaryDisabled` / `EnabledWithError` for display without a `when`.
- **`ListItemData.gatedBy(status: FeatureStatus?, warningColor)`**
  (`core-ui/mapper/feature/FeatureGateMapper.kt`) turns a menu row into how it should render: `null`
  status shimmers the row (`ListItemData.isLoading`, new field on `ListItemData`/`ListGroupView`),
  `Disabled`/`TemporaryDisabled` dims it and shows the server's message as the subtitle,
  `EnabledWithError` keeps it tappable with the message in [warningColor].

### Profile (`feature:profile`)

Only the rows that actually have a server flag are gated — `ProfileMenuItem.flag` carries it, `null`
for everything else (settings, support, logout, …). Gated today: `IDENTITY_INFO`(1),
`ACTIVE_RELATION`(2), `BANK_ACCOUNTS`→`BANK_ACCOUNT_LIST`(3), `DEPENDENTS`(5),
`ELECTRONIC_FILE`→`MY_ELECTRONIC_FILE`(46). `ProfileViewModel` loads
`observeFeatureStatuses(ProfileMenuItem.gatedFlags)` alongside the rest of `LoadProfile`;
`handleItemClick` reads `FeatureStatus.toGate()` before navigating and posts `ProfileEvent.ShowToast`
for the server's message, which `ProfileScreen` now actually shows (was a `// TODO` no-op) via the
same snackbar the home screen and deep links use.

### Treatment hub (`feature:treatment`)

`TreatmentFeatureFlags` (`ui/model/TreatmentFeatureFlags.kt`) is the single map from a hub tile to its
flag: «سوابق پزشکی»/«نسخه‌ها» → `PRESCRIPTION`(26); the insurance-card carousel, «هزینه‌های متفرقه» and
«تاییدیه‌ها» → `DESERVED_TREATMENT_101` (استحقاق درمان — everything reading the person's treatment
entitlement, not `DESERVED_TREATMENT`(25), which nothing currently navigates to). The health-profile
tile and the contracted-centers link have no flag and stay open. `TreatmentViewModel.openGated`
replaces the old one-off `openRecords`-only check and backs `OpenRecords`, the new `OpenMiscClaims`
and `OpenApprovals` intents alike; it reads `uiState.featureStatuses` first so a tap does not
re-fetch what `InitTreatmentFlow` already loaded via `observeFeatureStatuses(TreatmentFeatureFlags.all)`.
`CategoryTile` gained `isLoading`/`dimmed`; the insurance carousel shimmers until its flag resolves
and shows the server's message instead of the "no patient" placeholder when the flag is off.

### Home dashboard (`HomeScreen`/`HomeViewModel`)

Service cards, «خلاصهٔ سابقه» (gated on `WAGE_AND_HISTORY`) and campaign cards were already gated.
What was missing: `isAgentEnabled` used to default to `false`, so the AI ask-bar and its suggestion
chips just popped in once `FeatureFlag.AGENT` resolved `Enabled`. It is now `Boolean?` — `null` means
"not answered yet" and shimmers the bar's own footprint and three chip-shaped blocks instead of
leaving that header slot looking finished before it is; a failed lookup resolves to `false` (hidden)
rather than shimmering forever.

## 5c. Testing a flag state without server control — Developer Options → "تست فیچر فلگ‌ها"

Profile → Developer Options → **تست فیچر فلگ‌ها** (`feature:developerOptions`'s `featureFlags` package,
route `FeatureFlagsRoute`) lists every [[Feature-Flags#4-server-id--client-enum-featureflag|FeatureFlag]]
with what it currently resolves to. Long-pressing a row opens an editor to force that flag to
`Enabled` / `Disabled` / `TemporaryDisabled` / `EnabledWithError`, each with an optional message —
covering the states a real menu response might not currently be serving, without needing the server
side changed. A badge marks an overridden row; "پاک کردن همه‌ی بازنویسی‌ها" clears every override at once.

**How it reaches the rest of the app**: `FeatureFlagOverrideRepository` (core-domain interface;
`FeatureFlagOverrideRepositoryImpl` in core-datastore, one `Settings` entry pair — kind + message —
per flag, mirroring `DeveloperOptionsRepositoryImpl`'s base-URL override storage). `FeatureManagerImpl`
takes it as a second constructor parameter (default `NoOpFeatureFlagOverrideRepository`, so every
existing caller and test that doesn't care about overrides is unaffected) and reads it *ahead* of the
real menu in both `getFeatureStatus` and `observeFeatureStatuses` — present, it wins outright; absent,
the menu answers as always. This means every screen this document already describes (services,
profile, treatment, home) is automatically testable from this one screen with no per-feature wiring:
setting an override is visible on the very next flag check anywhere in the app, no restart.

**Debug-only in three layers**: the Developer Options entry itself only renders when
`AppConfig.isDebug` (`ProfileScreen`'s existing gate); `FeatureFlagOverrideRepositoryImpl` also
refuses to load or write overrides when `isDebug` is false, so a value left over in shared app storage
from a prior debug install can never leak into a release build.

⚠️ **A `combine` trap this code deliberately avoids**: `FeatureManager.observeFeatureStatuses` already
folds overrides into its statuses (via `FeatureManagerImpl`'s own `combine` of the override flow and
the menu flow). `FeatureFlagsViewModel.observeRows()` therefore does **not** additionally `combine` its
own `overrideRepository.observeOverrides()` with that same statuses flow — `combine` stops responding
to a still-live source once *any* one of its given flows completes (a single-shot flow completing
mid-combine silently freezes the whole thing), and a naive test double for `FeatureManager` that
answers with `flowOf(...)` per flag (unlike the real DB-backed menu flow, which never completes) hits
that exact trap. `observeRows()` instead uses `overrideRepository.observeOverrides().flatMapLatest { … }`,
re-subscribing to a fresh status read only when the overrides themselves change — see
`FeatureManagerImplTest`'s `"a later override change reaches an already-subscribed collector"` for the
regression test on this specific shape.

## 5d. Provisional flags — pending real registration on the server

`FeatureFlag.kt` has a dedicated block, below `AGENT(2000)`, for screens the real backend has not
registered an id for yet: `CHANGE_MOBILE(3001)`, `PERSONAL_INBOX(3002)`, `MY_REQUESTS(3003)`,
`STORIES_AND_SAVE_EVENTS(3004)`, `HEALTH_PROFILE(3005)`, `CONTRACTED_CENTERS(3006)`,
`CURRENT_YEAR_TREATMENT_COSTS(3007)`, `HOME_LAST_REQUESTS(3008)`. Each has a matching row in
`MockMenuData.kt` (and the sample `menu.json`) under its placeholder id, so it behaves exactly like
any server-backed flag today — resolved through `FeatureManager`, `Enabled` unless `mockMenuData`'s
own row says otherwise or the "Feature flags" dev screen (§5c) overrides it. The day the real
backend registers an id for one of these, only that flag's id here and its `mockMenuData` row need
to change — nothing that reads the flag does.

`STORIES_AND_SAVE_EVENTS` is shared on purpose: it gates both the home screen's «تازه‌ها» story rail
and profile's «ذخیره رویدادها» row, one flag for both features per product decision, not two.

Where each is read:
- **Profile** (`ProfileMenuItem`) — `CHANGE_MOBILE`, `MY_REQUESTS` (لیست درخواست‌ها), `PERSONAL_INBOX`
  (صندوق شخصی), `SAVE_EVENTS`→`STORIES_AND_SAVE_EVENTS`. Gated the same way as the server-backed rows
  in §5b (`ListItemData.gatedBy`).
- **Home** (`HomeViewModel.sectionStatusesFlow()`) — «تازه‌ها» (`StoryRail`) and «آخرین درخواست‌ها»
  (`HomeLastRequestsSection`). Both a tap and the section's visibility go through the flag: unresolved
  (`null`) still shows the section (never blocked on ambiguity, same rule as everywhere else), resolved
  `Disabled`/`TemporaryDisabled` hides it. Tapping a story channel or a request row routes through
  `HomeIntent.OnStoryChannelClick`/`OnLastRequestClick`/`OnLastRequestsSeeAllClick` — these used to call
  the screen's navigation callback directly, bypassing the ViewModel (and therefore any gate) entirely.
- **Treatment** (`TreatmentFeatureFlags`) — `healthProfile`, `contractedCenters`, `currentYearCosts`.
  The first two are navigational tiles gated the same way as `records`/`miscClaims`/`approvals`
  (`TreatmentIntent.OpenHealthProfile`/`OpenContractedCenters` → `openGated`); `currentYearCosts` gates
  a passive display card (`TreatmentCostSummary`) instead of a tap — the card is hidden once the flag
  resolves off rather than dimmed, since there is nothing on it to tap.

### A real gap this closed: the assistant bypassing a flag entirely

`AgentActionKey.toFeatureFlag()` mapped `GET_DEPENDENT`/`ADD_DEPENDENT`/`DEPENDENT_CANCELLATION*` and
`EDIT_BANK_ACCOUNT_*` to `null` (the `else -> null` fallthrough) despite `DEPENDENTS` and
`BANK_ACCOUNT_LIST` already existing — meaning the assistant could read, add or cancel a dependent, or
edit a bank account, even with that row switched off in profile. `AgentActionDispatcher.dispatch()`
only blocks an action whose key maps to a flag at all (step 1 in its own doc comment), so an
unmapped key was never gated, full stop. Both are now mapped to their existing flags; `EDIT_PHONE_NUMBER*`
maps to the new `CHANGE_MOBILE`, and `PROFILE_INFO`/`EDIT_PROFILE*` map to `IDENTITY_INFO` (the
closest existing concept). See `AgentActionKeyFeatureFlagTest` for the regression coverage.

`EDIT_ADDRESS*` remains unmapped — there is no address-editing screen or flag anywhere in this
codebase to gate it against yet.

## 5e. Keeping «خدمات» from repeating profile/treatment's own screens

Profile and the treatment hub are permanent bottom-bar tabs — never hidden by a role or a flag — so
any flag with a dedicated row in one of them showing up *again* as a generic card in «خدمات» is the
same destination reachable twice, not two different things. `GetVisibleServicesUseCase`
(`core-domain/useCases/common/`) is what the services tab reads instead of the raw
`GetMainMenuUseCase`: it takes a `ServiceCatalogAudience` (today just `TAMIN_SERVICES_TAB`, more can
be added the same way if another screen ever needs its own view of the menu) and filters the real
menu through `DedicatedScreenFlags` — the union of every flag `ProfileMenuItem` and
`TreatmentFeatureFlags` gate on. `TamminServicesViewModel` calls it in place of `GetMainMenuUseCase`;
every other caller of the menu (contracts, contract affairs, home, …) is unaffected.

The exclusion does not depend on role — `PRESCRIPTION`(26) has `showRole:[1,2]` (both insured and
pensioner see «نسخ الکترونیک» in the raw menu) but is dropped from every role's services tab alike,
since the treatment hub it belongs to is reachable by any role regardless of which tab they searched
from. `EDIT_IMAGE`(4) and `HOME_LAST_REQUESTS`(3008) are deliberately **not** in `DedicatedScreenFlags`
— `EDIT_IMAGE` has no `ProfileMenuItem` row yet (in progress on another branch), and
`HOME_LAST_REQUESTS` is a dashboard widget, not a duplicate of anything services would show.

`DedicatedScreenFlags` is hand-maintained, not derived from `ProfileMenuItem`/`TreatmentFeatureFlags`
directly: `feature:taminServices` may not import another feature module (see `CLAUDE.md`'s
module-boundary rule), so this core-domain list is the deliberate single source of truth instead.
Update it by hand whenever profile or the treatment hub gains or drops a gated row.

## 6. The AI Agent flag

The AI assistant is a standalone feature in the flag system.

**Id:** `AGENT(2000)` in `FeatureFlag.kt` — deliberately outside the employer range (`1001`–`1012`) so it is semantically distinct.

### Two-stage access control

When the user enters the Agent screen, two checks run in order:

```
Step 1: FeatureFlag check (fast — no API call)
  ├── AGENT Enabled  →  proceed to Step 2
  └── AGENT Disabled →  show server message immediately (no network request)

Step 2: User-level permission (API call — CheckChatAllowedUseCase)
  ├── canStartChat = true  →  enter chatbot
  └── canStartChat = false →  show errorMessage from API
```

Implemented in `AgentViewModel.handleCheckPermission()`.

### FAB visibility on the home screen

The floating action button that opens the Agent is shown in `TaminHamrahNavGraph.kt` only when both hold:

1. the current route is `Route.Home`
2. `FeatureFlag.AGENT` resolves to `FeatureStatus.Enabled`

This is wired through `collectAsState` on `featureManager.getFeatureStatus(FeatureFlag.AGENT)`, so it reacts immediately to server-side changes.

### Message priority when a service is disabled

| Priority | Source | Meaning |
|---|---|---|
| 1 | `entity.message` | AI-specific message for this particular action |
| 2 | `featureManager.getDisabledMessage(flag)` | message defined in `menu.json` for that service |
| 3 | fallback string | default text, last resort |

Implemented in `AgentActionDispatcher.dispatch()`.

Related: [[Navigation]] · [[AI-Agent]] · [[Glossary]]
