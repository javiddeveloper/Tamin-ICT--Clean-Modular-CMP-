package com.tamin.taminhamrah.dataSource.agent

internal const val FAKE_AGENT_ONE_RESPONSE = """
{
    "id": "0a9e6612-0f10-46ad-a875-7b6fcead7219",
    "eta": 3,
    "status": "DONE",
    "message": null,
    "result": {
        "session_id": "jkld-gkl4-v444v-hjk4656-sbhdkjf8-bbbb",
        "lastEntity": "doctorName:ali , proficiency:heart, nezam:123456",
        "entities": [
            {
                "key": "dastmozd_infos_per_year",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم سابقه مربوط به 1402 تا 1404 به صورت زیر می‌باشد",
                "message_id": "8a7ae66e-1fb7-4dc9-89d3-aacdf8a228fe"
            },
            {
                "key": "dastmozd_infos_salary",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم سابقه حقوق مربوط به 1402/04/01 تا 1404/04/30 به صورت زیر می‌باشد",
                "message_id": "2c83c66c-e449-4541-88b6-e460fe0356fc"
            },
            {
                "key": "dastmozd_infos_sum_total",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم مجموع کل سابقه مربوط به 14020401 تا 14040430 به صورت زیر می‌باشد",
                "message_id": "302ca9ae-9654-4315-9e8c-5c1172b3d5f0"
            },
            {
                "key": "dastmozd_infos_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین سابقه بیمه شما به صورت زیر می‌باشد",
                "message_id": "ecca966b-0680-42e9-84cd-8329b4453f12"
            },
            {
                "key": "dastmozd_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه",
                "message_id": "46fc9583-a27c-43d2-aa02-1cba3dfe60f6"
            },
            {
                "key": "dastmozd_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه از تاریخ 14020401 ",
                "message_id": "81307dc3-3b75-4796-80ef-fd42ae3d6fdc"
            },
            {
                "key": "dastmozd_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه تا تاریخ 14040430",
                "message_id": "cafabfcf-db89-4ed1-8437-46ac820cf98c"
            },
            {
                "key": "dastmozd_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه",
                "message_id": "7e2d8329-d5d1-42ab-9960-bd9202c225ab"
            },
            {
                "key": "history_job_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401"
                    ]
                },
                "data": null,
                "message": "لیست عناوین شغلی بر اساس تاریخ شروع",
                "message_id": "0bbb3e22-cba6-4ac5-a1d0-b57eddc7a638"
            },
            {
                "key": "history_job_infos_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین عنوان شغلی",
                "message_id": "40a5c92c-0d46-4437-801d-5e1c53d38571"
            },
            {
                "key": "calcIllness",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد",
                "message_id": "9685dfe5-91da-4c09-8e0e-f5caa88ed3e9"
            },
            {
                "key": "calcIllness",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد از تاریخ شروع",
                "message_id": "0447c044-b8f5-404f-9fc5-128ed154816f"
            },
            {
                "key": "calcIllness",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد تا تاریخ پایان",
                "message_id": "c171a2d4-a8c7-47eb-9439-aebbc7a17a14"
            },
            {
                "key": "repIllness_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "گزارش غرامت دستمزد (آخرین)",
                "message_id": "0a2d3971-7a3e-4c6d-8643-9895ffe1fc27"
            },
            {
                "key": "repIllness",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "گزارش غرامت دستمزد (همه)",
                "message_id": "b5f71c88-7e9a-446f-b119-c1534e318efe"
            },
            {
                "key": "dastmozd_infos_calcIllness_pensioner",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:13730101",
                        "endDate:13981229"
                    ]
                },
                "data": null,
                "message": "محاسبه مستمری بازنشستگی",
                "message_id": "8026976f-c666-4a6d-a6dd-35ff07702863"
            },
            {
                "key": "eligible_amount_pension",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "مبلغ استحقاق مستمری",
                "message_id": "be362317-4fc4-4a1e-b6bc-366f4298d997"
            },
            {
                "key": "average_dastmozd_infos",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "averageSalary:9"
                    ]
                },
                "data": null,
                "message": "میانگین دستمزد 9 سال آخر",
                "message_id": "859d3340-81d2-48c9-9718-6ef367d9ded4"
            },
            {
                "key": "pension_inquiry_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "استعلام وضعیت مستمری (آخرین)",
                "message_id": "697088b2-3416-478a-8a12-df4cbfc84d7b"
            },
            {
                "key": "tcr_price_certificate",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "خسارت متفرقه",
                "message_id": "697088b2-3416-478a-8a12-df4cbfc84d7b"
            },
            {
                "key": "pension_inquiry_all",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "استعلام وضعیت مستمری (همه)",
                "message_id": "97a1a04e-bc76-4f6c-a06b-2be080f7d67a"
            },
            {
                "key": "hokm_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030101",
                        "endDate:14031229"
                    ]
                },
                "data": null,
                "message": "مشاهده حکم",
                "message_id": "b90350dd-afbd-40ca-a76d-077dc18d42e2"
            },
            {
                "key": "hokm",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030101"
                    ]
                },
                "data": null,
                "message": "مشاهده حکم از تاریخ شروع",
                "message_id": "92c3fa9f-e219-4bcf-99e3-f240df2392e1"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 101"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (فرزند)",
                "message_id": "117cb392-6abf-4500-8f9b-91adf3001cd6"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "افراد تحت پوشش (همه)",
                "message_id": "a066006f-d91a-47c6-9370-9057c39a3fe3"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 101"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (پسر)",
                "message_id": "21488487-3535-4fde-bf01-c0d4ec54427c"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 102"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (دختر)",
                "message_id": "7810b2fe-c02b-4fcb-b6a3-a63d48d551c6"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 106",
                        "gendercode : 01"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (پدر)",
                "message_id": "a72373fb-af5d-4afa-b864-9bc90bc74c56"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 106",
                        "gendercode : 02"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (مادر)",
                "message_id": "7fa9f388-a356-4674-b519-2347c6923e7c"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode:124"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (خواهر)",
                "message_id": "c5f7b087-4a7a-44ae-8201-13ef6f1e66e3"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode : 124"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (برادر)",
                "message_id": "f6c8790b-f09b-4240-8ac1-89054b66a68c"
            },
            {
                "key": "get_dependent",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode : 100"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (زن/شوهر)",
                "message_id": "f6c8790b-f09b-4240-8ac1-89054b66a68c"
            },
            {
                "key": "fish",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "endDate:14040430",
                        "pensionerId:@pensionerId",
                        "nationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "کاربر محترم فیش مربوط به 14040401 به صورت زیر می‌باشد",
                "message_id": "7eaa9e53-2f88-47d2-a08a-4f33af550e1f"
            },
            {
                "key": "fish_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "pensionerId:@pensionerId",
                        "nationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "آخرین فیش ",
                "message_id": "7eaa9e53-2f88-47d2-a08a-4f33af550e1f"
            },
            {
                "key": "fish",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "کاربر محترم فیش مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:03"
                    ]
                },
                "data": null,
                "message": "کاربر محترم عیدی مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:03"
                    ]
                },
                "data": null,
                "message": "آخرین عیدی",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:08"
                    ]
                },
                "data": null,
                "message": "کاربر محترم معوقه مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:08"
                    ]
                },
                "data": null,
                "message": "آخرین معوقه",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "patient_history",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "لیست نسخه های شما از تاریخ startDate@ تا تاریخ endDate@ به صورت زیر میباشد: ",
                "message_id": "fd1ab80e-e497-4aa9-8598-9a54a47f5ba0"
            },
            {
                "key": "patient_history",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "لیست نسخه های شما از تاریخ startDate@ به صورت زیر میباشد: ",
                "message_id": "2ae1299f-a447-4e79-8a19-b033ca32078d"
            },
            {
                "key": "patient_history_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "آخرین نسخه",
                "message_id": "2ae1299f-a447-4e79-8a19-b033ca32078d"
            },
            {
                "key": "booklet_req",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "وضعیت استحقاق درمان به صورت زیر میباشد",
                "message_id": "4e9dc4d8-fead-488d-be28-3d645dc65ab1"
            },
            {
                "key": "history_services_last",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین بیمه پردازی شما به صورت زیر میباشد",
                "message_id": "ff2b4a85-4a82-4e70-bd2c-bdab1c099069"
            },
            {
                "key": "history_services",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "آخرین بیمه پردازی شما به صورت زیر میباشد",
                "message_id": "65a3f4cc-d3ac-4739-b2ec-210c993abdbd"
            },
            {
                "key": "last_tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "last_tracking_code",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری:آخرین  ",
                "message_id": "59fea35b-effd-44db-9b06-1c246944ec73"
            },
            {
                "key": "appoinmet",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "appoinmet",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": null,
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "dastmozdinfos_last_pay",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": null,
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "law",
                "step_number": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "law_item",
                        "name": "10)",
                        "reference": "مدیر عامل",
                        "content": "بیکاری به علت بروز حوادث قهریه و غیر مترقبه از قبیل سیل، زلزله، جنگ و آتش سوزی.",
                        "source_file": "WZ31AR0G89FL1VWIB8YJKHE81E0E0PCH.json",
                        "score": 0.85,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6845&id=9P7LB4KFB4B9GDRF55K70HY4QBB2Y502"
                    },
                    {
                        "item_type": "law_item",
                        "name": "8)",
                        "reference": "مدیر عامل",
                        "content": "بیکار از نظر قانون، بیمه شده ای است که بدون میل واراده بیکار شده و آماده کار باشد.",
                        "source_file": "WZ31AR0G89FL1VWIB8YJKHE81E0E0PCH.json",
                        "score": 0.85,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6842&id=KC1AXYZ6AMG6ZGIVTX75FJ1JUW6MRIIA"
                    }
                ],
                "message": "قوانین این است",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "general_response",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "text": "یباتلمین"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "یباتلمین"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "لنیمکبلمن"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "لینتمبل"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "ینشمکسیمن"
                    }
                ],
                "message": "درخواست بیماری شما در حال حاضر قابل پردازش نیست",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "message_response",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "مسیج تست"
                    }
                ],
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "profile_info",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "edit_profile_info",
                        "prompt": "ویرایش اطلاعات هویتی"
                    }
                ],
                "message": "اطلاعات شخصی شما",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "edit_mobile",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "edit_mobile",
                        "prompt": "ویرایش موبایل"
                    }
                ],
                "message": "ویرایش شماره موبایل",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "add_account_number",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "add_account_number",
                        "prompt": "اضافه کردن شماره حساب"
                    }
                ],
                "message": "اضافه کردن شماره حساب",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "appoinmet",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "برای متخصص داخلی"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "متخصص داخلی",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "message_item",
                        "message": "برای متخصص چشم"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "متخصص چشم",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "message_item",
                        "message": "مسیج فوتر"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_link",
                        "prompt": "یاز کزدن سایت ۱۴۲۰"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_dial",
                        "prompt": "تماس با ۱۴۲۰"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "message",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "مسیج هدر"
                    },
                    {
                        "item_type": "message_item",
                        "message": "مسیج فوتر"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_link",
                        "prompt": "یاز کزدن سایت ۱۴۲۰"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_dial",
                        "prompt": "تماس با ۱۴۲۰"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "register_contract",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_freelance"
                        },
                        "title": "مشاغل آزاد"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_optional"
                        },
                        "title": "اختیاری"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_woman"
                        },
                        "title": "زنان"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_student"
                        },
                        "title": "دانشجویی"
                    }
                ],
                "message": "همراه عزیز برای انعقاد قرار بیمه مشاغل آزاد رو دکمه زیر بزنید ",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "funeral_allowance",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "کمک هزینه ترحیم",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "short_term_orthosis",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "هزینه اروتز پروتز",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "extend_education",
                "step_number": 1,
                "payload": {
                    "filter": [
                        "educationCode:1234567890"
                    ]
                },
                "data": [],
                "message": "مجوز استعلام اشتغال به تحصیل برای فرزند",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "pregnancy_pay",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "هزینه ایام بارداری",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": " workers_payment",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "پرداخت حق بیمه کارگران ساختمانی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "confirmation_medical_authorities",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "مشاهده تاییده های دریافتی از مراجع پزشکی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "complete_info_of_real_workshop",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "تکمیل اطلاعات کارفرمایی اشخاص حقیقی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "pension_survivor",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "برقراری غیرحضوری مستمری بازماندگان",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "disability_pension",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "برقراری مستمری از کارافتادگی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "deferred_installment_certificate",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "درخواست گواهی کسر اقساط معوق",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "wedding_present",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "کمک هزینه ازدواج",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "occurrence_report",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "occurrence_report",
                        "prompt": "اعلام حادثه"
                    }
                ],
                "message": "اعلام حادثه",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "edit_profile",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "ویرایش تاریخ تولد",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "edit_address",
                "step_number": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "اصلاح نشانی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            }
        ]
    }
}
"""
internal const val FAKE_AGENT_RESPONSE = """{
    "id": "0a9e6612-0f10-46ad-a875-7b6fcead7219",
    "eta": 3,
    "status": "DONE",
    "message": null,
    "result": {
        "sessionId": "jkld-gkl4-v444v-hjk4656-sbhdkjf8-bbbb",
        "lastEntity": "doctorName:ali , proficiency:heart, nezam:123456",
        "entities": [
            {
                "key": "dastmozd_infos_per_year",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم سابقه مربوط به 1402 تا 1404 به صورت زیر می‌باشد",
                "message_id": "8a7ae66e-1fb7-4dc9-89d3-aacdf8a228fe"
            },
            {
                "key": "dastmozd_infos_salary",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم سابقه حقوق مربوط به 1402/04/01 تا 1404/04/30 به صورت زیر می‌باشد",
                "message_id": "2c83c66c-e449-4541-88b6-e460fe0356fc"
            },
            {
                "key": "dastmozd_infos_sum_total",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "کاربر محترم مجموع کل سابقه مربوط به 14020401 تا 14040430 به صورت زیر می‌باشد",
                "message_id": "302ca9ae-9654-4315-9e8c-5c1172b3d5f0"
            },
            {
                "key": "dastmozd_infos_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین سابقه بیمه شما به صورت زیر می‌باشد",
                "message_id": "ecca966b-0680-42e9-84cd-8329b4453f12"
            },
            {
                "key": "dastmozd_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه",
                "message_id": "46fc9583-a27c-43d2-aa02-1cba3dfe60f6"
            },
            {
                "key": "dastmozd_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه از تاریخ 14020401 ",
                "message_id": "81307dc3-3b75-4796-80ef-fd42ae3d6fdc"
            },
            {
                "key": "dastmozd_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه تا تاریخ 14040430",
                "message_id": "cafabfcf-db89-4ed1-8437-46ac820cf98c"
            },
            {
                "key": "dastmozd_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "سابقه پرداخت حق بیمه",
                "message_id": "7e2d8329-d5d1-42ab-9960-bd9202c225ab"
            },
            {
                "key": "history_job_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401"
                    ]
                },
                "data": null,
                "message": "لیست عناوین شغلی بر اساس تاریخ شروع",
                "message_id": "0bbb3e22-cba6-4ac5-a1d0-b57eddc7a638"
            },
            {
                "key": "history_job_infos_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین عنوان شغلی",
                "message_id": "40a5c92c-0d46-4437-801d-5e1c53d38571"
            },
            {
                "key": "calcIllness",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد",
                "message_id": "9685dfe5-91da-4c09-8e0e-f5caa88ed3e9"
            },
            {
                "key": "calcIllness",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030401"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد از تاریخ شروع",
                "message_id": "0447c044-b8f5-404f-9fc5-128ed154816f"
            },
            {
                "key": "calcIllness",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "محاسبه غرامت دستمزد تا تاریخ پایان",
                "message_id": "c171a2d4-a8c7-47eb-9439-aebbc7a17a14"
            },
            {
                "key": "repIllness_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "گزارش غرامت دستمزد (آخرین)",
                "message_id": "0a2d3971-7a3e-4c6d-8643-9895ffe1fc27"
            },
            {
                "key": "repIllness",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "گزارش غرامت دستمزد (همه)",
                "message_id": "b5f71c88-7e9a-446f-b119-c1534e318efe"
            },
            {
                "key": "dastmozd_infos_calcIllness_pensioner",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:13730101",
                        "endDate:13981229"
                    ]
                },
                "data": null,
                "message": "محاسبه مستمری بازنشستگی",
                "message_id": "8026976f-c666-4a6d-a6dd-35ff07702863"
            },
            {
                "key": "eligible_amount_pension",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "مبلغ استحقاق مستمری",
                "message_id": "be362317-4fc4-4a1e-b6bc-366f4298d997"
            },
            {
                "key": "average_dastmozd_infos",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "averageSalary:9"
                    ]
                },
                "data": null,
                "message": "میانگین دستمزد 9 سال آخر",
                "message_id": "859d3340-81d2-48c9-9718-6ef367d9ded4"
            },
            {
                "key": "pension_inquiry_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "استعلام وضعیت مستمری (آخرین)",
                "message_id": "697088b2-3416-478a-8a12-df4cbfc84d7b"
            },
            {
                "key": "tcr_price_certificate",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "خسارت متفرقه",
                "message_id": "697088b2-3416-478a-8a12-df4cbfc84d7b"
            },
            {
                "key": "pension_inquiry_all",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "استعلام وضعیت مستمری (همه)",
                "message_id": "97a1a04e-bc76-4f6c-a06b-2be080f7d67a"
            },
            {
                "key": "hokm_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030101",
                        "endDate:14031229"
                    ]
                },
                "data": null,
                "message": "مشاهده حکم",
                "message_id": "b90350dd-afbd-40ca-a76d-077dc18d42e2"
            },
            {
                "key": "hokm",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14030101"
                    ]
                },
                "data": null,
                "message": "مشاهده حکم از تاریخ شروع",
                "message_id": "92c3fa9f-e219-4bcf-99e3-f240df2392e1"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 101"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (فرزند)",
                "message_id": "117cb392-6abf-4500-8f9b-91adf3001cd6"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "افراد تحت پوشش (همه)",
                "message_id": "a066006f-d91a-47c6-9370-9057c39a3fe3"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 101"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (پسر)",
                "message_id": "21488487-3535-4fde-bf01-c0d4ec54427c"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 102"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (دختر)",
                "message_id": "7810b2fe-c02b-4fcb-b6a3-a63d48d551c6"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 106",
                        "gendercode : 01"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (پدر)",
                "message_id": "a72373fb-af5d-4afa-b864-9bc90bc74c56"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode: 106",
                        "gendercode : 02"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (مادر)",
                "message_id": "7fa9f388-a356-4674-b519-2347c6923e7c"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode:124"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (خواهر)",
                "message_id": "c5f7b087-4a7a-44ae-8201-13ef6f1e66e3"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode : 124"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (برادر)",
                "message_id": "f6c8790b-f09b-4240-8ac1-89054b66a68c"
            },
            {
                "key": "get_dependent",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "tendencyCode : 100"
                    ]
                },
                "data": null,
                "message": "افراد تحت پوشش (زن/شوهر)",
                "message_id": "f6c8790b-f09b-4240-8ac1-89054b66a68c"
            },
            {
                "key": "fish",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "endDate:14040430",
                        "pensionerId:@pensionerId",
                        "nationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "کاربر محترم فیش مربوط به 14040401 به صورت زیر می‌باشد",
                "message_id": "7eaa9e53-2f88-47d2-a08a-4f33af550e1f"
            },
            {
                "key": "fish_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "pensionerId:@pensionerId",
                        "nationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "آخرین فیش ",
                "message_id": "7eaa9e53-2f88-47d2-a08a-4f33af550e1f"
            },
            {
                "key": "fish",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:01"
                    ]
                },
                "data": null,
                "message": "کاربر محترم فیش مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:03"
                    ]
                },
                "data": null,
                "message": "کاربر محترم عیدی مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:03"
                    ]
                },
                "data": null,
                "message": "آخرین عیدی",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:08"
                    ]
                },
                "data": null,
                "message": "کاربر محترم معوقه مربوط به @startDate به صورت زیر می‌باشد",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "fish",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14040401",
                        "pensionerId:@pensionerId",
                        "NationalId:@nationalID",
                        "paymentType:08"
                    ]
                },
                "data": null,
                "message": "آخرین معوقه",
                "message_id": "7e6bec64-b935-42ba-b193-9681e748e98e"
            },
            {
                "key": "patient_history",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "لیست نسخه های شما از تاریخ startDate@ تا تاریخ endDate@ به صورت زیر میباشد: ",
                "message_id": "fd1ab80e-e497-4aa9-8598-9a54a47f5ba0"
            },
            {
                "key": "patient_history",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "لیست نسخه های شما از تاریخ startDate@ به صورت زیر میباشد: ",
                "message_id": "2ae1299f-a447-4e79-8a19-b033ca32078d"
            },
            {
                "key": "patient_history_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14021012",
                        "endDate:14021212"
                    ]
                },
                "data": null,
                "message": "آخرین نسخه",
                "message_id": "2ae1299f-a447-4e79-8a19-b033ca32078d"
            },
            {
                "key": "booklet_req",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "وضعیت استحقاق درمان به صورت زیر میباشد",
                "message_id": "4e9dc4d8-fead-488d-be28-3d645dc65ab1"
            },
            {
                "key": "history_services_last",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": null,
                "message": "آخرین بیمه پردازی شما به صورت زیر میباشد",
                "message_id": "ff2b4a85-4a82-4e70-bd2c-bdab1c099069"
            },
            {
                "key": "history_services",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "آخرین بیمه پردازی شما به صورت زیر میباشد",
                "message_id": "65a3f4cc-d3ac-4739-b2ec-210c993abdbd"
            },
            {
                "key": "last_tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401",
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "endDate:14040430"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری: ",
                "message_id": "4ce0813b-2106-41d8-8ebd-a62553ebd555"
            },
            {
                "key": "last_tracking_code",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": [
                        "startDate:14020401"
                    ]
                },
                "data": null,
                "message": "لیست کد پیگیری:آخرین  ",
                "message_id": "59fea35b-effd-44db-9b06-1c246944ec73"
            },
            {
                "key": "appoinmet",
                "stepNumber": 1,
                "payload": {
                    "filter": [
                    ]
                },
                "data": [
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "appoinmet",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": null,
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "dastmozdinfos_last_pay",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": null,
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "law",
                "stepNumber": 1,
                "payload": {
                    "itemType": 1,
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "law_item",
                        "name": "10)",
                        "reference": "مدیر عامل",
                        "content": "بیکاری به علت بروز حوادث قهریه و غیر مترقبه از قبیل سیل، زلزله، جنگ و آتش سوزی.",
                        "source_file": "WZ31AR0G89FL1VWIB8YJKHE81E0E0PCH.json",
                        "score": 0.85,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6845&id=9P7LB4KFB4B9GDRF55K70HY4QBB2Y502"
                    },
                    {
                        "item_type": "law_item",
                        "name": "8)",
                        "reference": "مدیر عامل",
                        "content": "بیکار از نظر قانون، بیمه شده ای است که بدون میل واراده بیکار شده و آماده کار باشد.",
                        "source_file": "WZ31AR0G89FL1VWIB8YJKHE81E0E0PCH.json",
                        "score": 0.85,
                        "url": "https://ai.tamin.ir/tree3_html?idx=6842&id=KC1AXYZ6AMG6ZGIVTX75FJ1JUW6MRIIA"
                    }
                ],
                "message": "قوانین این است",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "general_response",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "text": "یباتلمین"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "sdfsd",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "یباتلمین"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "لنیمکبلمن"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "لینتمبل"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "send_prompt",
                        "prompt": "ینشمکسیمن"
                    }
                ],
                "message": "درخواست بیماری شما در حال حاضر قابل پردازش نیست",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "message_response",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "مسیج تست"
                    }
                ],
                "message": "",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "profile_info",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "اطلاعات شخصی شما",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "edit_mobile",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "edit_mobile",
                        "prompt": "ویرایش موبایل"
                    }
                ],
                "message": "ویرایش شماره موبایل",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "add_account_number",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "add_account_number",
                        "prompt": "اضافه کردن شماره حساب"
                    }
                ],
                "message": "اضافه کردن شماره حساب",
                "message_id": "47da4216-5509-4f17-a12e-c5196f0f8c3b"
            },
            {
                "key": "appoinmet",
                "stepNumber": 1,
                "payload": {
                    "filter": [
                    ]
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "برای متخصص داخلی"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "متخصص داخلی",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "message_item",
                        "message": "برای متخصص چشم"
                    },
                    {
                        "item_type": "appoinmet_item",
                        "NAME": "ali",
                        "PROFICIENCY": "3",
                        "CITY": "sdg",
                        "ADDRESS": "dsf",
                        "CENTER": "fsdf",
                        "TITLE": "متخصص چشم",
                        "URL": "//jkif",
                        "MATCH_PERCENTAGE": "65"
                    },
                    {
                        "item_type": "message_item",
                        "message": "مسیج فوتر"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_link",
                        "prompt": "یاز کزدن سایت ۱۴۲۰"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_dial",
                        "prompt": "تماس با ۱۴۲۰"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "message",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "message_item",
                        "message": "مسیج هدر"
                    },
                    {
                        "item_type": "message_item",
                        "message": "مسیج فوتر"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_link",
                        "prompt": "یاز کزدن سایت ۱۴۲۰"
                    },
                    {
                        "item_type": "prompt_item",
                        "action_type": "open_support_dial",
                        "prompt": "تماس با ۱۴۲۰"
                    }
                ],
                "message": "",
                "message_id": "e6e66430-8de6-41cc-ba7d-b405377118a0"
            },
            {
                "key": "register_contract",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_freelance"
                        },
                        "title": "مشاغل آزاد"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_optional"
                        },
                        "title": "اختیاری"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_woman"
                        },
                        "title": "زنان"
                    },
                    {
                        "item_type": "deeplink",
                        "action_type": "local_deeplink",
                        "deeplink": {
                            "to": "contract_student"
                        },
                        "title": "دانشجویی"
                    }
                ],
                "message": "همراه عزیز برای انعقاد قرار بیمه مشاغل آزاد رو دکمه زیر بزنید ",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "funeral_allowance",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "کمک هزینه ترحیم",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "short_term_orthosis",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "هزینه اروتز پروتز",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "extend_education",
                "stepNumber": 1,
                "payload": {
                    "filter": [
                        "educationCode:1234567890"
                    ]
                },
                "data": [],
                "message": "مجوز استعلام اشتغال به تحصیل برای فرزند",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "pregnancy_pay",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "هزینه ایام بارداری",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": " workers_payment",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "پرداخت حق بیمه کارگران ساختمانی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "confirmation_medical_authorities",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "مشاهده تاییده های دریافتی از مراجع پزشکی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "complete_info_of_real_workshop",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "تکمیل اطلاعات کارفرمایی اشخاص حقیقی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "pension_survivor",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "برقراری غیرحضوری مستمری بازماندگان",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "disability_pension",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "برقراری مستمری از کارافتادگی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "deferred_installment_certificate",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "درخواست گواهی کسر اقساط معوق",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "wedding_present",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "کمک هزینه ازدواج",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "occurrence_report",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [
                    {
                        "item_type": "prompt_item",
                        "action_type": "occurrence_report",
                        "prompt": "اعلام حادثه"
                    }
                ],
                "message": "اعلام حادثه",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "edit_profile",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "ویرایش تاریخ تولد",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            },
            {
                "key": "edit_address",
                "stepNumber": 1,
                "payload": {
                    "filter": []
                },
                "data": [],
                "message": "اصلاح نشانی",
                "message_id": "34dfef1b-6296-4742-9544-0c0af8926a3f"
            }
        ]
    }
}
"""

