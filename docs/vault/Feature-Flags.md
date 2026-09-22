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
