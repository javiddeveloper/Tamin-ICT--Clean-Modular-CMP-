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

> ⚠️ **زبان گزارش‌ها انگلیسی است.** فایل‌های `MR-<id>.md` و `TEMPLATE.md` کاملاً به انگلیسی نوشته می‌شوند — عنوان‌ها، شرح موارد، جدول خلاصه و revision log. فقط همین `README.md` که سند فرایند است فارسی می‌ماند.

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

قبل از push، تأیید کن که sha مربوط به `mr-<id>` با sha همان branch روی remote یکی است. اگر یکی نبود یعنی branch جلو رفته و باید دوباره fetch کنی.

---

## گام ۲ — نوشتن گزارش (Round 1)

`TEMPLATE.md` را کپی کن به `review/MR-<id>.md` و پرش کن. **گزارش به انگلیسی نوشته می‌شود.**

**دامنه‌ی diff** — همیشه نسبت به merge-base، نه نسبت به نوک `develop`:

```powershell
$base = git merge-base origin/develop mr-<id>
git diff --stat $base..mr-<id>
```

**قواعد نوشتن:**

- هر مورد باید **قابل بازبینی** باشد: مسیر فایل، شماره‌ی خط، و اینکه دقیقاً چه چیزی باید عوض شود.
- برای هر مورد یک سناریوی خرابی مشخص بنویس، نه توصیف کلی. «this may cause problems» گزارش نیست.
- لحن توصیفی باشد نه قضاوتی. موضوع کد است، نه نویسنده‌ی آن.
- اگر مطمئن نیستی، وضعیت `⬜ needs confirmation` بگذار — نه اینکه به‌عنوان باگ قطعی ثبتش کنی.
- اگر مشکلی از قبل وجود داشته و این MR فقط منتقلش کرده، همین را صریح بنویس.
- بخش «What was done well» را خالی نگذار اگر واقعاً چیزی هست.
- **commit بررسی‌شده حتماً در header ثبت شود.** بدون آن، round بعدی نمی‌داند از کجا diff بگیرد.

سپس فایل را روی همان branch commit و push کن:

```powershell
git add review/MR-<id>.md
git commit -m "docs(review): add review report for MR !<id>"
git push origin HEAD:<نام-branch-اصلی>
```

---

## گام ۳ — بازبینی (Round 2 به بعد)

```powershell
git fetch origin "refs/merge-requests/<id>/head:mr-<id>" --force
git diff <commit-round-قبل>..mr-<id>          # فقط چیزی که عوض شده
```

**بند به بند** برو. برای هر مورد در جدول خلاصه، ستون وضعیت را به‌روز کن:

| نماد | معنی |
|---|---|
| `⬜ open` | هنوز اصلاح نشده |
| `✅ fixed` | بررسی شد و درست است |
| `🔄 needs rework` | تلاش شده ولی هنوز مشکل دارد — توضیح بده چرا |
| `➖ rejected` | توسعه‌دهنده دلیل آورده و پذیرفته شده — دلیل را ثبت کن |
| `⬜ needs confirmation` | منتظر پاسخ درباره‌ی نیت طراحی |

هر round یک سطر به **Revision log** اضافه می‌کند.

`✅ fixed` فقط وقتی ثبت می‌شود که کد واقعاً خوانده شده باشد. پیام commit توسعه‌دهنده مدرک نیست.

اگر در round جدید مشکل تازه‌ای پیدا شد، به انتهای فهرست اضافه می‌شود (شماره‌های قبلی جابه‌جا نمی‌شوند، وگرنه ارجاع‌های قبلی بی‌معنا می‌شود).

---

## گام ۴ — بستن

وقتی هیچ موردی در وضعیت `⬜` یا `🔄` نماند:

```powershell
git rm review/MR-<id>.md
git commit -m "docs(review): close out review for MR !<id>"
git push origin HEAD:<نام-branch-اصلی>
```

سپس worktree پاک شود:

```powershell
git worktree remove ../review-mr-<id>
git branch -D mr-<id>
```

⛔ **merge کردن به `develop` کار reviewer نیست.** بعد از بسته شدن گزارش، فقط اعلام کن که MR آماده است. خودِ merge از طریق GitLab و توسط انسان انجام می‌شود — تا approval، پایپ‌لاین CI و تصمیم زمان‌بندی سر جای خودشان بمانند.

---

## نکات

- اگر MR شامل branch دیگری هم merge شده باشد (مثل `!166` که `!164` را در خود دارد)، این را در header گزارش بنویس؛ وگرنه دو گزارش موازی روی یک کد نوشته می‌شود.
- گزارش را روی `develop` commit نکن. اگر اشتباهاً آنجا رفت، حذفش کن.
- فایل‌های این پوشه موقت‌اند. اگر `review/` جز `README.md` و `TEMPLATE.md` چیزی نداشت، یعنی هیچ MR بازی در انتظار بازبینی نیست.
- برای ثبت مستقیم کامنت روی GitLab به `glab` نیاز است (نصب شده، ولی احراز هویت با `glab auth login --hostname ci.tamin.ir` باید یک بار توسط کاربر انجام شود). بدون آن، گزارش فقط به‌صورت همین فایل منتقل می‌شود.
