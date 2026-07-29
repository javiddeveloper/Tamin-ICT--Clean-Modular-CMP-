package com.tamin.taminhamrah.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.os.Parcelable
import android.provider.Browser
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.AVERAGE_SALARY
import com.tamin.taminhamrah.Constants.ELIGIBLE_AMOUNT
import com.tamin.taminhamrah.Constants.HISTORY_YEAR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.local.services.entity.FancyShowCaseModel
import com.tamin.taminhamrah.data.local.services.entity.Shape
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.user.EligibilityStatusResponse
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.AiLawChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DateSplitFilter
import com.tamin.taminhamrah.utils.extentions.isNumericString
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianDatePickerDialog
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.security.auth.x500.X500Principal
import kotlin.collections.forEach
import kotlin.math.ceil

object Utility {


    fun isValidWebUrl(url : String): Boolean {
        if (url.isBlank()) return false

        return try {
            val uri = Uri.parse(url)
            uri.scheme == "http" || uri.scheme == "https"
        } catch (e: Exception) {
            false
        }
    }


    fun getDateSplitFilter(shamsiDate: String): DateSplitFilter? {

        return try {
            val year = shamsiDate.substring(0..3)
            val month = shamsiDate.substring(4..5)
            val day = shamsiDate.substring(6..7)
            DateSplitFilter(
                year = year,
                month = month,
                day = day,
                completeDate = year + month + day
            )
        } catch (e: Exception) {
            null
        }
    }

    fun TextView.typeText(
        text: String,
        delayMillis: Long = 7L,
        onProgress: (() -> Unit)? = null,
        onComplete: () -> Unit = {}
    ): Job {
        this.text = ""

        return CoroutineScope(Dispatchers.Main).launch {
            var counter = 0
            text.forEach { char ->
                if (!isActive) return@launch
                append(char.toString())
                if (++counter % 3 == 0) {
                    onProgress?.invoke()
                }
                delay(delayMillis)
            }
            onComplete()
        }
    }

