---
tags: [convention, howto]
---

# Temporary Manual-QA Mock Data — Repository Decorator Pattern

When a feature needs fake/mock data wired in for manual testing (no real backend
test account available yet), **do not** subclass the real `UseCase` classes and mark
them `open` to allow overriding — that touches production files (even a one-word
`open` addition) that then have to be reverted later, and is easy to forget to revert.

## The pattern: decorate the repository interface, not the use case

Implement the repository *interface* via Kotlin's `by` delegation, overriding only
the specific method(s) that need fake data and forwarding everything else to the
real instance:

```kotlin
class MockXRepository(
    private val real: XRepository,
) : XRepository by real {
    override fun theOneMethodThatNeedsFaking(): Flow<SomeDN> = flow {
        emit(FakeData.someValue)
    }
}
```

Wire it into Koin in its own dev-only module, loaded **after** the real data module,
so it overrides the interface binding:

```kotlin
val xMockModule = module {
    single<XRepository> { MockXRepository(get<XRepositoryImpl>()) }
}
```

This works because the production registration (`singleOf(::XRepositoryImpl) { bind<XRepository>() }`
in `core-data`'s `DataKoinModule.kt`) registers the real singleton under *both* the
concrete type and the bound interface — so `get<XRepositoryImpl>()` still resolves the
real thing even after a later-loaded module overrides the `XRepository`-typed binding.

Add the mock module to `sharedModules` in `shared/.../di/Koin.kt`, after the real data
module.

## Why this over use-case subclassing

First attempt at this (disability-pension manual QA — see [[Disability-Pension-Status]])
added `open` to 4 real `UseCase` classes in `core-domain` so `Mock*UseCase` subclasses
could override `invoke()`, mirroring the older `MockSubdominantUseCase` precedent. The
user rejected it: "wasn't there a simpler way — can't we give the mock data to the
repository at the Impl layer?" Rebuilt as the decorator above — **zero production files
touched**, the whole thing lives in one new package + one new DI module + one added line
in `sharedModules`, and is trivial to delete wholly before merging.

## Checklist

- One new package (e.g. `core/core-domain/.../repository/xMock/`) holding the
  `Mock*Repository` classes and any fake data objects.
- One new Koin module file registering the mock bindings.
- One added line in `sharedModules` (`shared/.../di/Koin.kt`), positioned after the real
  data module so the override wins.
- **This must be removed before merging** — delete the mock package, delete the mock
  module, and remove its line from `sharedModules`. Leave a comment or PR note as a
  reminder since nothing else will catch a forgotten mock module.
- Don't mock a generic, already-working endpoint that isn't specific to the feature
  under test (e.g. a shared image-upload use case, or an endpoint owned by an
  already-shipped feature) — only fake the feature-specific method(s) actually blocking
  manual QA.

Related: [[Disability-Pension-Status]] · [[Dependency-Injection]] · [[Data-and-Caching]]
