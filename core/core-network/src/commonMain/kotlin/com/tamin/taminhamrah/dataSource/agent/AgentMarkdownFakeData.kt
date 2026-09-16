package com.tamin.taminhamrah.dataSource.agent

/**
 * Server-rendered markdown fixture (`render_mode: SERVER`, `item_type: markdown`).
 *
 * Covers every block the client renders and every link outcome the deep link gate can reach:
 * - text: headings, emphasis, strikethrough, nested lists, quote, code, rule, grouped numbers
 * - a wide table
 * - formulas: an explicit `$$` block whose parentheses must all stay visible, a `label = expr`
 *   line, nested fractions, a power and a `math` fence
 * - links: enabled service, alias, disabled service (menu id 42 in MockMenuData), unknown key,
 *   prompt link, trusted and untrusted web links
 * - an empty markdown item, which must produce no bubble
 */
internal const val FAKE_AGENT_MARKDOWN_RESPONSE = """
{
    "id": "md-fixture-request",
    "eta": 2,
    "status": "DONE",
    "message": null,
    "result": {
        "sessionId": "md-fixture-session",
        "lastEntity": null,
        "render_mode": "SERVER",
        "entities": [
            {
                "key": "general_response",
                "item_type": "markdown",
                "step_number": 1,
                "message_id": "md-text",
                "data": [
                    {
                        "item_type": "markdown_item",
                        "format": "markdown",
                        "content_version": "1",
                        "text": "### خلاصه‌ی سوابق شما\n\nمتوسط دستمزد دو سال اخیر **۱۳۶ ۲۴۹ ۴۷۹ ریال** است و مدت خدمت *۲۴ ماه* ثبت شده.\n\nمبلغ قبلی ~~۱۲۰ ۰۰۰ ۰۰۰ ریال~~ اصلاح شد.\n\n- بیمه‌ی اجباری\n  - کارگاه اول\n  - کارگاه دوم\n- بیمه‌ی اختیاری\n\n1. ثبت درخواست\n2. بررسی مدارک\n3. صدور حکم\n\n> این اطلاعات بر اساس آخرین لیست ارسالی کارفرما است.\n\n```\nکد پیگیری: 140301-778\n```\n\n---\n\n#### جزئیات بیشتر\nبرای شماره‌ی `12345` در شعبه‌ی ۱۷ ثبت شده است."
                    }
                ]
            },
            {
                "key": "general_response",
                "item_type": "markdown",
                "step_number": 2,
                "message_id": "md-table",
                "data": [
                    {
                        "item_type": "markdown_item",
                        "format": "markdown",
                        "content_version": "1",
                        "text": "#### سوابق سالانه\n\n| سال | نام کارگاه | روزهای کارکرد | دستمزد ماهانه | نوع سابقه | شعبه |\n|---|---|---|---|---|---|\n| ۱۴۰۲ | شرکت الف | ۳۶۵ | ۱۰۰ ۰۰۰ ۰۰۰ ریال | عادی | ۱۷ |\n| ۱۴۰۳ | شرکت ب | ۲۱۰ | ۱۲۰ ۵۰۰ ۰۰۰ ریال | عادی | ۲۲ |\n| ۱۴۰۴ | شرکت \\| ج | ۹۰ | ۱۵۰ ۰۰۰ ۰۰۰ ریال | مشاغل آزاد | ۵ |"
                    }
                ]
            },
            {
                "key": "general_response",
                "item_type": "markdown",
                "step_number": 3,
                "message_id": "md-formula",
                "data": [
                    {
                        "item_type": "markdown_item",
                        "format": "markdown",
                        "content_version": "1",
                        "text": "### محاسبه‌ی حق بیمه\n\n$$\n2/π = (1 − 1/2²)(1 − 1/4²)(1 − 1/6²)…\n$$\n\nحق بیمه کل = (۱۳۶ ۲۴۹ ۴۷۹ × ۷٪ × ۲۴) / (۱)\n\nقسط ماهیانه = (۲۲۸ ۸۹۹ ۱۲۵) / (۱۲)\n\nنسبت = ((a + b) / (c)) / (d − 1)\n\n```math\nx^2 + y^(n+1) = (a − b)(a + b)\n```\n\nجمله‌ی عادی با علامت = که فرمول نیست."
                    }
                ]
            },
            {
                "key": "general_response",
                "item_type": "markdown",
                "step_number": 4,
                "message_id": "md-links",
                "data": [
                    {
                        "item_type": "markdown_item",
                        "format": "markdown",
                        "content_version": "1",
                        "text": "### خدمات مرتبط\n\n- [هدیه ازدواج](@wedding_present)\n- [پرداخت حق بیمه](@insurance_payment)\n- [اعتراض به سابقه](@objection_insurance_history)\n\nبرای دیدن نسخه‌ها [اینجا](agent://nav/electronic_prescription_list) را بزنید یا [فیش حقوقی](mytamin://feature/pensioner_pay_roll) را ببینید.\n\n- [پرداخت گروهی](@group_payment)\n- [سؤال بعدی](agent://prompt?text=%D8%B3%D9%88%D8%A7%D8%A8%D9%82%20%D9%85%D9%86%20%D8%B1%D8%A7%20%D9%86%D8%B4%D8%A7%D9%86%20%D8%A8%D8%AF%D9%87)\n- [سایت تأمین](https://www.tamin.ir)\n- [سایت دیگر](https://example.com)"
                    },
                    {
                        "item_type": "markdown_item",
                        "format": "markdown",
                        "content_version": "1",
                        "text": "   "
                    },
                    {
                        "item_type": "prompt_item",
                        "prompt": "آخرین فیش حقوقی من"
                    }
                ]
            }
        ]
    }
}
"""
