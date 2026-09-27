package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity

/** ذینفعان کارگاه. */
@Entity(tableName = "construction_beneficiary_pages", primaryKeys = ["listKey", "position"])
data class ConstructionBeneficiaryPageEntity(
    val listKey: String,
    val position: Int,
    val nationalCode: String?,
    val ownerType: String?,
    val requestNumber: Long?,
    val fileNumber: Long?,
    val requestDate: String?,
    val name: String?,
    val lastName: String?,
    val mobile: String?,
)

/** مدیریت پرداخت اقساط — installment letters of one workshop/branch. */
@Entity(tableName = "installment_letter_pages", primaryKeys = ["listKey", "position"])
data class InstallmentLetterPageEntity(
    val listKey: String,
    val position: Int,
    val workshopId: String?,
    val debitNumber: String?,
    val debitStepDescription: String?,
    val debitStatusDescription: String?,
    val debitStartDate: String?,
    val debitEndDate: String?,
    val remainingAmount: Long?,
    val debitNumberOld: String?,
)

/** بدهی‌های تقسیط‌شده — debit rows of one debit letter. */
@Entity(tableName = "installment_debit_pages", primaryKeys = ["listKey", "position"])
data class InstallmentDebitPageEntity(
    val listKey: String,
    val position: Int,
    val workshopId: String?,
    val debitNumber: String?,
    val debitStepDescription: String?,
    val debitStatusDescription: String?,
    val debitStartDate: String?,
    val debitEndDate: String?,
    val remainingAmount: Long?,
)

/** مدیریت اقساط و برگ پرداخت — installments of one debit letter. */
@Entity(tableName = "installment_construction_pages", primaryKeys = ["listKey", "position"])
data class InstallmentConstructionPageEntity(
    val listKey: String,
    val position: Int,
    val workshopId: String?,
    val debitNumber: String?,
    val debitSubCode: String?,
    val dtnAmount: Long?,
    val lastPaymentSheetAmount: Long?,
    val dtnExpireDate: String?,
    val lastPaymentSheetDescription: String?,
    val paymentDate: String?,
)
