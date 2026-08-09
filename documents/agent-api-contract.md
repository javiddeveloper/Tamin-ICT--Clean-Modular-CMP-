# قرارداد JSON دستیار هوشمند (Agent API)

این سند شکل دقیق داده‌ای است که اپ موبایل پارس می‌کند. نام فیلدها **حساس به حروف** است و
دقیقاً همان چیزی است که در کلاینت خوانده می‌شود؛ هر کلید ناشناخته بی‌صدا نادیده گرفته می‌شود
(`ignoreUnknownKeys`)، بنابراین یک اشتباه تایپی خطا نمی‌دهد و صرفاً آن مقدار `null` می‌ماند.

---

## ۱. چرخه‌ی کلی

```
GET  chat-allowed              → آیا کاربر مجاز است؟
POST search/service            → ارسال پرامپت (سرویس‌های بیمه‌ای)
POST search/rule               → ارسال پرامپت (جستجوی قوانین)
GET  request/track/{id}        → پیگیری تا رسیدن به DONE
GET  request/cancel/{id}       → لغو
```

کلاینت بعد از ارسال، هر `eta` ثانیه `track` را صدا می‌زند تا `status` برابر `DONE` شود
(حداکثر ۵ بار).

---

## ۲. بررسی دسترسی — `GET chat-allowed`

```json
{
  "status": 200,
  "family": "SUCCESSFUL",
  "reason": "OK",
  "data": {
    "canStartChat": true,
    "chatToken": "eyJhbGciOi...",
    "errorMessage": null
  }
}
```

`canStartChat: false` صفحه را می‌بندد و `errorMessage` را نشان می‌دهد.

---

## ۳. ارسال پرامپت — `POST search/service` یا `search/rule`

**نوع درخواست: `multipart/form-data`**

| بخش | نوع | توضیح |
|---|---|---|
| `data` | `application/json` | بدنه‌ی زیر |
| `file` | `audio/mp4` | **اختیاری** — فقط وقتی کاربر ویس فرستاده |

```json
{
  "prompt": "سابقه بیمه من را نشان بده",
  "sessionId": "sess-123",
  "lastEntity": "doctorName:ali, proficiency:heart",
  "chatToken": "eyJhbGciOi...",
  "userType": "INSURED"
}
```

- `sessionId` در اولین پیام `null` است؛ از پاسخ سرور برمی‌گردد و در پیام بعدی ارسال می‌شود.
- `lastEntity` برای حفظ رشته‌ی گفتگو است و عیناً از پاسخ قبلی برگردانده می‌شود.
- وقتی ویس ارسال می‌شود `prompt` خالی است و متن باید از فایل صوتی استخراج شود.

**پاسخ (پذیرش درخواست):**

```json
{
  "status": 200,
  "family": "SUCCESSFUL",
  "reason": "OK",
  "data": { "id": "req-abc-123", "eta": 3, "status": "PENDING" }
}
```

---

## ۴. پیگیری — `GET request/track/{id}`

```json
{
  "status": 200,
  "family": "SUCCESSFUL",
  "reason": "OK",
  "data": {
    "id": "req-abc-123",
    "eta": 2,
    "status": "DONE",
    "message": null,
    "errorStatus": null,
    "result": {
      "sessionId": "sess-123",
      "lastEntity": "…",
      "message": null,
      "entities": [ /* بخش ۵ */ ]
    }
  }
}
```

مقادیر مجاز `data.status`:

| مقدار | رفتار کلاینت |
|---|---|
| `PENDING` | ادامه‌ی pollها |
| `DONE` | `result.entities` رندر می‌شود |
| `FAILED` | پیام خطا با دکمه‌ی تلاش دوباره |
| `CANCEL` | تولید متوقف می‌شود |

---

## ۵. Entity — واحد پاسخ

هر entity **یک حباب** در چت می‌سازد و به همان ترتیب آرایه، یکی‌یکی نمایش داده می‌شوند.

