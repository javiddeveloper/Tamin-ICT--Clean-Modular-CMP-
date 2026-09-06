package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of
 * `workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/{workshopId}/{branchCode}`
 * — a ردیف پیمان of a workshop that has no تعهدنامه on file.
 *
 * Deliberately *not* [EmployerAgreementDTO], even though both describe a contract row of the same
 * workshop. Two things differ on the wire and one of them is fatal:
 *
 * - ردیف پیمان is `contractRow` here and `pymseq` there; تاریخ تعهد is `startDate` here and
 *   `startdate` there. Both spellings are the contract.
 * - the nested workshop object is a *different shape*: this endpoint sends `character` and
 *   `workshopStatus` as bare strings, while the employer-agreement endpoint sends them as objects.
 *   Reusing [EmployerWorkshopDTO] here would throw on the first row that carries either.
 *
 * The rest of the envelope (postal code, telephone, names, national code, role and ticket) is
 * fetched and unused; `ignoreUnknownKeys` drops it.
 */
@Serializable
data class WorkshopContractDTO(
    /** ردیف پیمان. Spelled `contractRow` on this endpoint only — see the class comment. */
    @SerialName("contractRow") val contractRow: String? = null,
    /** تاریخ تعهد. Camel-case here, lower-case `startdate` on the employer-agreement endpoint. */
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("workshop") val workshop: WorkshopContractInfoDTO? = null,
)

/**
 * The workshop this contract row belongs to.
 *
 * Only the three fields the card reads are modelled. The full object carries the same activity and
 * status columns [EmployerWorkshopDTO] does, but typed as plain strings rather than code/description
 * pairs — so nothing here may be widened by copying fields across from that class.
 */
@Serializable
data class WorkshopContractInfoDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)
