package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.os.Build
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.ItemChatBotDependentCancellationBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.CancellationReasons
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.RelationEnum
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class DependentCancellationBusinessGenerator : BusinessViewGenerator {

    private var binding: ItemChatBotDependentCancellationBinding? = null
    private var dateWatcher: TextWatcher? = null
    private var selectedDependentId: String = ""
    private var selectedReasonId: String = ""

    private var selectedDependentRelation: String = ""
    private var selectedDependentGender: String = ""


    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals("DEPENDENT_CANCELLATION", true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        if (binding == null) {
            host.formContainer.removeAllViews()
        }
        val currentBinding = ensureBinding(host)
        setupState(host, currentBinding, schema, message)
    }

    override fun cleanup() {
        dateWatcher?.let { watcher ->
            binding?.inputDate?.removeTextChangedListener(watcher)
        }
        dateWatcher = null
        binding = null
        selectedDependentId = ""
        selectedReasonId = ""
        selectedDependentRelation = ""
        selectedDependentGender = ""
    }

    private fun ensureBinding(host: FormHost): ItemChatBotDependentCancellationBinding {
        binding?.let { return it }
        val inflater = LayoutInflater.from(host.hostContext)
        return ItemChatBotDependentCancellationBinding.inflate(inflater, host.formContainer, false)
            .also {
                host.formContainer.removeAllViews()
                host.formContainer.addView(it.root)
                binding = it
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupState(
        host: FormHost,
        binding: ItemChatBotDependentCancellationBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        val step = schema.currentStep
        binding.apply {
            groupStep1.gone()
            groupStep2.gone()
            groupStep3.gone()
            progressBar.gone()
            updateStepIndicators(this, step)

            when (step) {
                1 -> setupStep1(host, this, schema, model)
                2 -> setupStep2(host, this, schema, model)
                3 -> setupStep3(this, schema)
            }

            btnCancel.setOnClickListener {
                host.dispatchFormAction(
                    ServiceNameEnum.DEPENDENT_CANCELLATION_CANCEL.key,
                    emptyMap()
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep1(
        host: FormHost,
        binding: ItemChatBotDependentCancellationBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep1.visible()
            if (schema.isLoading) progressBar.visible() else progressBar.gone()

            val dependentList = resolveOptions(schema, "dependentId")


            inputDependent.setOnClickListener { view ->
                showPopupMenu(view, dependentList) { selected ->
                    inputDependent.text = selected.title
                    selectedDependentId = selected.id ?: ""
                    val extras = selected.baseModel as? Map<String, Any?>
                    selectedDependentRelation = extras?.get("relation")?.toString() ?: ""
                    selectedDependentGender = extras?.get("gender")?.toString() ?: ""
                    inputDependent.error = null
                }
            }

            btnNextStep1.setOnClickListener {
                if (selectedDependentId.isEmpty()) {
                    inputDependent.error = "لطفا فرد مورد نظر را انتخاب کنید"
                    return@setOnClickListener
                }
                val updatedData = model.data?.toMutableMap() ?: mutableMapOf()
                updatedData["dependentId"] = selectedDependentId
                updatedData["nationalCode"] = selectedDependentId
                updatedData["relation"] = selectedDependentRelation
                updatedData["gender"] = selectedDependentGender
                host.dispatchFormAction(
                    ServiceNameEnum.DEPENDENT_CANCELLATION_CONFIRM.key,
                    updatedData.filterKeys { it != null }.map { it.key!! to it.value }.toMap()
                )
            }

            prefillStep1(binding, model, dependentList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep2(
        host: FormHost,
        binding: ItemChatBotDependentCancellationBinding,
        schema: FormSchema,
        model: AiGenerativeModel
    ) {
        binding.apply {
            groupStep2.visible()
            if (schema.isLoading) progressBar.visible() else progressBar.gone()

            if (selectedDependentRelation.isEmpty()) {
                selectedDependentRelation = model.data?.get("relation") ?: ""
            }
            if (selectedDependentGender.isEmpty()) {
                selectedDependentGender = model.data?.get("gender") ?: ""
            }

            val relation = RelationEnum.getRelation(
                relationCode = selectedDependentRelation,
                genderCode = selectedDependentGender
            )

            val reasonList = getReasonsBasedOnRelationAndGender(relation)

            val reasonListModelMenu = reasonList.map { MenuModel(id = it.reason, title = it.title) }

            inputReason.setOnClickListener { view ->
                showPopupMenu(view, reasonListModelMenu) { selected ->
                    inputReason.text = selected.title
                    selectedReasonId = selected.id ?: ""
                    inputReason.error = null
                }
            }

            attachDateWatcher(this)

            btnSubmit.setOnClickListener {
                validateAndSubmit(host, this)
            }

            prefillStep2(binding, model, reasonListModelMenu)
        }
    }

    private fun getReasonsBasedOnRelationAndGender(relation: RelationEnum): List<CancellationReasons> {
        return when (relation) {
            RelationEnum.FATHER -> CancellationReasons.getReasonForParent()
            RelationEnum.MOTHER -> CancellationReasons.getReasonForParent()
            RelationEnum.DAUGHTER -> CancellationReasons.getReasonsForDAUGHTER()
            RelationEnum.PARENT -> CancellationReasons.getReasonForParent()
            RelationEnum.SON -> CancellationReasons.getReasonsForSON()
            RelationEnum.SISTER -> CancellationReasons.getReasonForSibilings()
            RelationEnum.BROTHER -> CancellationReasons.getReasonForSibilings()
            RelationEnum.GOD_CHILD -> CancellationReasons.getReasonsForChild()
            RelationEnum.CHILD -> CancellationReasons.getReasonsForChild()
            RelationEnum.REMAINED -> CancellationReasons.getAllReasons()
            RelationEnum.WIFE -> CancellationReasons.getReasonsForWifeOrHusband()
            RelationEnum.EMPTY -> CancellationReasons.getAllReasons()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStep3(
        binding: ItemChatBotDependentCancellationBinding,
        schema: FormSchema
    ) {
        binding.apply {
            groupStep3.visible()
            val stepModel = schema.steps.getOrNull(schema.currentStep - 1)
            tvResultTitle.text = root.context.getString(R.string.title_dependent_cancellation)

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
                imgStatus.setImageResource(R.drawable.ic_check_circle_green)
                tvMessage.setTextColor(root.context.getColor(R.color.green_dark))
            }
            btnCancel.gone()
        }
    }

    private fun resolveOptions(schema: FormSchema, fieldId: String): List<MenuModel> {
        return schema.steps.flatMap { it.fields }
            .find { it.id == fieldId }?.options
            ?.map { MenuModel(title = it.title, id = it.id, baseModel = it.extras) } ?: emptyList()
    }

    private fun attachDateWatcher(binding: ItemChatBotDependentCancellationBinding) {
        dateWatcher?.let { binding.inputDate.removeTextChangedListener(it) }
        dateWatcher = DateFormatWatcher(binding.inputDate)
        binding.inputDate.addTextChangedListener(dateWatcher)
    }

    private fun validateAndSubmit(
        host: FormHost,
        binding: ItemChatBotDependentCancellationBinding
    ) {
        val date = binding.inputDate.text.toString()
        var hasError = false

        if (selectedReasonId.isEmpty()) {
            binding.inputReason.error = "لطفا علت ابطال را انتخاب کنید"
            hasError = true
        }
        if (date.length != 10) {
            binding.inputDate.error = "تاریخ را کامل وارد کنید (مثال: 1402/01/01)"
            hasError = true
        }
        if (!binding.cbApproval.isChecked) {
            hasError = true
            // Optionally show a toast or message for checkbox
        }

        if (!hasError) {
            val updatedData = host.currentData?.toMutableMap() ?: mutableMapOf()
            updatedData["dependentId"] = selectedDependentId
            updatedData["nationalCode"] = selectedDependentId
            updatedData["reasonId"] = selectedReasonId
            updatedData["cancellationDate"] = date

            host.dispatchFormAction(
                ServiceNameEnum.DEPENDENT_CANCELLATION_SUBMIT.key,
                updatedData.filterKeys { it != null }.map { it.key!! to it.value }.toMap()
            )
        }
    }

    private fun prefillStep1(
        binding: ItemChatBotDependentCancellationBinding,
        model: AiGenerativeModel,
        dependentList: List<MenuModel>
    ) {
        if (selectedDependentId.isEmpty()) {
            val pId = model.data?.get("dependentId") ?: model.data?.get("nationalCode")
            if (!pId.isNullOrEmpty()) {
                selectedDependentId = pId
                val dep = dependentList.find { it.id == pId }
                if (dep != null) {
                    binding.inputDependent.text = dep.title
                    val extras = dep.baseModel as? Map<String, Any?>
                    selectedDependentRelation = extras?.get("relation")?.toString() ?: ""
                    selectedDependentGender = extras?.get("gender")?.toString() ?: ""
                }
            }
        }
    }

    private fun prefillStep2(
        binding: ItemChatBotDependentCancellationBinding,
        model: AiGenerativeModel,
        reasonList: List<MenuModel>
    ) {
        if (selectedReasonId.isEmpty()) {
            val pId = model.data?.get("reasonId")
            if (!pId.isNullOrEmpty()) {
                selectedReasonId = pId
                val reason = reasonList.find { it.id == pId }
                if (reason != null) binding.inputReason.text = reason.title
            }
        }
        if (binding.inputDate.text.isNullOrEmpty()) {
            val pDate = model.data?.get("cancellationDate")
            if (!pDate.isNullOrEmpty()) binding.inputDate.setText(pDate)
        }
    }

    private fun updateStepIndicators(binding: ItemChatBotDependentCancellationBinding, step: Int) {
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
