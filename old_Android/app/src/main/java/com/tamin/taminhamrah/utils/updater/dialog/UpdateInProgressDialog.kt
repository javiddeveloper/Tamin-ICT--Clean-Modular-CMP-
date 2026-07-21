package com.tamin.taminhamrah.utils.updater.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.UpdateProgressLayoutBinding
import timber.log.Timber

/**
 * Dialog to show download progress to user
 */
class UpdateInProgressDialog : DialogFragment() {
    var cancelDownloadListener: CancelDownloadClickListener? = null

    companion object {
        fun getInstance(cancelDownloadClickListener: CancelDownloadClickListener): UpdateInProgressDialog {
            val frg = UpdateInProgressDialog()
            frg.cancelDownloadListener = cancelDownloadClickListener
            return frg
        }

    }

    lateinit var viewBinding: UpdateProgressLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = UpdateProgressLayoutBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        //   mView = inflater.inflate(R.layout.update_progress_layout, container, false)

        viewBinding.btnCancelUpdate.setOnClickListener {
            cancelDownloadListener?.onCancelDownloadClick()
        }
        isCancelable = false
        return viewBinding.root
    }

    fun updateProgress(progress: Int) {
        Timber.tag("updateProgressDebug")
            .i("updateProgress: progress=" + progress + " " + "\n" + " progressBar=" + viewBinding.progress)
        viewBinding.progress = progress
        viewBinding.txtDescription.text = "${getString(R.string.app_name)}-($progress%)"

    }

    override fun onStart() {
        super.onStart()
        // making width of dialog to match_parent
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    interface CancelDownloadClickListener {
        fun onCancelDownloadClick()
    }
}