package com.tamin.taminhamrah.dataSource.agent

/**
 * The answer shapes the other two fixtures do not reach. Together with
 * [FAKE_AGENT_MARKDOWN_RESPONSE] (server-rendered markdown) and [FAKE_AGENT_SHOWCASE_RESPONSE]
 * (one of every payload type) this is what the "all states" scenario of
 * [AgentRemoteDataSourceFakeImpl] is stitched from.
 *
 * Every entity here is answered without a backend — data-driven services (`law`, `appoinmet`,
 * `general_response`, `message`, the screen entry keys) or the demo `showcase` key — so the tour
 * works logged out. What each one exercises:
 * - a pie chart (the showcase only has bar and line)
 * - an error bubble that cannot be retried, and a service that throws (retry re-runs the service)
 * - a payload type the app does not know, which must degrade to plain text
 * - a `general_response` whose text carries an app link and prompt items
 * - a `message` entity with only `message_item`s, no `message`
 * - `law` and `appoinmet` items laid out by their client services
 * - an unknown key with text, link items (one known target, one unknown) and prompts
 * - an unknown key with nothing to show, which must produce no bubble at all
 * - a screen entry key (`disability_pension`) → title + one button
 * - media with missing pieces: a broken image URL, a voice without waveform, a video without poster
 * - a table with a short row (missing cells) and a key/value readout with a divider row
 * - a long plain text (typing animation) and mixed Persian/Latin digits
 * - standalone suggestions, which fold into the reply before them
 */
