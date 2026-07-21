package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.utils.ValidationUtil.Companion.persianToEnglish

internal fun showPopupMenu(view: View, items: List<MenuModel>, onSelect: (MenuModel) -> Unit) {
    val popup = PopupMenu(view.context, view)
    items.forEachIndexed { index, item ->
        popup.menu.add(0, index, index, item.title)
    }
    popup.setOnMenuItemClickListener { menuItem ->
        onSelect(items[menuItem.itemId])
        true
    }
    popup.show()
}

internal class DateFormatWatcher(
    private val editText: EditText
) : TextWatcher {
    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.toString() == current) return
        val englishInput = persianToEnglish(s.toString())
        var clean = englishInput.replace("\\D".toRegex(), "")
        if (clean.length > 8) clean = clean.take(8)
        var formatted = clean
        if (clean.length >= 5) formatted = clean.take(4) + "/" + clean.substring(4)
        if (clean.length >= 7) formatted = formatted.take(7) + "/" + formatted.substring(7)
        current = formatted
        editText.setText(formatted)
        try {
            editText.setSelection(formatted.length)
        } catch (_: Exception) { }
    }
}

internal class TimeFormatWatcher(
    private val editText: EditText
) : TextWatcher {
    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.toString() == current) return
        val englishInput = persianToEnglish(s.toString())
        var clean = englishInput.replace("\\D".toRegex(), "")
        if (clean.length > 4) clean = clean.take(4)
        var formatted = clean
        if (clean.length >= 3) {
            formatted = clean.take(2) + ":" + clean.substring(2)
        }
        current = formatted
        editText.setText(formatted)
        try {
            editText.setSelection(formatted.length)
        } catch (_: Exception) { }
    }
}
