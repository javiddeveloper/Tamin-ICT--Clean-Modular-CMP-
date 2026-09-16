---
tags: [architecture, domain]
---

# Assistant Markdown

Every assistant answer — rendered by the server, or built by a client service — is a markdown bubble (`ChatBubbleContent.Markdown`). This page is the contract for that markdown and how the client draws it.

## Where markdown comes from

1. **Server-rendered** — `render_mode: "SERVER"`, entity `item_type: "markdown"`, text in `data[].text` of `markdown_item`s. `AgentRepositoryImpl` puts the non-blank texts on `AiEntityDN.markdown`; `AgentActionDispatcher` turns them into bubbles **without running a service or checking the entity's flag** (the links inside are gated when tapped). An entity whose texts are all blank yields nothing — not «متوجه نشدم». Fixture: `AgentMarkdownFakeData.kt` (the fake data source serves it for any prompt that contains «مارک‌داون» or `markdown`).
2. **Client services** (`feature/agent/.../service/impl`) — build the same shape with `agentMarkdown { … }`: the server's title (`entity.message`) as `###`, then data as fields, tables, sections and link buttons. **No wording is added** — no greetings, no "tap the button below" sentences. Labels come from `strings.xml` through `AgentStrings`.
3. **Link items** — an entity's `deeplink` data items are appended by the dispatcher as buttons: a known key → `@key`; unknown → a prompt link with the item's title (native behaviour).

The raw text is stored in the conversation cache (`ChatBubbleCodec`, serial name `markdown`) and parsed only when drawn.

## Supported syntax

| Block | Syntax | Drawn as |
|---|---|---|
| heading | `#` … `######` | bold title (levels 1–2 larger) |
| paragraph | plain lines | text |
| list | `-`, `*`, `+`, `•`, `1.` / `1)`; nesting by 2-space indent | bullet or Persian ordinal |
| quote | `> …` | text with a primary side bar |
| code | ```` ``` ```` fence | text in a muted box |
| rule | `---`, `***`, `___` | divider |
| table | pipe table with a `---` delimiter row; `\|` inside a cell | `TableBubble` (scrolls when wide) + «جدول» + copy |
| formula | see below | `FormulaCard` + «فرمول» + copy |
| links | `[label](url)` anywhere | buttons after the text, the label staying in the sentence; **inside a table cell** a cell that is only a link is a compact button; a link inside other cell text is a tappable, underlined span |

Inline: `**bold**`, `*italic*`/`_italic_`, `***both***`, `~~strike~~`, `` `code` ``, and `\` escapes (`\*`). An in-word `_` is never emphasis.

**HTML is never rendered** — the text is model-generated. `<br>` becomes a line break and every other tag is removed.

## Formulas

- **Explicit** — a `$$ … $$` block (one or more lines) or a ```` ```math ```` fence: each non-blank line is one formula; `=` is part of the expression.
- **Heuristic** — a prose line shaped `label = expression` whose expression has a math operator (`/ ^ + − - × ÷ · ≤ ≥ ± < > ≈ ≠` or a superscript) and **no sentence punctuation** (`. ! ? ؟ ، :`; a decimal point between digits is allowed).

`MathParser` grammar (tightest first): primary (atom or `( … )` group) → power (`a^b`, `a²`) or subscript (`a_b`, drawn small beside the base) → fraction (`a/b/c`, left-associative) → row (operators or side by side, e.g. `(a)(b)`).

**Parentheses are never dropped.** Every `( … )` is a `Group` and is drawn with brackets stretched to the content height — including a group that is a fraction's numerator or denominator. The native renderer removed them, which changed how formulas read. `2/π = (1 − 1/2²)(1 − 1/4²)…` renders as a stacked `2/π`, then three bracketed factors each containing a stacked `1/2²`.

Space-separated atoms merge (`136 249 479`, `سنوات بیمه‌پردازی`); malformed input degrades (an unmatched `)` is shown, an open group is closed) and never throws.

**Empty brackets `()` are the one exception** — they hold nothing and are removed before parsing. The server has sent the pension formula as `مستمر = ( ((…) / (…))_۲ سال آخر × …) / () ۳۰ × سنوات بیمه‌پردازی`, where `/ () ۳۰` means `/ ۳۰` and `_۲ سال آخر` is a subscript (averaged over the last two years). `MathParserTest` keeps that exact line.

