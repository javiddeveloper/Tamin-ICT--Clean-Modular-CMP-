package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.os.Build
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.ItemChatBotEditBankAccountBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class EditBankAccountBusinessGenerator : BusinessViewGenerator {

    private var editBankBinding: ItemChatBotEditBankAccountBinding? = null
    private var dateWatcher: TextWatcher? = null
    private var selectedBankId: String = ""
    private var selectedAccountTypeId: String = ""

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals("EDIT_BANK_ACCOUNT", true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        if (editBankBinding == null) {
            host.formContainer.removeAllViews()
        }
        val binding = ensureBinding(host)
        setupState(host, binding, schema, message)
    }

    override fun cleanup() {
        dateWatcher?.let { watcher ->
            editBankBinding?.inputStartDate?.removeTextChangedListener(watcher)
        }
        dateWatcher = null
        editBankBinding = null
        selectedBankId = ""
        selectedAccountTypeId = ""
    }

    private fun ensureBinding(host: FormHost): ItemChatBotEditBankAccountBinding {
        editBankBinding?.let { return it }
        val inflater = LayoutInflater.from(host.hostContext)
        return ItemChatBotEditBankAccountBinding.inflate(inflater, host.formContainer, false).also {
            host.formContainer.removeAllViews()
            host.formContainer.addView(it.root)
            editBankBinding = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupState(
        host: FormHost,
        binding: ItemChatBotEditBankAccountBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        val step = schema.currentStep
        binding.apply {
            groupStep1.gone()
            groupStep2.gone()
            progressBar.gone()
            updateStepIndicators(this, step)
            tvTitle.text = "ویرایش شماره حساب"

            if (step == 1) {
                setupStep1(host, this, schema, model)
            } else if (step == 2) {
                setupStep2(this, schema)
            }

            btnCancel.setOnClickListener {
                host.dispatchFormAction(ServiceNameEnum.EDIT_BANK_ACCOUNT_CANCEL.key, emptyMap())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep1(
        host: FormHost,
        binding: ItemChatBotEditBankAccountBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep1.visible()
            btnSubmit.isEnabled = !schema.isLoading
            if (schema.isLoading) progressBar.visible() else progressBar.gone()

            val (bankList, accountTypeList) = resolveLists(schema)

            inputBankName.setOnClickListener { view ->
                showPopupMenu(view, bankList) { selected ->
                    inputBankName.text = selected.title
                    selectedBankId = selected.id ?: ""
                    inputBankName.error = null
                }
            }
            inputAccountType.setOnClickListener { view ->
                showPopupMenu(view, accountTypeList) { selected ->
                    inputAccountType.text = selected.title
                    selectedAccountTypeId = selected.id ?: ""
                    inputAccountType.error = null
                }
            }

            attachDateWatcher(binding)

            btnSubmit.setOnClickListener {
                if (validateAndSubmit(host, binding)) return@setOnClickListener
            }

            prefillFields(binding, model, bankList, accountTypeList)

            if (!schema.errorMessage.isNullOrEmpty()) {
                tvMessage.text = schema.errorMessage
                tvMessage.setTextColor(root.context.getColor(R.color.red_recycler_color_icon))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep2(
        binding: ItemChatBotEditBankAccountBinding,
        schema: FormSchema
    ) {
        binding.apply {
            groupStep2.visible()
            val stepModel = schema.steps.getOrNull(schema.currentStep - 1)
            tvResultTitle.gone()

            tvMessage.text = stepModel?.message ?: schema.message ?: "عملیات با موفقیت انجام شد"
            val bottomMessage = stepModel?.bottomMessage ?: schema.bottomMessage
            if (!bottomMessage.isNullOrEmpty()) {
                tvBottomMessage.text = bottomMessage
                tvBottomMessage.visible()
            } else {
                tvBottomMessage.gone()
            }
            val errorMsg = schema.errorMessage
            if (!errorMsg.isNullOrEmpty() && errorMsg != "عملیات با موفقیت لغو شد.") {
                imgStatus.setImageResource(R.drawable.ic_close_circle_red)
                tvMessage.setTextColor(root.context.getColor(R.color.red_recycler_color_icon))
            } else {
                if (errorMsg == "عملیات با موفقیت لغو شد.") {
                    imgStatus.setImageResource(R.drawable.ic_close_circle_red)
                    tvMessage.setTextColor(root.context.getColor(R.color.grey_dark_workshop_edited))
                } else {
                    imgStatus.setImageResource(R.drawable.ic_check_circle_green)
                    tvMessage.setTextColor(root.context.getColor(R.color.green_dark))
                }
            }
            btnCancel.gone()
        }
    }

    private fun resolveLists(schema: FormSchema): Pair<List<MenuModel>, List<MenuModel>> {
        var bankList: List<MenuModel> = emptyList()
        var accountTypeList: List<MenuModel> = emptyList()

        schema.steps.flatMap { it.fields }.find { it.id == "bankId" }?.let { field ->
            bankList = field.options.map { MenuModel(title = it.title, id = it.id) }
        }
        schema.steps.flatMap { it.fields }.find { it.id == "accountTypeId" }?.let { field ->
            accountTypeList = field.options.map { MenuModel(title = it.title, id = it.id) }
        }

        return bankList to accountTypeList
    }

    private fun attachDateWatcher(binding: ItemChatBotEditBankAccountBinding) {
        dateWatcher?.let { binding.inputStartDate.removeTextChangedListener(it) }
        dateWatcher = DateFormatWatcher(binding.inputStartDate)
        binding.inputStartDate.addTextChangedListener(dateWatcher)
    }

    private fun validateAndSubmit(
        host: FormHost,
        binding: ItemChatBotEditBankAccountBinding
    ): Boolean {
        val accountNumber = binding.inputAccountNumber.text.toString()
        val startDate = binding.inputStartDate.text.toString()
        var hasError = false

        if (selectedBankId.isEmpty()) {
            binding.inputBankName.error = "لطفا بانک را انتخاب کنید"
            hasError = true
        }
        if (selectedAccountTypeId.isEmpty()) {
            binding.inputAccountType.error = "لطفا نوع حساب را انتخاب کنید"
            hasError = true
        }
        if (accountNumber.isEmpty()) {
            binding.inputAccountNumber.error = "شماره حساب را وارد کنید"
            hasError = true
        }
        if (startDate.length != 10) {
            binding.inputStartDate.error = "تاریخ را کامل وارد کنید (مثال: 1402/01/01)"
            hasError = true
        } else {
            try {
                val parts = startDate.split("/")
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                if (month !in 1..12 || day < 1 || day > 31) {
                    binding.inputStartDate.error = "تاریخ نامعتبر است"
                    hasError = true
                }
            } catch (_: Exception) {
                binding.inputStartDate.error = "فرمت تاریخ صحیح نیست"
                hasError = true
            }
        }

        if (!hasError) {
            host.dispatchFormAction(
                ServiceNameEnum.EDIT_BANK_ACCOUNT_SUBMIT.key,
                mapOf(
                    "accountNumber" to accountNumber,
                    "bankId" to selectedBankId,
                    "accountTypeId" to selectedAccountTypeId,
                    "startDate" to startDate
                )
            )
        }
        return hasError
    }

    private fun prefillFields(
        binding: ItemChatBotEditBankAccountBinding,
        model: AiGenerativeModel,
        bankList: List<MenuModel>,
        accountTypeList: List<MenuModel>
    ) {
        if (selectedBankId.isEmpty()) {
            val pBankId = model.data?.get("bankId")
            if (!pBankId.isNullOrEmpty()) {
                selectedBankId = pBankId
                val bank = bankList.find { it.id == pBankId }
                if (bank != null) binding.inputBankName.text = bank.title
            }
        }
        if (selectedAccountTypeId.isEmpty()) {
            val pAccountTypeId = model.data?.get("accountTypeId")
            if (!pAccountTypeId.isNullOrEmpty()) {
                selectedAccountTypeId = pAccountTypeId
                val type = accountTypeList.find { it.id == pAccountTypeId }
                if (type != null) binding.inputAccountType.text = type.title
            }
        }
        if (binding.inputAccountNumber.text.isNullOrEmpty()) {
            val pAccountNumber = model.data?.get("accountNumber")
            if (!pAccountNumber.isNullOrEmpty()) binding.inputAccountNumber.setText(pAccountNumber)
        }
        if (binding.inputStartDate.text.isNullOrEmpty()) {
            val pStartDate = model.data?.get("startDate")
            if (!pStartDate.isNullOrEmpty()) binding.inputStartDate.setText(pStartDate)
        }
    }

    private fun updateStepIndicators(binding: ItemChatBotEditBankAccountBinding, step: Int) {
        binding.apply {
            when (step) {
                1 -> {
                    step1.setBackgroundResource(R.drawable.shape_circle_blue)
                    step2.setBackgroundResource(R.drawable.shape_circle_grey)
                }
                2 -> {
                    step1.setBackgroundResource(R.drawable.shape_circle_green)
                    step2.setBackgroundResource(R.drawable.shape_circle_green)
                }
            }
        }
    }
}
