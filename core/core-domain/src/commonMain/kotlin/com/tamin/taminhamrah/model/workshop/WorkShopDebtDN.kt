package com.tamin.taminhamrah.model.workshop

data class WorkShopDebtDN(
    val rowNum: Long,
    val debitNumber: String,
    val orderRecipeDate: String,
    val mastCustomerCode: String,
    val debitAmount: Long,
    val debitRemain: Long,
    val debitStartDate: String,
    val debitEndDate: String,
    val mastCustomerTypeCode: String,
    val peymanSequence: String,
    val debitCreateReasonCode: String,
    val debitCreateReasonDesc: String,
    val debitNumberInstallment: String,
    val mande: String,
    val bimehAmount: String,
    val bikariAmount: String,
    val sayerAmount: String,
    val debitStepCode: String,
    val debitStepDesc: String,
    val debitStatDesc: String,
    val debitStatCode: String,
    val stepCat: String,
    val docDateEblaghEjra: String,
    val badviNo: String,
    val badviDate: String,
    val calculateDate: String,
    val seqNo: Long
)

data class WorkShopDebtListDN(
    val list: List<WorkShopDebtDN>,
    val total: Int
)