internal const val FAKE_AGENT_EDGE_CASES_RESPONSE = """{
    "id": "edge-cases-fixture-request",
    "eta": 2,
    "status": "DONE",
    "message": null,
    "result": {
        "session_id": "edge-cases-fixture-session",
        "lastEntity": null,
        "entities": [
            {
                "key": "showcase",
                "step_number": 1,
                "message": null,
                "payload": {"type": "chart", "title": "سهم منابع", "kind": "pie", "labels": ["حق بیمه", "دولت", "سرمایه‌گذاری", "سایر"], "series": "سهم", "values": [62, 21, 12, 5], "unit": "٪"},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 2,
                "message": null,
                "payload": {"type": "error", "text": "این سرویس برای شما فعال نیست.", "retryable": false},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 3,
                "message": null,
                "payload": {"type": "throw", "text": "سرویس سوابق پاسخ نداد (خطای ساختگی سرویس)."},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 4,
                "message": "نوع ناشناخته‌ی payload به متن ساده تبدیل می‌شود.",
                "payload": {"type": "hologram", "text": "این متن نباید دیده شود"},
                "data": null
            },
            {
                "key": "general_response",
                "step_number": 5,
                "message": "برای ثبت درخواست [هدیه ازدواج](@wedding_present) ابتدا **سوابق** خود را بررسی کنید.",
                "payload": {"filter": []},
                "data": [
                    {"item_type": "prompt_item", "action_type": "send_prompt", "prompt": "سوابق من را نشان بده"},
                    {"item_type": "prompt_item", "action_type": "send_prompt", "prompt": "شرایط هدیه ازدواج چیست؟"}
                ]
            },
            {
                "key": "message",
                "step_number": 6,
                "message": "",
                "payload": {"filter": []},
                "data": [
                    {"item_type": "message_item", "message": "پیام سرآیند بدون متن اصلی"},
                    {"item_type": "prompt_item", "action_type": "open_support_dial", "prompt": "تماس با ۱۴۲۰"}
                ]
            },
            {
                "key": "law",
                "step_number": 7,
                "message": "ماده‌های مرتبط با بیمه بیکاری",
                "payload": {"itemType": 1, "filter": []},
                "data": [
                    {
                        "item_type": "law_item",
                        "name": "ماده ۲",
                        "reference": "قانون بیمه بیکاری",
                        "content": "بیکار از نظر این قانون بیمه‌شده‌ای است که بدون میل و اراده بیکار شده و آماده کار باشد.",
                        "score": 0.91,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6842"
                    },
                    {
                        "item_type": "law_item",
                        "name": "ماده ۶",
                        "reference": "قانون بیمه بیکاری",
                        "content": "بیمه‌شدگان بیکار در صورت احراز شرایط زیر استحقاق دریافت مقرری بیمه بیکاری را خواهند داشت: حداقل ۶ ماه سابقه پرداخت حق بیمه.",
                        "score": 0.85,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6845"
                    }
                ]
            },
            {
                "key": "appoinmet",
                "step_number": 8,
                "message": "نوبت‌های پیشنهادی",
                "payload": {"filter": []},
                "data": [
                    {"item_type": "appoinmet_item", "NAME": "دکتر سارا محمدی", "PROFICIENCY": "قلب و عروق", "CITY": "تهران", "ADDRESS": "خیابان ولیعصر، پلاک ۱۲۰", "CENTER": "درمانگاه شهید فیاض‌بخش", "TITLE": "نوبت ۱۴۰۵/۰۷/۰۳ ساعت ۱۰:۳۰", "URL": "https://nobat.tamin.ir/1", "MATCH_PERCENTAGE": "92"},
                    {"item_type": "appoinmet_item", "NAME": "دکتر رضا کریمی", "PROFICIENCY": "داخلی", "CITY": "کرج", "ADDRESS": "بلوار طالقانی", "CENTER": "بیمارستان البرز", "TITLE": "نوبت ۱۴۰۵/۰۷/۰۵ ساعت ۰۹:۰۰", "URL": "https://nobat.tamin.ir/2", "MATCH_PERCENTAGE": "71"}
                ]
            },
            {
                "key": "some_future_service",
                "step_number": 9,
                "message": "### کلید ناشناخته\n\nاین کلید هنوز در اپ پیاده نشده؛ متن سرور به‌صورت markdown و دکمه‌هایش زیر آن نمایش داده می‌شود.",
                "payload": {"filter": []},
                "data": [
                    {"item_type": "deeplink", "action_type": "local_deeplink", "deeplink": {"to": "wedding_present"}, "title": "هدیه ازدواج (مقصد شناخته‌شده)"},
                    {"item_type": "deeplink", "action_type": "local_deeplink", "deeplink": {"to": "teleport_screen"}, "title": "مقصد ناشناخته (ارسال به‌عنوان پرامپت)"},
                    {"item_type": "prompt_item", "action_type": "send_prompt", "prompt": "این سرویس چه می‌کند؟"}
                ]
            },
            {
                "key": "another_future_service",
                "step_number": 10,
                "message": "",
                "payload": {"filter": []},
                "data": []
            },
            {
                "key": "disability_pension",
                "step_number": 11,
                "message": "برقراری مستمری از کارافتادگی",
                "payload": {"filter": []},
                "data": []
            },
            {
                "key": "showcase",
                "step_number": 12,
                "message": null,
                "payload": {"type": "image", "image": "https://static.tamin.ir/this-image-does-not-exist.jpg", "caption": "تصویری که بارگذاری نمی‌شود"},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 13,
                "message": null,
                "payload": {"type": "voice", "audio": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "duration": "48000", "caption": "صوت بدون waveform"},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 14,
                "message": null,
                "payload": {"type": "video", "video": "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4", "duration": "10000", "caption": "ویدیو بدون تصویر پیش‌نمایش"},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 15,
                "message": null,
                "payload": {"type": "table", "title": "جدول با ردیف ناقص", "columns": ["ردیف", "عنوان", "مبلغ (ریال)", "وضعیت"], "rows": [["۱", "حق بیمه فروردین", "۱۲ ۵۰۰ ۰۰۰", "پرداخت‌شده"], ["۲", "حق بیمه اردیبهشت"], ["۳", "حق بیمه خرداد با عنوان بسیار طولانی که باید در سلول بشکند یا جدول را اسکرول کند", "۱۲ ۵۰۰ ۰۰۰", "معوق"]]},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 16,
                "message": null,
                "payload": {"type": "key_value", "title": "خلاصه با جداکننده", "items": [{"key": "نام", "value": "علی رضایی"}, {"key": "کد ملی", "value": "0012345678"}, {"key": "----", "value": ""}, {"key": "شعبه", "value": "۱۷ تهران"}, {"key": "مبلغ", "value": "136249479 ریال"}]},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 17,
                "message": null,
                "payload": {"type": "text", "text": "متن بلند برای بررسی انیمیشن تایپ و شکست خطوط. سازمان تأمین اجتماعی در سال 1404 حدود 47 میلیون بیمه‌شده و 5.2 میلیون مستمری‌بگیر را پوشش داده است. نسبت پشتیبانی به 9.0 رسیده و کسری ماهانه نزدیک ۹۰ همت برآورد می‌شود. کد پیگیری نمونه: ABC-140301-778 و شماره تماس 1420. این جمله برای اطمینان از رفتار اعداد لاتین و فارسی در کنار هم نوشته شده است."},
                "data": null
            },
            {
                "key": "showcase",
                "step_number": 18,
                "message": null,
                "payload": {"type": "suggestions", "prompts": ["پیشنهاد مستقل اول", "پیشنهاد مستقل دوم"]},
                "data": null
            }
        ]
    }
}
"""
