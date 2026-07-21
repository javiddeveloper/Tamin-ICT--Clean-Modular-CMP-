package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.ItemChatBotFuneralAllowanceBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class FuneralAllowanceBusinessGenerator : BusinessViewGenerator {

    private var binding: ItemChatBotFuneralAllowanceBinding? = null
    private var lastBoundMessageId: String? = null

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals(ServiceNameEnum.FUNERAL_ALLOWANCE_GET.key, ignoreCase = true) ||
                schema.key.equals(ServiceNameEnum.FUNERAL_ALLOWANCE_VALIDATE.key, ignoreCase = true) ||
                schema.key.equals(ServiceNameEnum.FUNERAL_ALLOWANCE_SAVE.key, ignoreCase = true) ||
                schema.key.equals(ServiceNameEnum.FUNERAL_ALLOWANCE_CONFIRM.key, ignoreCase = true) ||
                schema.key.equals(ServiceNameEnum.FUNERAL_ALLOWANCE_CANCEL.key, ignoreCase = true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        if (binding == null) {
            host.formContainer.removeAllViews()
        }

        val currentBinding = ensureBinding(host)
        lastBoundMessageId = message.id
        setupState(host, currentBinding, schema, message)
    }

    override fun cleanup() {
        binding = null
        lastBoundMessageId = null
    }

    private fun ensureBinding(host: FormHost): ItemChatBotFuneralAllowanceBinding {
        binding?.let { return it }
        val inflater = LayoutInflater.from(host.hostContext)
        return ItemChatBotFuneralAllowanceBinding.inflate(inflater, host.formContainer, false).also {
            host.formContainer.removeAllViews()
            host.formContainer.addView(it.root)
            binding = it
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupState(
        host: FormHost,
        binding: ItemChatBotFuneralAllowanceBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        val step = schema.currentStep

        binding.apply {
            groupStep1.gone()
            groupStep2.gone()
            groupStep3.gone()
            progressBar.gone()
            btnEditAccount.gone()
            btnConfirmEdit.gone()
            btnCancel.visible()
            
            updateStepIndicators(this, step)

            when (step) {
                1 -> {
                    groupStep1.visible()
                    btnValidate.isEnabled = !schema.isLoading
                    if (schema.isLoading) progressBar.visible() else progressBar.gone()
                }
                2 -> {
                    groupStep2.visible()
                    val fullName = model.data?.get("deceasedFullName")
                    val relation = model.data?.get("deceasedRelation")
                    
                    if (fullName != null && relation != null) {
                        val context = tvDeceasedInfo.context
                        val nameLabel = context.getString(R.string.full_name_deceased)
                        val relationLabel = context.getString(R.string.deceased_relative)
                        tvDeceasedInfo.text = "$nameLabel: $fullName\n$relationLabel: $relation"
                    } else {
                        val deceasedInfo = model.data?.get("deceasedInfo") ?: "اطلاعات متوفی تایید شد."
                        tvDeceasedInfo.text = deceasedInfo
                    }
                    btnSubmitRequest.isEnabled = !schema.isLoading
                    if (schema.isLoading) progressBar.visible() else progressBar.gone()
                }
                3 -> {
                    groupStep3.visible()
                    btnCancel.gone()
                    val stepModel = schema.steps.getOrNull(step - 1)
                    tvMessage.text = stepModel?.message ?: schema.message ?: "عملیات با موفقیت انجام شد"
                    
                    if (!schema.errorMessage.isNullOrEmpty()) {
                        imgStatus.setImageResource(R.drawable.ic_close_circle_red)
                        tvMessage.setTextColor(root.context.getColor(R.color.red_recycler_color_icon))
                        tvMessage.text = schema.errorMessage
                        
                        // Show edit account if error is related to bank account
                        if (schema.errorMessage.contains("شماره حساب") && !schema.errorMessage.contains("منوی حساب کاربری")) {
                            btnEditAccount.visible()
                            btnConfirmEdit.visible()
                        }
                    } else {
                        imgStatus.setImageResource(R.drawable.ic_check_circle_green)
                        tvMessage.setTextColor(root.context.getColor(R.color.green_dark))
                    }
                }
            }

            if (!schema.errorMessage.isNullOrEmpty() && step == 1) {
                inputNationalCode.error = schema.errorMessage
            } else {
                inputNationalCode.error = null
            }

            btnCancel.setOnClickListener {
                host.dispatchFormAction(ServiceNameEnum.FUNERAL_ALLOWANCE_CANCEL.key, emptyMap())
            }

            btnValidate.setOnClickListener {
                val nationalCode = inputNationalCode.text.toString()
                if (nationalCode.length == 10) {
                    host.dispatchFormAction(
                        ServiceNameEnum.FUNERAL_ALLOWANCE_VALIDATE.key,
                        mapOf("nationalCode" to nationalCode)
                    )
                } else {
                    inputNationalCode.error = "کد ملی صحیح نیست"
                }
            }

            btnSubmitRequest.setOnClickListener {
                host.dispatchFormAction(
                    ServiceNameEnum.FUNERAL_ALLOWANCE_SAVE.key,
                    mapOf("nationalCode" to inputNationalCode.text.toString())
                )
            }
            
            btnEditAccount.setOnClickListener {
                // Redirect to edit bank account scenario
                host.dispatchFormAction(ServiceNameEnum.EDIT_BANK_ACCOUNT_GET.key, emptyMap())
            }

            btnConfirmEdit.setOnClickListener {
                val requestId = model.data?.get("requestId") ?: ""
                host.dispatchFormAction(
                    ServiceNameEnum.FUNERAL_ALLOWANCE_CONFIRM.key,
                    mapOf("requestId" to requestId)
                )
            }
        }
    }

    private fun updateStepIndicators(binding: ItemChatBotFuneralAllowanceBinding, step: Int) {
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
}
