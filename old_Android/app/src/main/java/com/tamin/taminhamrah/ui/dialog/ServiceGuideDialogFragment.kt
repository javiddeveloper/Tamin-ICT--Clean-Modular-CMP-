package com.tamin.taminhamrah.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogServiceGuideBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel


class ServiceGuideDialogFragment :
    BaseBottomSheetDialogFragment<DialogServiceGuideBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_service_guide

    companion object {
        const val ARG_GUIDE_TITLE = "ARG_GUIDE_TITLE"
        const val ARG_GUIDE_RULES = "ARG_GUIDE_RULES"
    }

    var guideTitleStr: String = ""
    var guideRulesStr: String = ""

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.

//        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_App_BottomSheetDialog)
        // setStyle(DialogFragment.STYLE_NO_FRAME,0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getExtras()
        init()
    }

    private fun getExtras() {
        guideTitleStr = arguments?.getString(ARG_GUIDE_TITLE) ?: ""
        guideRulesStr = arguments?.getString(ARG_GUIDE_RULES) ?: ""
    }

    private fun init() {
        viewBinding?.apply {
            tvTitle.text = guideTitleStr
            tvGuide.text = guideRulesStr
        }
    }

}