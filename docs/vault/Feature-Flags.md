---
tags: [architecture, domain]
---

# Feature Flags and the Dynamic Menu

How services are fetched, categorized, gated and routed. Absorbed from the former `documents/features.md`.

## 1. Menu data — `MainServiceDto`

The home screen's services arrive dynamically from a web service in the data layer (currently via `MockMenuData`) as `MainServiceDto`. Key fields:

| Field | Meaning |
|---|---|
| `id` | unique service id (e.g. 15 for contract affairs) — also decides the order on screen, see below |
| `name` | display name |
| `showRole` | array of role numbers (e.g. `[1, 2]`) controlling who sees the service |
| `status` | current service state (active, disabled, webview, …) |
| `message` | error message to display when the service is unavailable |
| `sorting` | the server's explicit ordering key; **null for every locally served row** |
| `url` | target link when the service is a webview |

`menu.json` at the repo root is a local sample of this structure.

### `id` decides the order on screen, not the order of `MockMenuData`

The menu is not rendered straight off the list the data source returns. `CommonRepositoryImpl.getMainMenu`
writes it into Room (`menu_items`, `MenuEntity.id` as the primary key) and the UI reads it back
through `MenuDao.getMenuItems()` — `ORDER BY sorting ASC, id ASC`. Since `sorting` is null for every
row in `MockMenuData`, **`id` is the tie-break that actually orders the menu**. Writing the list in
one order and numbering it in another silently renders it in the numbering's order.

So each block of `MockMenuData` is written in display order with its ids ascending in step. Ids are
banded by audience:

| Band | Audience | `showRole` |
|---|---|---|
| `1`–`37` | insured | `1` |
| `101`–`110` | pensioners | `2` |
| `1001`–`1010` | employers | `3` |
| `2000` | AI assistant | all |
| `90`–`93` | **no menu row** — flags kept only for deep links / assistant actions | — |

⚠️ These ids are the app's own. They were the legacy server's until 2026-09-19 (the canonical dump is
`my-tamin-droid/temp_menu.csv`, see [[Reference-old-android]]), when the menu was reordered and
renumbered to match. So if `menu_data_<version>.txt` is ever switched back on in
`CommonRemoteDataSourceImpl.getMainMenu`, the server payload's ids will **not** line up — every
`FeatureFlag` id has to be remapped back to whatever that file sends before the remote menu can be
trusted.

### One service, two audiences

A service both an insured person and a pensioner reach is modelled one of two ways:

- **One row, `showRole = [1, 2]`** when a single position serves both (id `28`, نسخ الکترونیک).
- **Two rows with two ids and one `FeatureFlag` each** when each audience needs it in its own
  position — the pensioner's flag suffixed `_PENSIONER`, both routed to the same screen in
  `FeatureNavigation.kt`: `CALCULATE_WAGE_PENSION(11)`/`…_PENSIONER(104)`,
  `DISABILITY_PENSION(17)`/`…_PENSIONER(107)`, `REQUEST_PENSION_BY_SURVIVOR(21)`/`…_PENSIONER(108)`,
  `DESERVED_TREATMENT(27)`/`…_PENSIONER(110)`.

⚠️ Never give two rows the same id. `MenuEntity.id` is the primary key and the insert is
`OnConflictStrategy.REPLACE`, so the second row silently overwrites the first in the cache and one of
them disappears. `FeatureFlag.fromId` likewise takes the *first* match, so a repeated id in the enum
routes a menu row to whichever flag happens to be declared first.

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

## 5a. `STACK_HOLDER_LIST(1008)` — legal representative introduction, not `workshopStackholders`

`STACK_HOLDER_LIST(1008)` (in the employer `1001`–`1010` range) routes to
`feature:workshops`' `ui/legalRepresentative/**` flow — "معرفی نماینده اشخاص
حقوقی" (introducing a representative for a legal-entity employer). Wired via
`navigateToLegalRepresentativeWorkshops()` in `FeatureNavigation.kt`.

⚠️ Do not confuse this with `feature:workshops/ui/workshopStackholders`
(`GetWorkshopStackHoldersUseCase`, endpoint `workshop-services/workshop-stackholders/get-all`)
— that is a **separate, pre-existing, read-only** feature (a simple
nationalId/mobile list) with no relation to legal representatives, no OTP, and
no add/edit/delete. The two happen to share the word "stakeholder/stack
holder" in their naming, purely coincidentally (`WorkshopStackHolderDN` came
first); nothing currently links them and none of the flag id `1008` overlaps
with the flag that fronts `workshopStackholders` — searching for "stackholder"
across the module will surface both, so check which feature you actually mean
before touching either.

The legal-representative flow's own API family (`legal-stakeholders`,
`v.1/legal-stakeholders/units`, `legal-ticket*`) is a distinct, OTP-gated
contract ported from `old_android`'s `ui.home.services.employer.legalStackHolders`
package — see that package for the original behavior if the contract needs
re-verifying against a live backend.

## 6. The AI Agent flag

The AI assistant is a standalone feature in the flag system.

**Id:** `AGENT(2000)` in `FeatureFlag.kt` — deliberately outside the employer range (`1001`–`1010`) so it is semantically distinct.

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
