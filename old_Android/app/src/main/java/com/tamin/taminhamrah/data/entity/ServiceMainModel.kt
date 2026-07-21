package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ServiceMainModel(
    var title: String? = "",
    @SerializedName("qty")
    var count: Int? = 0,
    var type: Int? = 1,
    @SerializedName("service")
    var serviceList: MutableList<ServiceModel>? = null
) {
    var isSelected = true
    var scrollState:Parcelable? = null

    fun getListSize(): String {
        return "${serviceList?.size ?: 0} سرویس "
    }

}

data class ServiceModel(

    var id: Int,
    var name: String?,

    var newService: Boolean = false,
    @SerializedName("android")
    var active: Boolean = true,
    var icon: String?,
    var tracks: List<String>?,
    )  {


    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = "standards"

    @SerializedName("sorting")
    @Expose
    var sorting : Int = -1
    //get() {return Random.nextInt(1,2000) }
    //used in adapter
    var isExpanded = false

    var scrollState: Parcelable? = null

    fun getImageUrl(): String {

//        return "https://eservices.tamin.ir/pwa/assets/icon-eservices/${icon}.svg"
        return "https://ssodcfs.tamin.ir/Eservices/icon-eservices/${icon}.svg"
     //   return "https://eservices.tamin.ir/mobile/assets/icon-eservices/${icon}.svg"


    }



    override fun toString(): String {
        return "ServiceModel= { name=$name newService=$newService  active=$active}"
    }
}