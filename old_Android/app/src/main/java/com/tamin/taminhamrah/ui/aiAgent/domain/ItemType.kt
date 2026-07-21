package com.tamin.taminhamrah.ui.aiAgent.domain


sealed class ItemType(val key: String) {
    object GroupButton : ItemType(GROUP_BUTTON)
    object DeeplinkButton : ItemType(DEEP_LINK_BUTTON)
    object Text : ItemType(TEXT)
    object KeyValue : ItemType(TEXT)
    object DeepLinkWeb : ItemType(DEEP_LINK_WEB)
    object EditMobile : ItemType(EDIT_MOBILE)


    companion object {
        const val GROUP_BUTTON = "group_button"
        const val DEEP_LINK_WEB = "button_deeplink_web"
        const val DEEP_LINK_BUTTON = "button_deeplink"
        const val TEXT = "text"
        const val KEY_VALUE = "key_value"
        const val EDIT_MOBILE = "edit_mobile"


        fun fromString(key: String?): ItemType? {
            return when (key) {
                GROUP_BUTTON -> GroupButton
                DEEP_LINK_BUTTON -> DeeplinkButton
                TEXT -> Text
                KEY_VALUE ->   KeyValue
                DEEP_LINK_WEB -> DeepLinkWeb
                EDIT_MOBILE -> EditMobile
                // APPOINTMENT -> Appointment
                else -> null
            }
        }
    }
}