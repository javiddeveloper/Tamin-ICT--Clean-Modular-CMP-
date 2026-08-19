package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Field names/types mirror the legacy "occurence" backend endpoint exactly (confirmed against a
 * real successful request/response pair from the old native app) — epoch-millisecond date
 * strings, integer codes for gender/marital status/nationality/outcome, and the nested
 * documentFile/ocurrenceDocumentType document shape. Do not "clean up" these names to be more
 * readable; the backend rejects anything else with a 500.
 */
@Serializable
data class OccurrenceRequestDTO(
    @SerialName("birthDate") val birthDate: String,
    @SerialName("bossFullName") val bossFullName: String,
    @SerialName("bossMobileNumber") val bossMobileNumber: String,
    @SerialName("branchCode") val branchCode: String,
    @SerialName("branchName") val branchName: String,
    @SerialName("employeeDate") val employeeDate: String,
    @SerialName("gender") val gender: Int,
    @SerialName("insuranceID") val insuranceID: String,
    @SerialName("isuTypeDesc") val isuTypeDesc: String,
    @SerialName("isuTypecode") val isuTypecode: String,
    @SerialName("jobDesc") val jobDesc: String,
    @SerialName("marriageStatusCode") val marriageStatusCode: Int,
    @SerialName("nationCode") val nationCode: Int,
    @SerialName("occurrenceAddress") val occurrenceAddress: String,
    @SerialName("occurrenceDate") val occurrenceDate: String,
    @SerialName("occurrenceDesc") val occurrenceDesc: String,
    @SerialName("occurrenceDocumentList") val occurrenceDocumentList: List<OccurrenceDocumentDTO>,
    @SerialName("occurrenceResult") val occurrenceResult: Int,
    @SerialName("occurrenceTime") val occurrenceTime: String,
    @SerialName("pFirstName") val pFirstName: String,
    @SerialName("pLastName") val pLastName: String,
    @SerialName("pNationalCode") val pNationalCode: String,
    @SerialName("reportAddress") val reportAddress: String,
    @SerialName("reportJobLocation") val reportJobLocation: String,
    @SerialName("reportPostalCode") val reportPostalCode: String,
    @SerialName("reportTelephone") val reportTelephone: String,
    @SerialName("reporterType") val reporterType: String,
    @SerialName("rwworkfinish") val rwworkfinish: String,
    @SerialName("rwworkstart") val rwworkstart: String,
    @SerialName("vehicle") val vehicle: String,
    @SerialName("workshopAddress") val workshopAddress: String,
    @SerialName("workshopBranchCode") val workshopBranchCode: String,
    @SerialName("workshopCode") val workshopCode: String,
    @SerialName("workshopName") val workshopName: String,
    @SerialName("workshopPostalCode") val workshopPostalCode: String,
    @SerialName("workshopTelephone") val workshopTelephone: String,
)
