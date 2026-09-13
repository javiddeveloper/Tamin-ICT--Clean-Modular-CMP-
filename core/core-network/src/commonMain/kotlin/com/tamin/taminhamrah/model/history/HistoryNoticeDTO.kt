package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * What `sendeblagh` answers with.
 *
 * The message is under `text`, not `message` — the field name the previous app read, kept as the
 * server spells it.
 */
@Serializable
data class HistoryNoticeDTO(
    @SerialName("text") val text: String? = null,
)
