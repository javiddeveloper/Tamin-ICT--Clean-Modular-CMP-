package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class InboxPdfItem(var data: PdfItem? = null) : BaseResponseNew()
data class PdfItem(
    var hasImage: Boolean? = null,
    var hasText: Boolean? = null,
    var pdf: String? = null,
    var id: Int? = null,
    var text: Any? = null,
    var hasPDF: Boolean = false,
)