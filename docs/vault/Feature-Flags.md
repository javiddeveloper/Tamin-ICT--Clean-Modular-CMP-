---
tags: [architecture, domain]
---

# فیچرفلگ و منوی داینامیک

> سند مفصل و اصلی: `documents/features.md` — این صفحه فقط خلاصه‌ی قابل مرور است.

## ایده

منوی صفحه‌ی اصلی از سرور می‌آید (`MainServiceDto`)، نه هاردکد. هر سرویس دارای:

- `id` — شناسه‌ی عددی
- `name` — عنوان نمایشی
- `showRole` — آرایه‌ی نقش‌ها: `1` بیمه‌شده، `2` مستمری‌بگیر، `3` کارفرما
- `status` — وضعیت
- `message` — پیام خطا در صورت غیرفعال بودن
- `url` — در حالت WebView

`menu.json` در ریشه‌ی ریپو نمونه/داده‌ی محلی همین ساختار است؛ `MockMenuData.kt` در core-network نسخه‌ی موقت داده است.

## وضعیت‌ها (`FeatureStatus`)

| وضعیت | رفتار UI |
|---|---|
| `ACTIVE` | کلیک‌پذیر، رفتن به صفحه‌ی بومی |
| `TEMPORARY_DISABLED` | آلفا ۵۰٪، کلیک مسدود، دلیل زیر عنوان |
| `DISABLED` / `COMPLETELY_DISABLED` | مثل بالا |
| `ENABLED_WITH_ERROR` | کلیک‌پذیر و پررنگ، ولی هشدار قرمز زیر عنوان |
| `WEB_VIEW` | باز کردن `url` |

## زنجیره‌ی اجرا

```
کلیک کاربر
 → Intent (مثل OnServiceClick) → ViewModel
 → FeatureFlag.fromId(service.id)          ← FeatureFlag.kt
 → FeatureManager.getFeatureStatus(flag)   ← FeatureManagerImpl در core-data
 → Event: NavigateToService(flag) | NavigateToWeb(url)
 → NavController.navigateToFeature(flag)   ← shared/.../ui/navigation/FeatureNavigation.kt
```

`FeatureFlag` هرگز نباید با عدد خام جایگزین شود — `when` روی enum را exhaustive نگه می‌دارد.

## دستیار هوشمند (Agent)

- فلگ اختصاصی: `AGENT(2000)` — عمداً خارج از بازه‌ی کارفرمایی `1001–1012`.
- کنترل دسترسی دومرحله‌ای: اول `FeatureFlag` (بدون تماس شبکه)، بعد `CheckChatAllowedUseCase` (API).
  نقطه‌ی پیاده‌سازی: `AgentViewModel.handleCheckPermission()`
- FAB ورود به Agent در `TaminHamrahNavGraph.kt` فقط وقتی دیده می‌شود که مسیر جاری `Route.Home` باشد **و** وضعیت `AGENT` برابر `Enabled`.
- اولویت پیام غیرفعال بودن: `entity.message` › `featureManager.getDisabledMessage(flag)` › fallback ثابت. (در `AgentActionDispatcher.dispatch()`)

اسناد تکمیلی: `documents/agent.md` (معماری SDUI و فرم‌های embedded در چت) و `documents/agent-api-contract.md` (قرارداد JSON، case-sensitive، با `ignoreUnknownKeys`).

مرتبط: [[Navigation]] · [[Glossary]]
