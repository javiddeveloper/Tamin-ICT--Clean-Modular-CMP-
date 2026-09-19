---
tags: [gotcha]
---

# Known Gotchas — Recurring Lessons

Small, non-obvious traps that have actually bitten this codebase more than once or
cost real debugging time. Not full pages of their own — check here before repeating
the same mistake.

## A shared composable can serve two screens with independent correctness bars

`LegalRepresentativeOtpSection`
(`feature/workshops/.../ui/legalRepresentative/components/LegalRepresentativeOtpSection.kt`)
is shared between two screens that must stay visually independent:

1. **Add/Edit legal representative** (`AddLegalRepresentativeScreen.kt`) — needs a
   compact, single-box layout (short title, countdown row, 5-box OTP input, no outer
   wrapper once the code has been requested).
2. **Identity verification with security ticket** (`LegalRepresentativeOtpScreen.kt`)
   — renders the shared component with its original defaults, plus its own
   heading/icon/description above it and a separate confirm button below it.

A fix aimed at one screen's design can silently break the other's already-correct
layout, since both compose the same shared component with different surrounding
wrappers. Two lessons:

- Before editing a shared component's styling/params for one call site's sake, check
  the diff against **every** call site, not just the one you're fixing.
- Don't declare a UI claim "already matches" from reading source alone — the actual
  composition (nesting of `taminSurface`/outer wrapper `Column`s at the call site) can
  differ from what an isolated read of the shared component suggests. Ask for or
  generate a real screenshot before asserting correctness.

## A nested DTO's `@SerialName` that matches a sibling/ancestor key is suspicious

If a nested DTO's `@SerialName` exactly matches the JSON key of an *outer* or
*sibling* nesting level (e.g. the same key repeated three levels deep), treat it as
a likely copy-paste bug rather than assuming the shape is correct because it compiles
and looks structurally similar to a working reference model.

This is exactly how `TendencyInfoDTO.baseTendency`
(`core-network/.../model/personal/disabilityRequest/TendencyInfoDTO.kt`) silently
nulled out `tendencyCode`/`tendencyDescription` for every API response while every
unit test still passed — full write-up in [[Disability-Pension-Status]]'s "Gotcha:
the dependent's relation label" section.

**How to apply:** when writing or reviewing a new DTO for a nested JSON structure,
diff its `@SerialName` at each nesting level against a working sibling model in the
same package family that already parses successfully in production. Also treat any
endpoint response with real nested structure and zero JSON-fixture test coverage as
a red flag on its own — add one (`createMockKtorfit` + a realistic JSON body
asserting the deserialized field values), not just unit tests on the DN/PR-mapper
layer with hand-constructed objects that never exercise real `kotlinx.serialization`
parsing.

## A `DN → PR` mapper doing `timestamp?.toString()` on a date field is a latent bug

Stringifying a raw epoch-millis `Long` (`dateOfBirth?.toString() ?: ""`) renders as a
raw number like `316310400000` instead of a date. Route date fields through the
shared `PersianDateFormatter.formatTimestamp(...)` (`core-ui/.../util/PersianDateFormatter.kt`)
instead. This exact bug has been hit twice (`DisabilityDependentDN`, `DisabilityPersonalDN`
in `core-ui/.../mapper/personal/PersonalMapper.kt`) — before wiring a new screen that
displays a date field from an existing domain model, check whether its mapper already
has this bug rather than assuming it's fine because it compiles.

Related: [[Disability-Pension-Status]] · [[Error-Handling]] · [[Naming-Conventions]]
