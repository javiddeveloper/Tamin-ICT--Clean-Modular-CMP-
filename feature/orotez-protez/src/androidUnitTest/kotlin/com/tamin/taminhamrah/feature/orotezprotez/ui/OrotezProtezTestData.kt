package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN

/** Shared fixtures for the orotez-protez ViewModel tests. */
internal object OrotezProtezTestData {

    val mainInfo = RequestInsuredMainInfoDN(
        risuid = "risuid-1",
        nationalCode = "0012345678",
        firstName = "علی",
        lastName = "رضایی",
        mobileNumber = "09120000000",
        genderCode = "M",
        branchCode = "10",
        branchName = "شعبه مرکزی",
        bankAccount = null,
        bankName = null,
        insuranceTypeDesc = null,
        insuranceStatusDesc = null,
        branchWorkshops = listOf(
            BranchWorkshopDN(
                branchCode = "10",
                branchName = "شعبه مرکزی",
                workshopCode = "20",
                workshopName = "کارگاه اصلی",
            ),
        ),
    )

    /**
     * Empty on purpose: mapping insured persons to options calls `getString(Res.string...)`, and
     * Compose Multiplatform resources can't be resolved in a plain JVM unit test.
     */
    val insuredPersons: List<InsuredPersonDN> = emptyList()
}
