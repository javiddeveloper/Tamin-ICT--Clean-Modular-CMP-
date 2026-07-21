package com.tamin.taminhamrah.data.remote.models.services.workshop

import android.os.Parcelable
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import kotlinx.parcelize.Parcelize


class SmsListResponse: ListDataModel<SmsModel>()

@Parcelize
data class SmsModel(val id:Long?,val seqNo:Long?,val smsDescription:String?,val status:Int?,val voteType:Int?=null) : Parcelable {
    var index =""
    fun getObjectionStatus() =
         when (status) {
            1 -> ObjectionStatus.REGISTER.methodName
            2 -> ObjectionStatus.REVIEW.methodName
            3 -> ObjectionStatus.BOARD.methodName
            4 -> ObjectionStatus.RETRY.methodName
            5 -> ObjectionStatus.TIME.methodName
            6 -> ObjectionStatus.CONFIRM.methodName
            else -> ObjectionStatus.DEFAULT.methodName

        }


    enum class ObjectionStatus constructor(var methodName: String) {
        REGISTER("ثبت درخواست"),
        REVIEW("بازنگري محاسبات"),
        BOARD("طرح در هيئت"),
        RETRY("تجدید محاسبه شده"),
        TIME("تخصیص زمان"),
        CONFIRM("تایید رای"),
        DEFAULT("نامشخص")
    }
}