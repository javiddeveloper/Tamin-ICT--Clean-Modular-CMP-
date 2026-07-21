package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceDocumentFile
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceDocumentType
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceImage
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq

/**
 * Maps the accumulated AI-form payload (field id → value) into the [OccurrenceReq] body
 * expected by ServiceRepository.sendOccurrenceRequest — the same model the native
 * OccurrenceReportFragment builds. Kept as a pure function so it can be unit tested
 * independently of Android and the network layer.
 *
 * Dates are sent as normalized digit strings (slashes stripped). The native fragment
 * uses Jalali-picker timestamps; the inline AI form collects typed dates, so the digit
 * form is used here — documented divergence, not implicit behavior.
 */
fun Map<String, Any?>.toOccurrenceReq(): OccurrenceReq {
    val payload = this

    fun str(key: String): String? = payload[key]?.toString()?.takeIf { it.isNotBlank() }
    fun digits(key: String): String? = str(key)?.replace("/", "")

    fun jalaliToTimestampStr(key: String): String? {
        val dateStr = str(key) ?: return null
        val clean = com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(dateStr).trim()
        val parts = clean.split('/')
        if (parts.size != 3) return null
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null
        return try {
            val pDate = saman.zamani.persiandate.PersianDate()
            pDate.shYear = year
            pDate.shMonth = month
            pDate.shDay = day
            pDate.hour = 12
            pDate.minute = 0
            pDate.second = 0
            pDate.time.toString()
        } catch (e: Exception) {
            null
        }
    }

    val workshopId = str(OccurrenceFormKeys.WORKSHOP_ID)
    val workshopParts = workshopId?.split(OccurrenceFormKeys.WORKSHOP_ID_SEPARATOR)
    val workshopCode = workshopParts?.getOrNull(0)?.takeIf { it.isNotBlank() }
    val workshopBranchCode = workshopParts?.getOrNull(1)?.takeIf { it.isNotBlank() }

    // Each document entry is "<docTypeId>:<guid>" (or a bare "<guid>" when no type was
    // chosen). Recover the (docTypeId, guid) pairs, defaulting the type when absent.
    val documents = str(OccurrenceFormKeys.DOCUMENTS)
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?.map { entry ->
            val parts = entry.split(":", limit = 2)
            if (parts.size == 2 && parts[0].isNotBlank()) {
                parts[0] to parts[1]
            } else {
                OccurrenceFormKeys.DEFAULT_DOC_TYPE_ID to entry
            }
        }
        .orEmpty()

    return OccurrenceReq().apply {
        pNationalCode = str(OccurrenceFormKeys.NATIONAL_ID)
        pFirstName = str(OccurrenceFormKeys.FIRST_NAME)
        pLastName = str(OccurrenceFormKeys.LAST_NAME)
        nationCode = str(OccurrenceFormKeys.NATION_CODE)?.toLongOrNull()
        reporterType = str(OccurrenceFormKeys.REPORTER_TYPE)
        birthDate = jalaliToTimestampStr(OccurrenceFormKeys.BIRTH_DATE)
        gender = str(OccurrenceFormKeys.GENDER)?.toLongOrNull()

        workshopCode?.let { this.workshopCode = it }
        workshopBranchCode?.let { this.workshopBranchCode = it }
        workshopName = str(OccurrenceFormKeys.WORKSHOP_NAME)
        bossFullName = str(OccurrenceFormKeys.EMPLOYER_NAME)
        bossMobileNumber = str(OccurrenceFormKeys.EMPLOYER_PHONE)
        workshopAddress = str(OccurrenceFormKeys.WORKSHOP_ADDRESS)
        workshopTelephone = str(OccurrenceFormKeys.WORKSHOP_PHONE)
        workshopPostalCode = str(OccurrenceFormKeys.WORKSHOP_POSTAL_CODE)

        marriageStatusCode = str(OccurrenceFormKeys.MARITAL_STATUS)?.toLongOrNull()
        employeeDate = jalaliToTimestampStr(OccurrenceFormKeys.EMPLOYMENT_DATE)
        jobDesc = str(OccurrenceFormKeys.JOB_DESCRIPTION)
        reportJobLocation = str(OccurrenceFormKeys.REPORT_JOB_LOCATION)
        vehicle = str(OccurrenceFormKeys.VEHICLE)
        rwworkstart = str(OccurrenceFormKeys.WORK_START)
        rwworkfinish = str(OccurrenceFormKeys.WORK_END)
        reportAddress = str(OccurrenceFormKeys.RESIDENTIAL_ADDRESS)
        reportTelephone = str(OccurrenceFormKeys.REPORT_TELEPHONE)
        reportPostalCode = str(OccurrenceFormKeys.REPORT_POSTAL_CODE)

        occurrenceDate = jalaliToTimestampStr(OccurrenceFormKeys.ACCIDENT_DATE)
        occurrenceTime = str(OccurrenceFormKeys.ACCIDENT_TIME)
        occurrenceResult = str(OccurrenceFormKeys.ACCIDENT_RESULT)?.toLongOrNull()
        occurrenceAddress = str(OccurrenceFormKeys.ACCIDENT_LOCATION)
        occurrenceDesc = str(OccurrenceFormKeys.ACCIDENT_DESCRIPTION)

        insuranceID = str(OccurrenceFormKeys.INSURANCE_ID)
        branchCode = str(OccurrenceFormKeys.BRANCH_CODE)
        branchName = str(OccurrenceFormKeys.BRANCH_NAME)
        isuTypecode = str(OccurrenceFormKeys.ISU_TYPE_CODE)
        isuTypeDesc = str(OccurrenceFormKeys.ISU_TYPE_DESC)

        occurrenceDocumentList = ArrayList(
            documents.map { (docTypeId, guid) ->
                OccurrenceImage(
                    OccurrenceDocumentType(docTypeId),
                    OccurrenceDocumentFile(guid)
                )
            }
        )
    }
}
