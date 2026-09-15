---
tags: [architecture, domain]
---

# Deep Links

Every link that opens a screen — from the operating system, a story, the AI assistant, the assistant's entry button — goes through **one gate** that reads the feature flag. A service the menu switches off cannot be entered by link, whoever sent the link.

## The pieces

| Piece | Module | Role |
|---|---|---|
| `DeepLinkKey` | core-domain `deeplink/` | the only allowlist: wire key → `FeatureFlag` |
| `DeepLinkParser` | core-domain | every accepted link shape → `ParsedDeepLink`; never throws |
| `ResolveDeepLinkUseCase` | core-domain | applies the flag → `DeepLinkResolution` |
| `DeepLinkDispatcher` | core-domain (Koin `single`) | app-wide inbox; links wait here until the host can navigate |
| `LocalDeepLinkHandler` | core-ui `ui/deeplink/` | how a feature hands a link to the host |
| collector in `TaminHamrahNavGraph` | shared | resolves and navigates with `navigateToFeature(flag)` |

Feature modules never import each other (rule 4): they only call `LocalDeepLinkHandler.current.open(uri, source)`.

## Accepted shapes

| Link | Meaning |
|---|---|
| `@key`, `@key?a=b` | the assistant's markdown shorthand |
| `mytamin://feature/key?a=b` | OS deep link (Android intent filter, iOS `onOpenURL`) |
| `tamin://feature/FLAG_NAME` | older content; the flag name is looked up in `DeepLinkKey` (stories now use `@key`) |
| `agent://nav/key` | the assistant's long form |
| `agent://prompt?text=…` | sends the text as the next assistant prompt (handled inside the assistant) |
| `https://…` | a web page; from the assistant (`DeepLinkSource.AGENT`) only `tamin.ir` and its subdomains |

Keys are case-insensitive; query values are percent-decoded (UTF-8). Anything else is `Invalid` and shows «لینک نامعتبر است».

Arguments are used where a screen takes them: the host calls `navigateToDeepLink(key, args)`. `prescription_detail` is read by `PrescriptionDetailLink` (native keys `ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION`, `ARG_REQUEST_TYPE`/`PRES_TYPE`, `ARG_NATIONAL_CODE`, `ARG_CHILD_NATIONAL_CODE` — `0` means the insured, `ARG_FLAG_SATA`) and opens the treatment `RecordDetail` screen; without a prescription id it opens the list. The link carries no doctor, date or tracking code, so the detail header shows those as unknown. `FeatureFlag.PRESCRIPTION` (`electronic_prescription_list`, the menu tile) opens the treatment records on the medicine tab — `pensionInquiry`'s `PrescriptionScreen` is an empty placeholder.

There is **no `TOOLBAR_TITLE`**. The native app passed the page title in the link; here the title is the menu's own name for the service (`FeatureManager.getFeatureTitle(flag)`, returned as `OpenFeature.title`). Query parameters are kept only for real arguments, e.g. `prescription_detail`.

## The gate — `ResolveDeepLinkUseCase`

It applies exactly the rule a menu tap on the home screen applies, so a link can never reach more than a tap:

| `FeatureStatus` | Result |
|---|---|
| `Enabled` | `OpenFeature` |
| `EnabledWithError(msg)` | `OpenFeature(notice = msg)` — the message is shown and the screen opens |
| `WebView(url)` | `OpenWeb(url)` |
| `Disabled` / `TemporaryDisabled` | `Blocked(message)` |
| flag missing from the menu | `Blocked(null)` (a missing row reads as disabled) |
| menu cannot be read | `Blocked(null)` — fail closed |

`AGENT` additionally needs the cached chat permission (`AgentAccessStore.access.canStartChat`); see [[AI-Agent]].

`showRole` is **not** checked: the home screen lets users browse other roles' tabs and a tap there is not role-gated either. Adding role gating must change both places together.

## Delivery

- **Android** — `AndroidManifest.xml` has an intent filter for `mytamin://feature`; `MainActivity.handleIntent` submits it with `DeepLinkSource.SYSTEM`. `login` and `payment_callback` keep their own handling.
- **iOS** — `iOSApp.swift` `.onOpenURL` → `IncomingUrlKt.handleIncomingUrl(url)` (`shared/src/iosMain/.../ui/IncomingUrl.kt`), which routes payment return, login and feature links the same way as Android. Only compiled on macOS; not verified on Windows builds.
- **Cold start / logged out** — `DeepLinkDispatcher` buffers links; the nav graph collects them only while `isLoggedIn`, so a link opened before login runs after it.
- **Stories** — like the assistant, the viewer calls `LocalDeepLinkHandler` with `DeepLinkSource.APP_CONTENT` and `onOpened = close`, so the viewer closes only if the gate lets the link through.
- **Assistant** — markdown buttons and `DeepLink` bubbles call the handler with `DeepLinkSource.AGENT`. The flag is checked **on tap**, not when the answer arrived, because answers are cached and a flag may change later.

`navigateToFeature(flag, beforeOpen)` returns `false` when the app has no screen for a flag yet, or only an empty placeholder (currently `FRACTION_CONTRACT`, whose phase 2 UI is not built). The host then shows «این سرویس در حال حاضر در دسترس نیست» for links and for menu taps alike, and `beforeOpen` (e.g. closing the story viewer) does not run.

The server still sends `TOOLBAR_TITLE` in `@key?TOOLBAR_TITLE=…` links; it is ignored, the title comes from the menu.

## Adding a destination

1. The screen must be reachable from `navigateToFeature(flag)` in `shared/.../FeatureNavigation.kt`.
2. Add an entry to `DeepLinkKey` with the backend's key and the flag.
3. Add a case to `DeepLinkParserTest` / `ResolveDeepLinkUseCaseTest` if the key has special behaviour (alias, arguments).

Aliases: `insurance_payment` and `cancel_contract` point at the contracts flag, because their own screens need a contract the user picks first (native `AgentNavKeys.ALIASES`).

## Known gaps

- `iosApp/iosApp/Utils/DeepLinkHandler.swift` and `NavigationRouter.swift` are unused template leftovers; removing them needs an Xcode project edit on macOS.

Related: [[Feature-Flags]] · [[Navigation]] · [[AI-Agent]] · [[Agent-Markdown]]
