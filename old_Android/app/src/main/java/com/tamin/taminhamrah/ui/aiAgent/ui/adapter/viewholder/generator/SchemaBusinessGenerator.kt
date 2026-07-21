package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep

class SchemaBusinessGenerator(
    providedPluginRegistry: FormGeneratorPluginRegistry? = null
) : BusinessViewGenerator {
    private var pluginRegistry: FormGeneratorPluginRegistry? = providedPluginRegistry

    private var activeSchemaGenerator: BusinessViewGenerator? = null

    private var formLayout: LinearLayout? = null
    private var statusTextView: AppCompatTextView? = null
    private var timerTextView: AppCompatTextView? = null
    private var countDownTimer: CountDownTimer? = null
    private val fieldViews = mutableMapOf<String, Any>()
    private val fieldDefinitions = mutableMapOf<String, FormField>()
    private val fieldValues = mutableMapOf<String, String>()
    private val actionButtons = mutableMapOf<String, MaterialButton>()
    private val fieldErrorViews = mutableMapOf<String, TextView>()
    private var boundMessageId: String? = null
    private var currentSchemaKey: String? = null
    private var currentStepIndex: Int? = null
    private var cachedTypeface: Typeface? = null
    private var typefaceResolved = false
    private var lastPayload: Map<String?, String?>? = null
    private var lastIsLoading: Boolean = false

    /**
     * True only while [buildForm] is tearing down/recreating the view tree. Used to
     * suppress focus-change callbacks fired by removeAllViews() from dispatching
     * updateFormData — otherwise a rebuild detaches the focused EditText, which pushes
     * form data, which re-emits and rebuilds again, looping forever.
     */
    private var isBuilding: Boolean = false

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        val registry = resolvePluginRegistry(host)
        val schemaGenerator = registry.findSchemaGenerator(schema, message)
        if (schemaGenerator != null) {
            if (activeSchemaGenerator != schemaGenerator) {
                cleanupGenericState()
                activeSchemaGenerator?.cleanup()
                activeSchemaGenerator = schemaGenerator
            }
            schemaGenerator.bind(host, schema, message)
            return
        } else if (activeSchemaGenerator != null) {
            activeSchemaGenerator?.cleanup()
            activeSchemaGenerator = null
        }

        resolveTypeface(host)
        val step = schema.steps.firstOrNull { it.index == schema.currentStep }
            ?: schema.steps.firstOrNull()
        if (step == null) {
            host.showFallback(schema.message ?: schema.errorMessage ?: schema.key)
            return
        }
        val fileFieldsChanged = step.fields.any { field ->
            field.type == FormFieldType.FILE_UPLOAD &&
            message.data?.get(field.id) != lastPayload?.get(field.id)
        }
        val shouldRebuild = boundMessageId != message.id ||
            currentSchemaKey != schema.key ||
            currentStepIndex != step.index ||
            lastIsLoading != schema.isLoading ||
            fileFieldsChanged
        if (shouldRebuild) {
            isBuilding = true
            try {
                buildForm(host, schema, step, message)
            } finally {
                isBuilding = false
            }
        } else {
            updateStatus(schema, step)
            updateLoading(schema)
        }
    }

    override fun cleanup() {
        pluginRegistry?.cleanup()
        activeSchemaGenerator = null
        cleanupGenericState()
    }

    private fun resolvePluginRegistry(host: FormHost): FormGeneratorPluginRegistry {
        pluginRegistry?.let { return it }
        val schemaGenerators = FormChartDetailRendererProvider.getSchemaGenerators(host.hostContext)
        val renderers = FormChartDetailRendererProvider.getChartDetailRenderers(host.hostContext)
        return DefaultFormGeneratorPluginRegistry(
            schemaGenerators = schemaGenerators,
            chartDetailRenderers = renderers
        ).also { pluginRegistry = it }
    }

    private fun cleanupGenericState() {
        countDownTimer?.cancel()
        countDownTimer = null
        formLayout = null
        statusTextView = null
        timerTextView = null
        fieldViews.clear()
        fieldDefinitions.clear()
        fieldValues.clear()
        actionButtons.clear()
        fieldErrorViews.clear()
        boundMessageId = null
        currentSchemaKey = null
        currentStepIndex = null
        lastPayload = null
        lastIsLoading = false
    }

    private fun resolveTypeface(host: FormHost) {
        if (typefaceResolved) return
        typefaceResolved = true
        cachedTypeface = try {
            ResourcesCompat.getFont(host.hostContext, R.font.iran_sans_mobile_fa_num)
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun buildForm(
        host: FormHost,
        schema: FormSchema,
        step: FormStep,
        message: AiGenerativeModel
    ) {
        val previousValues = fieldValues.toMap()
        host.formContainer.removeAllViews()
        resetFormState()
        boundMessageId = message.id
        currentSchemaKey = schema.key
        currentStepIndex = step.index
        lastIsLoading = schema.isLoading
        lastPayload = message.data

        // Restore values
        previousValues.forEach { (k, v) ->
            fieldValues[k] = v
        }

        val ctx = host.hostContext
        val font = cachedTypeface

        val mainContainer = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(host.dp(16), host.dp(8), host.dp(16), host.dp(8))
            }
            setPadding(host.dp(16), host.dp(16), host.dp(16), host.dp(16))
            setBackgroundResource(R.drawable.bg_chat_bot_clickable)
        }
        formLayout = mainContainer
        host.formContainer.addView(mainContainer)

        buildHeader(host, mainContainer, schema, step, font)

        if (schema.isLoading) {
            mainContainer.addView(ProgressBar(ctx).apply {
                isIndeterminate = true
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = host.dp(8)
                    bottomMargin = host.dp(8)
                }
            })
        }

        if (isResultStep(schema, step)) {
            addResultView(host, mainContainer, schema, step)
            return
        }

        step.fields.forEach { field ->
            fieldDefinitions[field.id] = field
            fieldValues[field.id] = field.value.orEmpty()
            when (field.type) {
                FormFieldType.HIDDEN -> Unit
                FormFieldType.READ_ONLY -> addReadOnlyField(host, mainContainer, field)
                FormFieldType.DROPDOWN -> addDropdownField(host, mainContainer, field)
                FormFieldType.CHART -> addChartField(host, schema, mainContainer, field)
                FormFieldType.FILE_UPLOAD -> addFileUploadField(host, mainContainer, field)
                else -> addTextField(host, mainContainer, field)
            }
        }

        buildTimer(host, mainContainer, message, font)
        buildStatusView(host, mainContainer, schema, step, font)
        buildActions(host, mainContainer, schema, step, font)
        updateLoading(schema)
    }

    private fun resetFormState() {
        countDownTimer?.cancel()
        countDownTimer = null
        formLayout = null
        statusTextView = null
        timerTextView = null
        fieldViews.clear()
        fieldDefinitions.clear()
        fieldValues.clear()
        actionButtons.clear()
        fieldErrorViews.clear()
    }

    private fun buildHeader(
        host: FormHost,
        container: LinearLayout,
        schema: FormSchema,
        step: FormStep,
        font: Typeface?
    ) {
        val ctx = host.hostContext
        val headerLayout = RelativeLayout(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        container.addView(headerLayout)

        if (schema.showCancelButton && !isResultStep(schema, step)) {
            headerLayout.addView(createCancelButton(host, schema, step))
        }

        var belowHeaderId = 0
        if (schema.steps.size > 1) {
            val stepsLayout = createStepsLayout(host, schema)
            headerLayout.addView(stepsLayout)
            belowHeaderId = stepsLayout.id
        }

        step.title?.let { title ->
            val titleView = AppCompatTextView(ctx).apply {
                text = title
                typeface = font
                setTextColor(ContextCompat.getColor(ctx, R.color.black))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(0, host.dp(8), 0, host.dp(8))
                layoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                    if (belowHeaderId != 0) addRule(RelativeLayout.BELOW, belowHeaderId)
                }
            }
            if (schema.steps.size > 1) {
                headerLayout.removeView(headerLayout.findViewById(belowHeaderId))
                titleView.id = View.generateViewId()
                headerLayout.addView(titleView)
                val stepsView = createStepsLayout(host, schema)
                stepsView.layoutParams = (stepsView.layoutParams as RelativeLayout.LayoutParams).apply {
                    addRule(RelativeLayout.BELOW, titleView.id)
                    topMargin = host.dp(8)
                }
                headerLayout.addView(stepsView)
            } else {
                headerLayout.addView(titleView)
            }
        }
    }

    private fun createCancelButton(host: FormHost, schema: FormSchema, step: FormStep): AppCompatImageView {
        val ctx = host.hostContext
        return AppCompatImageView(ctx).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(host.dp(24), host.dp(24)).apply {
                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            setImageResource(R.drawable.ic_close_outline)
            imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.material_grey_500)
            )
            setPadding(host.dp(4), host.dp(4), host.dp(4), host.dp(4))
            background = with(TypedValue()) {
                context.theme.resolveAttribute(
                    android.R.attr.selectableItemBackgroundBorderless, this, true
                )
                ContextCompat.getDrawable(context, resourceId)
            }
            setOnClickListener {
                val cancelActionId = step.actions.firstOrNull { it.id.contains("cancel", true) }?.id
                    ?: schema.steps.flatMap { it.actions }.firstOrNull { it.id.contains("cancel", true) }?.id
                cancelActionId?.let { host.dispatchFormAction(it, emptyMap()) }
            }
        }
    }

    private fun createStepsLayout(host: FormHost, schema: FormSchema): LinearLayout {
        return LinearLayout(host.hostContext).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
        }.also { stepsLayout ->
            schema.steps.map { it.index }.sortedDescending().forEachIndexed { index, stepIndex ->
                stepsLayout.addView(View(host.hostContext).apply {
                    layoutParams = ViewGroup.LayoutParams(host.dp(12), host.dp(12))
                    background = when {
                        stepIndex < schema.currentStep -> ContextCompat.getDrawable(host.hostContext, R.drawable.shape_circle_green)
                        stepIndex == schema.currentStep -> ContextCompat.getDrawable(host.hostContext, R.drawable.shape_circle_blue)
                        else -> ContextCompat.getDrawable(host.hostContext, R.drawable.shape_circle_grey)
                    }
                })
                if (index != schema.steps.size - 1) {
                    stepsLayout.addView(View(host.hostContext).apply {
                        layoutParams = ViewGroup.MarginLayoutParams(host.dp(20), host.dp(1)).apply {
                            marginStart = host.dp(4)
                            marginEnd = host.dp(4)
                        }
                        setBackgroundColor(
                            ContextCompat.getColor(host.hostContext, R.color.grey_color_text_button_bordered)
                        )
                    })
                }
            }
        }
    }

    private fun isResultStep(schema: FormSchema, step: FormStep): Boolean {
        if (step.fields.isNotEmpty() || step.actions.isNotEmpty()) return false
        return !schema.errorMessage.isNullOrEmpty() ||
            !schema.message.isNullOrEmpty() ||
            !step.message.isNullOrEmpty()
    }

    private fun addResultView(
        host: FormHost,
        layout: LinearLayout,
        schema: FormSchema,
        step: FormStep
    ) {
        val ctx = host.hostContext
        val isError = !schema.errorMessage.isNullOrEmpty()

        val resultContainer = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        resultContainer.addView(AppCompatImageView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(host.dp(64), host.dp(64))
            setImageResource(
                if (isError) R.drawable.ic_close_circle_red else R.drawable.ic_check_circle_green
            )
        })

        resultContainer.addView(AppCompatTextView(ctx).apply {
            val text = schema.errorMessage ?: schema.message ?: step.message
            this.text = text
            typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
            textSize = 16f
            setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isError) R.color.red_recycler_color_icon else R.color.green_dark
                )
            )
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(16) }
        })

        layout.addView(resultContainer)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addReadOnlyField(host: FormHost, layout: LinearLayout, field: FormField) {
        val ctx = host.hostContext
        layout.addView(AppCompatTextView(ctx).apply {
            text = field.label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
            setPadding(host.dp(16), host.dp(8), host.dp(16), host.dp(2))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })
        layout.addView(AppCompatTextView(ctx).apply {
            text = field.value.orEmpty()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            setPadding(host.dp(16), host.dp(2), host.dp(16), host.dp(8))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })
    }

    private fun addTextField(host: FormHost, layout: LinearLayout, field: FormField) {
        val ctx = host.hostContext
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(12) }
        }

        container.addView(AppCompatTextView(ctx).apply {
            text = field.label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })

        val errorView = AppCompatTextView(ctx).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.red_recycler_color_icon))
            visibility = View.GONE
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(4) }
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }

        val initialValue = fieldValues[field.id]?.takeIf { it.isNotEmpty() }
            ?: field.value?.takeIf { it.isNotEmpty() }
            ?: host.currentData?.get(field.id).orEmpty()
        // Seed the value map so validation reflects what is displayed even when the user
        // does not re-type after a rebuild: setText() below runs before the TextWatcher is
        // attached, so the watcher alone would leave fieldValues empty for restored values.
        fieldValues[field.id] = initialValue
        val editText = AppCompatEditText(ctx).apply {
            setText(initialValue)
            isEnabled = field.enabled
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setBackgroundResource(R.drawable.bg_edit_text_border)
            setPadding(host.dp(10), host.dp(10), host.dp(10), host.dp(10))
            hint = field.hint.orEmpty()
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            inputType = when (field.type) {
                FormFieldType.PHONE -> InputType.TYPE_CLASS_PHONE
                FormFieldType.NUMBER, FormFieldType.OTP -> InputType.TYPE_CLASS_NUMBER
                FormFieldType.DATE, FormFieldType.TIME -> InputType.TYPE_CLASS_DATETIME
                else -> InputType.TYPE_CLASS_TEXT
            }
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(4) }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                fieldValues[field.id] = s?.toString().orEmpty()
                errorView.text = ""
                errorView.visibility = View.GONE
            }
        })
        if (field.type == FormFieldType.DATE) {
            editText.addTextChangedListener(DateFormatWatcher(editText))
        } else if (field.type == FormFieldType.TIME) {
            editText.addTextChangedListener(TimeFormatWatcher(editText))
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isBuilding) {
                if (field.type == FormFieldType.DATE) {
                    val value = editText.text?.toString().orEmpty()
                    if (value.isNotBlank()) {
                        val dateError = validateJalaliDate(field.id, value)
                        if (dateError != null) {
                            errorView.text = dateError
                            errorView.visibility = View.VISIBLE
                        } else {
                            errorView.text = ""
                            errorView.visibility = View.GONE
                        }
                    }
                }
                val updatedData = (host.currentData?.toMutableMap() ?: mutableMapOf()).apply {
                    putAll(fieldValues)
                }
                host.updateFormData(updatedData.mapKeys { it.key.orEmpty() })
            }
        }

        container.addView(editText)
        container.addView(errorView)
        layout.addView(container)
        fieldViews[field.id] = editText
        fieldErrorViews[field.id] = errorView
    }

    private fun addDropdownField(host: FormHost, layout: LinearLayout, field: FormField) {
        val ctx = host.hostContext
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(12) }
        }

        container.addView(AppCompatTextView(ctx).apply {
            text = field.label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })

        val errorView = AppCompatTextView(ctx).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.red_recycler_color_icon))
            visibility = View.GONE
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(4) }
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }

        val input = AppCompatTextView(ctx).apply {
            isEnabled = field.enabled
            isFocusable = false
            isClickable = true
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            setBackgroundResource(R.drawable.bg_edit_text_border)
            setPadding(host.dp(10), host.dp(10), host.dp(10), host.dp(10))
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_down, 0, 0, 0)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(4) }
        }

        val options = field.options

        var initialId = fieldValues[field.id].orEmpty()
        if (initialId.isEmpty()) {
            initialId = field.value.orEmpty()
        }
        if (initialId.isEmpty()) {
            initialId = host.currentData?.get(field.id) ?: ""
        }
        val initialOption = options.firstOrNull { it.id == initialId }
        if (initialOption != null) {
            input.text = initialOption.title
            fieldValues[field.id] = initialOption.id
        } else {
            input.hint = "انتخاب کنید"
        }

        input.setOnClickListener { view ->
            val menuItems = options.mapIndexed { index, option ->
                MenuModel(title = option.title, id = option.id, tag = index)
            }
            showPopupMenu(view, menuItems) { selected ->
                input.text = selected.title
                fieldValues[field.id] = selected.id ?: ""
                errorView.text = ""
                errorView.visibility = View.GONE
                val updatedData = (host.currentData?.toMutableMap() ?: mutableMapOf()).apply {
                    putAll(fieldValues)
                    put(field.id, selected.id ?: "")
                }
                host.updateFormData(updatedData.mapKeys { it.key.orEmpty() })
            }
        }

        container.addView(input)
        container.addView(errorView)
        layout.addView(container)
        fieldViews[field.id] = input
        fieldErrorViews[field.id] = errorView
    }

    private fun addFileUploadField(host: FormHost, layout: LinearLayout, field: FormField) {
        val ctx = host.hostContext
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(12) }
        }

        container.addView(AppCompatTextView(ctx).apply {
            text = field.label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })

        // Resolve already-uploaded GUIDs from the carried payload (or initial value).
        val storedGuids = (fieldValues[field.id]?.takeIf { it.isNotEmpty() }
            ?: field.value?.takeIf { it.isNotEmpty() }
            ?: host.currentData?.get(field.id)).orEmpty()
        fieldValues[field.id] = storedGuids

        // Premium upload card
        val uploadCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.bg_edit_text_border)
            setPadding(host.dp(16), host.dp(16), host.dp(16), host.dp(16))
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(8) }
            isClickable = field.enabled
            isEnabled = field.enabled
        }

        val uploadIcon = AppCompatImageView(ctx).apply {
            setImageResource(R.drawable.ic_doc)
            imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.material_blue_500))
            layoutParams = LinearLayout.LayoutParams(host.dp(24), host.dp(24)).apply {
                leftMargin = host.dp(12)
            }
        }
        uploadCard.addView(uploadIcon)

        val textContainer = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        textContainer.addView(AppCompatTextView(ctx).apply {
            text = "برای انتخاب و بارگذاری فایل کلیک کنید"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            gravity = Gravity.RIGHT
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })
        textContainer.addView(AppCompatTextView(ctx).apply {
            "فرمت‌های مجاز: JPG, PNG".also { text = it }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
            gravity = Gravity.RIGHT
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        })
        uploadCard.addView(textContainer)

        // Loading indicator inside card
        if (lastIsLoading) {
            val progress = ProgressBar(ctx, null, android.R.attr.progressBarStyleSmall).apply {
                layoutParams = LinearLayout.LayoutParams(host.dp(20), host.dp(20)).apply {
                    rightMargin = host.dp(12)
                }
            }
            uploadCard.addView(progress)
        }

        val errorView = AppCompatTextView(ctx).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(context, R.color.red_recycler_color_icon))
            visibility = View.GONE
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = host.dp(4) }
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }

        uploadCard.setOnClickListener {
            errorView.text = ""
            errorView.visibility = View.GONE
            val updatedData = (host.currentData?.toMutableMap() ?: mutableMapOf()).apply {
                putAll(fieldValues)
            }
            host.updateFormData(updatedData.mapKeys { it.key.orEmpty() })
            host.requestDocumentUpload(field.id)
        }

        container.addView(uploadCard)
        container.addView(errorView)

        // Render list of uploaded documents
        if (storedGuids.isNotEmpty()) {
            val guidsList = storedGuids.split(",").filter { it.isNotBlank() }
            if (guidsList.isNotEmpty()) {
                val listContainer = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = host.dp(8) }
                }

                guidsList.forEachIndexed { index, guid ->
                    val fileRow = LinearLayout(ctx).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        setPadding(host.dp(12), host.dp(8), host.dp(12), host.dp(8))
                        setBackgroundResource(R.drawable.bg_edit_text_border)
                        layoutParams = ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = host.dp(4) }
                    }

                    // Small file icon
                    val docIcon = AppCompatImageView(ctx).apply {
                        setImageResource(R.drawable.ic_doc)
                        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_dark))
                        layoutParams = LinearLayout.LayoutParams(host.dp(18), host.dp(18)).apply {
                            leftMargin = host.dp(8)
                        }
                    }
                    fileRow.addView(docIcon)

                    // Text
                    val fileNameText = AppCompatTextView(ctx).apply {
                        "تصویر بارگذاری شده ${index + 1} (${guid.takeLast(6)})".also { text = it }
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                        gravity = Gravity.RIGHT
                        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    fileRow.addView(fileNameText)

                    // Delete button
                    val deleteBtn = AppCompatImageView(ctx).apply {
                        setImageResource(R.drawable.ic_delete)
                        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_recycler_color_icon))
                        layoutParams = LinearLayout.LayoutParams(host.dp(20), host.dp(20))
                        isClickable = true
                        setOnClickListener {
                            val updatedList = guidsList.toMutableList().apply { removeAt(index) }
                            val newGuidString = updatedList.joinToString(",")
                            fieldValues[field.id] = newGuidString
                            val updatedData = (host.currentData?.toMutableMap() ?: mutableMapOf()).apply {
                                putAll(fieldValues)
                                put(field.id, newGuidString)
                            }
                            host.updateFormData(updatedData.mapKeys { it.key.orEmpty() })
                        }
                    }
                    fileRow.addView(deleteBtn)

                    listContainer.addView(fileRow)
                }
                container.addView(listContainer)
            }
        }

        layout.addView(container)
        fieldViews[field.id] = uploadCard
        fieldErrorViews[field.id] = errorView
    }

    private fun addChartField(host: FormHost, schema: FormSchema, layout: LinearLayout, field: FormField) {
        val items = field.chartItems.ifEmpty {
            val json = host.currentData?.get(field.id) ?: host.currentData?.get("chartData")
            ChartRenderer.parseChartItems(json)
        }

        val chartHandle = ChartRenderer.create(
            context = host.hostContext,
            items = items,
            typeface = cachedTypeface
        )
        pluginRegistry?.applyChartBehavior(host, schema, field, chartHandle)

        chartHandle.view.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = host.dp(12) }
        layout.addView(chartHandle.view)
    }

    private fun buildTimer(
        host: FormHost,
        container: LinearLayout,
        message: AiGenerativeModel,
        font: Typeface?
    ) {
        val targetTime = message.data?.get("targetTime")?.toLongOrNull()
        val expirationDuration = if (targetTime != null) {
            targetTime - System.currentTimeMillis()
        } else {
            message.data?.get("expirationDuration")?.toLongOrNull()
        }
        if (expirationDuration != null && expirationDuration > 0) {
            timerTextView = AppCompatTextView(host.hostContext).apply {
                typeface = font
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(ContextCompat.getColor(context, R.color.material_grey_500))
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = host.dp(8) }
            }
            container.addView(timerTextView)
            startTimer(expirationDuration, host)
        }
    }

    private fun buildStatusView(
        host: FormHost,
        container: LinearLayout,
        schema: FormSchema,
        step: FormStep,
        font: Typeface?
    ) {
        statusTextView = AppCompatTextView(host.hostContext).apply {
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setPadding(0, host.dp(8), 0, host.dp(8))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
        container.addView(statusTextView)
        updateStatus(schema, step)
    }

    private fun buildActions(
        host: FormHost,
        container: LinearLayout,
        schema: FormSchema,
        step: FormStep,
        font: Typeface?
    ) {
        if (step.actions.isEmpty()) return

        val actionsLayout = LinearLayout(host.hostContext).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, host.dp(8), 0, host.dp(8))
        }

        step.actions.forEach { action ->
            val isPrimary = action.style == FormActionStyle.PRIMARY
            val button = MaterialButton(host.hostContext).apply {
                text = action.title
                isEnabled = action.enabled && !schema.isLoading
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = host.dp(8) }
                typeface = font
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                if (isPrimary) {
                    cornerRadius = host.dp(8)
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(host.hostContext, R.color.material_blue_500)
                    )
                    setTextColor(ContextCompat.getColor(host.hostContext, R.color.white))
                } else {
                    setTextColor(ContextCompat.getColor(host.hostContext, R.color.material_blue_500))
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(host.hostContext, android.R.color.transparent)
                    )
                    rippleColor = ColorStateList.valueOf(
                        ContextCompat.getColor(host.hostContext, R.color.material_blue_500)
                    )
                }
                setPadding(host.dp(12), host.dp(12), host.dp(12), host.dp(12))
                setOnClickListener {
                    val shouldValidate = isPrimary && action.handler != FormActionHandler.ACTION_CONTENT
                    if (shouldValidate && !validateFields(step)) return@setOnClickListener
                    dispatchAction(host, action)
                }
            }
            actionButtons[action.id] = button
            actionsLayout.addView(button)
        }
        container.addView(actionsLayout)
    }

    private fun updateStatus(schema: FormSchema, step: FormStep) {
        val text = schema.errorMessage ?: schema.message ?: step.message
        if (text.isNullOrEmpty()) {
            statusTextView?.text = ""
            return
        }
        statusTextView?.text = text
        val ctx = statusTextView?.context ?: return
        statusTextView?.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (!schema.errorMessage.isNullOrEmpty())
                    R.color.red_recycler_color_icon
                else
                    R.color.green_dark
            )
        )
    }

    private fun updateLoading(schema: FormSchema) {
        actionButtons.values.forEach { it.isEnabled = !schema.isLoading && it.isEnabled }
    }

    private fun validateFields(step: FormStep): Boolean {
        var isValid = true
        step.fields.forEach { field ->
            if (field.type == FormFieldType.HIDDEN ||
                field.type == FormFieldType.READ_ONLY ||
                field.type == FormFieldType.CHART
            ) return@forEach

            val value = fieldValues[field.id].orEmpty()
            val errorView = fieldErrorViews[field.id]
            errorView?.text = ""
            errorView?.visibility = View.GONE

            if (field.required && value.isBlank()) {
                errorView?.text = "فیلد الزامی است"
                errorView?.visibility = View.VISIBLE
                isValid = false
                return@forEach
            }
            if (value.isNotBlank() && field.type == FormFieldType.DATE) {
                val dateError = validateJalaliDate(field.id, value)
                if (dateError != null) {
                    errorView?.text = dateError
                    errorView?.visibility = View.VISIBLE
                    isValid = false
                    return@forEach
                }
            }
            field.validations.forEach { validation ->
                val failed = when (validation.type) {
                    FieldValidationType.MIN_LENGTH ->
                        value.length < (validation.value?.toIntOrNull() ?: 0)
                    FieldValidationType.MAX_LENGTH ->
                        value.length > (validation.value?.toIntOrNull() ?: Int.MAX_VALUE)
                    FieldValidationType.LENGTH ->
                        value.length != (validation.value?.toIntOrNull() ?: value.length)
                    FieldValidationType.PATTERN ->
                        !Regex(validation.value.orEmpty()).matches(value)
                    FieldValidationType.STARTS_WITH ->
                        !value.startsWith(validation.value.orEmpty())
                }
                if (failed) {
                    errorView?.text = validation.message
                    errorView?.visibility = View.VISIBLE
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun validateJalaliDate(fieldId: String, value: String): String? {
        val cleanValue = com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(value)
        val parts = cleanValue.split('/')
        if (parts.size != 3) {
            return "فرمت تاریخ نادرست است (مثال: ۱۴۰۲/۰۱/۰۱)"
        }
        val year = parts[0].toIntOrNull() ?: return "فرمت تاریخ نادرست است"
        val month = parts[1].toIntOrNull() ?: return "فرمت تاریخ نادرست است"
        val day = parts[2].toIntOrNull() ?: return "فرمت تاریخ نادرست است"

        if (year !in 1300..1450) {
            return "سال وارد شده نامعتبر است"
        }
        if (month !in 1..12) {
            return "ماه وارد شده نامعتبر است"
        }
        val maxDays = when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> {
                if (saman.zamani.persiandate.PersianDate.isJalaliLeap(year)) 30 else 29
            }
            else -> 0
        }
        if (day !in 1..maxDays) {
            return "روز وارد شده نامعتبر است"
        }

        // Compare with today
        val today = saman.zamani.persiandate.PersianDate()
        val todayYear = today.shYear
        val todayMonth = today.shMonth
        val todayDay = today.shDay

        val isFuture = year > todayYear || 
                (year == todayYear && month > todayMonth) || 
                (year == todayYear && month == todayMonth && day > todayDay)

        if (isFuture) {
            return "تاریخ نمی‌تواند در آینده باشد"
        }

        // Cross-field validations
        if (fieldId == "employment_date" || fieldId == "accident_date") {
            val birthDateStr = com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(fieldValues["birth_date"].orEmpty())
            if (birthDateStr.isNotBlank()) {
                val bParts = birthDateStr.split('/')
                if (bParts.size == 3) {
                    val bYear = bParts[0].toIntOrNull()
                    val bMonth = bParts[1].toIntOrNull()
                    val bDay = bParts[2].toIntOrNull()
                    if (bYear != null && bMonth != null && bDay != null) {
                        val isBeforeBirth = year < bYear ||
                                (year == bYear && month < bMonth) ||
                                (year == bYear && month == bMonth && day < bDay)
                        if (isBeforeBirth) {
                            return "تاریخ نمی‌تواند قبل از تاریخ تولد باشد"
                        }
                    }
                }
            }
        }

        if (fieldId == "accident_date") {
            val empDateStr = com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(fieldValues["employment_date"].orEmpty())
            if (empDateStr.isNotBlank()) {
                val eParts = empDateStr.split('/')
                if (eParts.size == 3) {
                    val eYear = eParts[0].toIntOrNull()
                    val eMonth = eParts[1].toIntOrNull()
                    val eDay = eParts[2].toIntOrNull()
                    if (eYear != null && eMonth != null && eDay != null) {
                        val isBeforeEmp = year < eYear ||
                                (year == eYear && month < eMonth) ||
                                (year == eYear && month == eMonth && day < eDay)
                        if (isBeforeEmp) {
                            return "تاریخ حادثه نمی‌تواند قبل از تاریخ شروع به کار باشد"
                        }
                    }
                }
            }
        }

        return null
    }

    private fun dispatchAction(host: FormHost, action: FormAction) {
        if (action.handler == FormActionHandler.ACTION_CONTENT) {
            action.actionContent?.let { host.dispatchActionClick(it) }
            return
        }
        val data = mutableMapOf<String, Any?>()
        data.putAll(fieldValues)
        host.currentData?.let { current ->
            current.forEach { (k, v) -> if (k != null && !data.containsKey(k)) data[k] = v }
        }
        val normalizedData = data.mapValues { entry ->
            val valueStr = entry.value?.toString() ?: return@mapValues entry.value
            com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(valueStr)
        }
        host.dispatchFormAction(action.id, normalizedData)
    }

    private fun startTimer(duration: Long, host: FormHost) {
        countDownTimer?.cancel()
        if (duration <= 0) {
            timerTextView?.text = host.hostContext.getString(R.string.reset_timer_value)
            return
        }
        countDownTimer = object : CountDownTimer(duration, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                timerTextView?.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerTextView?.text = host.hostContext.getString(R.string.reset_timer_value)
            }
        }.start()
    }
}
