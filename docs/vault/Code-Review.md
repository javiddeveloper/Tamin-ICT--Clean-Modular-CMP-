---
tags: [convention, howto]
---

# Code Review

فرایند کامل و مرجع در **`review/README.md`** است — این صفحه فقط اشاره‌ی سریع.

## در یک نگاه

هر merge request باز یک فایل گزارش دارد: `review/MR-<id>.md` که **روی branch مبدأ همان MR** commit می‌شود، نه روی `develop`.

```
Round 1  → گزارش نوشته و روی branch مبدأ push می‌شود
         → developer اصلاح می‌کند و push می‌کند
Round 2  → همان فایل بند به بند به‌روز می‌شود
         → … تا بسته شدن همه‌ی موارد
         → فایل حذف و push می‌شود
         → merge از طریق GitLab توسط انسان
```

## چهار قاعده‌ای که نباید نقض شوند

1. **working tree کاربر دست نمی‌خورد.** برای checkout کردن branch مبدأ از `git worktree` استفاده کن، نه `git checkout` روی پوشه‌ی اصلی.
2. **commit بررسی‌شده در header گزارش ثبت می‌شود.** بدون آن، round بعدی نمی‌تواند diff افزایشی بگیرد.
3. **`✅ برطرف شد` فقط بعد از خواندن کد.** پیام commit مدرک نیست.
4. **merge به `develop` کار reviewer نیست.** بعد از بسته شدن گزارش فقط اعلام آمادگی می‌شود.

## دامنه‌ی diff

همیشه نسبت به merge-base:

```powershell
$base = git merge-base origin/develop mr-<id>
git diff $base..mr-<id>
```

## پیدا کردن MRهای باز بدون GitLab API

GitLab رفرنس‌های `refs/merge-requests/<id>/head` را expose می‌کند، پس بدون توکن هم می‌شود فهرستشان کرد:

```powershell
git ls-remote origin "refs/merge-requests/*/head"
```

MRهایی که head آن‌ها ancestor شاخه‌ی `develop` نیست، هنوز merge نشده‌اند. توجه: این «باز بودن» را قطعی نمی‌کند — ممکن است closed یا draft باشند. برای وضعیت واقعی، عنوان و کامنت‌ها به `glab` نیاز است:

```powershell
glab auth login --hostname ci.tamin.ir   # یک‌بار، توسط کاربر
```

مرتبط: [[Adding-a-Feature]] · [[Naming-Conventions]] · [[Home]]
