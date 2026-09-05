---
tags: [convention]
---

# Typography and Persian digits

The UI font is **Vazirmatn** (v33.003), bundled as `regular`/`light`/`medium`/`semi_bold` under `core-ui` compose resources.

Every `TextStyle` in `taminHamrahTypography()` (`core-ui/.../theme/Type.kt`) carries:

```
fontFeatureSettings = "tnum, ss01"
```

Vazirmatn's `ss01` stylistic set maps ASCII `0-9` to Persian digits `U+06F0`–`U+06F9` at paint time. `tnum` keeps those glyphs equal-width. Both features are in the font's GSUB table; this is not a generic OpenType convention.

## Two mechanisms, one job each

| | `ss01` on theme typography | `String.toPersianDigits()` |
|---|---|---|
| What it changes | Glyph painted on screen | Characters in the string |
| ASCII `0` becomes | still `U+0030`, looks like `۰` | `U+06F0` |
| Use for | Compose UI using `MaterialTheme.typography` | Share/copy payloads, notifications, any surface that does not use the theme typography |
| Copy/paste / APIs | Latin digits | Persian digits |

They are meant to coexist. `ss01` is a no-op on digits already in `U+06F0`–`U+06F9`, so calling `toPersianDigits()` and then rendering with the theme style is harmless.

`toPersianDigits()` is **not** being phased out. Do not migrate existing call sites onto `ss01`, and do not convert Compose UI strings to Persian codepoints solely so they look Persian — the typography already does that.

## Replacing `fontFeatureSettings`

`TextStyle.copy(fontFeatureSettings = "tnum")` **replaces** the default, it does not add to it. That drops `ss01` and the digits stay Latin. Only override when Latin digits are actually required, and comment why.

Related: [[Modules]] (`ui/theme/Type.kt`) · [[Tech-Stack]]
