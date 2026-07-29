package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

class InquiryEducationBusinessGenerator : BusinessViewGenerator {

    private var bindingView: View? = null
    private var codeDropdown: TextView? = null
    private var studyCodeInput: EditText? = null
    private var submitButton: View? = null
    private var progressBar: View? = null
    private var errorText: TextView? = null
    
    private var selectedCode: String? = null

    override fun matches(schema: FormSchema, message: AiGenerativeModel): Boolean {
        return schema.key.equals("inquiry_education", ignoreCase = true)
    }

    override fun bind(host: FormHost, schema: FormSchema, message: AiGenerativeModel) {
        val context = host.hostContext

        if (bindingView == null) {
            bindingView = LayoutInflater.from(context).inflate(R.layout.item_chat_bot_inquiry_education, host.formContainer, false)
            host.formContainer.addView(bindingView)
        }
        
        val view = bindingView ?: return

        val btnCancel = view.findViewById<View>(R.id.btnCancel)
        btnCancel.visibility = if (schema.showCancelButton == true) View.VISIBLE else View.GONE
        btnCancel.setOnClickListener {
            host.dispatchFormAction(ServiceNameEnum.EXTEND_EDUCATION_CANCEL.key, emptyMap())
        }

        val step1 = view.findViewById<View>(R.id.step1)
        val step2 = view.findViewById<View>(R.id.step2)
        val groupStep1 = view.findViewById<View>(R.id.groupStep1)
        val groupStep2 = view.findViewById<View>(R.id.groupStep2)
        
        progressBar = view.findViewById(R.id.progressBar)
        codeDropdown = view.findViewById(R.id.inputDependent)
        studyCodeInput = view.findViewById(R.id.inputStudyCode)
        submitButton = view.findViewById(R.id.btnSubmit)
        errorText = view.findViewById(R.id.tvError)

        progressBar?.visibility = if (schema.isLoading) View.VISIBLE else View.GONE
        errorText?.visibility = View.GONE

        if (!schema.errorMessage.isNullOrEmpty()) {
            errorText?.visibility = View.VISIBLE
            errorText?.text = schema.errorMessage
        }

        if (schema.currentStep == 1) {
            step1.setBackgroundResource(R.drawable.shape_circle_blue)
            step2.setBackgroundResource(R.drawable.shape_circle_grey)
            groupStep1.visibility = View.VISIBLE
            groupStep2.visibility = View.GONE
            
            setupStep1(schema, context, host)
        } else {
            step1.setBackgroundResource(R.drawable.shape_circle_green)
            step2.setBackgroundResource(R.drawable.shape_circle_blue)
            groupStep1.visibility = View.GONE
            groupStep2.visibility = View.VISIBLE
            
            setupStep2(schema, view, context)
        }
    }

    private fun setupStep1(schema: FormSchema, context: Context, host: FormHost) {
        val step = schema.steps.firstOrNull { it.index == 1 } ?: return
        
        val codeField = step.fields.firstOrNull { it.id == "code" }
        if (codeField != null) {
            val options = codeField.options ?: emptyList()
            if (selectedCode == null && options.isNotEmpty()) {
                selectedCode = options.first().id
                codeDropdown?.text = options.first().title
            }
            
            codeDropdown?.setOnClickListener {
                val menuModels = options.map { MenuModel(it.title ?: "", it.id) }
                showPopupMenu(codeDropdown!!, menuModels) { selected ->
                    selectedCode = selected.id
                    codeDropdown?.text = selected.title
                }
            }
        }
        
        submitButton?.setOnClickListener {
            val studyCode = studyCodeInput?.text?.toString()?.trim() ?: ""
            if (studyCode.isEmpty()) {
                errorText?.visibility = View.VISIBLE
                errorText?.text = "لطفا کد رهگیری تحصیلی را وارد کنید"
                return@setOnClickListener
            }
            
            if (selectedCode == null) {
                errorText?.visibility = View.VISIBLE
                errorText?.text = "لطفا فرد مورد نظر را انتخاب کنید"
                return@setOnClickListener
            }
            
            host.dispatchFormAction(
                ServiceNameEnum.EXTEND_EDUCATION_SUBMIT.key,
                mapOf(
                    "code" to selectedCode,
                    "studyCode" to studyCode
                )
            )
        }
    }
    
    private fun setupStep2(schema: FormSchema, view: View, context: Context) {
        val imgStatus = view.findViewById<ImageView>(R.id.imgStatus)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        
        tvMessage.text = schema.message ?: "عملیات با موفقیت انجام شد"
        
        if (!schema.errorMessage.isNullOrEmpty()) {
            imgStatus.setImageResource(R.drawable.ic_close_circle_red)
            tvMessage.text = schema.errorMessage
            tvMessage.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            imgStatus.setImageResource(R.drawable.ic_check_circle_green)
            tvMessage.setTextColor(ContextCompat.getColor(context, R.color.green_dark))
        }
    }

    override fun cleanup() {
        bindingView = null
        codeDropdown = null
        studyCodeInput = null
        submitButton = null
        progressBar = null
        errorText = null
    }
}
