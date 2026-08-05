package com.tamin.taminhamrah.useCases.user.mockUseCases

import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN

object ProfileMocks {
    val subdominantMockData = SubdominantDN(
        list = listOf(
            SubdominantItemDN(
                id = 1L,
                firstName = "مریم",
                lastName = "حسینی",
                fatherName = "محمد",
                nationalCode = "0012345678",
                relationDescription = "همسر",
                status = "فعال",
                insuranceId = "INS-1001"
            ),
            SubdominantItemDN(
                id = 2L,
                firstName = "امیرحسین",
                lastName = "رضایی",
                fatherName = "رضا",
                nationalCode = "0087654321",
                relationDescription = "فرزند پسر",
                status = "فعال",
                insuranceId = "INS-1002"
            ),
            SubdominantItemDN(
                id = 3L,
                firstName = "فاطمه",
                lastName = "رضایی",
                fatherName = "رضا",
                nationalCode = "0099887766",
                relationDescription = "فرزند دختر",
                status = "غیرفعال",
                insuranceId = "INS-1003"
            )
        ),
        total = "3"
    )
}
