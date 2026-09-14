---
tags: [reference, gotcha]
---

# `old_android/` — Reference Implementation

## What it is

The legacy native Android version of the Tamin Hamrah app. **It is not part of the project** and takes no part in the build — it is not included in `settings.gradle.kts`.

Its role is **reference**: KMP features are developed based on the behaviour and implementation of this version. When a question comes up like "what exactly is this screen supposed to do" or "what did this API contract look like", the answer is usually in there.

## Rules

- ⛔ **Never commit it.** It is excluded via the `old_android/` pattern in `.gitignore`, and no file under it is tracked.
- ⛔ Do not edit it — treat it as read-only.
- ✅ Read it to understand expected behaviour, business logic, or the older shape of an API.
- ✅ When porting, the result must follow the new architecture ([[Overview]], [[MVI-Pattern]]) — copying the old structure verbatim is not the goal.

## If `old_android/` doesn't exist in your checkout

On at least one developer's machine, `old_android/` is not actually present inside the
`tamin-kmp` checkout — the real legacy reference app lives in a separate sibling repo:

```
C:\Users\h_arabameri\AndroidStudioProjects\my-tamin-droid
```

Package `com.tamin.taminhamrah`, classic View/Fragment Android (Retrofit + Gson,
`GsonBuilder` with no custom `FieldNamingPolicy` — so its Kotlin data class property
names are the literal JSON wire keys, useful for confirming a real backend field name
when the KMP port's own DTO is suspect). If `old_android/` doesn't exist where you're
working, check whether a sibling checkout like this exists on the machine before
assuming the reference implementation is unavailable — don't just ask the user again.
This path is machine-specific and may differ or move; verify it's still there first.
See [[Disability-Pension-Status]] for a worked example of using it.

## Historical note

The `old_android/` pattern was once added to `.gitignore` with UTF-16 encoding (NUL bytes between the characters), so it never matched and the folder kept appearing as untracked in `git status`. It is now written as clean UTF-8. If it ever shows up in `git status` again, check the encoding of `.gitignore` first.

Related: [[Overview]] · [[Adding-a-Feature]]
