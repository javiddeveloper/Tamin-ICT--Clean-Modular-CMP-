package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO

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

fun PaymentSheetDTO.toDomain(): PaymentSheetDN {
    return PaymentSheetDN(
        orderNo = orderNo,
        orderRow = orderRow,
        payId = payId,
        mastCustomerCode = mastCustomerCode,
        rcntrow = rcntrow,
        mastCustomerName = mastCustomerName,
        debitCreateReasonCode = debitCreateReasonCode,
        debitCreateReasonDesc = debitCreateReasonDesc,
        debitNo = debitNo,
        docDate = docDate,
        paySeqAmount = paySeqAmount,
        orpStatusCode = orpStatusCode,
        orpStatusDesc = orpStatusDesc,
        cardDate = cardDate,
        payKindCode = payKindCode,
        payKindDesc = payKindDesc,
        ouragGno = ouragGno,
        ouragSDate = ouragSDate
    )
}
