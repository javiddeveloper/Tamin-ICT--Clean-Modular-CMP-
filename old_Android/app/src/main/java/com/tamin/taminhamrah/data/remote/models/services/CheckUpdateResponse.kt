package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import java.io.Serializable

class CheckUpdateResponse() : BaseResponseNew(), Serializable {
    val data: CheckUpdateData? = null

    override fun toString(): String {

        data?.apply {
            return "\nupdateLink=$updateLink\nupdateDetail=$updateDetail\nlastVersionCode=${getLastVersionCode()}\nminVersionCode=${getMinVersionCode()}\nmd5=$md5"
        }
        return "null"
    }
}

data class CheckUpdateData(
    var updateLink: String? = null,
    var updateDetail: String?,
    var md5: String,
    val mobileChangeList: List<ChangeListItem> = emptyList()
) {
    fun getLastVersionCode() =
        if (updateDetail?.split(":")?.size == 2) updateDetail?.split(":")?.get(0)?.toInt()
            ?: BuildConfig.VERSION_CODE else BuildConfig.VERSION_CODE

    fun getMinVersionCode() =
        if (updateDetail?.split(":")?.size == 2) updateDetail?.split(":")?.get(1)?.toInt()
            ?: 0 else BuildConfig.VERSION_CODE


    enum class UpdateStatus { OPTIONAL_UPDATE, FORCE_UPDATE, NO_UPDATE }

    fun getUpdateStatus() = when {
        BuildConfig.VERSION_CODE < getMinVersionCode() -> UpdateStatus.FORCE_UPDATE
        BuildConfig.VERSION_CODE >= getMinVersionCode() && BuildConfig.VERSION_CODE < getLastVersionCode() -> UpdateStatus.OPTIONAL_UPDATE
        else -> UpdateStatus.NO_UPDATE
    }

    fun getChangeList(): List<String> {
        val changeList: MutableList<String> = ArrayList()
        for (item in mobileChangeList)
            changeList.add(item.description)

        return changeList

    }


}

data class ChangeListItem(val description: String)