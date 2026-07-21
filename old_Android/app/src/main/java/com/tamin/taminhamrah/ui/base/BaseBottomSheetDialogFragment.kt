package com.tamin.taminhamrah.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.jetpack.paging3.FooterAdapter
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.appinterface.EndOfPaginationListener
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.SpecialResponses
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianDatePickerDialog
import com.tamin.taminhamrah.widget.CustomRecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, VM : BaseViewModel> :
    BottomSheetDialogFragment() {

    var viewBinding: VB? = null
        private set

    abstract val mViewModelDialog: VM?

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
//        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_App_BottomSheetDialog)
    }

    fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
     /*   viewBinding?.apply {
            root.setBackgroundResource(R.drawable.bg_left_corner_rounded_white)
        }*/


        mViewModelDialog?.mldLoadingState?.observe(this, ::updateLoadingState)
        mViewModelDialog?.mldErrorState?.observe(viewLifecycleOwner, Observer {
            it.let { // Only proceed if the event has never been handled
                handleServiceError(it)
            }
        })


        return viewBinding?.root
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun updateLoadingState(loadingState: LoadingState) {
        Timber.tag("loadingTest").wtf("updateLoadingState:  Called loadingState=$loadingState")
        if (loadingState == LoadingState.LOADING)
            showLoading()
        else
            hideLoading()

    }

    private var loadingView: View? = null

    fun showLoading() {
        if (loadingView == null) {
            try {
                loadingView = LayoutInflater.from(requireContext()).inflate(R.layout.view_loading, null)
                loadingView?.apply {
                    id = View.generateViewId()
                    translationZ = resources.getDimension(R.dimen.button_pressed_z_material)

                    layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                    (viewBinding?.root as? ViewGroup)?.addView(loadingView, 0)

                    setOnClickListener { }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: Error) {
                e.printStackTrace()
            }
        } else if (loadingView?.visibility == View.GONE) {
            loadingView?.visibility = View.VISIBLE
        }

        /* window.setFlags(
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
         )*/


    }

    fun hideLoading() {
        Timber.tag("loadingTest").i("hideLoading:  Called\n************************************")
        loadingView?.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }


    private fun handleServiceError(result: BaseResponseNew) {

        val map = HashMap<String, String>()
        map["CLASS"] = this.javaClass.simpleName
        map["METHOD"] = "handleServiceError"
        map["message"] = result.getMessage()
        map["code"] = result.getCode().toString()

        //TaminLogger.putLog(map)


        var message = result.getMessage()
        if (result is LoginResponse && result.getCode() == 500)
            message = getString(R.string.error_check_username_or_password)


        val resultName = result.javaClass.simpleName

        if (resultName in SpecialResponses.dismissDialogForResponse)
            return

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()


        dialog.arguments = createBundle(
            when {
                result.isNeedNetwork -> MessageOfRequestDialogFragment.MessageType.INFO
                result.needRefreshToken -> MessageOfRequestDialogFragment.MessageType.REFRESHTOKEN
                else -> MessageOfRequestDialogFragment.MessageType.ERROR
            },
            message,
            dismissType = if (resultName in SpecialResponses.finishPageForResponse || result.getCode() == 502 || result.getCode() == 1000/*JsonSyntaxException*/) MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS else MessageOfRequestDialogFragment.DismissType.NORMAL
        )
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")

    }

    fun getDatePicker(
        shYear: Int = 0,
        shMonth: Int = 0,
        shDay: Int = 0
    ): MyPersianDatePickerDialog? {
        return Utility.getDatePicker(requireContext(), shYear, shMonth, shDay)
    }

    fun setupRecycler(
        recycler: CustomRecyclerView?,
        adapter: BasePagingAdapter<*, *>,
        emptyMessage: String = getString(R.string.message_empty_list),
        tag: String = "",
        listener: EndOfPaginationListener? = null
    ) = recycler?.apply {
        setTag(tag)
        // getRecycler().adapter = adapter
        getRecycler().adapter = adapter.withLoadStateFooter(FooterAdapter { adapter.retry() })

        adapter.addLoadStateListener {
            if (adapter.itemCount < 1)
                recycler.showMessage(emptyMessage)
            else
                recycler.hideMessage()
        }


        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState: CombinedLoadStates ->


                if (isRemoteData())
                    updateLoadingState(if (loadState.refresh is LoadState.Loading) LoadingState.LOADING else LoadingState.NOT_LOADING)


                if (loadState.append is LoadState.NotLoading && loadState.append.endOfPaginationReached)
                    listener?.onEndOfPagination(tag)

            }
        }
    }

    private fun isRemoteData() = arguments?.getBoolean(Constants.IS_REMOTE_DATA) ?: false



    fun showAlertDialog(
        type: MessageOfRequestDialogFragment.MessageType,
        desc: String,
        dismissType: MessageOfRequestDialogFragment.DismissType = MessageOfRequestDialogFragment.DismissType.NORMAL
    ) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(type, desc, dismissType = dismissType)
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
    }
}