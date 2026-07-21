package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.repository.ai.model.FieldValidation
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

/**
 * Declarative definition of the "اعلام حادثه" (Workplace Accident Report, dashboard
 * service id 1011) as an AI-agent generative form. Mirrors the 4-step wizard of
 * OccurrenceReportFragment but expressed purely as a [FormSchema] so it renders inline
 * inside the chat via SchemaBusinessGenerator.
 *
 * Each step's primary [FormAction] id equals a [ServiceNameEnum] key. Pressing it
 * dispatches the matching use case, which returns the next step's schema while carrying
 * the accumulated field values forward in the payload. Step 5 is the terminal result
 * step (success tracking number or error).
 */
object OccurrenceFormKeys {
    const val SCHEMA_KEY: String = "OCCURRENCE_REPORT"

    // Step 1 – identity
    const val NATIONAL_ID: String = "nationalCode"
    const val BIRTH_DATE: String = "birth_date"

    // Step 2 – workshop
    const val WORKSHOP_ID: String = "workshop_id"
    const val EMPLOYER_NAME: String = "employer_full_name"
    const val EMPLOYER_PHONE: String = "employer_phone"
    const val WORKSHOP_ADDRESS: String = "workshop_address"
    const val WORKSHOP_PHONE: String = "workshop_phone"
    const val WORKSHOP_POSTAL_CODE: String = "workshop_postal_code"

    // Step 3 – personal & occupational
    const val MARITAL_STATUS: String = "marital_status"
    const val EMPLOYMENT_DATE: String = "employment_date"
    const val JOB_DESCRIPTION: String = "job_description"
    const val REPORT_JOB_LOCATION: String = "report_job_location"
    const val VEHICLE: String = "vehicle"
    const val WORK_START: String = "work_start_time"
    const val WORK_END: String = "work_end_time"
    const val RESIDENTIAL_ADDRESS: String = "residential_address"
    const val REPORT_TELEPHONE: String = "report_telephone"
    const val REPORT_POSTAL_CODE: String = "report_postal_code"

    // Step 4 – accident & documents
    const val ACCIDENT_DATE: String = "accident_date"
    const val ACCIDENT_TIME: String = "accident_time"
    const val ACCIDENT_RESULT: String = "accident_result"
    const val ACCIDENT_LOCATION: String = "accident_location"
    const val ACCIDENT_DESCRIPTION: String = "accident_description"
    const val DOCUMENTS: String = "supporting_documents"

    // Carried (non-rendered) values resolved from server lookups
    const val FIRST_NAME: String = "pFirstName"
    const val LAST_NAME: String = "pLastName"
    const val GENDER: String = "gender"
    const val REPORTER_TYPE: String = "reporterType"
    const val NATION_CODE: String = "nationCode"
    const val WORKSHOP_NAME: String = "workshopName"
    const val INSURANCE_ID: String = "insuranceId"
    const val BRANCH_CODE: String = "branchCode"
    const val BRANCH_NAME: String = "branchName"
    const val ISU_TYPE_CODE: String = "isuTypeCode"
    const val ISU_TYPE_DESC: String = "isuTypeDesc"
    const val TRACKING_NUMBER: String = "reportRefrenceNumber"

    /** A workshop dropdown id encodes "<workshopCode>|<branchCode>" so the submit
     * use case can recover both without a second lookup. */
    const val WORKSHOP_ID_SEPARATOR: String = "|"

    /** Default occurrence document type id used for AI-uploaded files. The native
     * fragment lets the user pick a doc type from getDocumentType(); the inline AI
     * upload uses this fixed type to keep the flow self-contained. */
    const val DEFAULT_DOC_TYPE_ID: String = "1"

    // Shared regex/validation building blocks
    private const val JALALI_DATE_PATTERN = "^\\d{4}/\\d{2}/\\d{2}$"
    private const val TIME_PATTERN = "^([01]?\\d|2[0-3]):[0-5]\\d$"
    private const val IRAN_MOBILE_PATTERN = "^09\\d{9}$"
    private const val IRAN_LANDLINE_PATTERN = "^(0\\d{10}|\\d{8})$"
    const val DESCRIPTION_MIN_LENGTH: Int = 20

