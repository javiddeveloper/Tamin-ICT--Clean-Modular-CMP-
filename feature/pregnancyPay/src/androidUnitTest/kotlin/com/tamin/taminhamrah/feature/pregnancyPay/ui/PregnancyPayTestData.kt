package com.tamin.taminhamrah.feature.pregnancyPay.ui

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyBranchWorkshopDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN

/** Shared fixtures for the pregnancy-pay ViewModel tests. */
internal object PregnancyPayTestData {

    val femaleMainInfo = PregnancyMainInfoDN(
        risuid = "risuid-1",
        nationalCode = "0012345678",
        firstName = "زهرا",
        lastName = "احمدی",
        mobileNumber = "09120000000",
        genderCode = "02",
        serviceDateTimeStamp = 0,
        bankAccount = "5678123459870012",
        bankName = "بانک ملت",
        insuranceTypeDesc = "بیمهٔ اجباری کارگری",
        insuranceStatusDesc = "برخوردار",
        branchWorkshops = listOf(
            PregnancyBranchWorkshopDN(
                branchCode = "10",
                branchName = "شعبه مرکزی",
                workshopName = "کارگاه اصلی",
            ),
        ),
    )

    val maleMainInfo = femaleMainInfo.copy(genderCode = "01")

    val pregnancyStatusOptions = listOf(
        PregnancyOptionDN(code = "1", name = "بارداری طبیعی"),
        PregnancyOptionDN(code = "2", name = "بارداری پرخطر"),
    )

    val pregnancyTypeOptions = listOf(
        PregnancyOptionDN(code = "1", name = "تک قلو"),
        PregnancyOptionDN(code = "2", name = "دوقلو"),
        PregnancyOptionDN(code = "3", name = "سه قلو یا بیشتر"),
    )
}
