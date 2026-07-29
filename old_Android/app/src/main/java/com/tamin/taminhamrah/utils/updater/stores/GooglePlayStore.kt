package com.tamin.taminhamrah.utils.updater.stores

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * shows apk in GooglePlay store
 */
class GooglePlayStore : Stores() {
    override fun setStoreData(context: Context?, item: UpdaterStoreList) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${item.packageName}")
        )
        showStore(context, intent, item, Store.GOOGLE_PLAY)
    }
}