# Code Review — MR !<id>

**Branch:** `<branch-name>`
**Link:** https://ci.tamin.ir/base/development/finance/tatmin-kmp/-/merge_requests/<id>
**Author:** <author>
**Target:** `develop`

| | |
|---|---|
| Review round | 1 |
| Date | <YYYY-MM-DD> |
| Reviewed commit | `<sha>` |
| Base (merge-base with develop) | `<sha>` |
| Size | <n> files, `+<x> / -<y>` |

<!-- If this MR carries another branch, say so here:
> This MR also contains `Feature-XXX` (MR !NNN); the two cannot be merged independently.
-->

---

## Summary

| # | Issue | File | Severity | Status |
|---|---|---|---|---|
| 1 | <short title> | `<file>` | High | ⬜ open |
| 2 | <short title> | `<file>` | Medium | ⬜ open |
| 3 | <short title> | `<file>` | Low | ⬜ needs confirmation |

**Severity** — High: wrong behaviour or data loss on the normal path · Medium: edge-case failure, or impact outside this MR's scope · Low: readability, consistency, technical debt

**Status** — `⬜ open` · `✅ fixed` · `🔄 needs rework` · `➖ rejected (with reason)` · `⬜ needs confirmation`

> **How to use this file:** fix the items and push to this same branch. Do not edit the Status
> column yourself — the reviewer updates it in the next round. If you disagree with an item,
> write your reasoning under that item instead of changing the code; it will be marked
> `➖ rejected`. Full process: `review/README.md` on `develop`.

---

## 1. <title> — <High/Medium/Low>

**File:** `<path>:<line>`

```kotlin
// current code
```

<What happens and why it is a problem — a concrete failure scenario, not a general remark.>

**Suggested change:**

```kotlin
// proposed code
```

---

## 2. <title> — <severity>

<same structure>

---

## What was done well

- <item>
- <item>

---

## Revision log

| Round | Date | Commit | Result |
|---|---|---|---|
| 1 | <YYYY-MM-DD> | `<sha>` | <n> items raised |

<!--
Next round:
- update the Status column in the Summary table
- append new findings at the end (do not renumber existing items)
- add a row to the Revision log
- when no ⬜ or 🔄 remains, delete this file and push — merging is not the reviewer's job
-->
