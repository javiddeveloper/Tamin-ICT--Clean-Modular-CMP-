package com.tamin.taminhamrah.data.remote.models.services

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import java.io.Serializable

class ServiceResponseModelNew(var data: List<ServiceItem>? = null) : BaseResponseNew()

data class ServiceItem(
    var id: Int = -1,
    var name: String = "",
    var newService: Boolean = false,
    var active: Boolean = true,
    var icon: String = "",
    var type: Int = 1,
    var showRole: ArrayList<Int>? = null,
    var hiddenForVersions: ArrayList<Int>? = null

) : Serializable {

    var hintTitle = "تیتر راهنما"
    var hintDesc =
        "متن راهنمای آزمایشی \n این متن راهنمای آزمایشی است" + "\n" + "متن راهنمای آزمایشی \n این متن راهنمای آزمایشی است" + "\n" + "متن راهنمای آزمایشی \n این متن راهنمای آزمایشی است"

    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = "standards"

    @SerializedName("sorting")
    @Expose
    var sorting: Int = -1

    var isExpanded = false

    var scrollState: Parcelable? = null

    fun getImageUrl() = "https://ssodcfs.tamin.ir/Eservices/icon-eservices/${icon}.svg"
    // fun getImageUrl() = "https://eservices-test.tamin.ir/mobile/icon-eservices/${icon}.svg"

    override fun toString(): String {
        return "ServiceModel= { name=$name newService=$newService  active=$active}"
    }
}


