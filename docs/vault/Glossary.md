---
tags: [reference, domain]
---

# واژه‌نامه‌ی دامنه

دامنه: بیمه و تأمین اجتماعی. نگاشت بین اصطلاح فارسی و نامی که در کد دیده می‌شود.

## نقش‌های کاربر

| فارسی | کد | `showRole` |
|---|---|---|
| بیمه‌شده | insured | `1` |
| مستمری‌بگیر | pensioner | `2` |
| کارفرما | employer / workshop owner | `3` |

## سرویس‌ها و مفاهیم

| فارسی | در کد |
|---|---|
| پروفایل / حساب کاربری | `feature:profile` |
| اطلاعات هویتی | `IdentityIn*`, `IdentityInfoEntity` |
| پرونده‌ی الکترونیک | `ElectronicFile*` |
| ارتباط فعال با تأمین | `ActiveRelation*` |
| تاریخچه‌ی نسخه (تغییرات اپ) | `VersionHistory*` |
| تماس با ما | `ContactUs*` |
| سوابق (تلفیقی) | `feature:history`, `MERGE_HISTORY` |
| کارگاه‌ها | `feature:workshops` |
| امور قراردادها | `feature:contracts` |
| بیمه‌ی دانشجویی / اختیاری / مشاغل آزاد / زنان خانه‌دار | `feature:studentInsuranceContract` → `STUDENT_INSURANCE`, `OPTIONAL_INSURANCE`, `FREELANCE_INSURANCE`, `HOUSEWIFE_INSURANCE` |
| استعلام مستمری | `PENSION_INQUIRY`, `feature:pensioner` (پکیج `pensionInquiry`) |
| محاسبه‌ی مستمری/حقوق | `CALCULATE_WAGE_PENSION` |
| فیش حقوقی | `PAY_ROLL` |
| حکم مستمری | `EDICT_PENSIONER` |
| گواهی کسر اقساط | `DEFERRED_INSTALLMENT_CERTIFICATE` |
| گواهی حقوق | `ISSUANCE_WAGE_CERTIFICATE` |
| مستمری ازکارافتادگی | `DISABILITY_PENSION` |
| دختر بازمانده | `GIRL_SURVIVOR` |
| درخواست مستمری بازماندگان | `REQUEST_PENSION_BY_SURVIVOR_112` |
| بازمانده / وراث | `survivor`, `Beneficiary`, `Recipient` |
| نسخه‌ی الکترونیک | `PRESCRIPTION` |
| استحقاق درمان | `DESERVED_TREATMENT_101` |
| درمان | `feature:treatment` |
| پرونده‌ی سلامت | `feature:healthProfile` |
| کارتابل | `feature:cartable` |
| صندوق پیام من | `feature:my-inbox` (پکیج `myinbox`), `PersonalInbox*` |
| تغییر شماره موبایل | `feature:change-mobile` (پکیج `changemobile`) |
| دستیار هوشمند | `feature:agent`, `FeatureFlag.AGENT` |
| شعبه | `Branch`, `InsuredActiveBranch` |
| تحت تکفل | `subdominant` |
| کارفرمای اصلی/فرعی | `Organization`, `RelationWithTaminInfo` |

## دامنه‌های سرور

| میزبان | کاربرد |
|---|---|
| `eservices.tamin.ir` | سرویس‌های اصلی |
| `account.tamin.ir` | احراز هویت (OAuth) |
| `medical.tamin.ir` | درمان |
| `ov.tamin.ir` | — |
| `sw.tamin.ir` | دستیار هوشمند (AI) |
| `apim.tamin.ir` | تغییر شماره موبایل |
| `profile.tamin.ir` | referer تغییر موبایل |

مرتبط: [[Feature-Flags]] · [[Modules]]
