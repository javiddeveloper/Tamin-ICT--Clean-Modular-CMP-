# Task 7 Report

## Status
- Completed Task 7 Step 4 final confirm, PDF viewer wiring, submit flow, and success dialog.
- Added `FinalStep.kt`, wired `TaminPdfViewer` with retry/dismiss support, and enabled final submit only after PDF confirmation.
- Added the parked `TODO(upload-component)` comment on `SurvivorInfo` upload placeholders and a toast/banner fallback when final `requestId` is unavailable.

## Compile
- Command: `.\gradlew.bat :feature:pensionSurvivor:compileDebugKotlinAndroid`
- Result: `BUILD SUCCESSFUL`

## Commit
- Message: `feat: pensionSurvivor final PDF and submit step`

## Concerns
- The final-step download CTA currently reuses the existing `girl_survivor_download_form` string; the behavior is correct, but copy may need a pension-specific resource later if product wants distinct wording.
- Final submit depends on the confirm-list response containing a `requestId`; when it is missing, the step now shows a banner/toast and keeps submit disabled.
