# فرایند Code Review

این پوشه محل نگه‌داری گزارش‌های review است. هر merge request باز، یک فایل دارد:

```
review/
├── README.md        ← همین فایل — فرایند
├── TEMPLATE.md      ← اسکلت گزارش جدید
└── MR-<id>.md       ← یک فایل به ازای هر MR باز
```

نام فایل دقیقاً `MR-<شماره>.md` است — مثل `MR-161.md`، `MR-164.md`. شماره همان عددی است که در URL مربوط به merge request می‌آید:
`https://ci.tamin.ir/base/development/finance/tatmin-kmp/-/merge_requests/164`

**فایل گزارش روی branch مبدأ همان MR است، نه روی `develop`.** به این ترتیب توسعه‌دهنده بدون هیچ کار اضافه‌ای آن را کنار کدش می‌بیند، و وقتی MR بسته شد فایل هم با آن می‌رود.

---

## چرخه‌ی کامل

```
Round 1  ─ reviewer ─→  گزارش روی branch مبدأ commit می‌شود
                            ↓
              developer اصلاح می‌کند و push می‌کند
                            ↓
Round 2  ─ reviewer ─→  همان فایل به‌روزرسانی می‌شود (بند به بند)
                            ↓
              … تا وقتی همه‌ی موارد بسته شوند
                            ↓
            reviewer فایل را حذف و push می‌کند
                            ↓
              merge از طریق GitLab انجام می‌شود
```

---

## گام ۱ — آماده‌سازی

⚠️ **قبل از هر چیز، working tree را دست نزن.**

روش امن، ساختن یک worktree جداست تا پوشه‌ی اصلی پروژه و تغییرات ذخیره‌نشده‌ی کاربر دست‌نخورده بماند:

```powershell
git fetch origin "refs/merge-requests/<id>/head:mr-<id>" --force
git worktree add ../review-mr-<id> mr-<id>
```

اگر به هر دلیل worktree ممکن نبود، اول باید تأیید شود که `git status` تمیز است. **هرگز روی working tree ای که تغییر ذخیره‌نشده دارد `checkout` نزن.**

برای پیدا کردن branch متناظر با یک شماره‌ی MR:

```powershell
git ls-remote origin "refs/merge-requests/<id>/head"
# سپس sha را با خروجی git ls-remote --heads origin تطبیق بده
```
