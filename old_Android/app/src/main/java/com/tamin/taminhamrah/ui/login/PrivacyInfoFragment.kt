package com.tamin.taminhamrah.ui.login

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentPrivacyBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel


class PrivacyInfoFragment :
    BaseBottomSheetDialogFragment<FragmentPrivacyBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId() = R.layout.fragment_privacy

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.apply {


//            webView.loadData(getString(R.string.label_privacy_description), "text/html", "utf-8");

            tvGuide.text = getText(R.string.label_privacy_description)
            tvGuide.movementMethod = LinkMovementMethod.getInstance()

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        fun getInstance() = PrivacyInfoFragment()
    }
}