```json
{
  "key": "dastmozd_infos",
  "stepNumber": 1,
  "message": "کاربر محترم، سابقه شما به شرح زیر است",
  "itemType": null,
  "payload": { "filter": ["startDate:14020101", "endDate:14031229"] },
  "data": null
}
```

| فیلد | نوع | نقش |
|---|---|---|
| `key` | string | تعیین می‌کند کدام سرویس اجرا شود (بخش ۷) |
| `stepNumber` | int | ترتیب نمایش در کارت مراحل |
| `message` | string | متن همراه پاسخ / عنوان جدول |
| `payload` | object | ورودی سرویس (بخش ۶) |
| `data` | array | آیتم‌های الحاقی، از جمله پیشنهادها |

### پیشنهادهای ادامه‌ی گفتگو

داخل `data` قرار می‌گیرند و کلاینت آن‌ها را **درون همان حباب پاسخ** نشان می‌دهد:

```json
"data": [
  { "item_type": "prompt_item", "prompt": "حقوق بازنشستگی من چقدر است؟" },
  { "item_type": "prompt_item", "prompt": "آخرین نسخه پزشکی من" }
]
```

---

## ۶. انواع محتوا (payload)

برای پاسخ‌هایی که سرور خودش محتوا را می‌سازد، `payload.type` نوع حباب را تعیین می‌کند.
**افزودن نوع جدید فقط با هماهنگی تیم موبایل** — هر `type` ناشناخته به متن ساده تبدیل می‌شود.

### ۶.۱ متن ساده
```json
{ "type": "text", "text": "مصارف ماهانه حدود ۲۱۰ همت است." }
```

### ۶.۲ متن با هدر
```json
{
  "type": "rich_text",
  "title": "کسری ۹۰ همتی تامین اجتماعی",
  "text": "متن اصلی خبر …",
  "footnote": "منبع: دنیای اقتصاد — ۱۴۰۵/۰۴/۲۲"
}
```
`footnote` اختیاری است.

### ۶.۳ کلید–مقدار
```json
{
  "type": "key_value",
  "title": "ارقام کلیدی",
  "items": [
    { "key": "کسری ماهانه", "value": "۹۰ همت" },
    { "key": "بدهی دولت",  "value": "۷۵۰ همت" }
  ]
}
```

### ۶.۴ جدول (ریسپانسیو)
```json
{
  "type": "table",
  "title": "ترکیب بدهی‌ها",
  "columns": ["عنوان", "مبلغ", "سهم"],
  "rows": [
    ["بدهی دولت", "۷۵۰ همت", "۷۹٪"],
    ["بدهی کارفرمایان", "۲۰۰ همت", "۲۱٪"]
  ]
}
```
- طول هر ردیف باید با `columns` برابر باشد؛ سلول کم با `-` پر می‌شود.
- ستون‌های زیاد مشکلی ندارد — جدول خودش افقی اسکرول می‌شود.

### ۶.۵ نمودار
```json
{
  "type": "chart",
  "title": "منابع و مصارف ماهانه",
  "kind": "bar",
  "labels": ["مصارف", "وصولی", "کسری"],
  "series": "ماهانه",
  "values": [210, 120, 90],
  "unit": "همت"
}
```
`kind`: `bar` | `line` | `pie` — مقدار نامعتبر به `bar` برمی‌گردد.
`values` عددی است (نه رشته) و طولش باید با `labels` بخواند.

### ۶.۶ تصویر
```json
{ "type": "image", "image": "https://…/photo.jpg", "caption": "شرح تصویر" }
```

### ۶.۷ ویدیو
```json
{
  "type": "video",
  "video": "https://…/clip.mp4",
  "thumbnail": "https://…/poster.jpg",
  "duration": "266000",
  "caption": "عنوان ویدیو"
}
```
- `video` باید **لینک مستقیم فایل** باشد (mp4/HLS) — لینک صفحه‌ی وب پخش نمی‌شود.
- `duration` بر حسب **میلی‌ثانیه** و به‌صورت **رشته**.
- `thumbnail` بهتر است فریمی از خود ویدیو باشد.

