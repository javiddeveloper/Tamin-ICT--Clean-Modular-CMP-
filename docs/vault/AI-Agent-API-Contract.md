---
tags: [reference, domain]
---

# AI Agent — JSON API Contract

The exact data shape the mobile app parses. Field names are **case sensitive** and are read verbatim by the client. Unknown keys are silently ignored (`ignoreUnknownKeys`), so a typo does not raise an error — the value simply stays `null`. Absorbed from the former `documents/agent-api-contract.md`.

Architecture behind this contract: [[AI-Agent]].

---

## 1. Overall cycle

```
GET  chat-allowed              → is the user allowed?
POST search/service            → send prompt (insurance services)
POST search/rule               → send prompt (regulation search)
GET  request/track/{id}        → poll until DONE
GET  request/cancel/{id}       → cancel
```

After sending, the client calls `track` every `eta` seconds until `status` is `DONE`, up to 5 times.

---

## 2. Access check — `GET chat-allowed`

```json
{
  "status": 200,
  "family": "SUCCESSFUL",
  "reason": "OK",
  "data": {
    "canStartChat": true,
    "chatToken": "eyJhbGciOi...",
    "errorMessage": null,
    "canSendVoice": true,
    "ttl": 3600
  }
}
```

- The whole answer is cached (`AgentAccessStore`), a refusal included; a transport failure keeps the last answer. The cached token is dropped **before** each check.
- `canStartChat: false` hides the assistant's entry point and the screen shows `errorMessage`; no conversation is created.
- `canSendVoice` enables the microphone. `ttl` is read but not acted on (same as the native app).
- Checked when home is shown and when the assistant opens.

---

## 3. Sending a prompt — `POST search/service` or `search/rule`

**Request type: `multipart/form-data`**

| Part | Type | Notes |
|---|---|---|
| `file` | `audio/wav` | **optional** — only for a voice message; sent **first** |
| `data` | `application/json` | the body below |

Every key is always written, nulls included (native Gson used `serializeNulls`); `AgentRequestDTO.toRequestBody` encodes with `encodeDefaults` and `explicitNulls` on, because the app's shared `Json` drops both.

```json
{
  "prompt": "سابقه بیمه من را نشان بده",
  "sessionId": "category_356ad05a-…",
  "lastEntity": "",
  "chatToken": "eyJhbGciOi...",
  "userType": "INSURED",
  "personal_info": { "national_id": "0012345678", "pensioner_id": null, "first_name": "…", "last_name": "…" },
  "prompt_type": "text",
  "state": null,
  "history": [],
  "device_type": "MOBILE",
  "response_type": "show_to_user"
}
```

- `chatToken` is read from `AgentAccessStore` at send time, not from the screen.
- `userType`: `ANONYMOUS` without a login, otherwise the stored type (`INSURED`, `PENSIONER`, `temporary`).
- **Expired token** — HTTP 400, or a body containing `INVALID_OR_EXPIRED_CHAT_TOKEN` (or `INVALID_OR_EXPIRED_TOKEN`), raises `ChatTokenExpiredException` in `AiChatTokenPlugin`. `SendAgentPromptUseCase` checks chat permission once and resends with the new token; a refusal ends the prompt with the server's reason; a second rejection fails.
- `sessionId` is the **local conversation id** (`category_<uuid>`, the `agent_sessions` row id), sent from the first message on. The server keys its memory of the chat on it; the `sessionId` in the answer is not used. Without a cached conversation (identity unknown) a per-conversation id is generated in memory.
- `lastEntity`, `state`, `history` come from the previous answer and are sent back untouched; `""` / `null` / `[]` when there is none. An answer without one of them keeps the previous value. The app never reads `state` or `history` (their shape changes, e.g. `pending_form` as a string or an object); they are stored as raw JSON in `agent_sessions.agentState` / `agentHistory`, so a reopened chat continues where it stopped.
- `personal_info` comes from `GetAgentPersonalInfoUseCase`: national id and name from the cached identity, `pensioner_id` only for pensioners (looked up once per national id). The whole block is `null` without a login or a real national id; a failed pensioner lookup sends `pensioner_id: null` rather than blocking the prompt.
- `prompt_type` is `voice` when a `file` part is attached, otherwise `text`.
- The multipart body is buffered into a `ByteArrayContent` before sending; Ktor's streamed multipart is a one-shot OkHttp body, which Chucker shows as "(body is empty)".
- When a voice message is sent, `prompt` is empty and the text must be extracted from the audio file.
- **Voice format is fixed: WAV, 16 kHz, mono, 16-bit PCM, file name `<uuid>.wav`** (`WavFormat`), as the native app recorded with WaveRecorder. The server's firewall rejects other audio: an `.m4a` (`audio/mp4`) upload came back **403 with an HTML "عدم امکان دسترسی" page**, never reaching the assistant. Android records with `AudioRecord` and writes the RIFF header on stop; iOS uses `AVAudioRecorder` with linear PCM. On the Android emulator the microphone records silence unless host audio input is enabled, and the server then answers «پیام شما خالی است».

