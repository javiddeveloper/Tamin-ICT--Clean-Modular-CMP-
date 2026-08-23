---
tags: [convention]
---

# Theme tokens

Compose UI must use tokens from `core-ui/ui/theme/`. Do not hardcode colors, `.dp`/`.sp`, or UI copy.

| Kind | Where |
|---|---|
| Color | `SemanticColors.kt` via `LocalTaminColors.current` |
| Space, radius, icon, elevation, stroke, header decoration | `Shape.kt` (`Spacing`, `CornerRadius`, `IconSize`, `Elevation`, `Thickness`, `ButtonDimens`, `HeaderDecoration`, `ShimmerSize`) |
| Type | `Type.kt` via `MaterialTheme.typography` |
| Copy | `core-ui/.../composeResources/values/strings.xml` |

If a value is missing, add a token there — do not leave a literal in a feature.

Persian digits: [[Typography]]. Components already in `core-ui/ui/components/`: [[Modules]].
