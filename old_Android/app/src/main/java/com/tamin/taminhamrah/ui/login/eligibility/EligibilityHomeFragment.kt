package com.tamin.taminhamrah.ui.login.eligibility

import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.EligibilityStatusResponse
import com.tamin.taminhamrah.databinding.FragmentHomeEligibilityBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EligibilityHomeFragment :
    BaseFragment<FragmentHomeEligibilityBinding, EligibilityHomeViewModel>() {

    override val mViewModel: EligibilityHomeViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_eligibility
    }

    override fun setupObserver() {
        mViewModel.mldEligibilityResult.observe(this, ::fetchEligibilityResult)
    }

    private var isNationalCode = true

    override fun initView() {
        viewDataBinding?.apply {
            widgetForeignNationalsCode.setHint(resources.getString(R.string.foreign_nationals_code))
        }
    }

    override fun getData() {

    }

    override fun onClick() {
        viewDataBinding?.apply {
            layHeader.imbTips.setOnClickListener {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO, "Tips")
            }
            btnInquiry.setOnClickListener {
                getUserData()
            }
            layHeader.imbBack.visibility = View.VISIBLE
            layHeader.imbBack.setOnClickListener {
                backButtonPress()
            }
            swChangeInput.setOnCheckedChangeListener { _, switchChecked ->
                checkedSwitch(switchChecked)
            }
        }
    }

    private fun checkedSwitch(switchChecked: Boolean) {
        if (switchChecked)
            manageSwitch(false, View.INVISIBLE, View.VISIBLE)
        else
            manageSwitch(true, View.VISIBLE, View.INVISIBLE)
    }

    private fun manageSwitch(
        flag: Boolean,
        viewNationalCode: Int,
        viewForeignNationalsCode: Int,
    ) {
        viewDataBinding?.apply {
            widgetNationalCode.visibility = viewNationalCode
            widgetForeignNationalsCode.visibility = viewForeignNationalsCode
        }
        isNationalCode = flag
        clearEditText()

    }

    private fun getUserData() {
        viewDataBinding?.apply {
            view?.windowToken?.let { hideKeyboard(requireContext(), it) }
            var nationalCode = if (isNationalCode)
                widgetNationalCode.getValueNationalCode()
            else
                widgetForeignNationalsCode.getValue()

            if (nationalCode.isEmpty()) return

            if (mViewModel.checkDataForeignNationalsCode(nationalCode, isNationalCode)) {
                widgetForeignNationalsCode.setError(resources.getString(R.string.please_enter_valid_value))
                return
            }
            if (!isNationalCode) {
                nationalCode = widgetForeignNationalsCode.checkStartZero(nationalCode).toString()
                if (nationalCode.isEmpty()) return
            }
            clearEditText()
            mViewModel.getUserEligibility(nationalCode, swChangeInput.isChecked)
        }
    }

    private fun clearEditText() {
        viewDataBinding?.apply {
            widgetNationalCode.setTextWidget("")
            widgetForeignNationalsCode.setTextWidget("")
        }

    }

    private fun fetchEligibilityResult(result: EligibilityStatusResponse) {

        if (result.reault == true) {
            if (result.isForeigner) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    getString(
                        R.string.eligibility_home_fragment_with_foreign_id,
                        result.nationalId.toString(),
                        result.referenceCode.toString()
                    )
                )
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    getString(
                        R.string.eligibility_home_fragment_with_national_id,
                        result.nationalId.toString(),
                        result.referenceCode.toString()
                    )
                )
            }
        } else {
            if (result.isForeigner) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(
                        R.string.dont_have_eligibility_home_fragment_with_foreign_id,
                        result.nationalId.toString(),
                        result.referenceCode.toString()
                    )
                )
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(
                        R.string.dont_have_eligibility_home_fragment_with_national_id,
                        result.nationalId.toString(),
                        result.referenceCode.toString()
                    )
                )
            }
        }

    }

    private fun apiError(message: String) {
//       (requireActivity() as? LoginActivity)?.hideLoading()
        if (message != "-1")
            showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR, message)
        else
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                resources.getString(R.string.explain_invalid_foreign_nationals_code)
            )
    }

}

