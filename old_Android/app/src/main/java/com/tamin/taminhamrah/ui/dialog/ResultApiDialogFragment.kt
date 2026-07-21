package com.tamin.taminhamrah.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.EligibilityStatusResponse
import com.tamin.taminhamrah.databinding.DialogResultApiBinding
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import com.tamin.taminhamrah.utils.Utility

class ResultApiDialogFragment :
    BaseDialogFragment<DialogResultApiBinding>(DialogResultApiBinding::inflate) {
    private var model: EligibilityStatusResponse? = null

    companion object {
        const val ARG_ELIGIBILITY_RESPONSE = "ARG_ELIGIBILITY_RESPONSE"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        init()
    }

    private fun fetchData() {
        model = arguments?.getParcelable(ARG_ELIGIBILITY_RESPONSE) as? EligibilityStatusResponse
    }

    private fun init() {
        setData()
        onClick()
    }

    private fun onClick() {
        viewBinding.btnOk.setOnClickListener { dismiss() }
    }

    private fun setData() {
        if (model?.reault == true)
            fillWidget(
                R.drawable.ic_correct,
                R.string.eligibility_result_correct,
                R.color.green_dark
            )
        else
            fillWidget(
                R.drawable.ic_incorrect,
                R.string.eligibility_result_incorrect,
                R.color.red
            )
    }

    private fun fillWidget(image: Int, messageId: Int, color: Int) {
        viewBinding.apply {
            imgResult.setImageResource(image)
            tvRequestResult.text = requireContext().resources.getString(messageId)
            tvRequestResult.setTextColor(ContextCompat.getColor(requireContext(), color))
            val message = model?.let { Utility.createTextEligibilityResult(requireContext(), it) }
            tvExplainResult.text = message
        }
    }


}