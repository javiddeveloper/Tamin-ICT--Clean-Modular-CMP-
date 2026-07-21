package com.tamin.taminhamrah.data.remote.models.services.occurrence

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class DocumentTypeResponse : ListDataModel<DocumentType>()

data class DocumentType(
    val docTypeId: String? = null,
    val docDesc: String? = null,
)

