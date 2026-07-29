package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.ItemChatBotWeddingPresentBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.widget.DatePickerWidget
import java.util.Date

class WeddingPresentBusinessGenerator : BusinessViewGenerator {

    private var binding: ItemChatBotWeddingPresentBinding? = null
    private var weddingDateTimestamp: Long = 0L
    private var weddingDateJalali: String = ""
    private var calcDateTimestamp: Long = 0L
    private var calcDateJalali: String = ""
    /** Local UI step when user navigates without an API call (e.g. step 2 → 3, step 3 → 4). */
    private var displayStep: Int? = null
    /** True after user changes calc date until a new calculate API succeeds. */
    private var calculationInvalidated: Boolean = false
    /** True only when user picks a date on step 3 date picker. */
    private var calcDateSelectedByUser: Boolean = false

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals("WEDDING_PRESENT", true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        if (binding == null) {
            host.formContainer.removeAllViews()
        }
        setupState(host, ensureBinding(host), schema, message)
    }

    override fun cleanup() {
        binding = null
        weddingDateTimestamp = 0L
        weddingDateJalali = ""
        calcDateTimestamp = 0L
        calcDateJalali = ""
        displayStep = null
        calculationInvalidated = false
        calcDateSelectedByUser = false
    }

    private fun ensureBinding(host: FormHost): ItemChatBotWeddingPresentBinding {
        binding?.let { return it }
        val inflater = LayoutInflater.from(host.hostContext)
        return ItemChatBotWeddingPresentBinding.inflate(inflater, host.formContainer, false).also {
            host.formContainer.removeAllViews()
            host.formContainer.addView(it.root)
            binding = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupState(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        val step = resolveStep(schema)
        val isFinalStepComplete = step == 4 &&
            !schema.message.isNullOrBlank() &&
            schema.errorMessage.isNullOrBlank()
        binding.apply {
            groupStep1.gone()
            groupStep2.gone()
            groupStep3.gone()
            groupStep4.gone()
            progressBar.gone()
            updateStepIndicators(this, step, isFinalStepComplete)

            if (schema.showCancelButton) btnCancel.visible() else btnCancel.gone()

            when (step) {
                1 -> setupStep1(host, this, schema, model)
                2 -> setupStep2(host, this, schema, model)
                3 -> setupStep3(host, this, schema, model)
                else -> setupStep4(host, this, schema, model)
            }

            btnCancel.setOnClickListener {
                host.dispatchFormAction(
                    ServiceNameEnum.WEDDING_PRESENT_CANCEL.key,
                    model.data
                        ?.filterKeys { it != null }
                        ?.map { it.key!! to it.value }
                        ?.toMap()
                        ?: emptyMap()
                )
            }
        }
    }

    private fun setupStep1(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep1.visible()
            if (schema.isLoading) progressBar.visible()

            tvUserInfo.text = buildUserInfoText(model)
            weddingDateTimestamp = model.data?.get("weddingDateTimestamp")?.toLongOrNull() ?: 0L
            weddingDateJalali = model.data?.get("weddingDateJalali").orEmpty()
            model.data?.get("partnerNationalCode")?.let { inputPartnerNationalCode.setText(it) }

            widgetDatePickerWeddingGift.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    weddingDateTimestamp = timeStamp
                    weddingDateJalali = jalaliDate
                    widgetDatePickerWeddingGift.clearError()
                }
            })

            if (schema.errorMessage.isNullOrBlank()) {
                tilPartnerNationalCode.error = null
                widgetDatePickerWeddingGift.clearError()
                tvStep1Error.gone()
            } else {
                tvStep1Error.visible()
                tvStep1Error.text = schema.errorMessage
            }

            checkboxCommitment.isChecked = model.data?.get("commitmentAccepted")?.toString() == "true"
            btnNextStep1.isEnabled = !schema.isLoading && checkboxCommitment.isChecked
            checkboxCommitment.setOnCheckedChangeListener { _, isChecked ->
                btnNextStep1.isEnabled = !schema.isLoading && isChecked
            }

