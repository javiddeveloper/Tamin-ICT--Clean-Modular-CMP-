---
tags: [moc]
---

# TaminX — نقشه دانش پروژه

اپلیکیشن **تأمین همراه** (`TaminX`) — یک اپ Kotlin Multiplatform + Compose Multiplatform برای اندروید و iOS، متعلق به سازمان تأمین اجتماعی.

> این vault نقطه‌ی شروع هر سشن است. به‌جای خواندن کل کدبیس، اول اینجا را بخوان.

## معماری

- [[Overview]] — تصویر کلی لایه‌ها و جریان داده
- [[Modules]] — فهرست کامل ماژول‌ها و مسئولیتشان
- [[MVI-Pattern]] — `BaseViewModel` و قرارداد State/Intent/Event
- [[Navigation]] — گراف‌های Compose Navigation و الگوی `xxxGraph`
- [[Dependency-Injection]] — Koin و ترتیب ماژول‌ها
- [[Networking]] — Ktor، چهار HttpClient، auth و refresh token
- [[Database]] — Room KMP، DAOها، schemaها

## قواعد کار

- [[Naming-Conventions]] — قرارداد نام فایل (و چرا build اجبارش نمی‌کند) ⚠️
- [[Adding-a-Feature]] — چک‌لیست افزودن فیچر جدید
- [[Code-Review]] — فرایند review مرج‌ریکوئست‌ها و پوشه‌ی `review/`

## ساخت و انتشار

- [[Build-and-Run]] — دستورات gradle، JDK، flavorها
- [[CI-CD]] — پایپ‌لاین GitLab

## مرجع

- [[Reference-old-android]] — `old_android/` چیست و چرا commit نمی‌شود ⚠️
- [[Tech-Stack]] — نسخه‌ها و کتابخانه‌های کلیدی
- [[Feature-Flags]] — سیستم منوی داینامیک و `FeatureFlag`
- [[Glossary]] — واژه‌نامه دامنه (فارسی ↔ کد)

## اسناد موجود در ریپو (خارج از vault)

| مسیر | موضوع |
|---|---|
| `documents/features.md` | معماری فیچرهای داینامیک، `showRole`، `FeatureStatus`، routing |
| `documents/agent.md` | معماری بازنویسی دستیار هوش مصنوعی (SDUI، فرم‌های embedded) |
| `documents/agent-api-contract.md` | قرارداد JSON دقیق API دستیار هوشمند |
