package com.tamin.taminhamrah.model.workshop

/**
 * وضعیت فعالیت کارگاه, as the service codes it.
 *
 * One table for the code: the filter sends it, the row is bucketed by it, and the list screen
 * offers it as an option. Keeping those three in one place is why the label and the colour live
 * with the UI rather than being a fourth parallel table here.
 *
 * The label shown on a card always comes from the server's own `workshopStatusDesc`; this enum
 * only decides which bucket an unknown or missing code falls into.
 */
enum class WorkshopActivityStatus(val code: String) {
    ACTIVE("01"),
    SEMI_ACTIVE("02"),
    INACTIVE("03"),
    ;

    companion object {
        /** Anything the service has not published a code for is treated as inactive. */
        fun fromCode(code: String?): WorkshopActivityStatus =
            entries.firstOrNull { it.code == code } ?: INACTIVE
    }
}
