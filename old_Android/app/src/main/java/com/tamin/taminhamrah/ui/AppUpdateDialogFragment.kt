package com.tamin.taminhamrah.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateData
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.databinding.DialogAppUpdateBinding
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AppUpdateDialogFragment () :
    BaseDialogFragment<DialogAppUpdateBinding>(DialogAppUpdateBinding::inflate) {

    private var mListener: DialogResultInterface.OnResultListener<Boolean>? = null

    companion object {
        fun getInstance(bundle: Bundle? = null): AppUpdateDialogFragment {
            val dialog = AppUpdateDialogFragment()
            dialog.arguments = bundle
            return dialog
        }
    }

    fun setResultListener(listener: DialogResultInterface.OnResultListener<Boolean>) {
        mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupObserver() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkUpdateResponse = arguments?.getSerializable("updateStatus") as CheckUpdateResponse?
        val updateFromCaffeBazaar = arguments?.getBoolean("caffeBazaarUpdate") ?: false
        if (checkUpdateResponse == null) {
            mListener?.onDialogResult(false)
            dismiss()
        } else
            checkUpdateResponse.data?.apply {
                Timber.tag("AppUpdateDialogFragment")
                    .i("onViewCreated:  updateStatus is " + checkUpdateResponse)

                if (updateLink.isNullOrEmpty())
                    viewBinding.btnDirectUpdate.gone()
                else
                    viewBinding.btnDirectUpdate.visible()



                if (getUpdateStatus() == CheckUpdateData.UpdateStatus.FORCE_UPDATE) {
                    viewBinding.labelDescription.text =
                        getString(R.string.label_app_force_update_description)
                    viewBinding.btnCancel.gone()
                    isCancelable = false
                } else {
                    viewBinding.labelDescription.text =
                        getString(R.string.label_app_update_description)
                    viewBinding.btnCancel.visible()
                    isCancelable = true
                }

                if (updateFromCaffeBazaar)
                    viewBinding.btnUpdateFromBazaar.visible()
                else
                    viewBinding.btnUpdateFromBazaar.gone()

            }





        onClick()
        getData()
        setupObserver()
    }

    private fun getData() {

    }

    private fun onClick() {
        viewBinding.apply {
            btnCancel.setOnClickListener {
                dismiss()
                mListener?.onDialogResult(false)
            }
            btnUpdateFromBazaar.setOnClickListener {
                dismiss()
                mListener?.onDialogResult(true)
            }
        }
    }
}