package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO

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

fun WorkshopDebitDTO.toDomain(): WorkshopDebitDN {
    return WorkshopDebitDN(
        debitNumber = debitNumber,
        debitCreateReasonCode = debitCreateReasonCode,
        debitCreateReasonDesc = debitCreateReasonDesc,
        debitStartDate = debitStartDate,
        debitEndDate = debitEndDate,
        debitAmount = debitAmount,
        debitRemain = debitRemain,
        withoutPentaltyAmount = withoutPentaltyAmount,
        penaltyList = penaltyList,
        penaltyPay = penaltyPay,
        sum = sum,
        nimOshr = this.nimOshr,
        debitStepDesc = debitStepDesc,
        debitStatDesc = debitStatDesc,
        debitStepCode = debitStepCode,
        debitStatCode = debitStatCode,
        mastCustomerTypeCode = mastCustomerTypeCode,
        mastCustomerCode = mastCustomerCode,
        peymanSequence = peymanSequence,
        debitCreateDate = debitCreateDate,
        cludatCode = cludatCode,
        cludatDesc = cludatDesc,
        nimOshrKol = nimOshrKol,
        docDate = docDate,
        stepCat = stepCat,
    )
}
