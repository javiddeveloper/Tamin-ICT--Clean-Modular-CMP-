---
tags: [architecture, convention, gotcha]
---

# Repositories, Caching, and Room Migrations

## Repository placement

No `BaseRepository` abstraction exists. Each `XRepositoryImpl` in
`core/core-data/src/commonMain/kotlin/com/tamin/taminhamrah/data/repository/**`
directly implements its `core-domain` interface, injecting a `XRemoteDataSource`
(core-network) and, only where caching is needed, a Room DAO ([[Database]]).

## Offline-first is selective, not global — decide deliberately

Only some domains cache to Room (personal inbox, user identity/profile, health,
treatment); most (workshops, contracts, pension, personal, history, user-requests) are
network-only. When adding a new repository method, decide:

- **Needs to show data instantly / work with a poor connection?** → cache-then-network
  pattern below.
- **Just a write/action endpoint, or data that's cheap to always refetch?** →
  network-only: `flow { emit(remoteDataSource.xxx(...)) }` or a plain `suspend fun`.
- **Genuinely unsure?** This is a product/UX decision, not a technical default — flag it
  rather than guessing.

## Cache-then-network pattern (when caching is warranted)

Canonical example, `core/core-data/.../data/repository/personalInbox/PersonalInboxRepositoryImpl.kt`:

```kotlin
override fun getInboxItems(query: ApiQueryParamDN?): Flow<List<PersonalInboxItemDN>> = flow {
    val localItems = personalInboxDao.getInboxItems().first()
    emit(localItems.map { it.toDomain() })                 // 1. emit cache immediately
    try {
        val remoteItems = personalInboxRemoteDataSource.getInboxItems(...)
        personalInboxDao.replaceAllInboxItems(remoteItems.map { it.toEntity() })  // 2. refresh cache
    } catch (e: Exception) {
        if (localItems.isEmpty()) throw e                  // 3. only fail if nothing to show
    }
    emitAll(personalInboxDao.getInboxItems().map { it.map { e -> e.toDomain() } })  // 4. re-emit
}.distinctUntilChanged()
```

DAO side: pair `@Insert`/`@Upsert` with a `@Query("DELETE FROM ...")`, combined
atomically in a default-body `@Transaction` function (`replaceAllInboxItems`-style) —
guarantees `Flow` observers never see an intermediate empty list during a background
refresh. For "current user" singleton-style data, use a fixed-PK single-row cache (see
`PersonalInboxSizeEntity.SINGLE_ROW_ID`) or clear-then-insert (`UserDao.upsertIdentityInfo`).

There is no TTL/expiry/ETag concept anywhere — freshness is "whatever was last
successfully fetched." Don't add expiry-timestamp fields unless asked.

### ⚠️ Consuming a cache-then-network `Flow` — `.collect`, never `.first()`

A cache-then-network method emits **twice**: once from the local cache, then again
after the network refresh. A ViewModel (or use case) that consumes this `Flow` with
`.first()` silently gets only the stale cache snapshot and never sees the fresh network
result — **this was a real, shipped bug**: `HistoryObjectionStepperViewModel`'s
province/city handlers used `.first()` and users saw only whatever cities happened to
already be cached, not the full network-refreshed list.

Always `.collect { emit(...) }` (typically inside a `try/finally` if loading-state
cleanup is needed) when a ViewModel needs to react to a cache-then-network `Flow`'s
eventual fresh value — see `StudentInsuranceContractViewModel.loadBranches()` for the
established, correct pattern. Because the underlying Room `Flow` never completes on its
own (it keeps observing table changes), a `.collect` here never completes either —
that's expected, not a leak to fix.

## Flow-vs-suspend consistency

Within one repository interface, methods that represent an observable data stream must
consistently return `Flow<T>`, not a mix of `suspend fun ...: T` and `fun ...: Flow<T>`.
This was a real, fixed inconsistency for personal inbox — don't reintroduce the same
mix in new repositories/use cases.

## Room schema changes — migration safety is a hard gate

`TaminHamrahDatabase.kt` has **no real `Migration` objects anywhere in the repo**.
`getRoomDatabase()` calls `.fallbackToDestructiveMigration(dropAllTables = true)`, so
any bump of `@Database(version = ...)` **drops and recreates every table**, wiping
local data for existing installs.

Before adding/changing an `@Entity` in a way that requires a schema version bump:

1. State explicitly that this will destructively wipe local cached data on upgrade (not
   server data — just the Room cache), since no migration path exists.
2. Confirm whether that's acceptable, or whether a real `Migration` should be written
   instead — don't bump the version silently as a side effect of an unrelated change.

## Datastore (`core-datastore`)

Wraps `multiplatform-settings` (`Settings`) directly — no extra abstraction layer.
Currently stores exactly two things: theme preference (`UserPreferencesRepositoryImpl`,
JSON-serialized `UserData` under key `"user_data_key"`) and auth/PKCE state
(`TokenStoreManagerImpl`: access token, refresh token, user id, code verifier,
token-valid flag — all plain `putString`/`getStringOrNull`, unencrypted). Feature
modules never touch `Settings`/DataStore directly — always go through `core-domain`
interfaces (`UserPreferencesRepository`, `TokenStoreManager`).

Related: [[Database]] · [[Mock-Data-Pattern]] · [[Networking]] · [[Overview]]