    fun formatChatTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale("fa"))
        return sdf.format(Date(timeMillis))
    }


    fun AiChatModel.extractMessageText(): String =
        when (this) {
            is AiTextModel -> message

            is AiClickableModel -> content.trim()

            is AiKeyValueModel ->
                keyValueItems.joinToString(separator = "\n") {
                    "● ${it._key}: ${it._value}"
                }

            is AiLawChatModel -> content?.trim().orEmpty()

            is AgentClickableModel -> {
                buildKeyValueText(content)
            }

            is AgentGroupButtonModel -> {
                title?.trim().orEmpty()
            }

            else -> ""
        }


    fun buildKeyValueText(keyValueItems: List<KeyValueModel>): String {
        return buildString {
            keyValueItems.forEach { item ->
                if (item._value.isEmpty()) {
                    append(item._key)
                } else {
                    append("● ${item._key}: ${item._value}")
                }
                append("\n")

            }
        }.trimEnd()
    }

    fun Context.shareText(
        text: String,
    ) {
        if (text.isBlank()) return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "اشتراک‌گذاری پیام"))
    }


    fun copyToClipBoard(context: Context, textToCopy: String?) {
        val clipboardManager: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("tamin", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
    }

    // if phoneNumber is valid return 0 , else return error text with  String Res Id
    fun checkPhoneNumber(phoneNumber: String): Int {
        if (phoneNumber.length < 11)
            return R.string.error_input_length_phone

        if (!(Pattern.compile("^0[1-8][1-9]{2}\\d{7}\$").matcher(phoneNumber).matches()))
            return R.string.error_input_phone


        return 0
    }

    // if mobileNumber is valid return 0 , else return error text with  String Res Id
    fun checkMobileNumber(mobileNumber: String): Int {
        if (mobileNumber.length < 11)
            return R.string.error_input_length_mobile

        if (!(Pattern.compile("^09[0-9]{9}\$").matcher(mobileNumber).matches()))
            return R.string.error_input_mobile

        return 0
    }

    fun Context.openInBrowser(url: String, configureIntent: ((Intent) -> Unit)?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                configureIntent?.invoke(this)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error launching browser")
        }
    }

    fun Intent.standardWebFlags(): Intent {
        return apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    fun getLicensePeriodList(): ArrayList<MenuModel>? {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("یک روز", "0"))
        itemList.add(MenuModel("یک هفته", "1"))
        itemList.add(MenuModel("یک ماه", "2"))
        itemList.add(MenuModel("یک سال", "3"))
        return itemList
    }

    fun getContractTypes() = listOf(
        MenuModel("قرارداد دانشجویی", "0"),
        MenuModel("قرارداد زنان خانه دار", "1"),
        MenuModel("قرارداد صاحبان حرف و مشاغل آزاد", "2"),
        MenuModel("قرارداد بیمه اختیاری", "3")
    )

    fun getEligibilityStatusList() = listOf(
        "داشتن حداقل 10 سال سابقه پرداخت حق بیمه نزد سازمان تأمین اجتماعی",
        "سن کمتر از 50 سال",
        "داشتن 0 روز سابقه و ... سن در زمان تقاضا",
        "داشتن حداکثر دو قرارداد حرف ومشاغل",
        "عدم احراز شرایط سن و سابقه"
    )

    fun getPageIdByTitle(title: String?) = when (title) {
        "درخواست هدیه ازدواج" -> R.id.action_service_to_wedding_present
        "ارتباط فعال با سازمان تأمین اجتماعی" -> R.id.action_service_to_active_relation
        "اطلاعات هویتی و شماره تأمین اجتماعی" -> R.id.action_service_to_identity_info
        "پیگیری وضعیت اعتراض" -> R.id.action_service_to_follow_protest_status
        "اعلام شماره حساب بانکی" -> R.id.action_service_to_declare_bank_account
        "استعلام و اعلام شماره حسابهای ثبت شده" -> R.id.action_service_to_bank_account_list
        /*
                "کلیه سوابق" -> R.id.action_servicesFragment_to_allHistoryInsuranceFragment
        */
        "کلیه سوابق" -> R.id.action_servicesFragment_to_mergeHistoryFragment

        "سوابق و ریز دستمزد بعد از سال 86" -> R.id.action_servicesFragment_to_wageAndHistoryFragment
        "مشاهده فیش حقوقی مستمری بگیران" -> R.id.action_services_to_pay_roll
        "مشاهده عناوین شغلی" -> R.id.action_servicesFragment_to_viewTitleJobFragment
        "مجموع سوابق" -> R.id.action_services_to_combined_record
        "مشاهده درخواست\u200Cهای تعهدات کوتاه مدت" -> R.id.action_servicesFragment_to_viewShortTermFragment
        "محاسبه برآوردی مبلغ هدیه ازدواج" -> R.id.action_servicesFragment_to_calculateMarriageAllowanceFragment
        "محاسبه برآوردی مبلغ غرامت دستمزد ایام بیماری" -> R.id.action_servicesFragment_to_calculateWageIllDaysFragment
        "محاسبه برآوردی مبلغ غرامت دستمزد ایام بارداری" -> R.id.action_servicesFragment_to_calculateWagePregnancyFragment
        "مشاهده حکم مستمری\u200Cبگیران" -> R.id.action_servicesFragment_to_edictPensionerFragment
        "دریافت عکس از پایگاه ثبت احوال" -> R.id.action_service_to_edit_image
        "صدور گواهی حقوق مستمری بگیران" -> R.id.action_servicesFragment_to_issuanceWageCertificateFragment
        "درخواست گواهی کسر اقساط معوق" -> R.id.action_servicesFragment_to_deferredInstallmentCertificateFragment
        "وضعیت حمایت درمانی" -> R.id.action_service_to_deserved_treatment
        "تعهدنامه فرزندان دختر" -> R.id.action_service_to_girl_survivor
        "اعلام سابقه به موسسات" -> R.id.action_servicesFragment_to_sendInsuranceHistoryToInstitutionFragment
        "نحوه محاسبه مبلغ مستمری بازنشستگی" -> R.id.action_servicesFragment_to_calculateWagePensionFragment
        "نسخ الکترونیک من" -> R.id.action_service_to_prescription
        "درخواست کمک هزینه اروتز پروتز" -> R.id.action_servicesFragment_to_orotezProtezFragment
        "کارگاه\u200Cهای کارفرما" -> R.id.action_service_workshop_all
        "درخواست کمک هزینه ایام بارداری" -> R.id.action_servicesFragment_to_requestForPregnancyPayFragment
        "اعتراض به سابقه دارای کسری کارکرد یا اشکال" -> R.id.action_servicesFragment_to_objectionInsuranceHistoryFragment
        "مشاهده و ثبت افراد تبعی توسط بیمه شده اصلی" -> R.id.action_servicesFragment_to_DependentsFragment
        "اعتراض به سوابق ناموجود" -> R.id.action_servicesFragment_to_objectionNonExistentHistoryFragment
        "مشاهده و اعتراض به گزارشات بازرسی های کارگاهی" -> R.id.action_services_to_performed_inspection
        "مشاهده مکاتبات و ابلاغ های انجام شده توسط سازمان" -> R.id.action_report_inspections
        "درخواست غرامت دستمزد ایام بیماری" -> R.id.action_servicesFragment_to_requestPaymentForillDaysFragment
        "درخواست کمک هزینه مراسم ترحیم" -> R.id.action_servicesFragment_to_requestFuneralGrantFragment
        "بازرسی انجام شده توسط سازمان" -> R.id.action_servicesFragment_to_listOfInspectionsPerformedFragment
        "پرونده الکترونیک من" -> R.id.action_servicesFragment_to_myElectronicFileFragment
        "پیمان\u200Cهای کارفرما" -> R.id.action_services_to_contractInfoFragment
        "پیمان\u200Cهای واگذارنده" -> R.id.action_services_to_assignerContractFragment
        //  "گواهی بازپرداخت هزینه های درمانی" -> R.id.action_servicesFragment_to_certificateReimbursmentOfTreatmentFragment
        "استعلام وضعیت مستمری بگیران" -> R.id.action_servicesFragment_to_inquirePensionStatusFragment
        "استعلام گواهی اشتغال به تحصیل" -> R.id.action_servicesFragment_to_inquiryEducationFragment
        "مستمری از کارافتادگی" -> R.id.action_to_disabilityPensionFragment
        "برقراری مستمری توسط بازماندگان" -> R.id.action_servicesFragment_to_requestPensionBySurvivor
        "برقراری مستمری بازنشستگی" -> R.id.action_services_to_retirementPension
        "گواهی حق بیمه ساختمانی" -> R.id.action_services_to_constructionInsuranceFragment

        else -> null
    }

    fun getPageIdByModelId(id: Int?) = when (id) {
        1 -> R.id.action_service_to_identity_info
        2 -> R.id.action_service_to_active_relation

        3 -> R.id.action_service_to_bank_account_list
        4 -> R.id.action_service_to_edit_image
        5 -> R.id.action_servicesFragment_to_DependentsFragment

        /*
                6 -> R.id.action_servicesFragment_to_allHistoryInsuranceFragment
        */
        6 -> R.id.action_servicesFragment_to_mergeHistoryFragment

        7 -> R.id.action_servicesFragment_to_wageAndHistoryFragment
        8 -> R.id.action_services_to_combined_record
        9 -> R.id.action_servicesFragment_to_sendInsuranceHistoryToInstitutionFragment
        10 -> R.id.action_servicesFragment_to_objectionNonExistentHistoryFragment
        11 -> R.id.action_servicesFragment_to_viewTitleJobFragment

        13 -> R.id.action_servicesFragment_to_viewShortTermFragment
        14 -> R.id.action_service_to_wedding_present
        16 -> R.id.action_servicesFragment_to_requestForPregnancyPayFragment
        15 -> R.id.action_servicesFragment_to_orotezProtezFragment

        17 -> R.id.action_servicesFragment_to_requestPaymentForillDaysFragment
        18 -> R.id.action_servicesFragment_to_requestFuneralGrantFragment
        19 -> R.id.action_servicesFragment_to_listOfInspectionsPerformedFragment
        20 -> R.id.action_servicesFragment_to_calculateMarriageAllowanceFragment
        21 -> R.id.action_servicesFragment_to_calculateWageIllDaysFragment
        22 -> R.id.action_servicesFragment_to_calculateWagePregnancyFragment
        23 -> R.id.action_servicesFragment_to_calculateWagePensionFragment

        25 -> R.id.action_service_to_deserved_treatment
        26 -> R.id.action_service_to_prescription

        //    33 -> R.id.action_servicesFragment_to_insuranceContractBusinessOwnersAndFreelancers

//        34 -> R.id.action_servicesFragment_to_concludingStudentInsuranceContractFragment
//        35 -> R.id.action_servicesFragment_to_insurancePayment
        34, 36, 33 -> R.id.action_dashboard_to_newContract
        35 -> R.id.action_servicesFragment_to_contractList
//        36 -> R.id.action_servicesFragment_to_womenContractFragment
        37 -> R.id.action_to_optionalContractFragment
        38 -> R.id.action_servicesFragment_to_inquiryEducationFragment
        39 -> R.id.action_to_fraction_contract_fragment
        //"پیگیری وضعیت اعتراض" -> R.id.action_service_to_follow_protest_status
        //   "اعلام شماره حساب بانکی" ->R.id.action_service_to_declare_bank_account
        40 -> R.id.action_servicesFragment_to_requestPensionBySurvivor
        41 -> R.id.action_services_to_retirementPension

        42 -> R.id.action_servicesFragment_to_objectionInsuranceHistoryFragment
        45 -> R.id.action_servicesFragment_to_objectionInsuranceHistoryFragment
        46 -> R.id.action_servicesFragment_to_myElectronicFileFragment
        47 -> R.id.action_servicesFragment_to_WorkersPaymentInfoFragment

        101 -> R.id.action_service_to_deserved_treatment
        102 -> R.id.action_service_to_prescription
        104 -> R.id.action_servicesFragment_to_inquirePensionStatusFragment
        105 -> R.id.action_services_to_pay_roll
        106 -> R.id.action_servicesFragment_to_edictPensionerFragment
        107 -> R.id.action_servicesFragment_to_issuanceWageCertificateFragment
        108 -> R.id.action_servicesFragment_to_deferredInstallmentCertificateFragment
        109 -> R.id.action_servicesFragment_to_calculateWagePensionFragment
        110 -> R.id.action_service_to_girl_survivor
        112 -> R.id.action_servicesFragment_to_requestPensionBySurvivor
        113 -> R.id.action_to_disabilityPensionFragment
        1001 -> R.id.action_service_workshop_all
        1002 -> R.id.action_services_to_contractInfoFragment
        1003 -> R.id.action_services_to_assignerContractFragment
        1004 -> R.id.action_service_to_complete_workshop_info
        1005 -> R.id.action_service_to_stack_holder_list


        1006 -> R.id.action_service_to_follow_protest_status
        1007 -> R.id.action_workshop_info_to_register_agreement

        1008 -> R.id.action_services_to_performed_inspection
        //   "مشاهده مکاتبات و ابلاغ های انجام شده توسط سازمان" ->      R.id.action_report_inspections
        1009 -> R.id.action_service_to_installment_debt
        1010 -> R.id.action_services_to_constructionInsuranceFragment
        1011 -> R.id.action_services_to_occurrence_fragment
        1012 -> R.id.action_services_to_laws_fragment
        //    "پیمان های کارفرما" ->  R.id.action_services_to_contractInfoFragment
        // "پیمان های واگذارنده" ->   R.id.action_services_to_assignerContractFragment
        //   "گواهی بازپرداخت هزینه های درمانی" ->   R.id.action_servicesFragment_to_certificateReimbursmentOfTreatmentFragment

        else -> null
    }

    fun isRooted() = File("system/bin/su").exists()
