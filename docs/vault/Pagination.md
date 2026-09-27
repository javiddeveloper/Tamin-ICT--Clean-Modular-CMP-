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
   existing non-paged method if other features use it. Network-only by default; to cache it,
   follow "Offline-first" below (only the first page may `replaceAll*`, later pages upsert).
2. **Use case** — a thin `GetXPageUseCase(query)`; register it in `DomainModule`.
3. **ViewModel** — `private val paginator = Paginator(loadPage = { getXPageUseCase(it).first() })`,
   then map `paginator.state` into a single `PagingChanged` partial state, and add
   `LoadNextPage` / `RetryNextPage` / `RefreshInbox`-style intents that call
   `loadNext()` / `retry()` / `refresh()`.
4. **Screen** — `lazyListState.OnLoadMore(enabled = !state.endReached && state.paginationError == null) { ... }`
   and a `PagingFooter` item after the list content.

The worked example is `feature/my-inbox` against `getPersonalInboxItemsPageUseCase` — it is
offline-first, so it uses `loadPages` instead of the step-3 `loadPage { … .first() }` form.

## Offline-first (optional)

Offline-first lives in the **existing** `getXPage` repository method — same as every other
cached flow in the app ([[Database]]); no extra method, flag or use case. Per request it emits:

1. this page's slice from Room — same `start`/`limit` **and filters** the server gets — as
   `PageDN(items, isFromCache = true)`, if non-empty;
2. then the network page (and writes it to Room);
3. if the network fails, it throws **only when that slice was empty** — otherwise the flow
   just completes after the cached slice.

Because the method now emits twice, **every caller must consume the whole flow** — `.first()`
would stop at the cache and never reach the network, silently. Making a method offline-first
therefore means updating all its callers:

```kotlin
Paginator(loadPages = { query -> getXPageUseCase(query) })   // pager
getXUseCase(...).collect { ... }                            // re-render on each emission
getXUseCase(...).last()                                     // one-shot: network, or cache offline
```

If the cached load runs under `merge(...)` alongside a network-only loader, give **each** loader
its own try/catch: offline, the network-only one throws and `merge` cancels its siblings, so the
cache never reaches the UI (history-objection stepper: insurance types killed the cached provinces).

### Migrating a `getXPage` to offline-first — checklist

Needs a Room table + DAO for the items (read ordered like the server, `@Upsert`, and a
`replaceAll*` `@Transaction`). If the table is new, see [[Database]] first — a schema version
bump wipes local data. Then:

