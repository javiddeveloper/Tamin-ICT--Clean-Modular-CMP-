import re

content = """package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.MenuServiceStatus

val mockMenuData = listOf(
    MainServiceDto(id = 1, name = "اطلاعات هویتی", type = 1, icon = "user", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 2, name = "ارتباط فعال", type = 1, icon = "relation", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 3, name = "شماره حساب‌ها", type = 1, icon = "credit-card", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 4, name = "ویرایش تصویر", type = 1, icon = "camera", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 5, name = "افراد تبعی", type = 1, icon = "relationship", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 6, name = "سوابق تلفیقی", type = 1, icon = "inbox", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 7, name = "سوابق و دستمزد", type = 1, icon = "bill", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 8, name = "مجموع سوابق", type = 1, icon = "budget", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 9, name = "اعلام سابقه", type = 1, icon = "paper-plane", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 10, name = "اعتراض به سوابق ناموجود", type = 1, icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 11, name = "عناوین شغلی", type = 1, icon = "list", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 13, name = "درخواست‌های تعهدات کوتاه مدت", type = 1, icon = "obligation", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 14, name = "هدیه ازدواج", type = 1, icon = "love", status = MenuServiceStatus.DISABLED, message = "شما شرایط دریافت هدیه ازدواج را ندارید"),
    MainServiceDto(id = 15, name = "کمک هزینه اروتز پروتز", type = 1, icon = "crutch", status = MenuServiceStatus.WEB_VIEW, url = "https://tamin.ir/ortez"),
    MainServiceDto(id = 16, name = "کمک هزینه ایام بارداری", type = 1, icon = "pregnancystp", status = MenuServiceStatus.COMPLETELY_DISABLED, message = "این سرویس کلا غیر فعال است"),
    MainServiceDto(id = 17, name = "غرامت دستمزد ایام بیماری", type = 1, icon = "medical", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 18, name = "کمک هزینه مراسم ترحیم", type = 1, icon = "death", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 19, name = "بازرسی‌ها", type = 1, icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 20, name = "محاسبه هدیه ازدواج", type = 1, icon = "wedding-presents", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 21, name = "محاسبه غرامت ایام بیماری", type = 1, icon = "medicine", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 22, name = "محاسبه غرامت ایام بارداری", type = 1, icon = "scan", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 23, name = "نحوه محاسبه مبلغ مستمری", type = 1, icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 25, name = "وضعیت حمایت درمانی", type = 1, icon = "first-aid-kit", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 26, name = "نسخ الکترونیک", type = 1, icon = "folder", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 33, name = "بیمه صاحبان حرف و مشاغل آزاد", type = 1, icon = "agreement-freelance", status = MenuServiceStatus.TEMPORARY_DISABLED, message = "سرویس موقتاً در دسترس نیست"),
    MainServiceDto(id = 34, name = "بیمه دانشجویی", type = 1, icon = "student", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 35, name = "امور قراردادها و پرداخت", type = 1, icon = "contract_payment", status = MenuServiceStatus.ENABLED_WITH_ERROR, message = "ارتباط با سامانه با کندی مواجه است"),
    MainServiceDto(id = 36, name = "بیمه زنان خانه‌دار", type = 1, icon = "woman_agreement-freelance", status = MenuServiceStatus.DISABLED, message = "شما شرایط ثبت‌نام را ندارید"),
    MainServiceDto(id = 37, name = "بیمه اختیاری", type = 1, icon = "optional-insurance", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 38, name = "استعلام گواهی اشتغال به تحصیل", type = 1, icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 39, name = "تکمیل سوابق کسری از ماه", type = 1, icon = "employer_info", status = MenuServiceStatus.COMPLETELY_DISABLED, message = "غیرفعال"),
    MainServiceDto(id = 40, name = "درخواست مستمری بازماندگان", type = 1, icon = "survivors", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 41, name = "مستمری بازنشستگی", type = 1, icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 42, name = "اعتراض به سابقه کسری دار", type = 1, icon = "objecting_history_bugs", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 46, name = "پرونده الکترونیک من", type = 1, icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 47, name = "اطلاعات پرداختی کارگران", type = 1, icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 101, name = "استحقاق درمان", type = 2, icon = "first-aid-kit", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 102, name = "نسخ الکترونیک", type = 2, icon = "folder", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 104, name = "استعلام وضعیت مستمری", type = 2, icon = "insurance", status = MenuServiceStatus.TEMPORARY_DISABLED, message = "سرویس استعلام وضعیت مستمری در حال بروزرسانی است"),
    MainServiceDto(id = 105, name = "مشاهده فیش حقوقی", type = 2, icon = "ticket", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 106, name = "مشاهده حکم", type = 2, icon = "announcement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 107, name = "صدور گواهی حقوق", type = 2, icon = "stamp", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 108, name = "گواهی کسر اقساط معوق", type = 2, icon = "document", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 109, name = "نحوه محاسبه مبلغ مستمری", type = 2, icon = "calc", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 110, name = "تعهدنامه فرزندان دختر", type = 2, icon = "agreement", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 112, name = "برقراری مستمری توسط بازماندگان", type = 2, icon = "survivors", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 113, name = "مستمری از کارافتادگی", type = 2, icon = "disability", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1001, name = "کارگاه‌ها", type = 3, icon = "workshop", status = MenuServiceStatus.ENABLED_WITH_ERROR, message = "دریافت اطلاعات لیست کارگاه‌ها با کندی همراه است"),
    MainServiceDto(id = 1002, name = "اطلاعات پیمان", type = 3, icon = "contract", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1003, name = "واگذارندگان", type = 3, icon = "ic_assigner", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1004, name = "تکمیل اطلاعات کارفرمایی", type = 3, icon = "employer_info", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1005, name = "لیست ذینفعان", type = 3, icon = "relationship", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1006, name = "پیگیری وضعیت اعتراض", type = 3, icon = "protest", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1007, name = "درخواست خدمات غیرحضوری", type = 3, icon = "onlineServiceReq", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1008, name = "بازرسی انجام شده", type = 3, icon = "cctv", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1009, name = "مدیریت بدهی", type = 3, icon = "student_inquiry", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1010, name = "بیمه ساختمانی", type = 3, icon = "workshop", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1011, name = "بروزرسانی", type = 3, icon = "update", status = MenuServiceStatus.ACTIVE),
    MainServiceDto(id = 1012, name = "قوانین", type = 3, icon = "document", status = MenuServiceStatus.ACTIVE)
)
"""

