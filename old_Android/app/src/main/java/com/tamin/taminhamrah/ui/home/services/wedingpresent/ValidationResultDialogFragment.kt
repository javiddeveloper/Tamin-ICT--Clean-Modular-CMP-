package com.tamin.taminhamrah.ui.home.services.wedingpresent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogValidationResultBinding
import com.tamin.taminhamrah.ui.base.BaseDialogFragment

class ValidationResultDialogFragment :
    BaseDialogFragment<DialogValidationResultBinding>(DialogValidationResultBinding::inflate) {
    companion object {
        const val ARG_RESULT = "ARG_RESULT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_validation_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun getExtra(): String? {
        return arguments?.getString(ARG_RESULT)
    }

    private fun init() {
        setData(getExtra())
        onClick()
    }

    private fun onClick() {
        viewBinding.btnOk.setOnClickListener { dismiss() }
    }

    private fun setData(resultStr: String?) {
        viewBinding.tvResult.text = resultStr
    }

}