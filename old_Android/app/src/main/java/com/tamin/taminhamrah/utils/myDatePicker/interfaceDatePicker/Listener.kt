package com.tamin.taminhamrah.utils.myDatePicker.interfaceDatePicker

import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianCalendar


interface Listener {
    @Deprecated("")
    fun onDateSelected(persianCalendar: MyPersianCalendar?)

    @Deprecated("")
    fun onDismissed()
}
