package com.tamin.taminhamrah.ui.system

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext

class ClipboardHelper : KoinComponent {
    private val context: Context = GlobalContext.get().get()

    fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
    }
}

actual fun copyToClipboard(text: String) {
    ClipboardHelper().copy(text)
}
