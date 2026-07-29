package com.tamin.taminhamrah.ui.home.services.employer.contract.model

import com.google.gson.annotations.SerializedName

data class MafasaHesabRequestModel(
    @SerializedName("cntamount")
    var amountCount: String = "0",
    @SerializedName("cntamountcurrency")
    var currencyAmount: String = "0",
    @SerializedName("cntamountcurrencyToR")
    var currencyAmountToRial: String = "0",
    @SerializedName("cntamounttotal")
    var totalAmount: Double = 0.0,
    @SerializedName("endDate")
    var endDate: String = "",//date in string eg:"2022-10-08T20:30:00.000Z"
    @SerializedName("letdate")
    var letterDate: String = "",//date in string eg:"2022-10-08T20:30:00.000Z"
    @SerializedName("letno")
    var letterNumber: String = "",
    @SerializedName("startDate")
    var startDate: String = "",//date in string eg:"2022-10-08T20:30:00.000Z"
    @SerializedName("dataDetail")
    var documentList: ArrayList<DocumentInfo> = ArrayList(),
    @SerializedName("hasLetImage")
    var hasLetImage: Boolean = false,
    @SerializedName("natcodecontract")
    var contractorWorkshopId: String = "",
    @SerializedName("subcontractor")
    var SubContractor: String = "0",//1 : yes, 0: no
    @SerializedName("subjectOwner")
    var subjectOwner: String = "",
    @SerializedName("subjectamount1")//مبلغ حق بیمه پرداخت شده
    var subjectamount1: String? = "",
    @SerializedName("subjectamount2")
    var subjectamount2: Any? = null,
    @SerializedName("subjectamount3")
    var subjectamount3: String? = "",
    @SerializedName("subjectamount4")
    var subjectamount4: String? = "",
    @SerializedName("subjectimage")
    var subjectimageId: String = "",
    @SerializedName("subjecttext1")
    var subjecttext1: String = "",//محل اعتبار طرح
    @SerializedName("subjecttext2")
    var subjecttext2: String = "",//ردیف بودجه
    @SerializedName("contractsubjectcode")
    var contractsubjectcode: String = ""//موضوع قرارداد
) {

    var contractsubjectTitle=""
    var subjectOwnerTitle =""
    fun clearValues() {
        subjectamount1 = ""
        subjectamount2 = null
        subjectamount3 = ""
        subjectamount4 = ""
        subjectimageId = ""
        subjecttext1 = ""
        subjecttext2 = ""
        subjectimageId=""
        subjectOwner = ""
        subjectOwnerTitle=""
    }
}

data class DocumentInfo(
    var documentId: String = "", var documentCode: String = "", var documentType: String = ""
)

enum class EnumDocumentType constructor(var code: String) {
    TYPE_IMAGE("1"),
    TYPE_PDF("2")
}

enum class EnumSubContractor constructor(var code: String) {
    YES("01"),
    NO("00")
}

enum class EnumDocumentCode constructor(var docName: String, var code: String) {
    TYPE_IMAGE("", "1"),
    TYPE_PDF_2("", "3"),
    TYPE_PDF_3("", "4")
}



