package com.tamin.taminhamrah.util

/**
 * Validation and input filtering utilities for form inputs across the application.
 */
object ValidationUtils {

    /** Legacy `Utility.checkPhoneNumber` landline pattern — area code `0[1-8][1-9]{2}` + 7 digits. */
    private val LANDLINE_REGEX = Regex("^0[1-8][1-9]{2}\\d{7}$")

    /**
     * Filters input to only digit characters and caps length to 11 digits.
     */
    fun validatePhoneNumber(input: String): String {
        return input.filter { it.isDigit() }.take(11)
    }

    /**
     * Filters input to only digit characters and caps length to 11 digits. Does not force a
     * `09` prefix while typing — [isMobileNumberValid] is what flags an incomplete/wrong-prefix
     * number, so the field can show a normal validation error instead of silently rewriting
     * whatever the user typed.
     */
    fun validateMobileNumber(input: String): String {
        return input.filter { it.isDigit() }.take(11)
    }

    /**
     * Filters input to only digit characters and caps length to 11 digits.
     */
    fun validateLandline(input: String): String {
        return input.filter { it.isDigit() }.take(11)
    }

    /**
     * Filters input to only digit characters and caps length to 10 digits.
     */
    fun validatePostcode(input: String): String {
        return input.filter { it.isDigit() }.take(10)
    }

    /**
     * Returns true if phone number is exactly 11 digits.
     *
     * Prefer [isLandlineNumberValid] for fixed-line numbers and [isMobileNumberValid] for mobiles.
     */
    fun isPhoneNumberValid(phone: String): Boolean {
        return phone.length == 11
    }

    /**
     * Returns true when [phone] matches the legacy Iranian landline pattern.
     */
    fun isLandlineNumberValid(phone: String): Boolean {
        return LANDLINE_REGEX.matches(phone)
    }

    /**
     * Returns true if landline is empty or matches the legacy landline pattern.
     */
    fun isLandlineValid(landline: String): Boolean {
        return landline.isEmpty() || isLandlineNumberValid(landline)
    }

    /**
     * Returns true if the phone matches an Iranian mobile number (`09` + 9 digits).
     */
    fun isMobileNumberValid(phone: String): Boolean {
        return Regex("^09\\d{9}$").matches(phone)
    }

    /**
     * Returns true if postcode is empty or exactly 10 digits.
     */
    fun isPostcodeValid(postcode: String): Boolean {
        return postcode.isEmpty() || postcode.length == 10
    }

    /**
     * Returns true if email is empty or matches a valid email pattern.
     */
    fun isEmailValid(email: String): Boolean {
        if (email.isBlank()) return true
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email.trim())
    }

    /** Iranian national-ID check digit is invalid for this all-zeros value even though it's 10 digits. */
    private const val NATIONAL_CODE_ALL_ZEROS = "0000000000"

    /**
     * Returns true if [nationalCode] is a checksum-valid 10-digit Iranian national ID, matching the
     * legacy app's `ValidationUtil.nationalCode` algorithm: the weighted sum of the first 9 digits
     * mod 11 must match the 10th (check) digit.
     */
    fun isNationalCodeValid(nationalCode: String): Boolean {
        if (nationalCode.length != 10 || nationalCode == NATIONAL_CODE_ALL_ZEROS) return false
        val digits = nationalCode.map { it.digitToIntOrNull() ?: return false }
        val sum = (0..8).sumOf { digits[it] * (10 - it) }
        val remainder = sum % 11
        val checkDigit = if (remainder < 2) remainder else 11 - remainder
        return digits[9] == checkDigit
    }

    /**
     * Returns true if [endTimestamp] (epoch millis) is on or after [startTimestamp] — the
     * general "end date must not be before start date" range check shared across date-range form
     * fields (e.g. inspection request's employment period). A same-day range is valid (e.g. a
     * single-day inspection period). Either side being unset (`null`, not yet picked) is treated
     * as valid so the error only appears once both dates are chosen.
     */
    fun isDateRangeValid(startTimestamp: Long?, endTimestamp: Long?): Boolean {
        if (startTimestamp == null || endTimestamp == null) return true
        return endTimestamp >= startTimestamp
    }
}
