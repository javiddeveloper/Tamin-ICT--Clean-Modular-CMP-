package com.tamin.taminhamrah.data.remote.models.user

import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate

class InboxResponseNew : ListDataModel<InboxItem>()

data class InboxItem(
    var image: Any? = null,
    var updateable: Boolean? = null,
    var nationalCode: String? = null,
    var read: String? = null,
    var data: Any? = null,
    var mobileNumber: String? = null,
    var receiveDate: Long? = null,
    var permission: Permission? = null,
    var hasImage: Boolean? = null,
    var sentNextDate: Any? = null,
    var type: InboxType? = null,
    var seenDate: Long? = null,
    var hasText: Boolean? = null,
    var refrenceId: Any? = null,
    var seen: Boolean? = null,
    var sentDate: Long? = null,
    var pdf: String? = null,
    var details: Details? = null,
    var subType: InboxType? = null,
    var id: Int? = null,
    var text: Any? = null,
    var hasPDF: Boolean = false,
    var email: String? = null,
    var status: String? = null,
    var expanded: Boolean = false
) {



    fun getPassword(item:InboxItem): String {
        return item.permission?.password?.toString()?:"-"
    }

    fun getPersianDate(timeStamp: Long?): String {
        return timeStamp?.let { ConvertDate.convertTimestampToPersianDate(timeStamp) } ?: ""
    }

    fun hasPermission(): Boolean {

        return !(permission == null || permission?.dateTo == null)
    }
}

data class InboxType(var typeDesc: String? = null, var typeCode: String? = null)

fun InboxType.asDomainModel(): MenuModel {
    return MenuModel(
        title = typeDesc,
        id = typeCode
    )
}

fun List<InboxType>.asDomainModel(): List<MenuModel> {
    return map {
        it.asDomainModel()
    }
}

data class Details(
    var branchCode: Any? = null,
    var workshopName: Any? = null,
    var eblaghNo: Any? = null,
    var branchName: Any? = null,
    var docDate: Any? = null,
    var id: Int? = null,
    var workshopCode: Any? = null,
    var docNO: Any? = null,
    var pymanSEQ: Any? = null,
)

data class Permission(
    var password: Long? = null,
    var dateTo: Long? = null,
    var id: Int? = null,
    var dateFrom: Long? = null

)