**Direction.** A formula containing Arabic-script letters (`MathParser.isRightToLeft`) is laid out right to left, like the sentence around it; one made only of digits and symbols (`2/π = …`, Persian digits included) left to right. Brackets take the shape of the side they stand on, so `(` is always drawn on the left of its content. Exponents and subscripts follow the base in reading order.

**Width.** The label and the expression stay on one line; a formula wider than the card scrolls sideways (breaking or shrinking it was tried and read worse). A phrase inside a formula wraps past 30% of the card width (centered), which keeps formulas built from long Persian phrases compact. A bracketed group reserves its bracket width before measuring its content, otherwise the brackets overflow and get clipped. **Axis.** Rows of a formula (`MathAxisRow`) line their children up on `MathAxis`, an alignment line each `FractionLayout` sets at its bar; a piece without a fraction uses half its height. So `label =`, operators and terms beside a fraction are level with the main fraction bar, not with the middle of a tall numerator. A subscript (`_۲ سال آخر`) is aligned the same way beside its base. Formula text uses `typography.bodyMedium` so it gets the app font.

## Bidi and digits

- Text runs get `toPersianDigits()` (not code, not link URLs).
- Space-grouped numbers are wrapped in an LTR isolate (U+2066…U+2069) so RTL prose cannot reverse their groups.

## Links

Buttons resolve through `DeepLinkParser` with `DeepLinkSource.AGENT`: unknown keys and untrusted web hosts are drawn disabled; `agent://prompt` sends a prompt; everything else goes to `LocalDeepLinkHandler`, which checks the target's flag. Full rules: [[Deep-Links]].

A link in a table cell (e.g. `[مشاهده جزئیات نسخه](@prescription_detail?…)`) is parsed by `MarkdownInlineParser` into a run carrying its url and drawn as a `LinkAnnotation.Clickable` going to the same handler; an unreadable one shows only its label. A plain `ChatBubbleContent.Text` message that contains a link (`MarkdownParser.containsLink`) is drawn through `MarkdownContent`, so no raw `[label](@key)` text reaches the screen.

## Service ports — decisions that differ from the native app

| Area | Native | Now | Why |
|---|---|---|---|
| wage «month» field | — | `WageDetailDN.month` is **days worked**; the month is the index | the earlier port showed days as month names |
| N-year average | iterated oldest years first | newest months first until N×365 days | "last N years" is the business rule; the native order was a bug |
| `sumDayItems` | counted month 3 twice | each month once | native bug |
| job titles | `historyjobinfos` | same (`GetHistoryJobInfosUseCase`) | the earlier port called the job-title *catalogue* |
| `patient_history` | e-prescription list, 5 items + detail link | same | the earlier port showed the patient health record |
| tracking codes | Jalali dates → epoch millis | same | the earlier port sent raw `YYYYMMDD` |
| latest prescription search | unbounded month loop | 12 months | native could loop forever |
| `dastmozd_infos_sum_total` | asked text/chart first | table + total + bar chart in one answer | no extra step or wording |
| eligible pension | own calculator copy | app's `CalculateWagePensionUseCase` | chat and screen must agree |
| entry-point keys (disability pension, illness, …) | description sentence + button | title + one button, label = menu name | no added wording |
| multi-step form flows (`edit_mobile_*`, `funeral_allowance_*`, …) | in-chat forms | not ported; the entry key opens the feature screen | features are implemented once ([[AI-Agent]] §1) |

Codes read from the server use enums, never magic strings: `PensionerStatusDN` (`00` not a pensioner), `PayRollItemKindDN` (`1/2/3`), `GenderCodeDN`, `DependentRelationDN` (native `RelationEnum`), `AgentItemType`, `AgentRenderMode`.

## Tests

`feature/agent/src/commonTest/.../markdown/*` (parser, inline, math), `service/DastmozdInfosAgentServiceTest`, `PensionAgentServicesTest`, `AgentServiceKeyCoverageTest`, `AgentMarkdownDispatchTest`; `core-network` `AgentMarkdownMappingTest`. Services are tested with `testStrings`, which returns resource keys, so no platform resources load.

Related: [[AI-Agent]] · [[AI-Agent-API-Contract]] · [[Deep-Links]] · [[Typography]]
