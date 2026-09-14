package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO

/**
 * Temporary, in-memory stand-in for the legal-representative endpoints
 * (`legal-stakeholders`, `v.1/legal-stakeholders/units`, `legal-ticket*`) — for manual QA
 * of the "معرفی نماینده اشخاص حقوقی" flow while no test account/backend is available.
 *
 * A singleton (bound via Koin `single<WorkShopsRemoteDataSource>`), so edits/deletes made
 * through the UI persist for the rest of the app session. Remove this file and the mock
 * branches in [WorkShopsRemoteDataSourceImpl] to go back to hitting the real API — the real
 * calls are left commented out right next to each mock branch for that purpose.
 */
internal object MockLegalRepresentativeData {

    val workshops = listOf(
        LegalRepresentativeWorkshopDTO(
            workshopId = "2361847",
            branchCode = "0711",
            workshopName = "شرکت آبان صنعت پارس",
            branchName = "شعبهٔ ۷ تهران",
            nationalId = "1234567890",
            special = true,
            representativeCount = 2,
        ),
        LegalRepresentativeWorkshopDTO(
            workshopId = "1187452",
            branchCode = "0304",
            workshopName = "کارگاه فلزکاری البرز",
            branchName = "شعبهٔ ۳ کرج",
            nationalId = "1234567890",
            special = false,
            representativeCount = 0,
        ),
        LegalRepresentativeWorkshopDTO(
            workshopId = "9098123",
            branchCode = "1201",
            workshopName = "بازرگانی نوین تجارت",
            branchName = "شعبهٔ ۱۲ مشهد",
            nationalId = "1234567890",
            special = true,
            representativeCount = 1,
        ),
        LegalRepresentativeWorkshopDTO(
            workshopId = "9098133",
            branchCode = "1203",
            workshopName = "بازرگانی نوین شسی",
            branchName = "شعبهٔ ۱۲ شسی",
            nationalId = "1234567890",
            special = false,
            representativeCount = 1,
        ),
    )

    val representatives: MutableList<LegalRepresentativeDTO> = mutableListOf(
        LegalRepresentativeDTO(
            stakeId = 1001L,
            nationalId = "0064129077",
            accessCode = "11000000",
            mobile = "09461290077",
            fullName = "سارا کریمی",
            startDate = 1715000000000L,
            workshopId = "2361847",
            workshopName = "شرکت آبان صنعت پارس",
            branchCode = "0711",
            special = true,
        ),
        LegalRepresentativeDTO(
            stakeId = 1002L,
            nationalId = "0451193308",
            accessCode = "00100000",
            mobile = "09451193308",
            fullName = "حسین نیک‌پور",
            startDate = 1718000000000L,
            workshopId = "2361847",
            workshopName = "شرکت آبان صنعت پارس",
            branchCode = "0711",
            special = true,
        ),
        LegalRepresentativeDTO(
            stakeId = 1003L,
            nationalId = "0071234567",
            accessCode = "01000000",
            mobile = "09121234567",
            fullName = "محمد رضایی",
            startDate = 1720000000000L,
            workshopId = "9098133",
            workshopName = "بازرگانی نوین تجارت",
            branchCode = "1203",
            special = false,
        ),
    )

    /**
     * Contracts (پیمان‌ها) per special workshop, keyed by "workshopId/branchCode" — mirrors the
     * legacy Android app's `EmployerWorkshop` rows: each entry is a (contract × representative)
     * assignment record, not a bare contract listing. [LegalRepresentativeContractDTO.nationalCode]
     * holds whoever currently has that contract row; null/blank means unassigned.
     */
    val contracts: MutableMap<String, MutableList<LegalRepresentativeContractDTO>> = mutableMapOf(
        "2361847/0711" to mutableListOf(
            LegalRepresentativeContractDTO(
                contractRow = "12407",
                firstName = "پیمان احداث ساختمان اداری مرکزی",
                nationalCode = "0064129077", // سارا کریمی (1001)
            ),
            LegalRepresentativeContractDTO(
                contractRow = "12511",
                firstName = "پیمان محوطه‌سازی و تاسیسات",
                nationalCode = "0064129077", // سارا کریمی (1001)
            ),
            LegalRepresentativeContractDTO(
                contractRow = "12684",
                firstName = "پیمان نگهداری تجهیزات سرمایشی",
                nationalCode = "0451193308", // حسین نیک‌پور (1002)
            ),
        ),
        "1187452/0304" to mutableListOf(
            LegalRepresentativeContractDTO(contractRow = "13092", firstName = "پیمان بازسازی سوله تولیدی"),
            LegalRepresentativeContractDTO(contractRow = "13154", firstName = "پیمان نگهداری خطوط تولید"),
        ),
    )

    private var nextStakeId = 1004L

    fun upsert(
        workshopId: String,
        branchCode: String,
        nationalCode: String,
        accessCode: String,
        special: Boolean,
        contractRows: List<String>,
    ) {
        val existing = representatives.indexOfFirst {
            it.workshopId == workshopId && it.branchCode == branchCode && it.nationalId == nationalCode
        }
        if (existing >= 0) {
            representatives[existing] = representatives[existing].copy(
                accessCode = accessCode,
                special = special,
            )
        } else {
            representatives.add(
                LegalRepresentativeDTO(
                    stakeId = nextStakeId++,
                    nationalId = nationalCode,
                    accessCode = accessCode,
                    mobile = null,
                    fullName = null,
                    startDate = 1725000000000L,
                    workshopId = workshopId,
                    workshopName = workshops.firstOrNull { it.workshopId == workshopId }?.workshopName,
                    branchCode = branchCode,
                    special = special,
                )
            )
        }
        if (special) {
            assignContracts(workshopId, branchCode, nationalCode, contractRows)
        }
    }

    /**
     * Re-points ownership of this workshop's contract rows to [nationalCode]: rows in
     * [contractRows] get assigned to them, and any row they held previously that isn't in the new
     * selection is freed back to unassigned — mirroring what the real submit endpoint does to the
     * underlying employer-agreement records.
     */
    private fun assignContracts(
        workshopId: String,
        branchCode: String,
        nationalCode: String,
        contractRows: List<String>,
    ) {
        val key = "$workshopId/$branchCode"
        val rows = contracts[key] ?: return
        for (i in rows.indices) {
            val row = rows[i]
            when {
                contractRows.contains(row.contractRow) -> rows[i] = row.copy(nationalCode = nationalCode)
                row.nationalCode == nationalCode -> rows[i] = row.copy(nationalCode = null)
            }
        }
    }

    fun delete(stakeId: Long) {
        representatives.removeAll { it.stakeId == stakeId }
    }
}
