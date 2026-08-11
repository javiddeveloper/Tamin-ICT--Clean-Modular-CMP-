package com.tamin.taminhamrah.feature.profile.ui.bankAccount.model

import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BankAccountDraftTest {

    private fun draft(
        bank: Bank? = Bank.REFAH,
        number: String = "421309428",
        type: AccountType? = AccountType.SAVINGS,
        millis: Long? = 1_761_696_000_000L,
    ) = BankAccountDraftPR(
        startDateMillis = millis,
        startDateLabel = "۱۴۰۴/۰۸/۰۷",
        bank = bank,
        accountType = type,
        accountNumber = number,
    )

    @Test
    fun `a complete refah account of nine digits is accepted`() {
        // The real account the service returned for a رفاه customer.
        assertNull(draft().firstError())
        assertTrue(draft().canSubmit)
    }

    @Test
    fun `each bank demands its own digit count`() {
        val lengths = mapOf(
            Bank.REFAH to 9, Bank.MELLI to 13, Bank.MELLAT to 10,
            Bank.TEJARAT to 10, Bank.SADERAT to 13, Bank.SEPAH to 13,
        )
        lengths.forEach { (bank, length) ->
            assertNull(
                draft(bank = bank, number = "1".repeat(length)).firstError(),
                "$bank should accept $length digits",
            )
            assertEquals(
                BankAccountFormError.WrongLength(length),
                draft(bank = bank, number = "1".repeat(length - 1)).firstError(),
                "$bank should reject ${length - 1} digits",
            )
        }
    }

    @Test
    fun `a number valid for one bank is wrong for another`() {
        val tenDigits = "1234567890"
        assertNull(draft(bank = Bank.MELLAT, number = tenDigits).firstError())
        assertEquals(
            BankAccountFormError.WrongLength(9),
            draft(bank = Bank.REFAH, number = tenDigits).firstError(),
        )
    }

    @Test
    fun `a missing bank is reported before any complaint about length`() {
        // Without a bank there is no length to be wrong about.
        assertEquals(
            BankAccountFormError.Incomplete,
            draft(bank = null, number = "1").firstError(),
        )
    }

    @Test
    fun `every empty field reads as incomplete`() {
        assertEquals(BankAccountFormError.Incomplete, draft(millis = null).firstError())
        assertEquals(BankAccountFormError.Incomplete, draft(type = null).firstError())
        assertEquals(BankAccountFormError.Incomplete, draft(number = "").firstError())
        assertEquals(BankAccountFormError.Incomplete, BankAccountDraftPR().firstError())
        assertFalse(BankAccountDraftPR().canSubmit)
    }

    @Test
    fun `the zero padding from an IBAN is dropped, because the help sheet asks for 18 digits`() {
        val padded = "000000000421309428"          // 9-digit رفاه account inside an 18-wide field
        assertEquals("421309428", draft(number = padded).normalizedAccountNumber)
        assertNull(draft(number = padded).firstError())
    }

    @Test
    fun `zeros are only dropped when they are what makes it too long`() {
        // Leading zeros that are part of a correctly sized number survive.
        assertEquals("012345678", draft(number = "012345678").normalizedAccountNumber)
        // Too long without leading zeros is still too long.
        assertEquals(
            BankAccountFormError.WrongLength(9),
            draft(number = "1234567890123").firstError(),
        )
    }

    @Test
    fun `trimming stops at the expected length instead of eating every leading zero`() {
        // A رفاه account of "000000012" padded to 18 must come back as 9 digits, zeros intact --
        // dropping all leading zeros would leave "12" and reject a perfectly good number.
        val padded = "000000000" + "000000012"
        assertEquals("000000012", draft(number = padded).normalizedAccountNumber)
        assertNull(draft(number = padded).firstError())
    }

    @Test
    fun `spaces and Persian digits survive being pasted in`() {
        assertEquals("421309428", draft(number = "۴۲۱ ۳۰۹ ۴۲۸").normalizedAccountNumber)
        assertEquals("421309428", draft(number = "421-309-428").normalizedAccountNumber)
        assertNull(draft(number = "۴۲۱۳۰۹۴۲۸").firstError())
    }
}
