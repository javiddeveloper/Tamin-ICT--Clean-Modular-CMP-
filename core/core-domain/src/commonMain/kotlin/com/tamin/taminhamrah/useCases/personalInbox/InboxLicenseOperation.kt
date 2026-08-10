package com.tamin.taminhamrah.useCases.personalInbox

enum class InboxLicenseOperation(val value: String) {
    CANCEL("cancel"),
    APPROVE("ok");

    override fun toString(): String = value
}
