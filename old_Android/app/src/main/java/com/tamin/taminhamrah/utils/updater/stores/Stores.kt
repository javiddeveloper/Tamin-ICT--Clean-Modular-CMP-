package com.tamin.taminhamrah.utils.updater.stores

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.utils.updater.pojo.Store
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * this super class has some functions to use them in child classes
 */
abstract class Stores {

    /**
     * Sets intent of the store
     */
    abstract fun setStoreData(context: Context?, item: UpdaterStoreList)

    protected fun showStore(
        context: Context?,
        intent: Intent,
        item: UpdaterStoreList,
        store: Store
    ) {
        try {
            context?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showUrlOrErrorToast(context, item, store)
        }
    }

    private fun showUrlOrErrorToast(context: Context?, item: UpdaterStoreList, store: Store) {
        if (item.url.isNotEmpty())
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
        else {
            val storeName = store.name.lowercase().replace("_", " ")
            Toast.makeText(
                context, String.format(context?.getString(R.string.please_install) ?:"", storeName),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}