package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices

import com.tamin.taminhamrah.model.content.LegalDocumentDN
import com.tamin.taminhamrah.repository.content.LegalDocumentRepository

/** Serves one canned تعهدنامه document; toggle [shouldThrow] to fake a missing id. */
class FakeLegalDocumentRepository : LegalDocumentRepository {
    var document: LegalDocumentDN = LegalDocumentDN(
        id = "employer-eservices-agreement",
        title = "تعهدنامهٔ خدمات غیرحضوری کارفرمایان",
        intro = "اینجانب {name} با کد ملی {nationalCode} متعهد می‌شوم",
        clauses = listOf("بند اول", "بند دوم"),
        acknowledgement = "موارد فوق را می‌پذیرم",
    )
    var shouldThrow: Boolean = false
    var lastRequestedId: String? = null
        private set

    override suspend fun getLegalDocument(id: String): LegalDocumentDN {
        lastRequestedId = id
        if (shouldThrow) throw RuntimeException("no such document: $id")
        return document
    }
}
