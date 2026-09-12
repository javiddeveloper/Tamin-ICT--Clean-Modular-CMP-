---
tags: [convention, architecture]
---

# Pagination

Offset pagination for the Tamin list endpoints, shared by Android and iOS.

There is **no AndroidX Paging 3** in this project and none should be added — it is an
Android-only library and would leave iOS with a duplicate implementation. The engine below
is plain Kotlin + coroutines in `:core:core-domain`, so both platforms run the same code.

## Where the pieces live

| Piece | Location | Role |
|---|---|---|
| `PageDN<T>` | `core-domain/model/paging/PageDN.kt` | one page: `items` + backend `total` |
| `PaginationConfig` | `core-domain/paging/PaginationConfig.kt` | `pageSize`, `firstPage`, and `ApiQueryParamDN.forPage()` |
| `PaginationState<T>` | `core-domain/paging/PaginationState.kt` | what consumers render |
| `Paginator<T>` | `core-domain/paging/Paginator.kt` | the state machine |
| `OnLoadMore` / `PagingFooter` | `core-ui/ui/paging/PagingListSupport.kt` | scroll trigger + footer |

`Paginator` sits in core-domain rather than core-ui because paging is about *driving
repository calls*, and core-domain already owns `ApiQueryParamDN`. It has no Compose or
platform dependency, and every feature module already depends on core-domain through
`TaminHamrahKmpFeaturePlugin`, so wiring it up needs no build changes.

## The backend contract (already existed — do not change it)

Every list endpoint takes `page` / `start` / `limit` via
[[Networking|ApiQueryBuilder]] and answers with `{ "total": ..., "list": [...] }`.
`start` is the real row offset; `page` is the ordinal that goes with it.

**Paging is zero-based**, confirmed against the live endpoint:

```
announcement/to-user?page=0&start=0&limit=10
announcement/to-user?page=1&start=10&limit=10
announcement/to-user?page=2&start=20&limit=10
```

This matches `ApiQueryParamDN`'s own defaults (`page = 0`, `start = 0`) and the legacy
native app's `GeneralPagingSource`, where `offset = position * pageSize` and `position`
starts at 0. So `PaginationConfig.firstPage` defaults to **0** and `start` is derived as
`(page - firstPage) * pageSize`.

`firstPage` stays configurable only because a handful of existing callers pass `page = 1`
with a large `limit` to pull a whole list in one request (`CityListQuery`) — there
`start = 0` is what actually drives the response, so the ordinal is inert. Contracts list
and branch list use 1-based `page` with `ContractsPaging.PAGE_SIZE` (10) and return
`PagedListDN`, same shape as `getFreeJobWages`. Do not change the default without a
captured request proving a given endpoint is one-based.

`total` is declared `String?` on `PersonalInboxListDTO` while the server sends a JSON
number; this decodes because the shared `Json` is configured with `isLenient = true`
(`NetworkKoinModule`). `toIntOrNull()` is therefore the correct read.

## Ownership rules

`Paginator` is the only place that decides:

- which page comes next, and the `start`/`limit` that go with it
- that a request is already in flight and a duplicate must be dropped
- that the collection is exhausted (backend `total`, or a short page as fallback)
- that a failure is retryable, and that scrolling must **not** auto-retry it
- what a reset does to items, error and end-of-list

The ViewModel maps `paginator.state` into one MVI partial state. The screen calls
`OnLoadMore` and renders `PagingFooter` — it never computes a page number.

Thread safety: all bookkeeping happens under a `Mutex`, the fetch runs outside the lock,
and a generation counter makes `refresh()` win over a page that is still in flight.

## Adding pagination to a screen

1. **Repository** — add `fun getXPage(query: ApiQueryParamDN): Flow<PageDN<XDN>>`. Keep the
   existing non-paged method if other features use it. Cache to Room **only when
   `query.start == 0`**: `replaceAll*` clears the table, so caching an appended page would
   drop everything before it.
2. **Use case** — a thin `GetXPageUseCase(query)`; register it in `DomainModule`.
3. **ViewModel** — `private val paginator = Paginator(loadPage = { getXPageUseCase(it).first() })`,
   then map `paginator.state` into a single `PagingChanged` partial state, and add
   `LoadNextPage` / `RetryNextPage` / `RefreshInbox`-style intents that call
   `loadNext()` / `retry()` / `refresh()`.
4. **Screen** — `lazyListState.OnLoadMore(enabled = !state.endReached && state.paginationError == null) { ... }`
   and a `PagingFooter` item after the list content.

The worked example is `feature/my-inbox` against `getPersonalInboxItemsPageUseCase`.

## Mutations reset the pager

After a delete or any change that shifts rows, call `paginator.refresh()` — not a re-subscribe.
With offset paging, mutating the collection invalidates every offset after the change, so a
removed row can otherwise reappear from a stale page.

See also: [[MVI-Pattern]], [[Networking]], [[Database]]
