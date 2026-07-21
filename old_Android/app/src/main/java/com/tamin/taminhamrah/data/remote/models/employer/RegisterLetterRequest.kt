package com.tamin.taminhamrah.data.remote.models.employer

data class RegisterLetterRequest(
    var brchCode: String? = "",
    var descriptions: String? = "",
    var leterImage: String? = "",
    var letetsubjectcode: Letetsubjectcode? = null,
    var letterRequestDetailCollection: ArrayList<LetterRequestDetailCollection> =ArrayList(),
    var rcntrow: String? = "null",
    var rwshid: String? = ""
)

