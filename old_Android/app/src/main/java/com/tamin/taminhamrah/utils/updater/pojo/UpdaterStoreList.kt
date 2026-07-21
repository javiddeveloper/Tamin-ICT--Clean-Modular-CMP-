package com.tamin.taminhamrah.utils.updater.pojo

import com.tamin.taminhamrah.R
import java.io.Serializable

/**
 * User has to pass this model to the library
 */
data class UpdaterFragmentModel(
    var title: String ="نسخه جدید اپلیکیشن با امکانات زیر ارایه گردیده است",
    var updateInfo: List<String> =listOf("بهبود رابط کاربری","سرویس جدید قرارداد بیمه دانشجویان","سرویس جدید نسخه الکترونیک","بهبود رابط کاربری","سرویس جدید قرارداد بیمه دانشجویان","سرویس جدید نسخه الکترونیک","بهبود رابط کاربری","سرویس جدید قرارداد بیمه دانشجویان","سرویس جدید نسخه الکترونیک") ,
    var isForceUpdate: Boolean? = false,
    var cafeBazaarEnable:Boolean?=false,
    var myKetEnable:Boolean?=false,
    var directLink:String
) : Serializable

/**
 * The model that we are using for list of stores
 */
data class UpdaterStoreList(
    var store: Store = Store.DIRECT_URL,
    var title: String = "Store",
    var icon: Int = R.drawable.appupdater_ic_cloud,
    var url: String = "",
    var packageName: String = ""
) : Serializable