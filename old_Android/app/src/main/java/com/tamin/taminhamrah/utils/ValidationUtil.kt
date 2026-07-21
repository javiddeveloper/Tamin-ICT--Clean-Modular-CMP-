package com.tamin.taminhamrah.utils

import android.content.Context
import android.util.Patterns
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.widget.edittext.ValidationResultModel
import java.util.regex.Pattern

class ValidationUtil {
    companion object {
        fun nationalCode(context: Context, nationalCode: String): ValidationResultModel {
            val model: ValidationResultModel
            val identicalDigits = arrayOf(
                "0000000000",
                "۰۰۰۰۰۰۰۰۰۰"/*,
                "1111111111",
                "۱۱۱۱۱۱۱۱۱۱",
                "2222222222",
                "۲۲۲۲۲۲۲۲۲۲",
                "3333333333",
                "۳۳۳۳۳۳۳۳۳۳",
                "4444444444",
                "۴۴۴۴۴۴۴۴۴۴",
                "5555555555",
                "۵۵۵۵۵۵۵۵۵۵",
                "6666666666",
                "۶۶۶۶۶۶۶۶۶۶",
                "7777777777",
                "۷۷۷۷۷۷۷۷۷۷",
                "8888888888",
                "۸۸۸۸۸۸۸۸۸۸",
                "9999999999",
                "۹۹۹۹۹۹۹۹۹۹"*/
            )

            when {
                nationalCode.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_national_code_field)
                    )
                    return model
                }
                nationalCode.length != 10 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_national_code)
                    )
                    return model
                }
                mutableListOf(*identicalDigits).contains(nationalCode) -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_national_code)
                    )
                    return model
                }
                else -> {
                    var sum = 0
                    for (i in 0..8) {
                        sum += Character.getNumericValue(nationalCode[i]) * (10 - i)
                    }
                    val lastDigit: Int
                    val divideRemaining = sum % 11
                    lastDigit = if (divideRemaining < 2) {
                        divideRemaining
                    } else {
                        11 - divideRemaining
                    }
                    return if (Character.getNumericValue(nationalCode[9]) == lastDigit) {
                        model = ValidationResultModel(true, "")
                        model
                    } else {
                        model = ValidationResultModel(
                            false,
                            context.getString(R.string.please_enter_valid_national_code)
                        )
                        model
                    }
                }
            }
        }

        fun registerPassword(context: Context, password: String): ValidationResultModel {
            val model: ValidationResultModel

            val uppercasePatten: Pattern = Pattern.compile("[A-Z ]")
            val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
            val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")
            when {
                password.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_password_field)
                    )
                    return model
                }
                password.length < 6 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_min_eight_character)
                    )
                    return model
                }
                !uppercasePatten.matcher(password).find() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_min_eight_character)
                    )
                    return model
                }
                !lowerCasePatten.matcher(password).find() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_min_eight_character)
                    )
                    return model
                }
                !digitCasePatten.matcher(password).find() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_min_eight_character)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }
        }

        fun enterPassword(context: Context, password: String): ValidationResultModel {
            val model: ValidationResultModel

            when {
                password.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_password_field)
                    )
                    return model
                }
                password.length < 6 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_password)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }
        }

        fun expression(context: Context, expression: String): ValidationResultModel {
            val model: ValidationResultModel

            when {
                expression.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }
                expression.length<=1 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }
        }

        fun validEmail(context: Context, expression: String): ValidationResultModel {
            val model: ValidationResultModel

            when {
                expression.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }
                expression.length < 3 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }

                !Patterns.EMAIL_ADDRESS.matcher(expression).matches() ||
                        !Pattern.compile(
                            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
                        ).matcher(expression).matches() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_email)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }
        }

        fun number(context: Context, number: String): ValidationResultModel {
            val model: ValidationResultModel
            when {
                number.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }
                number.length < 0 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_value)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }

        }

        fun validMobile(context: Context, number: String): ValidationResultModel {
            val model: ValidationResultModel
            when {
                number.trim { it <= ' ' }.isEmpty() -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_fill_field)
                    )
                    return model
                }
                number.length < 0 ||
                        number.length < 11 ||
                        number.length > 11 -> {
                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_mobile_number)
                    )
                    return model
                }

                !Patterns.PHONE.matcher(number).matches() ||
                        !number.matches(Regex("^[+]?[0-9]{10,13}$")) -> {

                    model = ValidationResultModel(
                        false,
                        context.getString(R.string.please_enter_valid_mobile_number)
                    )
                    return model
                }
                else -> {
                    model = ValidationResultModel(true, "")
                    return model
                }

            }

        }

        fun startWithZero(context: Context, number: String): ValidationResultModel {
            val model: ValidationResultModel
            val digit = number.substring(0, 1)
            model = if (digit.toInt() == 0) {
                ValidationResultModel(
                    false,
                    context.getString(R.string.please_enter_valid_value)
                )
            } else ValidationResultModel(true, "")
            return model

        }

        fun startWithZero(number: String): Boolean {
            val digit = number.substring(0, 1)
            return digit.toInt() == 0
        }



        fun persianToEnglish(input: String): String {
            var result = ""
            var en = '0'
            if (input.contains('۰') ||
                input.contains('۱') ||
                input.contains('۲') ||
                input.contains('۳') ||
                input.contains('۴') ||
                input.contains('۵') ||
                input.contains('۶') ||
                input.contains('۷') ||
                input.contains('۸') ||
                input.contains('۹')
            ) {

                for (ch in input) {
                    en = ch
                    when (ch) {
                        '۰' -> en = '0'
                        '۱' -> en = '1'
                        '۲' -> en = '2'
                        '۳' -> en = '3'
                        '۴' -> en = '4'
                        '۵' -> en = '5'
                        '۶' -> en = '6'
                        '۷' -> en = '7'
                        '۸' -> en = '8'
                        '۹' -> en = '9'
                    }
                    result = "${result}$en"
                }
            } else {
                result = input
            }
            return result
        }


        fun isProbablyArabic(s: String): Boolean {
            for (i in 0 until Character.codePointCount(s, 0, s.length)) {
                val c = s.codePointAt(i)
                if (c in 0x0600..0x06FF || c in 0xFB50..0xFDFF || c in 0xFE70..0xFEFF) return true
            }
            return false
        }


////companion object
    }
}