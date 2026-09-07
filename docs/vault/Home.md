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
- [[Payments]] — the one payment flow every feature uses, and its mock gateway

## Conventions

- [[Naming-Conventions]] — the file-naming contract (and why the build does not enforce it) ⚠️
- [[Adding-a-Feature]] — checklist for a new screen or feature module
- [[Pagination]] — the cross-platform `Paginator` (no AndroidX Paging in this project)
- [[Typography]] — Vazirmatn, `ss01` Persian digits vs `toPersianDigits()`
- [[Theme]] — colors, spacing, radius, and string tokens (no hardcoded UI values)

## Build and release

- [[Build-and-Run]] — gradle commands, JDK, flavors
- [[CI-CD]] — the GitLab pipeline

## Domain

- [[Feature-Flags]] — dynamic menu, `FeatureFlag`, `FeatureManager`
- [[History-Objection]] — اعتراض به سوابق ناموجود, and why its repository is still a stub ⚠️
- [[AI-Agent]] — architecture of the AI assistant rewrite
- [[AI-Agent-API-Contract]] — exact JSON contract the client parses
- [[Glossary]] — Persian domain term ↔ name in code

## Reference

- [[Reference-old-android]] — what `old_android/` is and why it is never committed ⚠️
- [[Tech-Stack]] — versions and key libraries
