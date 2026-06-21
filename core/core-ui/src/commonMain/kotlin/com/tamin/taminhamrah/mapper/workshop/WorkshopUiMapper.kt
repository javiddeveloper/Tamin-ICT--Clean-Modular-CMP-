package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListPR
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR

fun EmployerWorkshopDN.toPresentation(): EmployerWorkshopPR {
    return EmployerWorkshopPR(
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

fun EmployerAgreementDN.toPresentation(): EmployerAgreementPR {
    return EmployerAgreementPR(
        pymseq = pymseq,
        regno = regno,
        firstname = firstname,
        emailaddr = emailaddr,
        workshop = workshop?.toPresentation(),
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

fun EmployerAgreementListDN.toPresentation(): EmployerAgreementListPR {
    return EmployerAgreementListPR(
        list = list?.map { it.toPresentation() },
        total = total
    )
}

fun PaymentSheetDN.toPresentation(): PaymentSheetPR {
    return PaymentSheetPR(
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

fun PaymentSheetListDN.toPresentation(): PaymentSheetListPR {
    return PaymentSheetListPR(
        list = list?.map { it.toPresentation() },
        total = total
    )
}
