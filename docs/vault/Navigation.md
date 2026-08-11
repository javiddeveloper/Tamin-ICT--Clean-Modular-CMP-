---
tags: [architecture]
---

# ناوبری

کتابخانه: **Compose Multiplatform Navigation** (`org.jetbrains.androidx.navigation:navigation-compose`) با routeهای **type-safe** مبتنی بر `kotlinx.serialization`.
(نکته: `androidx.navigation3` هم در version catalog و در `androidApp` هست، ولی گراف اصلی روی navigation-compose سوار است.)

## فایل‌های کلیدی

| فایل | نقش |
|---|---|
| `shared/.../ui/navigation/TaminHamrahNavGraph.kt` | گراف ریشه — همه‌ی `xxxGraph()`ها اینجا وصل می‌شوند |
| `shared/.../ui/navigation/NavRoutes.kt` | routeهای سطح اپ |
| `shared/.../ui/navigation/NavigationTab.kt` | تب‌های نوار پایین |
| `shared/.../ui/navigation/FeatureNavigation.kt` | `NavController.navigateToFeature(flag)` — پل بین `FeatureFlag` و مقصد |
| `feature/<x>/.../Navigation.kt` | routeها و گراف همان فیچر |

## الگوی هر فیچر

```kotlin
@Serializable
sealed interface ProfileRoute {
    @Serializable data object Graph : ProfileRoute
    @Serializable data class  Main(val userId: String? = null) : ProfileRoute
    @Serializable data object ElectronicFile : ProfileRoute
    // …
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,   // مقصدهای خارج از این فیچر → callback
    onOpenUrl: (String) -> Unit,
    onBack: () -> Unit
) {
    navigation<ProfileRoute.Graph>(startDestination = ProfileRoute.Main()) { … }
}
```

قاعده‌ی مهم: **ناوبری داخل فیچر** مستقیم با `navController.navigate(Route.X)` انجام می‌شود؛ **ناوبری به فیچر دیگر** به‌صورت callback به بالا پاس داده می‌شود تا ماژول‌های فیچر به هم وابسته نشوند.

## گرفتن ViewModel

```kotlin
val vm = koinViewModel<XViewModel>()                          // معمولی، scope همان صفحه
val vm = backStackEntry.sharedViewModel<XViewModel>(navController)  // اشتراکی در طول یک گراف
```

`sharedViewModel` یک extension داخلی پروژه است (`com.tamin.taminhamrah.ui.sharedViewModel`).

## انیمیشن انتقال

`composableWithFadeTransitions<Route>` — نسخه‌ی fade از `composable`، در `core-ui` تعریف شده. برای صفحات معمولی از همین استفاده کن تا حس اپ یکدست بماند؛ `composable` خام هم در کد هست ولی الگوی غالب نیست.

## مسیریابی داینامیک از منو

`FeatureFlag` (آمده از سرور) → `navigateToFeature(flag)` → متد `navigateToXxx()` که هر فیچر خودش export می‌کند. جزئیات کامل در [[Feature-Flags]] و `documents/features.md`.

مرتبط: [[Modules]] · [[Feature-Flags]]