    fun maritalOptions(): List<FormOption> = listOf(
        FormOption(id = "0", title = "مجرد"),
        FormOption(id = "1", title = "متأهل")
    )

    /** Static accident-result list, matching the native OccurrenceReportViewModel. */
    fun accidentResultOptions(): List<FormOption> = listOf(
        FormOption(id = "0", title = "فوت"),
        FormOption(id = "1", title = "از کار افتادگی کلی"),
        FormOption(id = "2", title = "از کار افتادگی جزئی (کاهش توانایی بین ۳۳٪ تا ۶۶٪)"),
        FormOption(id = "3", title = "نقص عضو (کاهش توانایی کمتر از ۳۳٪)"),
        FormOption(id = "4", title = "در حال استراحت پزشکی"),
        FormOption(id = "5", title = "دریافت غرامت پزشکی و بهبودی"),
        FormOption(id = "6", title = "هیچکدام")
    )

    fun jalaliDateValidation(): FieldValidation = FieldValidation(
        type = FieldValidationType.PATTERN,
        value = JALALI_DATE_PATTERN,
        message = "تاریخ را به صورت ۱۴۰۲/۰۱/۰۱ وارد کنید"
    )

    fun timeValidation(): FieldValidation = FieldValidation(
        type = FieldValidationType.PATTERN,
        value = TIME_PATTERN,
        message = "ساعت را به صورت 08:30 وارد کنید"
    )

    fun mobileValidation(): FieldValidation = FieldValidation(
        type = FieldValidationType.PATTERN,
        value = IRAN_MOBILE_PATTERN,
        message = "شماره همراه معتبر نیست (با ۰۹ و ۱۱ رقم)"
    )

    fun landlineValidation(): FieldValidation = FieldValidation(
        type = FieldValidationType.PATTERN,
        value = IRAN_LANDLINE_PATTERN,
        message = "شماره تلفن معتبر نیست (۱۱ رقم با پیش‌شماره یا ۸ رقم بدون پیش‌شماره)"
    )

    fun descriptionMinLengthValidation(): FieldValidation = FieldValidation(
        type = FieldValidationType.MIN_LENGTH,
        value = DESCRIPTION_MIN_LENGTH.toString(),
        message = "شرح حادثه باید حداقل $DESCRIPTION_MIN_LENGTH کاراکتر باشد"
    )
}

/**
 * Builds the full 4-step (+ result) accident report schema.
 *
 * @param payload     accumulated field values carried across steps (field id → value).
 * @param step        the step to render (1..5).
 * @param workshopOptions dynamically loaded workshop choices for step 2.
 */
