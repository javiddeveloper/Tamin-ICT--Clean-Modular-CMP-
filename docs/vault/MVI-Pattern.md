---
tags: [architecture, convention]
---

# الگوی MVI و BaseViewModel

فایل: `core/core-ui/src/commonMain/kotlin/com/tamin/taminhamrah/base/BaseViewModel.kt`

```kotlin
abstract class BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>(initialState: STATE) : ViewModel()
```

چهار پارامتر ژنریک — همه‌ی ViewModelهای پروژه از این ارث می‌برند.

## قرارداد

| عضو | جهت | توضیح |
|---|---|---|
| `sendIntent(intent)` | UI → VM | تنها راه ورود رویداد کاربر |
| `uiState: StateFlow<STATE>` | VM → UI | تک‌منبع حقیقتِ صفحه |
| `events: Flow<EVENT>` | VM → UI | اثرات یک‌بارمصرف (ناوبری، اسنک‌بار) |
| `sendEvent(event)` | داخل VM | `protected` |
| `doAsyncTask { … }` | داخل VM | لانچ در `viewModelScope` |

سه متد که باید override شوند:

```kotlin
protected abstract fun handleIntent(intent: INTENT): Flow<PARTIAL_STATE>
protected abstract fun reduceState(currentState: STATE, partialState: PARTIAL_STATE): STATE
protected abstract fun createErrorState(message: String): PARTIAL_STATE
```

## نحوه‌ی کار پایپ‌لاین

```
intentChannel (UNLIMITED)
  → flatMapMerge { handleIntent(it).catch { emit(createErrorState(...)) } }
    → scan(initialState) { state, partial -> reduceState(state, partial) }
      → _uiState.value = newState
```

نکات که راحت گاز می‌گیرند:

- **`flatMapMerge`** یعنی intentها **موازی** اجرا می‌شوند، نه صف‌شده. اگر ترتیب مهم است، خودت باید در `handleIntent` مدیریتش کنی.
- `catch` روی هر intent جداست، پس یک خطا کل پایپ‌لاین را نمی‌کشد. پیام پیش‌فرض خطا `"خطای نامشخص"` است.
- `eventChannel` از نوع `BUFFERED` است و با `receiveAsFlow` مصرف می‌شود — یعنی تک‌مصرف‌کننده.
- `doAsyncTask` هیچ dispatcher سفارشی‌ای ست نمی‌کند (یک `// todo` در کد اشاره به همین دارد) — کار IO را خودت به dispatcher مناسب ببر.

## فایل Contract

هر صفحه یک `contract/<Screen>Contract.kt` دارد که چهار نوع را کنار هم تعریف می‌کند: `State` (data class)، `PartialState` (sealed)، `Event` (sealed)، `Intent` (sealed).
نمونه‌های خوب برای الگوبرداری: `feature/profile/.../ui/identity/contract/IdentityInContract.kt` و `.../ui/activeRelation/contract/ActiveRelationContract.kt`.

## تست

`turbine` در همه‌ی ماژول‌های فیچر به‌صورت خودکار در `commonTest` هست (از `TaminHamrahKmpFeaturePlugin`) — برای assert روی `uiState` و `events` از آن استفاده کن.

مرتبط: [[Overview]] · [[Navigation]] · [[Adding-a-Feature]]
