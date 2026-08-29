# Final Fix 1 Report

## Status
- Implemented all three fixes from `final-fix-1-brief.md` on `Feature-EM-2576-pension-survivor`.

## Changes
1. Final PDF is invalidated whenever survivor data reloads or the deceased national id changes, with `finalPdfRevision` appended to the cached viewer file name.
2. Survivor contact drafts now persist across `SurvivorInfo` reopen within the same main session via main-state draft storage and route/result handoff.
3. `loadRequestId()` now keeps the legacy empty-filter call, selects the highest non-null request id, and warns when multiple final requests are returned.

## Verification
- Passed: `.\gradlew.bat :feature:pensionSurvivor:allTests :feature:pensionSurvivor:compileDebugKotlinAndroid :shared:compileDebugKotlinAndroid`
- No linter errors on edited files.

## Commit
- Requested message: `fix: invalidate final PDF and preserve survivor contact drafts`