/**
 * Renders one of every bubble type, one entity at a time, so the whole chat surface
 * can be exercised without a backend.
 *
 * Each entity carries its content in the payload the same way the server would, so
 * adjusting the demo means editing this fixture — never the service that reads it.
 * Sample figures and imagery come from a public news item about the organisation's
 * finances so the demo reads realistically.
 *
 * NOTE: the video URL is an Aparat CDN link whose wmsAuthSign token expires within
 * hours. When the clip stops loading, re-run scripts/refresh_showcase_video.py to pull
 * a fresh link, or point the entity at any direct mp4.
 */
internal const val FAKE_AGENT_SHOWCASE_RESPONSE = """{
    "id": "showcase-request",
    "eta": 1,
    "status": "DONE",
    "message": null,
    "result": {
        "sessionId": "showcase-session",
        "lastEntity": null,
        "entities": [
            {
                "key": "showcase",
                "stepNumber": 1,
                "payload": {"type": "rich_text", "title": "کسری ۹۰ همتی تامین اجتماعی", "text": "مدیرعامل سازمان تامین اجتماعی از ناترازی ۹۰ همتی منابع و بدهی ۷۵۰ همتی دولت به این سازمان خبر داد و به شمار ۲۹۰ هزار نفری متقاضیان بیمه بیکاری اشاره کرد.", "footnote": "منبع: دنیای اقتصاد — ۱۴۰۵/۰۴/۲۲"},
                "data": null,
                "message": "گزارش وضعیت مالی",
                "message_id": "showcase-1"
            },
            {
                "key": "showcase",
                "stepNumber": 2,
                "payload": {"type": "text", "text": "مصارف ماهانه سازمان حدود ۲۱۰ همت است، در حالی که وصول حق بیمه ماهانه کمتر از ۱۲۰ همت گزارش شده است."},
                "data": null,
                "message": "توضیح تکمیلی",
                "message_id": "showcase-2"
            },
            {
                "key": "showcase",
                "stepNumber": 3,
                "payload": {"type": "image", "image": "https://cdn.donya-e-eqtesad.com/thumbnail/lxq0x0mNjWDN/QHn8O9nsSzT8qCU7RegsN6Pbb5v74eEtbKeSOh05RaYNq9kWHVLNyUt7TZyzEhnm/0d50adf9ZjoxMzU1NDQ5MC5qcGd8ZnVpOjE2MjY0NTIxfGw6ZmF8djoxfHdpOjU2Nw+copy.jpg", "caption": "نشست خبری مدیرعامل سازمان تامین اجتماعی"},
                "data": null,
                "message": "تصویر خبر",
                "message_id": "showcase-3"
            },
            {
                "key": "showcase",
                "stepNumber": 4,
                "payload": {"type": "key_value", "title": "ارقام کلیدی گزارش", "items": [{"key": "کسری ماهانه", "value": "۹۰ همت"}, {"key": "مصارف ماهانه", "value": "۲۱۰ همت"}, {"key": "وصول حق بیمه", "value": "کمتر از ۱۲۰ همت"}, {"key": "بدهی دولت", "value": "۷۵۰ همت"}]},
                "data": null,
                "message": "ارقام کلیدی",
                "message_id": "showcase-4"
            },
            {
                "key": "showcase",
                "stepNumber": 5,
                "payload": {"type": "table", "title": "ترکیب بدهی‌ها به سازمان", "columns": ["عنوان", "مبلغ", "سهم"], "rows": [["بدهی دولت", "۷۵۰ همت", "۷۹٪"], ["بدهی کارفرمایان", "۲۰۰ همت", "۲۱٪"], ["جمع کل", "۹۵۰ همت", "۱۰۰٪"]]},
                "data": null,
                "message": "جدول بدهی‌ها",
                "message_id": "showcase-5"
            },
            {
                "key": "showcase",
                "stepNumber": 6,
                "payload": {"type": "table", "title": "آمار پوشش بیمه‌ای", "columns": ["شاخص", "۱۴۰۲", "۱۴۰۳", "۱۴۰۴", "۱۴۰۵", "روند"], "rows": [["بیمه‌شدگان (میلیون)", "۴۴", "۴۵", "۴۶", "۴۷", "صعودی"], ["مستمری‌بگیران (میلیون)", "۴.۶", "۴.۸", "۵.۰", "۵.۲", "صعودی"], ["نسبت پشتیبانی", "۹.۵", "۹.۳", "۹.۱", "۹.۰", "نزولی"]]},
                "data": null,
                "message": "جدول عریض",
                "message_id": "showcase-6"
            },
            {
                "key": "showcase",
                "stepNumber": 7,
                "payload": {"type": "chart", "title": "منابع و مصارف ماهانه", "kind": "bar", "labels": ["مصارف", "وصولی", "کسری"], "series": "ماهانه", "values": [210, 120, 90], "unit": "همت"},
                "data": null,
                "message": "نمودار منابع و مصارف",
                "message_id": "showcase-7"
            },
            {
                "key": "showcase",
                "stepNumber": 8,
                "payload": {"type": "chart", "title": "ترکیب بدهی‌ها", "kind": "line", "labels": ["کارفرمایان", "دولت"], "series": "بدهی", "values": [200, 750], "unit": "همت"},
                "data": null,
                "message": "روند بدهی",
                "message_id": "showcase-8"
            },
            {
                "key": "showcase",
                "stepNumber": 9,
                "payload": {"type": "video", "video": "https://caspian27.cdn.asset.aparat.com/aparat-video/7551fc70a1e2efbe0a54cb3371b29ba272389328-480p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjdjM2IzODlkZDAzNGFhNDA0MzEyY2NkMzgzYzlhMGM5IiwiZXhwIjoxNzg1MjcyMjk0LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.bU1IoSMLqmxzxvOdHwUINjlSF81xFdC1yaNpTZyJY-Q", "thumbnail": "https://static.cdn.asset.aparat.com/avt/72389328-3124-l__6255.jpg?width=900&quality=90&secret=zUQZBEhejLAPA-WoOQ0g3Q", "duration": "266000", "caption": "آموزش پرداخت بیمه اختیاری و آزاد تامین اجتماعی با گوشی موبایل"},
                "data": null,
                "message": "گزارش تصویری",
                "message_id": "showcase-9"
            },
            {
                "key": "showcase",
                "stepNumber": 10,
                "payload": {"type": "voice", "audio": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "duration": "372000", "caption": "خلاصه صوتی گزارش", "waveform": [2000, 6500, 12000, 18000, 9000, 4200, 15000, 22000, 17000, 8000, 3000, 11000, 19000, 26000, 21000, 12000, 5000, 9500, 16000, 7000]},
                "data": null,
                "message": "خلاصه صوتی",
                "message_id": "showcase-10"
            },
            {
                "key": "showcase",
                "stepNumber": 11,
                "payload": {"type": "deep_link", "title": "مشاهده اطلاعات کارگاه", "destination": "workshops"},
                "data": null,
                "message": "دسترسی به سرویس",
                "message_id": "showcase-11"
            },
            {
                "key": "showcase",
                "stepNumber": 12,
                "payload": {"type": "web_link", "title": "متن کامل گزارش", "url": "https://donya-e-eqtesad.com/4281737"},
                "data": null,
                "message": "متن کامل خبر",
                "message_id": "showcase-12"
            },
            {
                "key": "showcase",
                "stepNumber": 13,
                "payload": {"type": "processing", "steps": ["بررسی درخواست", "دریافت آمار", "آماده‌سازی پاسخ"], "active": "2", "completed": "true"},
                "data": null,
                "message": "مراحل پردازش",
                "message_id": "showcase-13"
            },
            {
                "key": "showcase",
                "stepNumber": 14,
                "payload": {"type": "error", "text": "دریافت آمار لحظه‌ای ممکن نشد."},
                "data": null,
                "message": "نمونه خطا",
                "message_id": "showcase-14"
            },
            {
                "key": "showcase",
                "stepNumber": 15,
                "payload": {"type": "suggestions", "prompts": ["بدهی دولت به تامین اجتماعی چقدر است؟", "چند نفر مستمری‌بگیر هستند؟", "شرایط بیمه بیکاری چیست؟"]},
                "data": null,
                "message": "پیشنهادها",
                "message_id": "showcase-15"
            }
        ]
    }
}
"""