**Response (request accepted):**

```json
{
  "status": 200,
  "family": "SUCCESSFUL",
  "reason": "OK",
  "data": { "id": "req-abc-123", "eta": 3, "status": "PENDING" }
}
```

---

## 4. Tracking — `GET request/track/{id}`

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
      "entities": [ /* section 5 */ ]
    }
  }
}
```

Allowed values of `data.status`:

| Value | Client behaviour |
|---|---|
| `PENDING` | keep polling |
| `DONE` | render `result.entities` |
| `FAILED` | `data.message` (or a default text) with a retry button |
| `CANCEL` | stop generation |

---

## 5. Entity — the unit of response

> `stepNumber` / `itemType` are also accepted as `step_number` / `item_type` (the server sends snake_case), `sessionId` also as `session_id`. `result.render_mode` is `SERVER` when entities carry rendered markdown.

### 5.0 Markdown entity (`render_mode: SERVER`)

```json
{
  "key": "general_response",
  "item_type": "markdown",
  "step_number": 1,
  "message_id": "…",
  "data": [
    { "item_type": "markdown_item", "format": "markdown", "content_version": "1", "text": "### عنوان

…" },
    { "item_type": "prompt_item", "prompt": "سؤال بعدی" }
  ]
}
```

No service runs for it and its key's flag is not checked; blank texts are dropped. Syntax, formulas and links: [[Agent-Markdown]]. Links use `[label](@key)` (keys in `DeepLinkKey`), `agent://prompt?text=…` or `https://*.tamin.ir`; there is no `TOOLBAR_TITLE` — the app uses the menu's name. See [[Deep-Links]].

### Link items

```json
{ "item_type": "deeplink", "action_type": "local_deeplink", "deeplink": { "to": "contract_freelance" }, "title": "مشاغل آزاد" }
```

Appended as a button to the answer; an unknown `to` sends `title` as a prompt.

Each entity produces **one bubble** in chat, displayed one at a time in array order.

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

| Field | Type | Role |
|---|---|---|
| `key` | string | selects which service runs (section 7) |
| `stepNumber` | int | display order in the step card |
| `message` | string | accompanying text / table title |
| `payload` | object | service input (section 6) |
| `data` | array | attached items, including suggestions |

### Follow-up suggestions

These go inside `data` and the client shows them **within the same response bubble**:

```json
"data": [
  { "item_type": "prompt_item", "prompt": "حقوق بازنشستگی من چقدر است؟" },
  { "item_type": "prompt_item", "prompt": "آخرین نسخه پزشکی من" }
]
```

---

## 6. Content types (`payload`)

For responses where the server builds the content itself, `payload.type` determines the bubble type.
**New types only by agreement with the mobile team** — any unknown `type` degrades to plain text.

### 6.1 Plain text
```json
{ "type": "text", "text": "مصارف ماهانه حدود ۲۱۰ همت است." }
```

### 6.2 Text with a header
```json
{
  "type": "rich_text",
  "title": "کسری ۹۰ همتی تامین اجتماعی",
  "text": "متن اصلی خبر …",
  "footnote": "منبع: دنیای اقتصاد — ۱۴۰۵/۰۴/۲۲"
}
```
`footnote` is optional.

### 6.3 Key–value
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

### 6.4 Table (responsive)
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
- Each row's length must match `columns`; a missing cell is filled with `-`.
- Many columns are fine — the table scrolls horizontally on its own.

### 6.5 Chart
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
`kind`: `bar` | `line` | `pie` — an invalid value falls back to `bar`.
`values` must be numeric (not strings) and its length must match `labels`.

### 6.6 Image
```json
{ "type": "image", "image": "https://…/photo.jpg", "caption": "شرح تصویر" }
```

### 6.7 Video
```json
{
  "type": "video",
  "video": "https://…/clip.mp4",
  "thumbnail": "https://…/poster.jpg",
  "duration": "266000",
  "caption": "عنوان ویدیو"
}
```
- `video` must be a **direct file link** (mp4/HLS) — a web page link will not play.
- `duration` is in **milliseconds**, as a **string**.
- `thumbnail` should ideally be a frame from the video itself.

### 6.8 Audio
```json
{
  "type": "voice",
  "audio": "https://…/voice.mp3",
  "duration": "372000",
  "caption": "خلاصه صوتی",
  "waveform": [2000, 6500, 12000, 18000]
}
```
`waveform` is optional (values 0–32767); without it a default waveform is drawn.

### 6.9 Deep link (navigate to an in-app screen)
```json
{ "type": "deep_link", "title": "مشاهده اطلاعات کارگاه", "destination": "workshops" }
```
Allowed `destination` values: `disability_pension` · `deferred_installment` · `contracts` · `workshops` · `prescription`

### 6.10 Web link
```json
{ "type": "web_link", "title": "متن کامل گزارش", "url": "https://…" }
```

### 6.11 Processing steps
```json
{
  "type": "processing",
  "steps": ["بررسی درخواست", "دریافت آمار", "آماده‌سازی پاسخ"],
  "active": "2",
  "completed": "true"
}
```

### 6.12 Error
```json
{ "type": "error", "text": "دریافت آمار لحظه‌ای ممکن نشد." }
```

### 6.13 Suggestions (as a standalone bubble)
```json
{ "type": "suggestions", "prompts": ["سوال اول", "سوال دوم"] }
```

---

## 7. Service keys (`key`)

For responses whose data comes from the app's own services, `key` is the selector and `payload` only carries filters:

```json
{ "filter": ["startDate:14020101", "endDate:14031229"] }
```

Implemented keys:

| Group | Keys |
|---|---|
| Wage | `dastmozd_infos` · `dastmozd_infos_last` · `dastmozd_infos_per_year` · `dastmozd_infos_salary` · `dastmozd_infos_sum_total` |
| Average wage | `average_dastmozd_infos` · `average_dastmozd_infos_per_date` · `dastmozdinfos_last_pay` |
| Pension | `pension_inquiry_all` · `pension_inquiry_last` |
| Payslip and decree | `fish` · `fish_last` · `hokm` · `hokm_last` |
| Treatment | `booklet_req` |
| Employment history | `history_job_infos` · `history_job_infos_last` |
| Profile | `profile_info` · `get_dependent` |
| General | `general_response` · `message` · `law` |
| Screen entry | `disability_pension` · `deferred_installment_certificate` · `register_contract` · `complete_info_of_real_workshop` · `patient_history` |

An unknown key is not an error; if it carries a `message` it is rendered as markdown. `workers_payment` is also accepted as `worker_payment`.

Client service answers are markdown built from the server's `message` (as the title) and the data; see [[Agent-Markdown]] for each key's rules.

---

## 8. Implementation notes

1. **Field naming** — `sessionId` and `stepNumber` are camelCase, while `item_type` and `prompt` inside `data` are snake_case. This inconsistency is inherited from the previous version and the client reads both as-is.
2. **Ordering** — entities are displayed in array order with a delay between them, so put the response in the logically correct order in that array.
3. **Media links** — must be reachable without authentication and without a short expiry, because conversations are stored locally and may be reopened days later.
4. **`lastEntity`** — whatever you send comes back verbatim on the next request; use it to preserve context.
5. **Size** — each entity is one bubble. Split a long answer into several entities so it renders progressively and stays readable.

Related: [[AI-Agent]] · [[Feature-Flags]] · [[Networking]]
