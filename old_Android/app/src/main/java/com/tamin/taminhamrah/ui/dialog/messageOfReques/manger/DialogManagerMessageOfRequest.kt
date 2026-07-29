package com.tamin.taminhamrah.ui.dialog.messageOfReques.manger

import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import timber.log.Timber

open class DialogManagerMessageOfRequest {
    fun createDialog(): MessageOfRequestDialogFragment {
        val dialog = MessageOfRequestDialogFragment()
        dialog.isCancelable = false
        return dialog
    }

    companion object {
        @Volatile
        private var instance: DialogManagerMessageOfRequest? = null
        fun getInstanceOfDialog(): MessageOfRequestDialogFragment {
            val currentInstance = instance ?: synchronized(this) {
                instance ?: DialogManagerMessageOfRequest().also { instance = it }
            }
            return currentInstance.createDialog()
        }
    }
}