fun buildOccurrenceSchema(
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    workshopOptions: List<FormOption> = emptyList(),
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false,
    docTypes: List<FormOption> = emptyList()
): FormSchema {

    fun value(key: String): String? = payload?.get(key)?.toString()

    fun continueAction(target: ServiceNameEnum): List<FormAction> = buildList {
        add(
            FormAction(
                id = target.key,
                title = "ادامه",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.OCCURRENCE_REPORT_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }

    val step1 = FormStep(
        index = 1,
        title = "اطلاعات هویتی",
        fields = listOf(
            FormField(
                id = OccurrenceFormKeys.NATIONAL_ID,
                label = "کد ملی",
                type = FormFieldType.READ_ONLY,
                value = value(OccurrenceFormKeys.NATIONAL_ID)
            ),
            FormField(
                id = OccurrenceFormKeys.BIRTH_DATE,
                label = "تاریخ تولد",
                type = FormFieldType.DATE,
                value = value(OccurrenceFormKeys.BIRTH_DATE),
                required = true,
                hint = "۱۴۰۲/۰۱/۰۱",
                validations = listOf(OccurrenceFormKeys.jalaliDateValidation())
            )
        ),
        actions = continueAction(ServiceNameEnum.OCCURRENCE_REPORT_WORKSHOP)
    )

    val step2 = FormStep(
        index = 2,
        title = "اطلاعات کارگاه",
        fields = listOf(
            FormField(
                id = OccurrenceFormKeys.WORKSHOP_ID,
                label = "کارگاه",
                type = FormFieldType.DROPDOWN,
                value = value(OccurrenceFormKeys.WORKSHOP_ID),
                required = true,
                options = workshopOptions
            ),
            FormField(
                id = OccurrenceFormKeys.EMPLOYER_NAME,
                label = "نام و نام خانوادگی کارفرما",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.EMPLOYER_NAME),
                required = true,
                hint = "مثال: علی علوی"
            ),
            FormField(
                id = OccurrenceFormKeys.EMPLOYER_PHONE,
                label = "شماره تماس کارفرما",
                type = FormFieldType.PHONE,
                value = value(OccurrenceFormKeys.EMPLOYER_PHONE),
                required = true,
                hint = "مثال: ۰۹۱۲۳۴۵۶۷۸۹",
                validations = listOf(OccurrenceFormKeys.mobileValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.WORKSHOP_ADDRESS,
                label = "آدرس کارگاه",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.WORKSHOP_ADDRESS),
                required = true,
                hint = "مثال: تهران، خیابان آزادی، ..."
            ),
            FormField(
                id = OccurrenceFormKeys.WORKSHOP_PHONE,
                label = "شماره تماس کارگاه",
                type = FormFieldType.PHONE,
                value = value(OccurrenceFormKeys.WORKSHOP_PHONE),
                required = false,
                hint = "مثال: ۰۲۱۲۳۴۵۶۷۸",
                validations = listOf(OccurrenceFormKeys.landlineValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.WORKSHOP_POSTAL_CODE,
                label = "کد پستی کارگاه",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.WORKSHOP_POSTAL_CODE),
                required = true,
                hint = "مثال: ۱۲۳۴۵۶۷۸۹۰"
            )
        ),
        actions = continueAction(ServiceNameEnum.OCCURRENCE_REPORT_PERSONAL)
    )

    val step3 = FormStep(
        index = 3,
        title = "اطلاعات شغلی و شخصی",
        fields = listOf(
            FormField(
                id = OccurrenceFormKeys.MARITAL_STATUS,
                label = "وضعیت تأهل",
                type = FormFieldType.DROPDOWN,
                value = value(OccurrenceFormKeys.MARITAL_STATUS),
                required = true,
                options = OccurrenceFormKeys.maritalOptions()
            ),
            FormField(
                id = OccurrenceFormKeys.EMPLOYMENT_DATE,
                label = "تاریخ شروع به کار",
                type = FormFieldType.DATE,
                value = value(OccurrenceFormKeys.EMPLOYMENT_DATE),
                required = true,
                hint = "۱۴۰۲/۰۱/۰۱",
                validations = listOf(OccurrenceFormKeys.jalaliDateValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.JOB_DESCRIPTION,
                label = "عنوان شغلی",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.JOB_DESCRIPTION),
                required = true,
                hint = "مثال: کارشناس فنی"
            ),
            FormField(
                id = OccurrenceFormKeys.REPORT_JOB_LOCATION,
                label = "محل کار",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.REPORT_JOB_LOCATION),
                required = true,
                hint = "مثال: کارگاه مرکزی"
            ),
            FormField(
                id = OccurrenceFormKeys.VEHICLE,
                label = "وسیله نقلیه مورد استفاده",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.VEHICLE),
                required = true,
                hint = "مثال: سرویس شرکت"
            ),
            FormField(
                id = OccurrenceFormKeys.WORK_START,
                label = "ساعت شروع کار",
                type = FormFieldType.TIME,
                value = value(OccurrenceFormKeys.WORK_START),
                required = true,
                hint = "08:00",
                validations = listOf(OccurrenceFormKeys.timeValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.WORK_END,
                label = "ساعت پایان کار",
                type = FormFieldType.TIME,
                value = value(OccurrenceFormKeys.WORK_END),
                required = true,
                hint = "16:00",
                validations = listOf(OccurrenceFormKeys.timeValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.RESIDENTIAL_ADDRESS,
                label = "آدرس محل سکونت",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.RESIDENTIAL_ADDRESS),
                required = true,
                hint = "مثال: تهران، خیابان انقلاب، ..."
            ),
            FormField(
                id = OccurrenceFormKeys.REPORT_TELEPHONE,
                label = "تلفن محل سکونت",
                type = FormFieldType.PHONE,
                value = value(OccurrenceFormKeys.REPORT_TELEPHONE),
                required = true,
                hint = "مثال: ۰۲۱۲۳۴۵۶۷۸",
                validations = listOf(OccurrenceFormKeys.landlineValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.REPORT_POSTAL_CODE,
                label = "کد پستی محل سکونت",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.REPORT_POSTAL_CODE),
                required = true,
                hint = "مثال: ۱۲۳۴۵۶۷۸۹۰"
            )
        ),
        actions = continueAction(ServiceNameEnum.OCCURRENCE_REPORT_ACCIDENT)
    )

    val step4 = FormStep(
        index = 4,
        title = "جزئیات حادثه و بارگذاری مدارک",
        fields = listOf(
            FormField(
                id = OccurrenceFormKeys.ACCIDENT_DATE,
                label = "تاریخ حادثه",
                type = FormFieldType.DATE,
                value = value(OccurrenceFormKeys.ACCIDENT_DATE),
                required = true,
                hint = "۱۴۰۲/۰۱/۰۱",
                validations = listOf(OccurrenceFormKeys.jalaliDateValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.ACCIDENT_TIME,
                label = "ساعت حادثه",
                type = FormFieldType.TIME,
                value = value(OccurrenceFormKeys.ACCIDENT_TIME),
                required = true,
                hint = "12:00",
                validations = listOf(OccurrenceFormKeys.timeValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.ACCIDENT_RESULT,
                label = "نتیجه حادثه",
                type = FormFieldType.DROPDOWN,
                value = value(OccurrenceFormKeys.ACCIDENT_RESULT),
                required = true,
                options = OccurrenceFormKeys.accidentResultOptions()
            ),
            FormField(
                id = OccurrenceFormKeys.ACCIDENT_LOCATION,
                label = "محل وقوع حادثه",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.ACCIDENT_LOCATION),
                required = true,
                hint = "مثال: محوطه کارگاه"
            ),
            FormField(
                id = OccurrenceFormKeys.ACCIDENT_DESCRIPTION,
                label = "شرح حادثه",
                type = FormFieldType.TEXT,
                value = value(OccurrenceFormKeys.ACCIDENT_DESCRIPTION),
                required = true,
                hint = "مثال: سقوط از ارتفاع هنگام کار با داربست",
                validations = listOf(OccurrenceFormKeys.descriptionMinLengthValidation())
            ),
            FormField(
                id = OccurrenceFormKeys.DOCUMENTS,
                label = "مدارک و تصاویر",
                type = FormFieldType.FILE_UPLOAD,
                value = value(OccurrenceFormKeys.DOCUMENTS),
                required = true,
                options = docTypes
            )
        ),
        actions = buildList {
            add(
                FormAction(
                    id = ServiceNameEnum.OCCURRENCE_REPORT_SUBMIT.key,
                    title = "ثبت درخواست",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.PRIMARY
                )
            )
            if (showCancelButton) {
                add(
                    FormAction(
                        id = ServiceNameEnum.OCCURRENCE_REPORT_CANCEL.key,
                        title = "انصراف",
                        handler = FormActionHandler.SERVICE,
                        style = FormActionStyle.SECONDARY
                    )
                )
            }
        }
    )

    val resultStep = FormStep(index = 5, title = "نتیجه")

    return FormSchema(
        key = OccurrenceFormKeys.SCHEMA_KEY,
        steps = listOf(step1, step2, step3, step4, resultStep),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}
