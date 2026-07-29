package com.tamin.taminhamrah.widget

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.CustomRecyclerviewBinding
import com.tamin.taminhamrah.enums.ItemDecorationType
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class CustomRecyclerView(val mContext: Context, val attrs: AttributeSet?) :
    ConstraintLayout(mContext, attrs), DialogClickInterface.onClickListener {
    private lateinit var viewBinding: CustomRecyclerviewBinding
    private var onRetryClickListener: OnRetryClickListener? = null
    private var recyclerTag: String = ""

    init {
        initLayout()
    }

    private fun initLayout() {
        mContext.apply {
            viewBinding = CustomRecyclerviewBinding.inflate(
                LayoutInflater.from(this),
                this@CustomRecyclerView,
                true
            )
            attrs?.let { setAttribute(context, it) }

        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SMRecycler, 0, 0)
            val index = ta.getInteger(R.styleable.SMRecycler_decoration_type, 0)
            val type: ItemDecorationType = ItemDecorationType.values()[index]
            recyclerCustom.apply {
                if (itemDecorationCount == 0) {
                    when (type) {
                        ItemDecorationType.VERTICAL -> {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                        ItemDecorationType.TAMIN_BG -> {
                            addItemDecoration(UiUtils.BackgroundItemDecorationDrowable(context))
                        }

                        ItemDecorationType.GRID_MULTI_COL -> {
                            addItemDecoration(UiUtils.GridSpacingItemDecoration(5, 20))
                        }

                        ItemDecorationType.HORIZONTAL -> {

                            addItemDecoration(UiUtils.HorizontalItemMarginDecoration(40))
                        }
                        ItemDecorationType.MENU -> {
                            addItemDecoration(
                                UiUtils.createDivider(context)
                            )
                        }
                        ItemDecorationType.LINE -> {
                            addItemDecoration(UiUtils.createDivider(this.context))
                        }
                        ItemDecorationType.GRID_3_ITEM -> {
                            layoutManager = GridLayoutManager(context, 3)
                            addItemDecoration(UiUtils.GridSpacingItemDecoration(3, 40))
                        }
                        ItemDecorationType.GRID_2_ITEM -> {
                            layoutManager = GridLayoutManager(context, 2)
                            addItemDecoration(UiUtils.GridSpacingItemDecoration(2, 40))
                        }
                        else -> {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }
                }
            }
        }
    }

    fun getRecycler(): RecyclerView {
        return viewBinding.recyclerCustom
    }

    fun setMessage(message: String) {
        viewBinding.tvMessage.text = message
    }

    fun showMessage(message: String? = null) {
        if (message != null)
            setMessage(message)
        viewBinding.apply {
            recyclerCustom.gone()
            tvMessage.visible()
        }
    }

    fun hideMessage() {
        viewBinding.apply {
            recyclerCustom.visible()
            tvMessage.gone()
        }
    }


    fun showRetryDialog(fm: FragmentManager, errorMessage: String? = null) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(errorMessage ?: mContext.getString(R.string.error_general))
        dialog.setDialogClickListener(this)
        dialog.show(fm, "")
    }


    fun setOnRetryClickListener(onRetryClickListener: OnRetryClickListener) {
        this.onRetryClickListener = onRetryClickListener
    }

    fun setTag(tag: String) {
        this.tag = tag
    }

    private fun createBundle(
        desc: String
    ): Bundle {
        val bundle = Bundle()
        bundle.putSerializable(
            Constants.DIALOG_MESSAGE_TYPE,
            MessageOfRequestDialogFragment.MessageType.ERROR
        )
        bundle.putString(Constants.DIALOG_DESC, desc)
        bundle.putString(Constants.TITLE_CONFIRM_BUTTON, mContext.getString(R.string.retry))

        return bundle
    }

    interface OnRetryClickListener {
        fun onRetryClick(tag: String)
    }

    //
    interface OnLoadMore {
        fun loadData(currentPage: Int, pageSize: Int, startIndex: Int, tag: String)
    }

    override fun onConfirmClick() {
        onRetryClickListener?.onRetryClick(recyclerTag)
    }

    override fun onCancelClick() {
    }
}