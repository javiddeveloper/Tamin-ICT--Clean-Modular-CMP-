package com.tamin.taminhamrah.utils

import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.tamin.taminhamrah.R

object ChatMessageLongPressHelper {

    fun attach(
        view: View,
        textProvider: () -> String,
        onResend: ((String) -> Unit)? = null
    ) {
        view.setOnLongClickListener {
            showMenu(view, textProvider(), onResend)
            true
        }
    }

    private fun showMenu(
        anchor: View,
        text: String,
        onResend: ((String) -> Unit)?
    ) {
        val popupMenu = PopupMenu(anchor.context, anchor)
        popupMenu.menuInflater.inflate(
            R.menu.menu_user_message,
            popupMenu.menu
        )
        if (onResend == null) {
            popupMenu.menu.removeItem(R.id.action_resend)
        }

        forceShowIcons(popupMenu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_copy -> {
                    Utility.copyToClipBoard(anchor.context, text)
                    true
                }
                R.id.action_resend -> {
                    onResend?.invoke(text)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun forceShowIcons(popupMenu: PopupMenu) {
        try {
            val field = popupMenu.javaClass.getDeclaredField("mPopup")
            field.isAccessible = true
            val helper = field.get(popupMenu)
            helper.javaClass
                .getMethod("setForceShowIcon", Boolean::class.java)
                .invoke(helper, true)
        } catch (_: Exception) {
        }
    }
}
