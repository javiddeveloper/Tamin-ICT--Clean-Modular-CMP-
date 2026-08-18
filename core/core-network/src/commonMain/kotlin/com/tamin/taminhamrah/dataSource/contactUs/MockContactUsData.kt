package com.tamin.taminhamrah.dataSource.contactUs

import com.tamin.taminhamrah.model.contactUs.*

val mockContactUsData = ContactUsInfoDto(
    hotline = HotlineDto(
        title = "مرکز تماس شبانه‌روزی",
        number = "1420",
        dialNumber = "1420"
    ),
    socialChannels = listOf(
        SocialChannelDto(
            id = "email",
            title = "ایمیل",
            type = "EMAIL",
            actionUrl = "mailto:info@tamin.ir"
        ),
        SocialChannelDto(
            id = "faq",
            title = "سوالات متداول",
            type = "FAQ",
            actionUrl = "https://tamin.ir/faq"
        ),
        SocialChannelDto(
            id = "whatsapp",
            title = "واتساپ",
            type = "WHATSAPP",
            actionUrl = "https://whatsapp.com/channel/0029VawcdiZHbFV4ws6Klm1X"
        ),
        SocialChannelDto(
            id = "igap",
            title = "آی‌گپ",
            type = "IGAP",
            actionUrl = "https://igap.net/tamin"
        ),
        SocialChannelDto(
            id = "bale",
            title = "بله",
            type = "BALE",
            actionUrl = "https://ble.ir/tamin_media"
        ),
        SocialChannelDto(
            id = "aparat",
            title = "آپارات",
            type = "APARAT",
            actionUrl = "https://www.aparat.com/tamin_media"
        ),
        SocialChannelDto(
            id = "gap",
            title = "گپ",
            type = "GAP",
            actionUrl = "https://gap.im/tamin_media"
        ),
        SocialChannelDto(
            id = "soroush",
            title = "سروش",
            type = "SOROUSH",
            actionUrl = "https://splus.ir/tamin_media"
        ),
        SocialChannelDto(
            id = "rubika",
            title = "روبیکا",
            type = "RUBIKA",
            actionUrl = "https://rubika.ir/tamin_media"
        ),
        SocialChannelDto(
            id = "eitaa",
            title = "ایتا",
            type = "EITAA",
            actionUrl = "https://www.eitaa.com/tamin_media"
        )
    ),
    contactDetails = listOf(
        ContactDetailDto(
            id = "phone",
            type = "PHONE",
            title = "تلفن",
            value = "1420",
            actionUrl = "tel:02164501",
            canCopy = true
        ),
        ContactDetailDto(
            id = "fax",
            type = "FAX",
            title = "فکس",
            value = "021-66931008",
            actionUrl = "tel:02166931008",
            canCopy = true
        ),
        ContactDetailDto(
            id = "address",
            type = "ADDRESS",
            title = "نشانی",
            value = "تهران، خیابان آزادی، جنب وزارت تعاون، کار و رفاه اجتماعی، پلاک ۳۵۹، سازمان تأمین اجتماعی",
            actionUrl = "geo:35.7011,51.3752",
            canCopy = true
        ),
        ContactDetailDto(
            id = "postal_code",
            type = "POSTAL_CODE",
            title = "کد پستی",
            value = "1457965595",
            actionUrl = "",
            canCopy = true
        ),
        ContactDetailDto(
            id = "official_website",
            type = "WEBSITE",
            title = "درگاه رسمی",
            value = "tamin.ir",
            actionUrl = "https://tamin.ir",
            canCopy = false
        ),
        ContactDetailDto(
            id = "news_portal",
            type = "NEWS",
            title = "پایگاه خبری",
            value = "news.tamin.ir",
            actionUrl = "https://news.tamin.ir",
            canCopy = false
        ),
        ContactDetailDto(
            id = "email_info",
            type = "EMAIL",
            title = "پست الکترونیک",
            value = "info@tamin.ir",
            actionUrl = "mailto:info@tamin.ir",
            canCopy = true
        )
    ),
    footerTitle = "پاسخگوی شما هستیم",
    footerSubtitle = "۲۴ ساعته، ۷ روز هفته در کنار شما"
)
