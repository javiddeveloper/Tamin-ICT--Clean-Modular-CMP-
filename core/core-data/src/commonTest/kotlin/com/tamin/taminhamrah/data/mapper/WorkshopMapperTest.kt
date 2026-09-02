package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkshopMapperTest {

    @Test
    fun `toDomain blanks out a literal null-string smsDescription instead of showing the word null`() {
        val result = SmsMessageDTO(id = 65188505, smsDescription = "null", status = "6").toDomain()

        assertEquals("", result.description)
    }

    @Test
    fun `toDomain keeps a real smsDescription untouched`() {
        val result = SmsMessageDTO(id = 1, smsDescription = "اعتراض ثبت شد", status = "3").toDomain()

        assertEquals("اعتراض ثبت شد", result.description)
    }
}
