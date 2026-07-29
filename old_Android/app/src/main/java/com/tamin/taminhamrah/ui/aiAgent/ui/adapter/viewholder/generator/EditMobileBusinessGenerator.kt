package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.annotation.SuppressLint
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.ItemChatBotEditMobileBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class EditMobileBusinessGenerator : BusinessViewGenerator {

    private var editMobileBinding: ItemChatBotEditMobileBinding? = null
    private var countDownTimer: CountDownTimer? = null
    private var lastBoundMessageId: String? = null

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals("EDIT_MOBILE", true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        val previousId = lastBoundMessageId

        if (editMobileBinding == null) {
            host.formContainer.removeAllViews()
        }

        val binding = ensureBinding(host)
        lastBoundMessageId = message.id
        setupState(host, binding, schema, message, previousId)
    }

    override fun cleanup() {
        countDownTimer?.cancel()
        countDownTimer = null
        editMobileBinding = null
        lastBoundMessageId = null
    }

    private fun ensureBinding(host: FormHost): ItemChatBotEditMobileBinding {
        editMobileBinding?.let { return it }
        val inflater = LayoutInflater.from(host.hostContext)
        return ItemChatBotEditMobileBinding.inflate(inflater, host.formContainer, false).also {
            host.formContainer.removeAllViews()
            host.formContainer.addView(it.root)
            editMobileBinding = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupState(
        host: FormHost,
        binding: ItemChatBotEditMobileBinding,
        schema: FormSchema,
        model: AiGenerativeModel,
        previousId: String?
    ) {
        val step = schema.currentStep
        val newPhoneNumber = model.data?.get("newPhone")
        binding.apply {
            val currentPhone = model.data?.get("currentPhone")
                ?: model.data?.get("currentPhoneNumber")
                ?: model.data?.get("phone")
            val targetTime = model.data?.get("targetTime")?.toLongOrNull()
            val expirationDuration = if (targetTime != null) {
                targetTime - System.currentTimeMillis()
            } else {
                model.data?.get("expirationDuration")?.toLongOrNull() ?: 120000L
            }

            groupStep1.gone()
            groupStep2.gone()
            groupStep3.gone()
            progressBar.gone()
            groupExpanded.visible()
            updateStepIndicators(this, step)
            tvTitle.text = "ویرایش شماره موبایل"
            inputMobileCurrent.setText(currentPhone)

            if (!newPhoneNumber.isNullOrEmpty()) {
                inputMobile.setText(newPhoneNumber)
            } else if (previousId != model.id) {
                inputMobile.setText("")
            }
            if (previousId != model.id) {
                inputVerifyCode.setText("")
            }

            when (step) {
                1 -> {
                    groupStep1.visible()
                    btnSendOtp.isEnabled = !schema.isLoading
                    if (schema.isLoading) progressBar.visible() else progressBar.gone()
                    if (schema.showCancelButton) btnCancel.visible() else btnCancel.gone()
                }
                2 -> {
                    groupStep2.visible()
                    tvSentTo.text = root.context.getString(R.string.sended_otp, newPhoneNumber ?: "")
                    btnVerify.isEnabled = !schema.isLoading
                    if (schema.isLoading) progressBar.visible() else progressBar.gone()
                    startTimer(expirationDuration, this, host)
                    if (schema.showCancelButton) btnCancel.visible() else btnCancel.gone()
                }
                3 -> {
                    groupStep3.visible()
                    val stepModel = schema.steps.getOrNull(step - 1)
                    tvResultTitle.gone()

                    tvMessage.text = stepModel?.message ?: schema.message ?: "عملیات با موفقیت انجام شد"
                    val bottomMessage = stepModel?.bottomMessage ?: schema.bottomMessage
                    if (!bottomMessage.isNullOrEmpty()) {
                        tvBottomMessage.text = bottomMessage
                        tvBottomMessage.visible()
                    } else {
                        tvBottomMessage.gone()
                    }
                    if (!schema.errorMessage.isNullOrEmpty()) {
                        imgStatus.setImageResource(R.drawable.ic_close_circle_red)
                        tvMessage.setTextColor(root.context.getColor(R.color.red_recycler_color_icon))
                    } else {
                        imgStatus.setImageResource(R.drawable.ic_check_circle_green)
                        tvMessage.setTextColor(root.context.getColor(R.color.green_dark))
                    }
                    btnCancel.gone()
                }
            }

            if (!schema.errorMessage.isNullOrEmpty()) {
                when (step) {
                    1 -> inputMobile.error = schema.errorMessage
                    2 -> inputVerifyCode.error = schema.errorMessage
                }
            } else {
                inputMobile.error = null
                inputVerifyCode.error = null
            }

            btnCancel.setOnClickListener {
                host.dispatchFormAction(ServiceNameEnum.EDIT_PHONE_NUMBER_CANCEL.key, emptyMap())
            }
            btnSendOtp.setOnClickListener {
                val newPhoneInput = inputMobile.text.toString()
                if (newPhoneInput.length == 11 && newPhoneInput.startsWith("09")) {
                    host.dispatchFormAction(
                        ServiceNameEnum.EDIT_PHONE_NUMBER_SEND_OTP.key,
                        mapOf("newPhone" to newPhoneInput)
                    )
                } else {
                    inputMobile.error = "شماره موبایل صحیح نیست"
                }
            }
            btnVerify.setOnClickListener {
                val code = inputVerifyCode.text.toString()
                val hash = model.data?.get("editMobileHash")
                if (code.length >= 4) {
                    host.dispatchFormAction(
                        ServiceNameEnum.EDIT_PHONE_NUMBER_VERIFY_OTP.key,
                        mapOf(
                            "newPhone" to (newPhoneNumber ?: ""),
                            "code" to code,
                            "editMobileHash" to (hash ?: "")
                        )
                    )
                } else {
                    inputVerifyCode.error = "کد تایید صحیح نیست"
                }
            }
            btnEditNumber.setOnClickListener {
                host.dispatchFormAction(ServiceNameEnum.EDIT_PHONE_NUMBER_GET.key, emptyMap())
            }
        }
    }

    private fun updateStepIndicators(binding: ItemChatBotEditMobileBinding, step: Int) {
        binding.apply {
            when (step) {
                1 -> {
                    step1.setBackgroundResource(R.drawable.shape_circle_blue)
                    step2.setBackgroundResource(R.drawable.shape_circle_grey)
                    step3.setBackgroundResource(R.drawable.shape_circle_grey)
                }
                2 -> {
                    step1.setBackgroundResource(R.drawable.shape_circle_green)
                    step2.setBackgroundResource(R.drawable.shape_circle_blue)
                    step3.setBackgroundResource(R.drawable.shape_circle_grey)
                }
                3 -> {
                    step1.setBackgroundResource(R.drawable.shape_circle_green)
                    step2.setBackgroundResource(R.drawable.shape_circle_green)
                    step3.setBackgroundResource(R.drawable.shape_circle_green)
                }
            }
        }
    }

    private fun startTimer(duration: Long, binding: ItemChatBotEditMobileBinding, host: FormHost) {
        countDownTimer?.cancel()
        if (duration <= 0) {
            binding.tvCountDown.text = host.hostContext.getString(R.string.reset_timer_value)
            return
        }
        countDownTimer = object : CountDownTimer(duration, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.tvCountDown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvCountDown.text = host.hostContext.getString(R.string.reset_timer_value)
            }
        }.start()
    }
}
