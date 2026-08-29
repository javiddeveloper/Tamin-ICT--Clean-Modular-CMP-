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
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitPR
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListPR
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelPR

fun EmployerWorkshopDN.toPresentation(): EmployerWorkshopPR {
    return EmployerWorkshopPR(
        sswn = sswn,
        branchTitle = branchTitle,
        branchName = branchName,
        lastAddress = lastAddress,
        characterCode = characterCode,
        characterDesc = characterDesc,
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

fun WorkshopDebitDN.toPresentation(): WorkshopDebitPR {
    return WorkshopDebitPR(
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
        nimOshr = nimOshr,
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

fun WorkshopDebitListDN.toPresentation(): WorkshopDebitListPR {
    return WorkshopDebitListPR(
        list = list?.map { it.toPresentation() },
        total = total
    )
}

fun WorkshopDebtInquiryDN.toPresentation(): WorkshopDebtInquiryPR {
    return WorkshopDebtInquiryPR(
        status = status,
        workshopId = workshopId,
        branchCode = branchCode,
        workshopName = workshopName,
        result = result,
        amount1 = amount1,
        sDate = sDate,
        amount2 = amount2,
        amount3 = amount3
    )
}

fun WorkShopDebtDN.toPresentation(): WorkShopDebtPR {
    return WorkShopDebtPR(
        rowNum = rowNum, debitNumber = debitNumber, debitAmount = debitAmount,
        debitRemain = debitRemain, debitCreateReasonDesc = debitCreateReasonDesc,
        debitStatDesc = debitStatDesc
    )
}

fun WorkshopsDebtListModelDN.toPresentation(): WorkshopsDebtListModelPR {
    return WorkshopsDebtListModelPR(
        debitNumber = debitNumber, debitAmount = debitAmount, debitRemain = debitRemain,
        status = status, debitCreateReasonCode = debitCreateReasonCode
    )
}

fun WorkshopMemberDN.toPresentation(): WorkshopMemberPR {
    return WorkshopMemberPR(
        leavingWorkStatus = leavingWorkStatus, leavingWorkDate = leavingWorkDate,
        specialSubType = specialSubType
    )
}

fun WorkshopNewMemberDN.toPresentation(): WorkshopNewMemberPR {
    return WorkshopNewMemberPR(
        id = id,
        dateOfStart = dateOfStart,
        insuranceId = insuranceId,
        relationWithTamin = relationWithTamin,
        organizationId = organizationId,
        workshopId = workshopId,
        job = job
    )
}

fun WorkshopStackHolderDN.toPresentation(): WorkshopStackHolderPR {
    return WorkshopStackHolderPR(
        stackId = stackId, nationalId = nationalId, mobile = mobile, stackType = stackType
    )
}
