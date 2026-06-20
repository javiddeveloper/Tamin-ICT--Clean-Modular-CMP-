package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO

fun EmployerWorkshopDTO.toDomain(): EmployerWorkshopDN {
    return EmployerWorkshopDN(
        sswn = sswn,
        branchTitle = branchTitle,
        workshopApproveDate = workshopApproveDate,
        inclusionDate = inclusionDate,
        brhCode = brhCode,
        activityName = activityName,
        workshopRegisterDate = workshopRegisterDate,
        branchCode = branchCode,
        workshopName = workshopName,
        employerName = employerName,
        actitvityCode = actitvityCode,
        userId = userId,
        workshopId = workshopId,
        workshopUnemployedStat = workshopUnemployedStat
    )
}

fun EmployerAgreementDTO.toDomain(): EmployerAgreementDN {
    return EmployerAgreementDN(
        pymseq = pymseq,
        regno = regno,
        firstname = firstname,
        emailaddr = emailaddr,
        workshop = workshop?.toDomain(),
        nationalno = nationalno,
        mobileno = mobileno,
        startdate = startdate,
        mastcusttype = mastcusttype,
        createdt = createdt,
        masttyp = masttyp,
        logicalDeleted = logicalDeleted,
        regemailseq = regemailseq,
        lastname = lastname,
        special = special,
        risuid = risuid,
        nationalcode = nationalcode,
        enddate = enddate,
        letDate = letDate,
        regdate = regdate,
        roletype = roletype,
        dname = dname,
        letNo = letNo,
        createuid = createuid
    )
}
