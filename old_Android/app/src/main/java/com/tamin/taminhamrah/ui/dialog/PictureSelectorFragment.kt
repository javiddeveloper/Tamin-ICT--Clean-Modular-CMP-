package com.tamin.taminhamrah.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.services.entity.FancyShowCaseModel
import com.tamin.taminhamrah.databinding.DialogPictureSelectorBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureSelectorFragment :
    BaseBottomSheetDialogFragment<DialogPictureSelectorBinding, BaseViewModel>() {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_picture_selector

    companion object {
        const val ARG_TITLE = "ARG_TITLE"
    }

    interface OnButtonClick {
        fun onCameraButtonClick()
        fun onGalleryButtonClick()
    }

    private var onClickListener: OnButtonClick? = null

    private var onStopDialogListener: AdapterInterface.OnStopDialogListener? = null
    var viewsList = arrayListOf<FancyShowCaseModel>()


    fun setListener(listener: OnButtonClick) {
        onClickListener = listener
    }

    fun setStopListener(listener: AdapterInterface.OnStopDialogListener) {
        onStopDialogListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun init() {
val title = arguments?.getString(ARG_TITLE)
        viewBinding?.labelTitle?.text = getString(R.string.labels_select, title)
    }

    private fun onClick() {
        viewBinding?.apply {
            btnCamera.setOnClickListener {
                onClickListener?.onCameraButtonClick()
                dismiss()
            }

            btnGallery.setOnClickListener {
                onClickListener?.onGalleryButtonClick()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onStopDialogListener?.onStop()
    }

}