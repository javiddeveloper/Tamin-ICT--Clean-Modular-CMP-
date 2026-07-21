package com.tamin.taminhamrah.ui.treatment.treatmentCard

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentTreatmentCardBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.treatment.TreatmentViewModel
import com.tamin.taminhamrah.ui.treatment.model.CardBackgroundEnumClass
import com.tamin.taminhamrah.ui.treatment.model.TreatmentCardDataModel
import com.tamin.taminhamrah.utils.UiUtils.getQrCodeBitmap
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TreatmentCardFragment : BaseFragment<FragmentTreatmentCardBinding, TreatmentViewModel>() {

    //region Base Variables
    companion object {
        private const val DATA_ARG = "DATA_ARG"

        fun newInstance(model: TreatmentCardDataModel): TreatmentCardFragment {
            val fragment = TreatmentCardFragment()
            val args = Bundle()
            args.putParcelable(DATA_ARG, model)
            fragment.arguments = args
            return fragment
        }
    }


    override val mViewModel: TreatmentViewModel by viewModels()
    var userInfo: TreatmentCardDataModel? = null
    //endregion

    //region Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_treatment_card

    override fun setupObserver() {
    }

    override fun initView() {
        viewDataBinding?.apply {
            root.setBackgroundResource(CardBackgroundEnumClass.GRAY.backgroundResId)
        }
    }

    override fun getData() {
        userInfo = arguments?.getParcelable(DATA_ARG) as? TreatmentCardDataModel
        setUserInfo()
    }

    override fun onClick() {

    }
    //endregion

    //region Listeners

    //endregion

    //region Utils
    private fun setUserInfo() {
        (arguments?.getParcelable(DATA_ARG) as? TreatmentCardDataModel)?.let { model ->
            viewDataBinding?.apply {
                tvName.text = model.name
                tvValueNationalId.text = model.nationalCode
                tvValueInsuranceId.text = model.insuranceNumber
                if (model.isTreatmentSupport == true) {
                    btnShowDescription.isGone = true
                    txtDeserveDescription.isGone = true
                } else {
                    btnShowDescription.isGone = false
                    txtDeserveDescription.isGone = false
                    txtDeserveDescription.text = model.treatmentSupportDescription
                    btnShowDescription.setOnClickListener {
                        showInfoDialog(
                            "${model.treatmentSupportDescription}\n${model.message}" ?: ""
                        )

                    }
                }
                imgBarcode.setImageBitmap(getQrCodeBitmap(model.qrCodeFilePath))
                icTreatmentStatus
                val treatmentStatus = when (model.isTreatmentSupport) {
                    true -> {
                        CardBackgroundEnumClass.GREEN
                    }

                    false -> {
                        CardBackgroundEnumClass.RED
                    }

                    null -> {
                        CardBackgroundEnumClass.GRAY
                    }
                }
                root.setBackgroundResource(treatmentStatus.backgroundResId)
                treatmentStatus.drawableResId?.let { drawableResId ->
                    icTreatmentStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            drawableResId
                        )
                    )
                }
                treatmentStatus.statusTreatmentDescResId?.let { str ->
                    tvTreatmentStatus.text = getString(str)
                }
            }
        }
    }

    private fun showInfoDialog(treatmentSupportDescription: String) {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            treatmentSupportDescription,
            titleId = R.string.inquiry_eligibility
        )

    }

    fun updateUI(isSupported: Boolean) {
        // Update the UI based on the response
        // For example, update the treatment support status
        userInfo?.isTreatmentSupport = isSupported

        // Update other UI elements as needed
        val treatmentStatus = if (userInfo?.isTreatmentSupport == true) {
            CardBackgroundEnumClass.GREEN
        } else {
            CardBackgroundEnumClass.RED
        }
        viewDataBinding?.apply {
            root.setBackgroundResource(treatmentStatus.backgroundResId)
            treatmentStatus.statusTreatmentDescResId?.let { statusTreatmentDescResId ->
                tvTreatmentStatus.text = getString(statusTreatmentDescResId)
            }
            treatmentStatus.drawableResId?.let { drawableResId ->
                icTreatmentStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        drawableResId
                    )
                )
            }
        }
    }

    //endregion


}