package com.tamin.taminhamrah.repository.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import kotlinx.coroutines.flow.Flow

/** تکمیل اطلاعات کارفرمایی — identity details for a workshop the signed-in user employs at. */
interface EmployerInfoRepository {
    fun getLegalWorkshop(legalWorkshopId: String): Flow<LegalWorkshopDN>

    fun getLegalWorkshopCeo(nationalCode: String, birthDateMillis: Long): Flow<LegalWorkshopCeoDN>

    /** Sends the validation ticket for the legal path. Returns the service's own message. */
    fun requestLegalTicket(mobile: String, email: String, ceoNationalCode: String): Flow<String>

    fun submitLegalWorkshopInfo(request: LegalWorkshopInfoRequestDN): Flow<String>

    /** Sends the validation ticket for the real path, to the contacts on the user's account. */
    fun requestRealTicket(mobile: String, email: String): Flow<String>

    fun submitRealWorkshopInfo(request: RealWorkshopInfoRequestDN): Flow<String>
}