//
//    // get from build info
//        val buildTags = Build.TAGS
//        if (buildTags != null && buildTags.contains("test-keys")) {
//            return true
//        }
//
//        // check if /system/app/Superuser.apk is present
//        try {
//            val file = File("/system/app/Superuser.apk")
//            if (file.exists()) {
//                return true
//            }
//        } catch (e1: java.lang.Exception) {
//            // ignore
//        }
//
//        // try executing commands
//        return (canExecuteCommand("/system/xbin/which su")
//                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su"))
//    }

    // executes a command on the system
    private fun canExecuteCommand(command: String): Boolean {
        val executedSuccesfully: Boolean = try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: java.lang.Exception) {
            false
        }
        return executedSuccesfully
    }

    fun sendDialAction(context: Context, numberToDial: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val telUri: Uri =
            Uri.parse("tel:" + numberToDial.replace("#".toRegex(), Uri.encode("#")))
        intent.data = telUri
        context.startActivity(intent)
    }

    fun getDecimalFormat(): DecimalFormat {
        val decimalFormatSymbols: DecimalFormatSymbols =
            DecimalFormatSymbols.getInstance(Locale.US)
        return DecimalFormat("#,###", decimalFormatSymbols)
    }

    fun removeThousandSeparated(_value: String): Long {
        var value = _value
        if (TextUtils.isEmpty(value)) return 0
        val separator = getDecimalFormat().decimalFormatSymbols.groupingSeparator
        value = removePersianNumber(
            value.replace(separator.toString(), "").replace("٬".toRegex(), "")
                .replace(",".toRegex(), "").replace("،".toRegex(), "")
        )
        return value.toLong()
    }

    fun removePersianNumber(persianNumber: String): String {
        var number = persianNumber
        val charMap = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        for (i in charMap.indices) {
            number = number.replace(charMap[i], i.toString()[0])
        }
        return number
    }

    fun getThousandSeparated(input: Double): String? {
        return getDecimalFormat().format(input)
    }

    fun createTextEligibilityResult(
        context: Context,
        _model: EligibilityStatusResponse
    ): String {
        val sb = StringBuilder()
        val weakBuilder = WeakReference(sb)
        weakBuilder.get()?.append(context.getString(R.string.owner_id))
        weakBuilder.get()?.append(" ")
        if (ValidationUtil.startWithZero(_model.nationalId!!))
            weakBuilder.get()?.append(context.getString(R.string.national_code))
        else
            weakBuilder.get()?.append(context.getString(R.string.foreign_nationals_code))
        weakBuilder.get()?.append(" ")
        weakBuilder.get()?.append(_model.nationalId)
        weakBuilder.get()?.append(" ")
        if (_model.reault == true) {
            weakBuilder.get()?.append(context.getString(R.string.covered_eligibility_treatment))
        } else {
            weakBuilder.get()
                ?.append(context.getString(R.string.not_covered_eligibility_treatment))
        }

        if (!_model.referenceCode.isNullOrBlank()) {
            weakBuilder.get()?.append("\n")
            weakBuilder.get()?.append("\n")

            weakBuilder.get()?.append(context.getString(R.string.reference_code))
            weakBuilder.get()?.append(" ")
            weakBuilder.get()?.append(_model.referenceCode)
        }

        return weakBuilder.get().toString()
    }


    fun hideKeyboard(context: Context, windowToken: IBinder) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun hideKeyboardInBottomSheetDialog(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun showKeyboard(context: Context, windowToken: IBinder) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun sharePdfFile(context: Context, filePath: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriFromFile(context, File(filePath)))
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        shareIntent.type = "application/pdf"
        context.startActivity(Intent.createChooser(shareIntent, "share.."))
    }

    fun shareImageFile(context: Context, filePath: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriFromFile(context, File(filePath)))
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        shareIntent.type = "image/jpeg"
        context.startActivity(Intent.createChooser(shareIntent, "share.."))
    }

    private fun uriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "com.tamin.taminhamrah.provider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun exportFile(sourceFilePath: String, context: Context): String {

        val destinationFilePath =
            "${Environment.getRootDirectory()}"
        val destinationFile = File(destinationFilePath)
        if (!destinationFile.exists()) {
            destinationFile.mkdirs()
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "Attachment_$timeStamp.pdf"

        val expFile = File(destinationFilePath, fileName)

        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null
        try {
            inChannel = FileInputStream(File(sourceFilePath)).channel
            outChannel = FileOutputStream(expFile).channel
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return ""
        }
        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return ""
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
        return destinationFilePath
    }


    @SuppressLint("SimpleDateFormat")
    fun copyFileToDownloads(
        downloadedFile: File,
        context: Context,
        toolbarTitle: String,
        mimeType: String = "text/pdf"
    ): String? {
//            val finalUri : Uri? = copyFileToDownloads(downloadedFile, context)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var extention = "pdf"
        if (mimeType != "text/pdf")
            extention = "jpg"

        val fileName = "$toolbarTitle _ $timeStamp.$extention"

        //This will be used only on android P-
        val DOWNLOAD_DIR =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
//                    put(MediaStore.MediaColumns.SIZE, getFileSize(downloadedFile))
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val authority = "${context.packageName}.provider"
            val destinyFile = File(DOWNLOAD_DIR, fileName)
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream =
                    BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }

        return DOWNLOAD_DIR.absolutePath
    }


    fun sendShare(activity: FragmentActivity, body: String?, shareTitle: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        val newIntent = Intent.createChooser(shareIntent, shareTitle)
        newIntent.flags = FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(newIntent)
    }

    fun openLink(activity: FragmentActivity, url: String?, token: String? = null) {

        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
        token?.let {
            val bundle = Bundle()
            bundle.putString("Content-Type", "application/json")
            bundle.putString("Authorization", token)
            browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle)

            Timber.tag("token").d(token)
        }

        activity.startActivity(browserIntent)
    }

    fun getToolbarTitle(arguments: Bundle?): String {

        return arguments?.getString(Constants.TOOLBAR_TITLE) ?: ""
    }

    fun getServiceId(arguments: Bundle?): Int {
        return arguments?.getInt(Constants.SERVICE_ID) ?: -1
    }

    fun getToolbarSubTitle(arguments: Bundle?): String {
        return arguments?.getString(Constants.TOOLBAR_SUBTITLE) ?: ""
    }

    fun getToolbarIconImage(arguments: Bundle?): String {
        return arguments?.getString(Constants.TOOLBAR_ICON_IMAGE) ?: ""
    }

    fun getToolbarIconDrawable(arguments: Bundle?): Int {
        return arguments?.getInt(Constants.TOOLBAR_ICON_IMAGE) ?: 0
    }

    fun getToolbarMoreInfo(arguments: Bundle?): Parcelable? {
        return arguments?.getParcelable(Constants.ARRAYLIST)
    }

    fun getToolbarSub2(arguments: Bundle?): String {

        return arguments?.getString(Constants.TOOLBAR_SUB_SUBTITLE) ?: ""
    }

    fun getFile(context: Context, id: String): File? {
        val root: String? = context.getExternalFilesDir(null)?.absolutePath
        val myDir = File("$root/Inbox")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val fname = "Attachment_$id.pdf"
        val file = File(myDir, fname)

        return if (file.exists()) {
            file
        } else null
    }

    fun savePdfFile(context: Context, pdfStr: String?, id: String): File {
        val root: String? = context.getExternalFilesDir(null)?.absolutePath
        val myDir = File("$root/Inbox")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val fname = "Attachment_$id.pdf"
        val file = File(myDir, fname)
        return if (file.exists()) {
            file
        } else {
            try {
                val out = FileOutputStream(file)
                val pdfAsBytes: ByteArray = Base64.decode(pdfStr, 0)
                out.write(pdfAsBytes)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            file
        }
    }


    fun savePdfFile(
        context: Context,
        body: ResponseBody?,
        resolver: ContentResolver,
        name: String
    ): File? {
        if (body == null)
            return null
        var file = getPdfFile(context, name)
        try {
            var input = body.byteStream()
            resolver.openOutputStream(file.toUri()).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output!!.write(buffer, 0, read)
                }
                output!!.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun writeResponseBodyToDisk(
        title: String,
        context: Context,
        body: ResponseBody?,
        extension: String? = null
    ): String {
        try {
            val root = context.getExternalFilesDir(null)
            val seprator = File.separator.toString()
            val ext = extension ?: "pdf"
            val filePath = "$root$seprator $title.$ext"
            val futureStudioIconFile = File(filePath)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body?.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int? = inputStream?.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    read?.let {
                        outputStream.write(fileReader, 0, it)
                        fileSizeDownloaded += it.toLong()
                    }
                    Timber.tag("File Download: ").d("$fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                return filePath
            } catch (e: IOException) {
                return "filePath"
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            return ""
        }
    }

    fun writeByteStreamToDisk(
        title: String,
        context: Context,
        inputStream: InputStream?,
        extension: String? = null
    ): File? {
        val uuid = UUID.randomUUID()
        var newFile: File? = null
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                // todo change the file location/name according to your needs

                val root = context.getExternalFilesDir(null)
                val seprator = File.separator.toString()
                val ext = extension ?: "pdf"
                val filePath = "$root$seprator $title-$uuid.$ext"
                newFile = File(filePath)
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    var fileSizeDownloaded: Long = 0
                    outputStream = FileOutputStream(newFile)
                    while (true) {
                        val read: Int? = inputStream?.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        read?.let {
                            outputStream.write(fileReader, 0, it)
                            fileSizeDownloaded += it.toLong()
                        }
                    }
                    outputStream.flush()
                    inputStream?.close()
                    outputStream.close()

//                Toast.makeText(context, "fileSize: ${futureStudioIconFile.length()}", Toast.LENGTH_LONG).show()
                    return@launch
                } catch (e: IOException) {
                    // Toast.makeText(context, e.printStackTrace().toString() , Toast.LENGTH_LONG).show()
                    inputStream?.close()
                    outputStream?.close()
                    return@launch
                }
            } catch (e: IOException) {
                //  Toast.makeText(context, e.printStackTrace().toString() , Toast.LENGTH_LONG).show()
                return@launch
            }
        }

        var jobFinished = false
        while (!jobFinished) {
            if (job.isCompleted)
                jobFinished = true

        }

        return newFile
    }

    fun getPdfFile(context: Context, name: String): File {
        val root: String? =
            context.getExternalFilesDir(null)?.absolutePath  //Environment.DIRECTORY_DOWNLOADS
        val myDir = File("$root/Inbox")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val file = File(myDir, name)
        return file
    }

    fun openPdfFile(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val outputFileUri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        intent.setDataAndType(outputFileUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val shareIntent = Intent.createChooser(intent, "بازکردن فایل با").apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            FLAG_ACTIVITY_NO_HISTORY
        }

        context.startActivity(shareIntent)
    }

    fun refreshFragment(navController: NavController) {
        val id = navController.currentDestination?.id
        id?.let {
            navController.popBackStack(id, true)
            navController.navigate(id)

        }
    }

    fun getNumberWithSeparator(value: Long?): String {
        val formatter = NumberFormat.getInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.groupingSeparator = ','
        formatter.decimalFormatSymbols = symbols
        return if (value == null) "0"
        else (formatter.format(value))
    }

    fun getNumberWithSeparatorForStringValue(value: String?): String {
        value?.let {
            if (value.trim() == "" || !value.isNumericString()) {
                return "0"
            }
            val formatter = NumberFormat.getInstance() as DecimalFormat
            val symbols = formatter.decimalFormatSymbols

            symbols.groupingSeparator = ','
            formatter.decimalFormatSymbols = symbols
            return formatter.format(value.toLong())
        }
        return "0"
    }


    fun getRialWithSeparator(value: Long?): String {

        if (value == null) {
            return "0 ریال"
        }
        var s: String = ""
        try {
            // The comma in the format specifier does the trick
            s = String.format("%,d", value)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return "$s ریال"

    }


    fun addSeparator(value: Int?): String {
        if (value == null) {
            return "0"
        }
        var s: String = "0"
        try {
            s = String.format("%,d", value)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return s
    }

    fun removeNumberSeparator(value: String): String {
        if (value.equals("")) return "0"
        return value.replace(",", "")
    }

    fun createMaterialTapTargetPrompt(
        activity: Activity,
        view: FancyShowCaseModel,
        typeface: Typeface?,
    ): MaterialTapTargetPrompt? {
        return MaterialTapTargetPrompt.Builder(activity)
            .setTarget(view.view)
            .setPrimaryText(view.title)
            .setSecondaryTextTypeface(typeface)
            .setPrimaryTextTypeface(typeface)
            .setSecondaryText(view.detail)
            .setPrimaryTextSize(R.dimen.font_title_size)
            .setSecondaryTextSize(R.dimen.font_sub_title_size)
            .setAutoDismiss(true)
            .setBackButtonDismissEnabled(false)
            .setTextGravity(Gravity.CLIP_VERTICAL)
            .setPrimaryTextTypeface(typeface, Typeface.BOLD)
            .setBackgroundColour(ContextCompat.getColor(activity, R.color.bg_tap_target_view))
            .setPrimaryTextColour(
                ContextCompat.getColor(
                    activity,
                    R.color.color_title_tap_target_view
                )
            )
            .setSecondaryTextColour((ContextCompat.getColor(activity, R.color.white)))
            .setPromptBackground(FullscreenPromptBackground())
            .setFocalColour(Color.TRANSPARENT)
            .apply {
                if (view.shape == Shape.RECT) {
                    setPromptFocal(RectanglePromptFocal().setCornerRadius(100F, 100F))
                }
                if (view.clickable == false)
                    setCaptureTouchEventOnFocal(true)
            }
            .create()
    }

    /*   fun getDatePicker(context: Context): PersianDatePickerDialog? {
           val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
           val today = PersianDate()
           return PersianDatePickerDialog(context)
               .setPositiveButtonString(context.getString(R.string.label_ok))
               .setNegativeButton(context.getString(R.string.label_cancel))
               .setTodayButton(context.getString(R.string.label_today))
               .setTodayButtonVisible(true)
               .setMinYear(1300)
               .setMaxYear(today.shYear + 1)
               .setInitDate(today.shYear, today.shMonth, today.shDay)
               .setActionTextColor(Color.GRAY)
               .setTypeFace(typeface)
               .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
               .setShowInBottomSheet(true)
               .setTitleColor(ContextCompat.getColor(context, R.color.textColorTitle))
               .setActionTextColorResource(R.color.colorAccent)
               .setAllButtonsTextSize(12)
       }  */

    //For example, year / month / day
    fun getDatePicker(
        context: Context,
        shYear: Int = 0,
        shMonth: Int = 0,
        shDay: Int = 0,
        maxYear: Int = 1
    ): MyPersianDatePickerDialog? {
        val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
        val today = PersianDate()
        return MyPersianDatePickerDialog(context)
            .setPositiveButtonString(context.getString(R.string.label_ok))
            .setNegativeButton(context.getString(R.string.label_cancel))
            .setTodayButton(context.getString(R.string.label_today))
            .setTodayButtonVisible(true)
            .setMinYear(1300)
            .setMaxYear(today.shYear + maxYear)
            .setInitDate(
                if (shYear > 0) shYear else today.shYear,
                if (shMonth > 0) shMonth else today.shMonth,
                if (shDay > 0) shDay else today.shDay
            )
            .setActionTextColor(Color.GRAY)
            .setTypeFace(typeface)
            .setTitleType(MyPersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setTitleColor(ContextCompat.getColor(context, R.color.colorAccent))
            .setActionTextColorResource(R.color.colorAccent)
            .setAllButtonsTextSize(12)
    }

    //For example, year / month, can not select a day in the date picker
    fun getMyCustomDatePicker(context: Context): MyPersianDatePickerDialog? {
        val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
        val today = PersianDate()
        return MyPersianDatePickerDialog(context)
            .setPositiveButtonString(context.getString(R.string.label_ok))
            .setNegativeButton(context.getString(R.string.label_cancel))
            .setTodayButton(context.getString(R.string.label_today))
            .setTodayButtonVisible(true)
            .setMinYear(1300)
            .setMaxYear(today.shYear + 1)
            .setInitDate(today.shYear, today.shMonth, today.shDay)
            .setActionTextColor(Color.GRAY)
            .setTypeFace(typeface)
            .setTitleType(MyPersianDatePickerDialog.MONTH_YEAR)//
            .setShowDayPicker(false) //
            .setShowInBottomSheet(true)
            .setTitleColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .setActionTextColorResource(R.color.colorPrimary)
            .setAllButtonsTextSize(12)
    }


    fun getYearAndMonthWithDate(str: String): String {
        val monthNames = arrayOf(
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند"
        )
        var date = str.take(6)
        if (date.contains("+")) date.replace("+", "")
        var month = date.takeLast(2)
        var year = date.take(4)
        var strDate = monthNames.get(month.toInt() - 1) + " ماه " + year
        return strDate
    }


    fun getYearAndMonthWithDate(year: String, month: String): String {
        val monthNames = arrayOf(
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند"
        )
        return monthNames[month.toInt() - 1] + " ماه " + year
    }

    fun getFormattedJalaliDate(persianPickerDate: MyPersianPickerDate): String {

        val persianMonth = if (persianPickerDate.persianMonth in 1..9) {
            "0${persianPickerDate.persianMonth}"
        } else "${persianPickerDate.persianMonth}"

        val persianDate = if (persianPickerDate.persianDay in 1..9) {
            "0${persianPickerDate.persianDay}"
        } else "${persianPickerDate.persianDay}"


        return "${persianPickerDate.persianYear}/$persianMonth/$persianDate"
    }

    fun checkInputIsValidBranch(str: String): Boolean {
        val regex = Pattern.compile("[a-zA-Z$&+،,:;=\\\\?@#|/'<>.^*()%!-]")
        return !regex.matcher(str).find()
    }

    fun checkInputIsValidEnglishChar(str: String): Boolean {
        val regex = Pattern.compile("^[A-Za-z0-9-]+$")
        return regex.matcher(str).matches()
    }

    fun checkInputIsValidEMail(str: String): Boolean {
        val regex = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return regex.matcher(str).matches()
    }

    fun checkInputIsValidAddress(str: String): Boolean {
        val regex = Pattern.compile("[a-zA-Z$&+:;=\\\\?@#|/'<>.^*()%!]")
        return !regex.matcher(str).find()
    }

    fun checkInputIsValidPersionName(str: String): Boolean {
        val regex = Pattern.compile("[a-zA-Z1-9$&+،,:;=\\\\?@#|/'<>.^*()%!-]")
        return !regex.matcher(str).find()
    }

    fun checkTimeFormat(str: String): Boolean {

        val regex = Pattern.compile("^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])\$")
        val newStr = removePersianNumber(str)
        return (regex.matcher(newStr).find())
    }

    fun getDateSeparator(value: String?): String {

        return when {
            value == null -> {
                return "-"
            }

            value.length == 8 -> {
                "${value.subSequence(0, 4)}/${value.subSequence(4, 6)}/${
                    value.subSequence(
                        6,
                        8
                    )
                }"
            }

            else -> value
        }
    }

    fun differenceBetweenShamsiDate(
        startDate: String = "",
        endDate: String = ""
    ): Triple<Int, Int, Int> {

        val start = startDate.replace("/", "")
        val end = endDate.replace("/", "")
        return if (start.length == 8 && end.length == 8) {
            val startYear = (start.substring(0, 4).toInt())
            val endYear = (end.substring(0, 4).toInt())
            val startMonth = (start.substring(4, 6).toInt())
            val endMonth = (end.substring(4, 6).toInt())
            val startDay = (start.substring(6, 8).toInt())
            val endDay = (end.substring(6, 8).toInt())

            val startTimeStamp =
                convertPersianDateToTimeStamp(year = startYear, month = startMonth, day = startDay)
            val endTimeStamp =
                convertPersianDateToTimeStamp(year = endYear, month = endMonth, day = endDay)
            val diffDay = differenceBetweenTimestamps(startTimeStamp, endTimeStamp)

            val y = diffDay / 365
            val m = (diffDay % 365) / 30
            val d = (diffDay % 365) % 30

            Triple(y.toInt(), m.toInt(), d.toInt())
        } else
            Triple(0, 0, 0)
    }

    fun convertPersianDateToTimeStamp(year: Int, month: Int, day: Int): Long {
        val grDate = PersianDate().jalali_to_gregorian(year, month, day)
        return if (grDate.size == 3) {
            val date =
                SimpleDateFormat("yyyy-MM-dd").parse("${grDate[0]}-${grDate[1]}-${grDate[2]}")
            date?.time ?: 0
        } else {
            0L
        }
    }

    fun convertJalaliToGregorianString(date: String): String {
        return try {
            if (!date.contains("/")) return ""

            val parts = date.split("/")
            if (parts.size != 3) return ""

            val jY = parts[0].toInt()
            val jM = parts[1].toInt()
            val jD = parts[2].toInt()

            val pDate = PersianDate()
            val gDate = pDate.jalali_to_gregorian(jY, jM, jD)

            String.format(Locale.US, "%04d-%02d-%02d", gDate[0], gDate[1], gDate[2])
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun differenceBetweenTimestamps(comboStart: Long?, comboEnd: Long?): Long {
        comboStart?.let { start ->
            comboEnd?.let { end ->
                return TimeUnit.MILLISECONDS.toDays(end - start)
            }
        }
        return 0
    }

    fun normalizeHistoryDuration(
        historyYears: String?,
        historyMonths: String?,
        historyDays: String?
    ): HistoryDuration {
        var years = historyYears?.toIntOrNull() ?: 0
        var months = historyMonths?.toIntOrNull() ?: 0
        val days = historyDays?.toIntOrNull() ?: 0

        months += days / 30
        val normalizedDays = days % 30

        years += months / 12
        val normalizedMonths = months % 12

        return HistoryDuration(
            years = years,
            months = normalizedMonths,
            days = normalizedDays
        )
    }


    fun isDebuggable(ctx: Context?): Boolean {
        if (ctx == null) {
            return false
        }
        return try {
            val appInfo = ctx.packageManager.getApplicationInfo(ctx.packageName, 0)
            (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    private val DEBUG_DN: X500Principal = X500Principal("CN=Android Debug,O=Android,C=US")
//    fun isDebuggable2(ctx: Context?): Boolean {
//        var debuggable = false
//        try {
//            val pinfo =
//                ctx!!.packageManager.getPackageInfo(ctx.packageName, PackageManager.GET_SIGNATURES)
//            val signatures: Array<Signature>? = pinfo.signatures
//            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
//
//            for (i in signatures?.indices!!) {
//                val stream = ByteArrayInputStream(signatures.get(i).toByteArray())
//                val cert: X509Certificate = cf.generateCertificate(stream) as X509Certificate
//
//                val certificatePrincipal = cert.subjectX500Principal.toString()
//                //  debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN)
//                if (certificatePrincipal.contains("CN=Android Debug") && certificatePrincipal.contains(
//                        "O=Android"
//                    ) && certificatePrincipal.contains("C=US")
//                )
//                    debuggable = true
//
//                if (debuggable) break
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            //debuggable variable will remain false
//        } catch (e: CertificateException) {
//            //debuggable variable will remain false
//        } catch (e: Exception) {
//            //debuggable variable will remain false
//        }
//        return debuggable
//    }

    fun getBrowserPackageName(context: Context): String? {

        val packageNames: List<String> = listOf(
            "org.mozilla.firefox",
            "org.mozilla.firefox.App",
            "com.android.chrome",
            "com.google.android.apps.chrome",
            "com.chrome.canary",
            "com.chrome.dev",
            "com.chrome.beta",
            "com.sec.android.app.sbrowser",
            "com.huawei.browser",
            "com.android.browser",
            "com.mi.globalbrowser",
            "Mozilla.Firefox.Lite", "org.mozilla.rocket"
        )


//        val packages: List<ApplicationInfo> =
//            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//
//        val packageNames = packages.filter {
//            val launchIntent =
//                context.packageManager.getLaunchIntentForPackage(it.packageName)
//
//            launchIntent?.resolveActivity(context.packageManager) != null
//        }.map { it.packageName }

        return CustomTabsClient.getPackageName(context, packageNames, false)

    }


    fun constructExtraHeadersIntent(session: CustomTabsSession, token: String): CustomTabsIntent? {
        val intent = CustomTabsIntent.Builder(session).build()

        // Example non-cors-whitelisted headers.
//        val headers = Bundle()
//        headers.putString("Authorization",token)
//
//        intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)
        return intent
    }


    fun getTendencyResId(genderCode: String?, tendencyCode: String) = when (tendencyCode) {
        "124" -> {
            R.string.survivor
        }

        "106", "110" -> {
            when (genderCode) {
                "01" -> R.string.father
                "02" -> R.string.mother
                else -> R.string.parents
            }
        }

        "101", "104" -> {
            R.string.son
        }

        "102", "105" -> {
            R.string.daughter
        }

        "111", "112", "117" -> {
            when (genderCode) {
                "01" -> R.string.son
                "02" -> R.string.daughter
                else -> R.string.child
            }
        }

        "133", "118", "123" -> {
            R.string.step_child
        }

        "100", "103", "107", "108", "109" -> {
            R.string.spouse
        }

        else -> {
            0
        }
    }

    fun getAge(birthDate: Long, deathDate: Long? = System.currentTimeMillis()): Int {
        val diffDay = differenceBetweenTimestamps(birthDate, deathDate)
        return (diffDay / 365).toInt()
    }

    fun calculateDayAndWageOfHistory(
        list: List<WageAndHistoryModel>,
        sumHistoryDays: String
    ): HashMap<String, String> {
        val listDays = ArrayList<String>()
        val listWages = ArrayList<String>()
        val basicWage = 11112690
        val premiumPaymentYear = sumHistoryDays.toDouble() / 365
        sumHistoryDays
        list.forEach { item ->
            val months = arrayListOf(
                item.hismon1,
                item.hismon2,
                item.hismon3,
                item.hismon4,
                item.hismon5,
                item.hismon6,
                item.hismon7,
                item.hismon8,
                item.hismon9,
                item.hismon10,
                item.hismon11,
                item.hismon12
            )

            val wages = arrayListOf(
                item.hiswage1,
                item.hiswage2,
                item.hiswage3,
                item.hiswage4,
                item.hiswage5,
                item.hiswage6,
                item.hiswage7,
                item.hiswage8,
                item.hiswage9,
                item.hiswage10,
                item.hiswage11,
                item.hiswage12
            )
            months.forEach { month ->
                if (!month.isNullOrBlank()) {
                    listDays.add(month)
                    //  premiumPaymentDay += month.toInt()
                }
            }
            wages.forEach { wage ->
                if (!wage.isNullOrBlank())
                    listWages.add(wage)
            }
        }
        var sumDays = 0
        var sumWages = 0.0

        (listDays.size - 1 downTo 1).forEach { i ->
            if (sumDays < 730) {
                sumDays += listDays[i].toInt()
                sumWages += listWages[i].toInt()
            } else {
                return@forEach
            }
        }

        val premiumPayment =
            BigDecimal(premiumPaymentYear).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        val averageSalary = ceil(sumWages / 24)
        var eligibleAmount = ceil((averageSalary / 30) * premiumPayment)
        if (premiumPayment >= 20 && eligibleAmount < 11112690) {
            eligibleAmount = basicWage.toDouble()
        }
        if (premiumPayment < 20) {
            val minWage = (premiumPayment / 30) * basicWage
            if (eligibleAmount < minWage) {
                eligibleAmount = minWage
            }
        }
        return hashMapOf(
            AVERAGE_SALARY to getRialWithSeparator(averageSalary.toLong()),
            HISTORY_YEAR to String.format("%.2f", premiumPaymentYear),
            ELIGIBLE_AMOUNT to getRialWithSeparator(eligibleAmount.toLong())
        )
    }


}

data class HistoryDuration(
    val years: Int,
    val months: Int,
    val days: Int
)

fun getLicensePeriodList(): ArrayList<MenuModel>? {
    val itemList = ArrayList<MenuModel>()
    itemList.add(MenuModel("یک روز", "0"))
    itemList.add(MenuModel("یک هفته", "1"))
    itemList.add(MenuModel("یک ماه", "2"))
    itemList.add(MenuModel("یک سال", "3"))
    return itemList
}

fun convertStrToLong(str: String): Long {
    return if (str.isNumericString())
        str.toLong()
    else
        0L
}

fun <T : Serializable?> getSerializable(arguments: Bundle?, name: String, clazz: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arguments?.getSerializable(name, clazz)!!
    else
        arguments?.getSerializable(name) as T
}

fun getTodayStartTimestamp(): Long {
    return getTodayCalendar().timeInMillis
}

fun previousMonth(): Long {
    val today = getTodayCalendar()
    today.add(Calendar.MONTH, -6)
    return today.timeInMillis
}
fun Context.isBiometricAvailable(): Boolean {
    val biometricManager = BiometricManager.from(this)
    return biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    ) == BiometricManager.BIOMETRIC_SUCCESS
}

fun getTodayCalendar(): Calendar {
    // Get a Calendar instance for the system's default timezone and locale
    val calendar = Calendar.getInstance()

fun getTodayCalendar(): Calendar {
    // Get a Calendar instance for the system's default timezone and locale
    val calendar = Calendar.getInstance()
    // To use UTC, which is often recommended for timestamps:
    // val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    // Set the time to the beginning of the day
    calendar.set(Calendar.HOUR_OF_DAY, 0) // 24-hour format
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

    // To use UTC, which is often recommended for timestamps:
    // val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    // Set the time to the beginning of the day
    calendar.set(Calendar.HOUR_OF_DAY, 0) // 24-hour format
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}


fun String.replaceLast(oldValue: String, newValue: String): String {
    val lastIndex = lastIndexOf(oldValue)
    if (lastIndex == -1) {
        return this
    }
    val prefix = substring(0, lastIndex)
    val suffix = substring(lastIndex + oldValue.length)
    return "$prefix$newValue$suffix"
}