### ۶.۸ صوت
```json
{
  "type": "voice",
  "audio": "https://…/voice.mp3",
  "duration": "372000",
  "caption": "خلاصه صوتی",
  "waveform": [2000, 6500, 12000, 18000]
}
```
`waveform` اختیاری است (اعداد ۰ تا ۳۲۷۶۷)؛ اگر نباشد یک موج پیش‌فرض رسم می‌شود.

### ۶.۹ لینک داخلی (رفتن به صفحه‌ای در اپ)
```json
{ "type": "deep_link", "title": "مشاهده اطلاعات کارگاه", "destination": "workshops" }
```
مقادیر مجاز `destination`: `disability_pension` · `deferred_installment` · `contracts` ·
`workshops` · `prescription`

### ۶.۱۰ لینک وب
```json
{ "type": "web_link", "title": "متن کامل گزارش", "url": "https://…" }
```

### ۶.۱۱ مراحل پردازش
```json
{
  "type": "processing",
  "steps": ["بررسی درخواست", "دریافت آمار", "آماده‌سازی پاسخ"],
  "active": "2",
  "completed": "true"
}
```

### ۶.۱۲ خطا
```json
{ "type": "error", "text": "دریافت آمار لحظه‌ای ممکن نشد." }
```

### ۶.۱۳ پیشنهادها (به‌صورت حباب مستقل)
```json
{ "type": "suggestions", "prompts": ["سوال اول", "سوال دوم"] }
```

---

## ۷. کلیدهای سرویس (`key`)

برای پاسخ‌هایی که داده از سرویس‌های داخلی اپ می‌آید، `key` تعیین‌کننده است و `payload`
فقط فیلترها را می‌دهد:

```json
{ "filter": ["startDate:14020101", "endDate:14031229"] }
```

کلیدهای پیاده‌سازی‌شده:

| گروه | کلیدها |
|---|---|
| دستمزد | `dastmozd_infos` · `dastmozd_infos_last` · `dastmozd_infos_per_year` · `dastmozd_infos_salary` · `dastmozd_infos_sum_total` |
| میانگین دستمزد | `average_dastmozd_infos` · `average_dastmozd_infos_per_date` · `dastmozdinfos_last_pay` |
| مستمری | `pension_inquiry_all` · `pension_inquiry_last` |
| فیش و حکم | `fish` · `fish_last` · `hokm` · `hokm_last` |
| درمان | `booklet_req` |
| سوابق شغلی | `history_job_infos` · `history_job_infos_last` |
| پروفایل | `profile_info` · `get_dependent` |
| عمومی | `general_response` · `message` · `law` |
| ورود به صفحه | `disability_pension` · `deferred_installment_certificate` · `register_contract` · `complete_info_of_real_workshop` · `patient_history` |

کلید ناشناخته باعث خطا نمی‌شود؛ اگر `message` داشته باشد به‌صورت متن ساده نمایش داده می‌شود.

---

## ۸. نکات مهم برای پیاده‌سازی

1. **نام فیلدها** — `sessionId` و `stepNumber` به‌صورت camelCase؛ `item_type` و `prompt`
   داخل `data` به‌صورت snake_case (این ناهماهنگی از نسخه‌ی قبلی باقی مانده و کلاینت
   هر دو را همین‌طور می‌خواند).
2. **ترتیب** — entityها به ترتیب آرایه و با فاصله‌ی زمانی نمایش داده می‌شوند؛ ترتیب منطقی
   پاسخ را در همان آرایه رعایت کنید.
3. **لینک مدیا** — باید بدون احراز هویت و بدون انقضای کوتاه قابل دسترسی باشند، چون گفتگو
   به‌صورت محلی ذخیره می‌شود و ممکن است روزها بعد دوباره باز شود.
4. **`lastEntity`** — هر چه بفرستید عیناً در درخواست بعدی برمی‌گردد؛ برای حفظ context از آن
   استفاده کنید.
5. **اندازه** — هر entity یک حباب است. پاسخ طولانی را به چند entity بشکنید تا یکی‌یکی و
   خوانا نمایش داده شود.
