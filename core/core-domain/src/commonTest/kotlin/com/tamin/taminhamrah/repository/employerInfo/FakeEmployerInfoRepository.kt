package com.tamin.taminhamrah.repository.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeEmployerInfoRepository : EmployerInfoRepository {

    var legalWorkshopResult: LegalWorkshopDN = LegalWorkshopDN(name = "شرکت تست", nationalCode = "10100000000")
    var legalWorkshopCeoResult: LegalWorkshopCeoDN = LegalWorkshopCeoDN(firstName = "علی", lastName = "محمدی")
    var requestLegalTicketResult: String = "12345"
    var submitLegalWorkshopInfoResult: String = "OK"
    var requestRealTicketResult: String = "54321"
    var submitRealWorkshopInfoResult: String = "OK"

    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("Fake error")

    var lastLegalNationalCode: String? = null
    var lastCeoNationalCode: String? = null
    var lastCeoBirthDateMillis: Long? = null
    var lastLegalTicketMobile: String? = null
    var lastLegalTicketEmail: String? = null
    var lastLegalTicketCeoNationalCode: String? = null
    var lastLegalWorkshopInfoRequest: LegalWorkshopInfoRequestDN? = null
    var lastRealTicketMobile: String? = null
    var lastRealTicketEmail: String? = null
    var lastRealWorkshopInfoRequest: RealWorkshopInfoRequestDN? = null

    override fun getLegalWorkshop(legalWorkshopId: String): Flow<LegalWorkshopDN> = flow {
        if (shouldThrowError) throw error
        lastLegalNationalCode = legalWorkshopId
        emit(legalWorkshopResult)
    }

    override fun getLegalWorkshopCeo(nationalCode: String, birthDateMillis: Long): Flow<LegalWorkshopCeoDN> = flow {
        if (shouldThrowError) throw error
        lastCeoNationalCode = nationalCode
        lastCeoBirthDateMillis = birthDateMillis
        emit(legalWorkshopCeoResult)
    }

    override fun requestLegalTicket(mobile: String, email: String, ceoNationalCode: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastLegalTicketMobile = mobile
        lastLegalTicketEmail = email
        lastLegalTicketCeoNationalCode = ceoNationalCode
        emit(requestLegalTicketResult)
    }

    override fun submitLegalWorkshopInfo(request: LegalWorkshopInfoRequestDN): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastLegalWorkshopInfoRequest = request
        emit(submitLegalWorkshopInfoResult)
    }

    override fun requestRealTicket(mobile: String, email: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastRealTicketMobile = mobile
        lastRealTicketEmail = email
        emit(requestRealTicketResult)
    }

    override fun submitRealWorkshopInfo(request: RealWorkshopInfoRequestDN): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastRealWorkshopInfoRequest = request
        emit(submitRealWorkshopInfoResult)
    }
}
