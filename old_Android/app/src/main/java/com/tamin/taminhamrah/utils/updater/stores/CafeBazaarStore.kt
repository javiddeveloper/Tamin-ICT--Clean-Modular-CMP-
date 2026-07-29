package com.tamin.taminhamrah.utils.updater.stores

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * shows apk in CafeBazaar store
 */
class CafeBazaarStore : Stores() {
    override fun setStoreData(context: Context?, item: UpdaterStoreList) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("bazaar://details?id=${item.packageName}")
        intent.setPackage("com.farsitel.bazaar")
        showStore(context, intent, item, Store.CAFE_BAZAAR)
    }
}