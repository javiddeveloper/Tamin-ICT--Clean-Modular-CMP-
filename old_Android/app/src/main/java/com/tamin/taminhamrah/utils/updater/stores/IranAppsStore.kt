package com.tamin.taminhamrah.utils.updater.stores

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * shows apk in IranApps store
 */
class IranAppsStore : Stores() {
    override fun setStoreData(context: Context?, item: UpdaterStoreList) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage("ir.tgbs.android.iranapp")
        intent.data = Uri.parse("iranapps://app/${item.packageName}")
        showStore(context, intent, item, Store.IRAN_APPS)
    }
}