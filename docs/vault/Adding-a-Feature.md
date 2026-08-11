---
tags: [convention, howto]
---

# چک‌لیست افزودن فیچر یا صفحه‌ی جدید

## صفحه‌ی جدید داخل یک فیچر موجود

1. `ui/<screen>/contract/<Screen>Contract.kt` — چهار نوع `State` / `PartialState` / `Event` / `Intent`
2. `ui/<screen>/<Screen>ViewModel.kt` — ارث از `BaseViewModel` ([[MVI-Pattern]])
3. `ui/<screen>/<Screen>Screen.kt` (+ `components/` برای اجزای مختصِ صفحه)
4. ثبت ViewModel در `di/<X>Module.kt` همان فیچر
5. افزودن route به `sealed interface <X>Route` و یک `composableWithFadeTransitions<…>` در `Navigation.kt` ([[Navigation]])

## ماژول فیچر کاملاً جدید

1. پوشه `feature/<name>/` + `build.gradle.kts` با `id("TaminHamrah.kmp.feature")`
   (این پلاگین همه‌ی coreها، Koin، coroutines و turbine را خودش می‌دهد — دستی اضافه نکن.)
2. `include(":feature:<name>")` در `settings.gradle.kts`
3. `Navigation.kt` — routeها + `fun NavGraphBuilder.<name>Graph(...)` + `fun NavController.navigateTo<Name>()`
4. `di/<Name>Module.kt`
5. **ثبت ماژول Koin در `sharedModules` داخل `shared/.../di/Koin.kt`** — فراموش شود، خطای runtime می‌گیری نه compile ([[Dependency-Injection]])
6. وصل کردن گراف در `shared/.../ui/navigation/TaminHamrahNavGraph.kt`
7. اگر از منوی سرور باز می‌شود: افزودن case در `FeatureNavigation.kt` و مقدار متناظر در `FeatureFlag` ([[Feature-Flags]])
8. وابستگی `:shared` به ماژول جدید در `shared/build.gradle.kts`

## هنگام افزودن مدل یا endpoint

ترتیب رو به پایین را رعایت کن و پسوندها را دقیق بگذار — build وگرنه می‌شکند ([[Naming-Conventions]]):

```
core-network/model/<domain>/XDto.kt         ← شکل JSON
core-network/apiService/<domain>/XApiService.kt
core-network/dataSource/<domain>/XRemoteDataSource(+Impl).kt
core-data/data/mapper/XMapper.kt            ← Dto → DN
core-domain/model/<domain>/XDN.kt
core-domain/repository/<domain>/XRepository.kt      ← interface
core-data/…/XRepositoryImpl.kt
core-domain/useCases/<domain>/XUseCase.kt
core-ui/model/<domain>/XPR.kt
core-ui/mapper/<domain>/XMapper.kt          ← DN → PR
```

## قبل از تمام‌شده اعلام کردن

```powershell
.\gradlew.bat :feature:<name>:compileDebugKotlinAndroid
.\gradlew.bat testDebugUnitTest
```

## چیزهایی که راحت فراموش می‌شوند

- ثبت ماژول Koin (بند ۵ بالا)
- تغییر Entity بدون commit کردن schema JSON جدید ([[Database]])
- ساختن کامپوننتی که از قبل در `core-ui/ui/components/` هست (۵۹ فایل — اول بگرد)
- تغییر آدرس سرور فقط در یک جا از دو جای موجود ([[Networking]])
