package com.tamin.taminhamrah.utils.updater.utils

import com.tamin.taminhamrah.utils.updater.AppUpdaterDialog
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterFragmentModel
import com.tamin.taminhamrah.utils.updater.pojo.UpdaterStoreList

/**
 * This inline function helps building stores in DSL way
 */
inline fun store(block: UpdaterStoreList.() -> Unit): UpdaterStoreList {
    return UpdaterStoreList().apply(block)
}

/**
 * This inline function helps building UpdateDialog in DSL way
 */
inline fun updateDialogBuilder(block: UpdaterFragmentModel.() -> Unit): AppUpdaterDialog {
    val updaterModel = UpdaterFragmentModel(directLink = "").apply(block)
    with(updaterModel) {
        return AppUpdaterDialog.getInstance(
            title,
            updateInfo,
            isForceUpdate ?: false,
            cafeBazaarEnable ?: false,
            myKetEnable ?:false,
            ""
        )
    }
}