lines = [l for l in content.split('\n') if l.strip().startswith('MainServiceDto')]

name_map = {}
for line in lines:
    name_match = re.search(r'name = "([^"]+)"', line)
    type_match = re.search(r'type = (\d+)', line)
    id_match = re.search(r'id = (\d+)', line)
    if name_match and type_match and id_match:
        name = name_match.group(1)
        role = int(type_match.group(1))
        real_id = int(id_match.group(1))
        
        # replace type = X with nothing
        new_line = re.sub(r',\s*type\s*=\s*\d+', '', line)
        
        if name in name_map:
            name_map[name]['roles'].append(role)
        else:
            name_map[name] = {'line': new_line.strip(), 'roles': [role], 'id': real_id}

out_lines = []
out_lines.append("package com.tamin.taminhamrah.dataSource.commonSource\n")
out_lines.append("import com.tamin.taminhamrah.model.common.MainServiceDto")
out_lines.append("import com.tamin.taminhamrah.model.common.MenuServiceStatus\n")
out_lines.append("val mockMenuData = listOf(")

items = []
for name, data in name_map.items():
    line = data['line']
    roles = data['roles']
    roles_str = ', '.join(map(str, roles))
    # inject showRole = listOf(...) before status or end
    line = re.sub(r'\)$', f', showRole = listOf({roles_str}))', line)
    # remove trailing comma if it got duplicated or ensure no trailing comma
    items.append('    ' + line)

out_lines.append(',\n'.join(items))
out_lines.append(")")

with open(r'core\core-network\src\commonMain\kotlin\com\tamin\taminhamrah\dataSource\commonSource\MockMenuData.kt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(out_lines))
