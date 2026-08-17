package com.tamin.taminhamrah.util

/**
 * Validation and input filtering utilities for form inputs across the application.
 */
object ValidationUtils {

    /**
     * Filters input to only digit characters and caps length to 11 digits.
     */
    fun validatePhoneNumber(input: String): String {
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
     */
    fun isPhoneNumberValid(phone: String): Boolean {
        return phone.length == 11
    }

    /**
     * Returns true if landline is empty or exactly 11 digits.
     */
    fun isLandlineValid(landline: String): Boolean {
        return landline.isEmpty() || landline.length == 11
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

    /**
     * Validates Iranian National ID using 10-digit checksum algorithm.
     */
    fun isNationalIdValid(nationalId: String): Boolean {
        if (nationalId.length != 10 || !nationalId.all { it.isDigit() }) return false
        if (nationalId.toSet().size == 1) return false

        val digits = nationalId.map { it.digitToInt() }
        val checkDigit = digits[9]
        val sum = (0..8).sumOf { i -> digits[i] * (10 - i) }
        val remainder = sum % 11

        return if (remainder < 2) {
            checkDigit == remainder
        } else {
            checkDigit == (11 - remainder)
        }
    }
}
