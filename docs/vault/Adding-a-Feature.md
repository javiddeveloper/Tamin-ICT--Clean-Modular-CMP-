---
tags: [convention, howto]
---

# Adding a Feature or Screen

## A new screen inside an existing feature

1. `ui/<screen>/contract/<Screen>Contract.kt` — the four types `State` / `PartialState` / `Event` / `Intent`
2. `ui/<screen>/<Screen>ViewModel.kt` — extends `BaseViewModel` ([[MVI-Pattern]])
3. `ui/<screen>/<Screen>Screen.kt` (+ `components/` for parts specific to this screen)
4. Register the ViewModel in that feature's `di/<X>Module.kt`
5. Add the route to `sealed interface <X>Route` and a `composableWithFadeTransitions<…>` entry in `Navigation.kt` ([[Navigation]])

## A brand-new feature module

1. Create `feature/<name>/` with a `build.gradle.kts` applying `id("TaminHamrah.kmp.feature")`
   (that plugin already supplies every core module, Koin, coroutines and turbine — do not add them manually)
2. `include(":feature:<name>")` in `settings.gradle.kts`
3. `Navigation.kt` — routes + `fun NavGraphBuilder.<name>Graph(...)` + `fun NavController.navigateTo<Name>()`
4. `di/<Name>Module.kt`
5. **Register the Koin module in `sharedModules` in `shared/.../di/Koin.kt`** — miss this and you get a runtime failure, not a compile error ([[Dependency-Injection]])
6. Attach the graph in `shared/.../ui/navigation/TaminHamrahNavGraph.kt`
7. If it opens from the server menu: add a case in `FeatureNavigation.kt` and the matching `FeatureFlag` value ([[Feature-Flags]])
8. Add the dependency from `:shared` in `shared/build.gradle.kts`

## When adding a model or endpoint

Work downward and get the suffixes right ([[Naming-Conventions]]):

```
core-network/model/<domain>/XDTO.kt          ← JSON shape
core-network/apiService/<domain>/XApiService.kt
core-network/dataSource/<domain>/XRemoteDataSource(+Impl).kt
core-data/data/mapper/XMapper.kt             ← DTO → DN
core-domain/model/<domain>/XDN.kt
core-domain/repository/<domain>/XRepository.kt       ← interface
core-data/…/XRepositoryImpl.kt
core-domain/useCases/<domain>/XUseCase.kt
core-ui/model/<domain>/XPR.kt
core-ui/mapper/<domain>/XMapper.kt           ← DN → PR
```

## Before calling it done

```powershell
.\gradlew.bat :feature:<name>:compileDebugKotlinAndroid
.\gradlew.bat testDebugUnitTest
```

## Easy things to forget

- Registering the Koin module (step 5 above)
- Changing an Entity without committing the new schema JSON ([[Database]])
- Building a component that already exists in `core-ui/ui/components/` (59 files — search first)
- Changing a server endpoint in only one of the two places it is declared ([[Networking]])
- Guarding submit-style intents with `if (state.isLoading) return@flow` — `flatMapMerge` runs intents concurrently ([[MVI-Pattern]])
