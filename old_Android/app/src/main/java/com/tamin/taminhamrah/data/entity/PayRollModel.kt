package com.tamin.taminhamrah.data.entity

data class PayRollModel(
    var purePayment :Long=0,
    var itemListPayment: List<KeyValueModel>? = null,
    var itemListReduce: List<KeyValueModel>? = null,
    var itemListLoan: List<KeyValueModel>? = null,
)


