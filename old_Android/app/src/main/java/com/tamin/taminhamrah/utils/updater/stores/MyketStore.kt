package com.tamin.taminhamrah.utils.updater.stores

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * shows apk in Myket store
 */
class MyketStore : Stores() {
    override fun setStoreData(context: Context?, item: UpdaterStoreList) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse("myket://details?id=${item.packageName}")
        showStore(context, intent, item, Store.MYKET)
    }
}