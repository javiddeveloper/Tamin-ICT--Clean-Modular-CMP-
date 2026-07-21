package com.tamin.taminhamrah.ui.dialog.messageOfReques

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.Constants.CANCEL_BUTTON
import com.tamin.taminhamrah.Constants.DIALOG_DESC
import com.tamin.taminhamrah.Constants.DIALOG_MESSAGE_TYPE
import com.tamin.taminhamrah.Constants.DIALOG_TITLE
import com.tamin.taminhamrah.Constants.DISMISS_TYPE_DIALOG
import com.tamin.taminhamrah.Constants.TITLE_CONFIRM_BUTTON
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.DialogMessageOfRequestBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import com.tamin.taminhamrah.ui.login.LoginActivity
import com.tamin.taminhamrah.utils.extentions.gone
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject
import androidx.core.graphics.drawable.toDrawable

@AndroidEntryPoint
class MessageOfRequestDialogFragment : BaseDialogFragment<DialogMessageOfRequestBinding>(
    DialogMessageOfRequestBinding::inflate
) {

    private val mViewModel: MessageOfRequestDialogViewModel by viewModels()
    var messageType: MessageType? = null
    var titleBtnOk: String? = null
    var titleBtnCancel: String? = null
    private var onClickListener: DialogClickInterface.onClickListener? = null

    private var dismissType = DismissType.NORMAL
    fun setDialogClickListener(clickListener: DialogClickInterface.onClickListener) {
        onClickListener = clickListener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        childFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { (it as? DialogFragment)?.dismiss() }
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        initDialog()
    }

    private fun initDialog() {
        try {
            arguments?.let {
                messageType = it.getSerializable(DIALOG_MESSAGE_TYPE) as? MessageType
                val titleId = it.getInt(DIALOG_TITLE)
                val description = it.getString(DIALOG_DESC)
                val addCancelButton = it.getBoolean(CANCEL_BUTTON)
                val titleConfirm = it.getString(TITLE_CONFIRM_BUTTON)
                dismissType =
                    (it.getSerializable(DISMISS_TYPE_DIALOG) as? DismissType?) ?: DismissType.NORMAL
                viewBinding.apply {
                    if (addCancelButton) {
                        btnCancel.visibility = View.VISIBLE
                    }
                    if (!titleConfirm.isNullOrBlank()) {
                        btnOk.text = titleConfirm
                    }
                }
                when (messageType) {
                    MessageType.REFRESHTOKEN -> {
                        fillWidget(
                            titleId = R.string.message_need_to_refresh_token_title,
                            TitleColor = R.color.text_color_dialog_red,
                            desc = getString(R.string.message_need_to_refresh_token),
                            backGroundParentColor = R.drawable.bg_item_red_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_red
                        )
                    }

                    MessageType.ERROR -> {
                        fillWidget(
                            titleId =if (titleId != 0) titleId else R.string.unable_to_request,
                            TitleColor = R.color.text_color_dialog_red,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_red_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_red
                        )
                    }

                    MessageType.SUCCESS -> {
                        fillWidget(
                            titleId = R.string.request_has_been_successfully_submitted,
                            TitleColor = R.color.text_color_dialog_green,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_green_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_green
                        )
                    }

                    MessageType.INFO -> {

                        fillWidget(
                            titleId = R.string.processing_result,
                            TitleColor = R.color.text_color_dialog_blue,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_blue_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_blue
                        )
                    }

                    MessageType.WARNING -> {
                        fillWidget(
                            titleId = if (titleId != 0) titleId else R.string.warning_result,
                            TitleColor = R.color.text_color_dialog_orange,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_orange_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_orange,
                            titleConfirm ?: getString(R.string.ok)
                        )
                    }

                    MessageType.CONFIRM -> {
                        fillWidget(
                            titleId = if (titleId == 0) R.string.warning_confirm else titleId,
                            TitleColor = R.color.text_color_dialog_blue,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_blue_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_blue,
                            getString(R.string.ok)
                        )
                    }

                    MessageType.SUCCESS2ACTION -> {
                        fillWidget(
                            titleId = R.string.request_has_been_successfully_submitted,
                            TitleColor = R.color.text_color_dialog_green,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_green_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_green,
                            backGroundCancelButtonColor = R.drawable.bg_btn_dialog_blue,
                            titleCancel = getString(R.string.understand)
                        )
                    }
                    MessageType.WITHOUT_TITLE -> {
                        fillWidget(
                            titleId = null,
                            TitleColor = R.color.text_color_dialog_orange,
                            desc = description,
                            backGroundParentColor = R.drawable.bg_item_orange_top,
                            backGroundButtonColor = R.drawable.bg_btn_dialog_orange,
                            titleConfirm ?: getString(R.string.ok)
                        )
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            Timber.tag("initDialog: ").i(e.message.toString())
        }
    }

    private fun onClick() {
        viewBinding.apply {
            btnOk.setOnClickListener {
                    dismiss()
                    onClickListener?.onConfirmClick()
                    when (messageType) {
                        MessageType.REFRESHTOKEN -> {
                            mViewModel.logout()
                            requireActivity().finish()
                            requireActivity().startActivity(
                                Intent(
                                    requireActivity(),
                                    LoginActivity::class.java
                                )
                            )
                        }
                        else -> {}
                    }
            }
            btnCancel.setOnClickListener {
                onClickListener?.onCancelClick()
                dismiss()
            }
        }
    }

    enum class MessageType {
        ERROR,
        SUCCESS,
        INFO,
        REFRESHTOKEN,
        WARNING,
        CONFIRM,
        WITHOUT_TITLE,
        SUCCESS2ACTION,
    }

    enum class DismissType {
        NORMAL,
        BACK_TO_PREVIOUS
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun fillWidget(
        titleId: Int?,
        TitleColor: Int,
        desc: String?,
        backGroundParentColor: Int,
        backGroundButtonColor: Int,
        titleConfirm: String? = null,
        backGroundCancelButtonColor: Int? = null,
        titleCancel: String? = null
    ) {
        viewBinding.apply {
            btnOk.setTextColor(ContextCompat.getColor(requireContext(), TitleColor))
            btnCancel.setTextColor(ContextCompat.getColor(requireContext(), TitleColor))
            titleId?.let {
            labelTitle.text = requireContext().getString(titleId)
            labelTitle.setTextColor(ContextCompat.getColor(requireContext(), TitleColor))
            } ?: run { labelTitle.gone() }

            labeldesc.text = HtmlCompat.fromHtml(desc ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
//            }

            parent.setBackgroundResource(backGroundParentColor)
            btnOk.setBackgroundResource(backGroundButtonColor)
            btnCancel.setBackgroundResource(backGroundButtonColor)
            backGroundCancelButtonColor?.let {
                btnCancel.setBackgroundResource(backGroundCancelButtonColor)
                btnCancel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color_dialog_blue
                    )
                )
            }
            if (titleBtnOk != null) {
                btnOk.text = titleBtnOk
            } else {
                titleConfirm?.let {
                    btnOk.text = it
                }
            }
            titleCancel?.let {
                btnCancel.text = it
            }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        Timber.tag("onDismissType").i("Dismiss Call")
        if (dismissType == DismissType.BACK_TO_PREVIOUS)
            requireActivity().onBackPressed()

        super.onDismiss(dialog)
    }
    /*
        fun dismissAllDialogs(manager: FragmentManager) {
            val fragments: List<Fragment> = manager.fragments
            for (fragment in fragments) {
                if (fragment is DialogFragment)
                     fragment.dismissAllowingStateLoss()
                if (fragment.isAdded &&   fragment.childFragmentManager.backStackEntryCount>0)
                     dismissAllDialogs(fragment.childFragmentManager)
                else
                    Log.i("dismissAllDialogs: ","childFragmentManager")
            }
        }*/

    override fun onPause() {
        super.onPause()
        //  dismissAllDialogs(requireActivity().supportFragmentManager)
    }

}