            btnNextStep1.setOnClickListener {
                val partnerNationalCode = inputPartnerNationalCode.text?.toString()?.trim().orEmpty()
                if (weddingDateTimestamp <= 0L) {
                    widgetDatePickerWeddingGift.setError(
                        ContextCompat.getString(root.context, R.string.message_selecte_marriage_date)
                    )
                    return@setOnClickListener
                }
                if (partnerNationalCode.length != 10) {
                    tilPartnerNationalCode.error =
                        ContextCompat.getString(root.context, R.string.error_not_valid_national_id)
                    return@setOnClickListener
                }

                val updatedData = model.data?.toMutableMap() ?: mutableMapOf()
                updatedData["partnerNationalCode"] = partnerNationalCode
                updatedData["weddingDateTimestamp"] = weddingDateTimestamp.toString()
                updatedData["weddingDateJalali"] = weddingDateJalali
                updatedData["commitmentAccepted"] = "true"
                host.dispatchFormAction(
                    ServiceNameEnum.WEDDING_PRESENT_VALIDATE.key,
                    updatedData.filterKeys { it != null }.map { it.key!! to it.value }.toMap()
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep2(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep2.visible()
            if (schema.isLoading) progressBar.visible()

            val message = schema.message
                ?: model.data?.get("validateMessage")?.toString()
                ?: "اعتبارسنجی تاریخ عقد با موفقیت انجام شد"

            tvValidateMessage.text = message
            imgValidateStatus.setImageResource(R.drawable.ic_check_circle_green)
            tvValidateMessage.setTextColor(root.context.getColor(R.color.green_dark))

            btnProceedToCalculate.isEnabled = !schema.isLoading
            btnProceedToCalculate.setOnClickListener {
                navigateLocally(host, binding, schema, model, step = 3)
            }
        }
    }

    private fun setupStep3(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep3.visible()
            if (schema.isLoading) progressBar.visible()

            val hasServerCalcResult = schema.currentStep >= 3 &&
                !model.data?.get("amountPayable").isNullOrBlank()
            if (hasServerCalcResult) {
                calcDateTimestamp = model.data?.get("calcDateTimestamp")?.toLongOrNull() ?: 0L
                calcDateJalali = model.data?.get("calcDateJalali").orEmpty()
                calcDateSelectedByUser = calcDateTimestamp > 0L
            } else {
                calcDateTimestamp = 0L
                calcDateJalali = ""
                calcDateSelectedByUser = false
                calculationInvalidated = false
            }

            widgetDatePickerMarriage.setOnClickListener { rootLayoutResult.gone() }
            widgetDatePickerMarriage.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    calcDateTimestamp = timeStamp
                    calcDateJalali = jalaliDate
                    calcDateSelectedByUser = true
                    widgetDatePickerMarriage.clearError()
                    calculationInvalidated = true
                    rootLayoutResult.gone()
                    updateCalculateButton(binding)
                }
            })

            if (schema.errorMessage.isNullOrBlank()) {
                tvCalcError.gone()
                widgetDatePickerMarriage.clearError()
            } else {
                tvCalcError.visible()
                tvCalcError.text = schema.errorMessage
            }

            val amountPayable = model.data?.get("amountPayable").orEmpty()
            val totalSalary = model.data?.get("totalSalary").orEmpty()
            val hasCalculationResult = hasServerCalcResult &&
                amountPayable.isNotBlank() &&
                !calculationInvalidated
            if (hasCalculationResult) {
                rootLayoutResult.visible()
                AmountPayableValue.text = amountPayable
                SumSalaryLastTwoYearsTitleValue.text = totalSalary.ifBlank { "-" }
            } else {
                rootLayoutResult.gone()
            }

            btnProceedToSubmit.gone()
            btnCal.isEnabled = !schema.isLoading
            updateCalculateButton(this, hasCalculationResult)
            btnCal.setOnClickListener {
                if (hasCalculationResult) {
                    navigateLocally(host, binding, schema, model, step = 4)
                    return@setOnClickListener
                }
                if (!calcDateSelectedByUser || calcDateTimestamp <= 0L) {
                    widgetDatePickerMarriage.setError(
                        ContextCompat.getString(root.context, R.string.message_selecte_marriage_date)
                    )
                    return@setOnClickListener
                }
                val updatedData = model.data?.toMutableMap() ?: mutableMapOf()
                updatedData["calcDateTimestamp"] = calcDateTimestamp.toString()
                updatedData["calcDateJalali"] = calcDateJalali
                host.dispatchFormAction(
                    ServiceNameEnum.WEDDING_PRESENT_CALCULATE.key,
                    updatedData.filterKeys { it != null }.map { it.key!! to it.value }.toMap()
                )
            }
        }
    }

    private fun updateCalculateButton(
        binding: ItemChatBotWeddingPresentBinding,
        hasCalculationResult: Boolean = false
    ) {
        binding.btnCal.text = ContextCompat.getString(
            binding.root.context,
            if (hasCalculationResult) R.string.confirm else R.string.label_calculate_marriage_allowance
        )
    }

    private fun resolveStep(schema: FormSchema): Int {
        val schemaStep = schema.currentStep
        displayStep?.let { local ->
            if (schemaStep >= local) {
                displayStep = null
                if (schemaStep == 3 && !schema.isLoading && schema.errorMessage.isNullOrBlank()) {
                    calculationInvalidated = false
                }
                return schemaStep
            }
            return local
        }
        return schemaStep
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun navigateLocally(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel,
        step: Int
    ) {
        displayStep = step
        setupState(host, binding, schema, model)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep4(
        host: FormHost,
        binding: ItemChatBotWeddingPresentBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep4.visible()
            btnCancel.gone()
            if (schema.isLoading) progressBar.visible()

            val isSuccess = !schema.message.isNullOrBlank() && schema.errorMessage.isNullOrBlank()

            if (isSuccess) {
                btnSubmit.gone()
                imgSubmitStatus.visible()
                tvSubmitMessage.visible()
                tvSubmitMessage.text = schema.message
                imgSubmitStatus.setImageResource(R.drawable.ic_check_circle_green)
                tvSubmitMessage.setTextColor(root.context.getColor(R.color.green_dark))
            } else if (!schema.errorMessage.isNullOrBlank()) {
                btnSubmit.gone()
                imgSubmitStatus.visible()
                tvSubmitMessage.visible()
                tvSubmitMessage.text = schema.errorMessage
                imgSubmitStatus.setImageResource(R.drawable.ic_close_circle_red)
                tvSubmitMessage.setTextColor(root.context.getColor(R.color.red_recycler_color_icon))
            } else {
                btnSubmit.visible()
                imgSubmitStatus.gone()
                tvSubmitMessage.gone()
                btnSubmit.isEnabled = !schema.isLoading
                btnSubmit.setOnClickListener {
                    host.dispatchFormAction(
                        ServiceNameEnum.WEDDING_PRESENT_SUBMIT.key,
                        model.data
                            ?.filterKeys { it != null }
                            ?.map { it.key!! to it.value }
                            ?.toMap()
                            ?: emptyMap()
                    )
                }
            }
        }
    }

    private fun buildUserInfoText(model: AiGenerativeModel): String {
        fun value(key: String) = model.data?.get(key)?.takeIf { !it.isNullOrBlank() } ?: "-"
        return buildString {
            append("شماره بیمه: ")
            append(value("risuid"))
            append("\nنام و نام خانوادگی: ")
            append(value("insuranceFirstName"))
            append(" ")
            append(value("insuranceLastName"))
            append("\nنوع بیمه: ")
            append(value("insuranceTypeDesc"))
            append("\nوضعیت بیمه: ")
            append(value("insuranceStatusDesc"))
            append("\nشماره حساب: ")
            append(value("bankAccount"))
            append("\nنام بانک: ")
            append(value("bankName"))
            append("\nآخرین شعبه بیمه پردازی: ")
            append(value("branchName"))
            append("\nشماره همراه: ")
            append(value("mobilNumber"))
        }
    }

    private fun updateStepIndicators(
        binding: ItemChatBotWeddingPresentBinding,
        step: Int,
        isFinalStepComplete: Boolean = false
    ) {
        binding.apply {
            val grey = R.drawable.shape_circle_grey
            val blue = R.drawable.shape_circle_blue
            val green = R.drawable.shape_circle_green

            step1.setBackgroundResource(if (step == 1) blue else green)
            step2.setBackgroundResource(when (step) { 1 -> grey; 2 -> blue; else -> green })
            step3.setBackgroundResource(when (step) { 1, 2 -> grey; 3 -> blue; else -> green })
            step4.setBackgroundResource(
                when {
                    step < 4 -> grey
                    isFinalStepComplete -> green
                    else -> blue
                }
            )
        }
    }
}
