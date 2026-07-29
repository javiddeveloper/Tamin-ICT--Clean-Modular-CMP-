package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class DebitObjectionResponse(
    var data: DebitObjection? = null
) : BaseResponseNew()

data class DebitObjection(
    val badviDate: String? = null,
    val badviNo: String? = null,
    val branchCode: String? = null,
    val debitNumber: String? = null,
    val debitStatCode: String? = null,
    val debitStepCode: String? = null,
    val objectionDate: String? = null,
    val objectionDesc: String? = null,
    val objectionPhotos: List<ObjectionPhoto>? = null,
    val objectionType: String? = null,
    val peymanSequence: String? = null,
    val seporde: String? = null,
    val status: String? = null,
    val type1: String? = null,
    val type2: String? = null,
    val type3: String? = null,
    val type4: String? = null,
    val type5: String? = null,
    val type6: String? = null,
    val type7: String? = null,
    val type8: String? = null,
    val type9: String? = null,
    val type10: String? = null,
    val type11: String? = null,
    val type12: String? = null,
    val type13: String? = null,
    val type14: String? = null,
    val type15: String? = null,
    val type16: String? = null,
    val type17: String? = null,
    val type18: String? = null,
    val type19: String? = null,
    val workshopId: String? = null,
    var seqNo: Int? = null,
    var defectDesc: Any? = null,
    var defectFlag: Any? = null,
    var createUserId: String? = null,
    var createDate: Long? = null,
    var confirmDate: Any? = null,
    var orderNumber: Any? = null,
    var identifier: Any? = null,
    var refId: String? = null,
    var payment: Any? = null,
    var heyatDate: Any? = null,
    var heyatTime: Any? = null,
    var voteType: Any? = null,
    var confirmUserId: Any? = null,
    var action: Any? = null
)

data class ObjectionPhoto(
    val guid: String?,
    val type: String?
)