---
tags: [reference, domain]
---

# Glossary

Domain: insurance and social security. Maps the Persian term used by the business to the name it carries in the code. Persian terms are kept as-is because they are the data, not prose.

## User roles

| Persian | English | In code | `showRole` |
|---|---|---|---|
| بیمه‌شده | insured person | `insured` | `1` |
| مستمری‌بگیر | pensioner | `pensioner` | `2` |
| کارفرما | employer | `employer` / workshop owner | `3` |

## Services and concepts

| Persian | English | In code |
|---|---|---|
| پروفایل / حساب کاربری | profile / account | `feature:profile` |
| اطلاعات هویتی | identity information | `IdentityIn*`, `IdentityInfoEntity` |
| پرونده‌ی الکترونیک | electronic file | `ElectronicFile*` |
| ارتباط فعال با تأمین | active relation with Tamin | `ActiveRelation*` |
| تاریخچه‌ی نسخه | app version history | `VersionHistory*` |
| تماس با ما | contact us | `ContactUs*` |
| سوابق تلفیقی | merged contribution history | `feature:history`, `MERGE_HISTORY` |
| کارگاه‌ها | workshops | `feature:workshops` |
| امور قراردادها | contract affairs | `feature:contracts` |
| بیمه‌ی دانشجویی | student insurance | `STUDENT_INSURANCE` |
| بیمه‌ی اختیاری | optional insurance | `OPTIONAL_INSURANCE` |
| بیمه‌ی مشاغل آزاد | freelance insurance | `FREELANCE_INSURANCE` |
| بیمه‌ی زنان خانه‌دار | housewives' insurance | `HOUSEWIFE_INSURANCE` |
| استعلام مستمری | pension inquiry | `PENSION_INQUIRY`, `feature:pensioner` (package `pensionInquiry`) |
| محاسبه‌ی مستمری | pension calculation | `CALCULATE_WAGE_PENSION` |
| فیش حقوقی | payslip | `PAY_ROLL` |
| حکم مستمری | pension decree | `EDICT_PENSIONER` |
| گواهی کسر اقساط | deferred installment certificate | `DEFERRED_INSTALLMENT_CERTIFICATE` |
| گواهی حقوق | wage certificate | `ISSUANCE_WAGE_CERTIFICATE` |
| مستمری ازکارافتادگی | disability pension | `DISABILITY_PENSION` |
| دختر بازمانده / تعهدنامه فرزندان دختر | surviving daughter commitment | `GIRL_SURVIVOR`, `feature:girlSurvivor` |
| درخواست مستمری بازماندگان | survivor pension request | `REQUEST_PENSION_BY_SURVIVOR_112` |
| بازمانده / وراث | survivor / beneficiary | `survivor`, `Beneficiary`, `Recipient` |
| نسخه‌ی الکترونیک | e-prescription | `PRESCRIPTION` |
| استحقاق درمان | treatment entitlement | `DESERVED_TREATMENT_101` |
| درمان | treatment | `feature:treatment` |
| پرونده‌ی سلامت | health profile | `feature:healthProfile` |
| کارتابل | task inbox (cartable) | `feature:cartable` |
| صندوق پیام من | my inbox | `feature:my-inbox` (package `myinbox`), `PersonalInbox*` |
| تغییر شماره موبایل | change mobile number | `feature:change-mobile` (package `changemobile`) |
| افراد تحت تکفل | dependents | `feature:addDependent`, `subdominant` |
| دستیار هوشمند | AI assistant | `feature:agent`, `FeatureFlag.AGENT` |
| شعبه | branch | `Branch`, `InsuredActiveBranch` |
| تحت تکفل | dependent | `subdominant` |
| کارفرمای اصلی/فرعی | primary/secondary employer | `Organization`, `RelationWithTaminInfo` |
| دستمزد | wage | `dastmozd` (in AI service keys) |
| حکم | decree | `hokm` (in AI service keys) |
| فیش | payslip | `fish` (in AI service keys) |

## Server domains

| Host | Used for |
|---|---|
| `eservices.tamin.ir` | main services |
| `account.tamin.ir` | authentication (OAuth) |
| `medical.tamin.ir` | treatment |
| `ov.tamin.ir` | — |
| `sw.tamin.ir` | AI assistant |
| `apim.tamin.ir` | mobile number change |
| `profile.tamin.ir` | referer for mobile number change |

Related: [[Feature-Flags]] · [[AI-Agent-API-Contract]] · [[Modules]]
