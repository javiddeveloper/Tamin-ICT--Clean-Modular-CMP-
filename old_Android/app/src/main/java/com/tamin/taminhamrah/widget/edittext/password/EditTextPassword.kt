package com.tamin.taminhamrah.widget.edittext.password

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WidgetEdittextPasswordBinding
import com.tamin.taminhamrah.utils.ValidationUtil
import com.tamin.taminhamrah.widget.BaseWidget

class EditTextPassword(mContext: Context, attrs: AttributeSet?) : BaseWidget(mContext, attrs) {
    private var isShowPassword: Boolean = true
    private lateinit var viewBinding :WidgetEdittextPasswordBinding
    override fun initLayout(context: Context?, attrs: AttributeSet?) {
        viewBinding = WidgetEdittextPasswordBinding.inflate(LayoutInflater.from(context), this, true)
//        inflateLayout(context, R.layout.widget_edittext_password)
        onClick()
    }

    private fun onClick() {
        viewBinding.imgDisplayPassword.setOnClickListener {
            displayPassword()
        }
    }

    private fun displayPassword() {
        viewBinding.apply {
            val password = edtPassword.text.toString()
            if (!password.isNullOrEmpty()) {
                if (isShowPassword) {
                    edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    isShowPassword = false
                    imgDisplayPassword.setColorFilter(
                        ContextCompat.getColor(context, R.color.red),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    isShowPassword = true
                    imgDisplayPassword.setColorFilter(
                        ContextCompat.getColor(context, R.color.gray),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

    }

    fun getValueRegister(): String {
        viewBinding.apply {
            val password = edtPassword.text.toString()
            val model = ValidationUtil.registerPassword(context, password)
            return if (model.status)
                password
            else {
                edtPassword.error = model.message
                ""
            }
        }

    }

    fun getValueEnter(): String {
        viewBinding.apply {
            val password = edtPassword.text.toString()
            val model = ValidationUtil.enterPassword(context, password)
            return if (model.status)
                password
            else {
                edtPassword.error = model.message
                ""
            }
        }
    }
    fun getPureValue(): String {
        viewBinding.apply {
       return edtPassword.text.toString()

        }
    }
}