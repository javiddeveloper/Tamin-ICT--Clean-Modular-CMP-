# Task 4 Report: Main screen chrome + Step 1 Rules UI + nav/DI wiring

**Status:** DONE  
**Branch:** `Feature-EM-2576-pension-survivor`

---

## Summary

Wired the app to the standalone `:feature:pensionSurvivor` module, removed the conflicting pensioner route/viewmodel stubs, and replaced the screen placeholder with a `GirlSurvivor`-style host that renders the 4-step chrome and a real Rules step. Step 2–4 content is intentionally left as lightweight placeholders for Tasks 5–7, while the existing Task 3 ViewModel now drives the stepper, back/next controls, and rules-document event handling.

---

## Files Modified

| File | Change |
|------|--------|
| `feature/pensionSurvivor/.../ui/PensionSurvivorScreen.kt` | Replaced placeholder scaffold with full screen host: themed top bar, 4-step indicator, step switching, bottom actions, and event handling |
| `feature/pensionSurvivor/.../ui/components/RulesStep.kt` | Added Step 1 UI with info banner, rules CTA, commitment card, and checkbox wiring |
| `core/core-ui/.../strings.xml` | Added pension survivor strings for title, step labels, rules CTA, commitment text, next CTA, placeholder, and fallback toast |
| `shared/.../di/Koin.kt` | Registered `pensionSurvivorModule` |
| `shared/.../ui/navigation/TaminHamrahNavGraph.kt` | Switched `pensionSurvivorScreen` import to the new feature module |
| `shared/.../ui/navigation/FeatureNavigation.kt` | Switched `navigateToPensionSurvivor()` import to the new feature module |
| `feature/pensioner/.../Navigation.kt` | Removed legacy `PensionSurvivorRoute`, `navigateToPensionSurvivor()`, and `pensionSurvivorScreen()` |
| `feature/pensioner/.../di/PensionInquiryModule.kt` | Removed legacy `PensionSurvivorViewModel` Koin registration |
| `feature/pensioner/.../ui/pensionSurvivor/*` | Deleted now-unused stub screen/viewmodel/contract files |

---

## Behavior Implemented

1. The app now resolves pension survivor navigation from `shared` into `:feature:pensionSurvivor`, not `:feature:pensioner`.
2. The new screen mirrors `GirlSurvivorScreen` structure: top hero bar, `StepIndicator`, event collector, and sticky bottom actions.
3. Step 1 renders:
   - rules info banner
   - `مشاهده ضوابط و مقررات` CTA -> `PensionSurvivorIntent.ViewRules`
   - commitment text with `%1$s` applicant full name insertion
   - checkbox enabled after applicant profile load
   - next button enabled only when commitment is accepted
4. `OpenRulesDocument` currently shows a fallback toast and leaves `TODO(rules-url)` in code because no legacy rules URL/PDF source was found in this workspace.
5. Steps 2–4 are compile-safe placeholders that still participate in the 4-step chrome and navigation flow.

---

## Verification

### Compile

```powershell
.\gradlew.bat :feature:pensionSurvivor:compileDebugKotlinAndroid :shared:compileDebugKotlinAndroid
```

**Result:** `BUILD SUCCESSFUL` in 1m 9s.

### Notes from build output

Unrelated existing warnings were emitted from `feature/profile`, `feature/agent`, and shared `expect/actual` declarations; no new pension survivor compile failures remained.

---

## Self-Review

### Brief compliance

| Requirement | Done |
|-------------|------|
| Replace `PensionSurvivorScreen.kt` with real screen chrome | ✅ |
| Create `ui/components/RulesStep.kt` | ✅ |
| Add strings in `core-ui` resources | ✅ |
| Wire `shared` Koin to `pensionSurvivorModule` | ✅ |
| Wire `TaminHamrahNavGraph` to new feature package | ✅ |
| Wire `FeatureNavigation` to new feature package | ✅ |
| Remove duplicate pensioner route/navigation/viewmodel | ✅ |
| Delete unused pensioner stub package files | ✅ |
| Compile `:shared` and feature module | ✅ |
| Leave unknown rules document as TODO + toast fallback | ✅ |

### Notes / concerns

1. The actual rules URL/PDF is still unknown in this repo snapshot, so `OpenRulesDocument` currently toasts a fallback message and leaves `TODO(rules-url)`.
2. Steps 2–4 are intentional placeholders and will need real UI in Tasks 5–7.
3. I did not add new automated tests for this task; requested verification was module compilation.