1. **Repository** — rewrite the existing method to this shape (the inbox version, verbatim):

   ```kotlin
   override fun getXPage(query: ApiQueryParamDN): Flow<PageDN<XDN>> = flow {
       // 1. this page's slice of the cache, same order + filters as the server
       val cached = xDao.getAll().first().drop(query.start).take(query.limit)
       if (cached.isNotEmpty()) emit(PageDN(items = cached.map { it.toDomain() }, isFromCache = true))

       // 2. network; offline with a cached slice → stop quietly, nothing cached → throw
       val response = try {
           remote.getX(query)
       } catch (e: CancellationException) {
           throw e
       } catch (e: Exception) {
           if (cached.isEmpty()) throw e
           return@flow
       }

       // 3. first page replaces the cache (drops stale/deleted rows); later pages append
       val entities = response.list.orEmpty().map { it.toEntity() }
       if (query.start == 0) xDao.replaceAll(entities) else xDao.upsert(entities)
       emit(PageDN(items = response.list.orEmpty().map { it.toDomain() }, total = response.total))
   }
   ```

   **Preferred table shape for a paged list** (used from construction files on): a dedicated
   `XPageEntity(listKey, position, @Embedded row)` with `primaryKeys = ["listKey", "position"]`,
   written with `position = query.start + index` and read with
   `WHERE listKey = :k ORDER BY position LIMIT :limit OFFSET :offset`. `listKey` comes from
   `query.pageCacheKey(parentIds…)` (core-data `repository/paging/PageCacheKey.kt`): parent ids +
   filters + sorts, so each search/parent is its own list and replacing one never wipes another.
   `position` keeps the server's order exactly. The DAO gets `getPageSlice` / `upsertPage` /
   `clearPages(listKey)` / `@Transaction replacePages`. Worked examples:
   `ConstructionFilePageEntity` + `ConstructionFileDao` + `getConstructionFilesPage`;
   `ContractAffairPageEntity` + `ContractAffairDao` + `ContractAffairRepositoryImpl.getContractsPage`
   (embeds the existing `ContractEntity`; its read mapper is `toContractAffairDomain()` because
   `ContractEntity.toDomain()` already maps to the contracts feature's model, and it returns `null`
   for a nested object whose columns are all empty, so a cached row equals the network one);
   `InspectionDao` + `InspectionPageEntities.kt` + `InspectionRepositoryImpl` — four lists in one
   repository (inspections, workshop inspections, branches, job titles), so it uses a private
   `offlineFirstPage(query, readCached, fetch, write)` helper; the two inspection lists share one
   table and are told apart by a `pageCacheKey(scope)` prefix;
   `ConstructionInsurancePageDao` + `ConstructionInsurancePageEntities.kt` — beneficiaries,
   installment letters (keyed by workshop + branch), debit list and installments (keyed by debit
   number + branch), same private helper in `ConstructionInsuranceRepositoryImpl`. A child list
   (e.g. one debit letter's installments) is only available offline if it was opened online;
   `EmployerServicesPageDao` + `EmployerServicesPageEntities.kt` — خدمات غیرحضوری کارفرمایان's
   workshops-without-agreement and contract rows (keyed by workshop + branch). These were `suspend`
   methods returning `PagedListDN`; they became `Flow<PageDN<…>>` (a suspend fun can't emit the
   cache and then the network) and every fake was updated. The nested workshop block is stored via
   `@Embedded(prefix = "workshop_") WorkshopSummaryColumns`. **`getEmployerAgreements` is still
   network-only** — it is shared with 4 feature/workshops ViewModels and was left for later;
   `JobTitlePageDao` + `JobTitlePageEntity` + `CommonRepositoryImpl.getJobTitlePage` — the
   new-member form's job picker; its one-shot by-code lookup uses `.last()` and gets its own
   `listKey` from the `JOB_CODE` filter.

   The inbox's `drop/take` over a plain table loads the whole table; fine for tens–hundreds of rows. For bigger tables add a
   `LIMIT :limit OFFSET :offset` query (`CityProvinceDao.getCitiesSlice`). If the list has
   **filters**, the slice and the replace must use the same filter, or one screen's list wipes
   another's — see the cities example below.
2. **Every caller** — grep the method/use case and replace `.first()`: pager →
   `Paginator(loadPages = { query -> getXPageUseCase(query) })`; one-shot → `.last()`; stream →
   `.collect`. A missed `.first()` compiles and silently never hits the network once cached.
3. **Use case KDoc** — say "offline-first, collect the whole flow".
4. **Tests** — cache then network; offline with cache; offline and empty throws; later page
   appends; first page replaces. Make the fake DAO behave like Room (upsert merges by id, reads
   sorted) — see `PersonalInboxRepositoryImplTest.FakeDao`.

The simplest worked example is `PersonalInboxRepositoryImpl.getInboxItemsPage` + `MyInboxViewModel`.

### What `Paginator` does with it

`Paginator` collects every emission of one request:

- a cached **first** page shows immediately (`isFromCache`, `isRefreshing`, no full-screen spinner);
- the last emission wins — normally the network page, which replaces it;
- if the last emission is still the cache (offline) it is used like any page: end-of-list
  comes from its size / `total`, so cached slices keep paging offline until a short one;
- a thrown error after the cache keeps the cached items and sets `error`.

Network-only callers use the secondary constructor: `Paginator(loadPage = { getXPageUseCase(it).first() })`.

**Worked example — cities and provinces** (`CityProvinceRepositoryImpl.getCitiesPage`,
`getCitiesByProvincePage`, `getProvincesPage` — all three; a screen's city list may use either
city method, e.g. complete-employer-info uses `getCitiesByProvincePage`):

- A fresh **first page** (`start == 0`) from the network **replaces** the cached list for that
  scope in one transaction (`replaceCitiesMatching(cityName, provinceCode)` — same WHERE as the
  read; `replaceAllProvinces`), so stale rows are dropped. Later pages are appended. Scoping
  matters: opening province 07 must not wipe province 08, and a "teh" search must not wipe other
  cities. Trade-off: after a refresh, the cache holds only the pages fetched since.
  **Known, accepted:** the *unfiltered* city list's scope is every city, so its fresh first page
  (fraction contract, or the ill-days/add-dependent picker with an empty search) deletes all
  cached cities, including every province's list, and keeps only that page. A province picker
  opened offline afterwards may be empty until it is loaded online again.
- Cities are read with `CityProvinceDao.getCitiesSlice(cityName, provinceCode, limit, offset)`.
  The server filters from `CityListQuery` (`CITY_NAME` LIKE `*term*`, `PROVINCE_CODE_CITY`) are
  translated to that query (province codes compared ignoring leading zeros). Any other filter
  or a sort skips the cache, so it never shows rows
  the server wouldn't return.
- Provinces (~31 rows) are sliced in memory from `getAllProvinces()`; filtered requests skip the cache.
- Offline order is `cityName`/`provinceName` ASC, which may differ from the server's order; a
  list mixing online and offline pages can show a duplicate or a gap at the seam.
- Callers: ill-days, add-dependent, workshop recently-added-members (`loadPages`), complete
  employer info provinces and cities (`loadPages`), history-objection and contract flow (`collect`),
  fraction contract (`.last()`).

**Worked example — personal inbox** (`getInboxItemsPage`): no filters, so the scope is the whole
table and the checklist code applies as-is. After a delete, `refresh()` briefly shows the cached
first page (still holding the deleted item) until the network page replaces it.

**Worked example — my requests** (`UserRequestRepositoryImpl.getUserRequestsPage` + feature/userRequest
`UserRequestsViewModel`): the search (refCode / type) is not in the `ApiQueryParamDN` — the filters are
built in core-data — so the method takes `(search, page)` and the ViewModel's `loadPages` lambda reads
the current search; a new search sets it and calls `refresh()`. It reuses the shared `user_requests`
table (also read by cartable and Home): only an unfiltered first page `replaceAll`s, anything else upserts.
The status tabs filter loaded items client-side, so the screen wraps `OnLoadMore` in
`key(state.requests.size)` — otherwise a tab that hides a whole page leaves the end in view and
never re-triggers. Before this, the screen sent no `limit` and showed only the first 10 requests.

## Mutations reset the pager

After a delete or any change that shifts rows, call `paginator.refresh()` — not a re-subscribe.
With offset paging, mutating the collection invalidates every offset after the change, so a
removed row can otherwise reappear from a stale page.

See also: [[MVI-Pattern]], [[Networking]], [[Database]]
