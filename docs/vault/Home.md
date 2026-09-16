---
tags: [moc]
---

# TaminX — Project Knowledge Map

**تأمین همراه** (`TaminX`) — a Kotlin Multiplatform + Compose Multiplatform app for Android and iOS, built for the Iranian Social Security Organization.

> This vault is the starting point for every session. Read the relevant page here before searching the codebase.

## Architecture

- [[Overview]] — layers and how a request flows through them
- [[Modules]] — every module and what it owns
- [[MVI-Pattern]] — `BaseViewModel` and the State/Intent/Event contract
- [[Navigation]] — Compose Navigation graphs and the `xxxGraph` pattern
- [[TopArea-System]] — scroll-driven collapsing headers (nested scroll fold/unfold + snap)
- [[Dependency-Injection]] — Koin modules and registration order
- [[Networking]] — Ktor, the five HTTP clients, auth and token refresh
- [[Database]] — Room KMP, DAOs, schemas
- [[Data-and-Caching]] — repository patterns, offline-first decision rubric, the `.first()` vs `.collect()` shipped bug
- [[Error-Handling]] — exception-based error chain, `BaseDTO`, no `Result<T>` wrapper
- [[Payments]] — the one payment flow every feature uses, and its mock gateway
- [[Debug-Tooling]] — `TokenSlot`, the back-to-back debug login, and the `isDebug` gate

## Conventions

- [[Naming-Conventions]] — the file-naming contract (and why the build does not enforce it) ⚠️
- [[Adding-a-Feature]] — checklist for a new screen or feature module
- [[Mock-Data-Pattern]] — repository-decorator pattern for temporary manual-QA mock data
- [[Gotchas]] — recurring, non-obvious traps worth checking before repeating them
- [[Pagination]] — the cross-platform `Paginator` (no AndroidX Paging in this project)
- [[Typography]] — Vazirmatn, `ss01` Persian digits vs `toPersianDigits()`
- [[Theme]] — colors, spacing, radius, and string tokens (no hardcoded UI values)

## Build and release

- [[Build-and-Run]] — gradle commands, JDK, flavors
- [[CI-CD]] — the GitLab pipeline

## Domain

- [[Feature-Flags]] — dynamic menu, `FeatureFlag`, `FeatureManager`
- [[History-Objection]] — اعتراض به سوابق ناموجود
- [[Objection-Insurance]] — اعتراض به سابقه کسری‌دار (Phase 1 data + stub)
- [[Disability-Pension-Status]] — مستمری از کارافتادگی, network/domain/usecase layer done, UI not started ⚠️
- [[Debt-Objection-Status]] — پیگیری وضعیت اعتراض به بدهی, `:feature:workshops` → `ui/objectionStatus`
- [[Stories]] — «تازه‌ها» rail and the full-screen story viewer (front-end only, mock catalogue) ⚠️
- [[AI-Agent]] — architecture of the AI assistant rewrite
- [[AI-Agent-API-Contract]] — exact JSON contract the client parses
- [[Agent-Markdown]] — the markdown every assistant answer uses, formulas, service ports
- [[Deep-Links]] — the one feature-flag gate every link goes through
- [[Glossary]] — Persian domain term ↔ name in code

## Reference

- [[Reference-old-android]] — what `old_android/` is and why it is never committed ⚠️
- [[Tech-Stack]] — versions and key libraries
