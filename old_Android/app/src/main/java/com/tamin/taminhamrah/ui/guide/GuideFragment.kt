package com.tamin.taminhamrah.ui.guide

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentGuideBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.makeTextViewResizable
import com.tamin.taminhamrah.utils.extentions.maxLineDefault
import com.tamin.taminhamrah.utils.extentions.visible
import timber.log.Timber
import java.io.Serializable

class GuideFragment :
    BaseBottomSheetDialogFragment<FragmentGuideBinding, BaseViewModel>() {

    override val mViewModelDialog : BaseViewModel by viewModels()

    override fun getLayoutId()= R.layout.fragment_guide


    var onDismissListener: DismissListener? = null
    var onShowGuideClickListener: ShowGuideClickListener? = null
    var hasConditionList = false

    companion object {
        const val ARG_GUIDE_MODEL = "ARG_GUIDE_MODEL"

        @JvmStatic
        fun newInstance(arg: Bundle? = null) =
            GuideFragment().apply {
                arguments = arg
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun getGuideModel(): GuideDataModel? {
        return arguments?.getSerializable(ARG_GUIDE_MODEL) as? GuideDataModel
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.onDismiss()
        super.onDismiss(dialog)
    }

    fun init() {
        val model = getGuideModel()
        if (model == null) {
            dismiss()
            return
        }
        viewBinding?.apply {
            if (model.title.isEmpty() && model.guide.isEmpty()) {
                tvTitle.gone()
                tvGuide.gone()
            } else {
                tvTitle.text = model.title
                tvGuide.text = model.guide
                if (tvGuide.text.lines().size>= maxLineDefault)
                    tvGuide.makeTextViewResizable(
                        expandText = getString(R.string.more_desc),
                        callBack = object : ShowMoreClickListener {
                            override fun onShowMoreClick() {
                                Timber.tag("GuideFragment").i("onShowMoreClick: ")

                                if (hasConditionList) {
                                    tvConditionTitle.visible()
                                    recycler.visible()
                                }
                            }

                            override fun onShowLessClick() {
                                Timber.tag("GuideFragment").i("onShowLessClick: ")
                                if (hasConditionList) {
                                    tvConditionTitle.gone()
                                    recycler.gone()
                                }
                            }
                        })
            }

            if (model.firstTime || !model.tapTargetEnable)
                btnShowCase.gone()
            else if (model.tapTargetEnable)
                btnShowCase.visible()

            btnShowCase.setOnClickListener {
                onShowGuideClickListener?.onShowGuideClick()
            }

            if (!model.hintItems.isNullOrEmpty()) {
                hasConditionList = true
                recycler.adapter = GuideListAdapter(model.hintItems!!)
                tvConditionTitle.text =
                    model.conditionTitle ?: getString(R.string.label_title_condition)
                if (model.title.isBlank() && model.guide.isBlank()) {
                    tvConditionTitle.visible()
                    recycler.visible()
                }
            } else {
                if (model.title.isBlank() && model.guide.isBlank())
                    dismiss()
                hasConditionList = false
                tvConditionTitle.gone()
                recycler.gone()
            }
        }
    }

    interface DismissListener {
        fun onDismiss()
    }

    interface ShowGuideClickListener {
        fun onShowGuideClick()
    }

    data class GuideDataModel(
        var title: String,
        var guide: String,
        var hintItems: List<String>? = null,
        var conditionTitle: String? = null
    ) : Serializable {
        var firstTime = false
        var tapTargetEnable = false
    }

    interface ShowMoreClickListener {
        fun onShowMoreClick()
        fun onShowLessClick()
    }

}