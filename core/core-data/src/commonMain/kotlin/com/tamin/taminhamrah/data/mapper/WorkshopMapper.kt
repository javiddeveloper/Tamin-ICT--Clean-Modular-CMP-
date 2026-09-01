package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO

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

fun WorkshopDebtInquiryDTO.toDomain(): WorkshopDebtInquiryDN {
    return WorkshopDebtInquiryDN(
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

fun WorkShopDebtDTO.toDomain(): WorkShopDebtDN {
    return WorkShopDebtDN(
        rowNum = rowNum ?: 0L,
        debitNumber = debitNumber ?: "",
        orderRecipeDate = orderRecipeDate ?: "",
        mastCustomerCode = mastCustomerCode ?: "",
        debitAmount = debitAmount ?: 0L,
        debitRemain = debitRemain ?: 0L,
        debitStartDate = debitStartDate ?: "",
        debitEndDate = debitEndDate ?: "",
        mastCustomerTypeCode = mastCustomerTypeCode ?: "",
        peymanSequence = peymanSequence ?: "",
        debitCreateReasonCode = debitCreateReasonCode ?: "",
        debitCreateReasonDesc = debitCreateReasonDesc ?: "",
        debitNumberInstallment = debitNumberInstallment ?: "",
        mande = mande ?: "",
        bimehAmount = bimehAmount ?: "",
        bikariAmount = bikariAmount ?: "",
        sayerAmount = sayerAmount ?: "",
        debitStepCode = debitStepCode ?: "",
        debitStepDesc = debitStepDesc ?: "",
        debitStatDesc = debitStatDesc ?: "",
        debitStatCode = debitStatCode ?: "",
        stepCat = stepCat ?: "",
        docDateEblaghEjra = docDateEblaghEjra ?: "",
        badviNo = badviNo ?: "",
        badviDate = badviDate ?: "",
        calculateDate = calculateDate ?: "",
        seqNo = seqNo ?: 0L
    )
}

fun WorkshopNewMemberDTO.toDomain(): WorkshopNewMemberDN {
    return WorkshopNewMemberDN(
        id = id ?: 0L,
        dateOfStart = dateOfStart ?: 0L,
        insuranceId = insuranceId ?: "",
        relationWithTamin = relationWithTamin ?: 0,
        organizationId = organizationId ?: "",
        workshopId = workshopId ?: "",
        job = job ?: ""
    )
}

fun WorkshopsDebtListModelDTO.toDomain(): WorkshopsDebtListModelDN {
    return WorkshopsDebtListModelDN(
        indebtednessAmount = indebtednessAmount ?: 0,
        insuranceAmount = insuranceAmount ?: 0,
        debitAmount = debitAmount ?: 0,
        debitCreateReasonCode = debitCreateReasonCode ?: "",
        debitEndDate = debitEndDate ?: "",
        debitNumber = debitNumber ?: "",
        debitRemain = debitRemain ?: 0,
        debitStartDate = debitStartDate ?: "",
        status = status ?: ""
    )
}

fun WorkshopMemberDTO.toDomain(): WorkshopMemberDN {
    return WorkshopMemberDN(
        leavingWorkStatus = leavingWorkStatus ?: "",
        leavingWorkDate = leavingWorkDate ?: "",
        specialSubType = specialSubType ?: ""
    )
}

fun WorkshopStackHolderDTO.toDomain(): WorkshopStackHolderDN {
    return WorkshopStackHolderDN(
        stackId = stackId ?: 0,
        mobile = mobile ?: "",
        birthDate = birthDate ?: 0L,
        telephon = telephon ?: "",
        userId = userId ?: "",
        nationalId = nationalId ?: "",
        stackType = stackType ?: "",
        startDate = startDate ?: 0L,
        email = email ?: ""
    )
}

fun LegalRepresentativeWorkshopDTO.toDomain(): LegalRepresentativeWorkshopDN {
    return LegalRepresentativeWorkshopDN(
        workshopId = workshopId ?: "",
        branchCode = branchCode ?: "",
        workshopName = workshopName,
        branchName = branchName,
        nationalId = nationalId,
        special = special ?: false,
        representativeCount = representativeCount,
    )
}

fun LegalRepresentativeDTO.toDomain(): LegalRepresentativeDN {
    return LegalRepresentativeDN(
        stakeId = stakeId ?: 0L,
        nationalId = nationalId ?: "",
        accessCode = accessCode ?: "",
        mobile = mobile,
        fullName = fullName,
        startDate = startDate,
        workshopId = workshopId ?: "",
        workshopName = workshopName,
        branchCode = branchCode ?: "",
        special = special ?: false,
    )
}

fun LegalRepresentativeRequestDN.toDto(ticket: String): LegalRepresentativeRequestDTO {
    val accessCode = buildString {
        append(if (hasElectronicNotification) '1' else '0')
        append(if (hasInternetList) '1' else '0')
        append(if (hasInsuredRegistration) '1' else '0')
        append("00000")
    }
    return LegalRepresentativeRequestDTO(
        accessCode = accessCode,
        branchCode = branchCode,
        nationalCode = nationalCode,
        workshopId = workshopId,
        special = special,
        ticket = ticket,
        contractRows = contractRows.takeIf { special && it.isNotEmpty() },
    )
}

fun LegalRepresentativeContractDTO.toDomain(): LegalRepresentativeContractDN {
    return LegalRepresentativeContractDN(
        contractRow = contractRow ?: "",
        title = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { null },
        nationalCode = nationalCode,
    )
}
