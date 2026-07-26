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
}
