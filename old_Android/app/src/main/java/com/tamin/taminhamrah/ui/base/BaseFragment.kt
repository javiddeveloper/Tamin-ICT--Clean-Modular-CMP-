package com.tamin.taminhamrah.ui.base

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.services.entity.FancyShowCaseModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.databinding.ViewAppbarServiceBinding
import com.tamin.taminhamrah.databinding.ViewAppbarServiceStepperBinding
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.ActionAppBarInterface
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment.DismissType
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.guide.GuideFragment
import com.tamin.taminhamrah.ui.guide.GuideFragment.Companion.ARG_GUIDE_MODEL
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.LoadingView
import com.tamin.taminhamrah.utils.SpecialResponses.dismissDialogForResponse
import com.tamin.taminhamrah.utils.SpecialResponses.finishPageForResponse
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.navigateSafe
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianDatePickerDialog
import com.tamin.taminhamrah.widget.CustomRecyclerView
import com.tamin.taminhamrah.widget.RecyclerViewWithEmptyMessage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence
import kotlin.math.abs


abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment(),
    CustomRecyclerView.OnRetryClickListener {

    var myTypeface: Typeface? = null

    var viewDataBinding: T? = null
        private set


    protected var onActionAppBarClickListener: ActionAppBarInterface.OnActionClickListener? = null

    abstract val mViewModel: V?

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Pair<Int, Any?>

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun setupObserver()

    fun updateLastSeen() {
        lifecycleScope.launch {
            Utility.getServiceId(arguments).let {
                if (it != -1) {
                    Timber.tag("addInAppliedService")
                        .i("updateLastSeen: called with id=" + it + " ")
                    mViewModel?.updateAppliedService(it)
                } else
                    Timber.tag("addInAppliedService")
                        .i("updateLastSeen: called NOT with id=" + it + " ")

            }

        }
    }

    fun updateLoadingState(loadingState: LoadingState) {
        Timber.tag("loadingTest").wtf("updateLoadingState:  Called loadingState=$loadingState")
        if (loadingState == LoadingState.LOADING)
            showLoading()
        else
            hideLoading()

    }

    abstract fun initView()
    abstract fun getData(

    )

    abstract fun onClick()

    fun setLoadMoreAction(): RecyclerViewWithEmptyMessage.OnLoadMore {
        return object : RecyclerViewWithEmptyMessage.OnLoadMore {
            override fun loadData(currentPage: Int, pageSize: Int, startIndex: Int) {
                getData()
            }
        }
    }

    fun backButtonPress() {
        requireActivity().onBackPressed()
    }


    protected var rootView: View? = null

    private var doubleBackToExitPressedOnce = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myTypeface = ResourcesCompat.getFont(requireContext(), R.font.iran_sans_mobile_fa_num)
        var inited = false
        if (rootView == null) {
            // Inflate the layout for this fragment
            performDataBinding(inflater, container)
            rootView = viewDataBinding?.root

        } else {
            inited = true
        }


        mViewModel?.mldLoadingState?.observe(viewLifecycleOwner, ::updateLoadingState)
        mViewModel?.mldErrorState?.observe(viewLifecycleOwner, Observer {
            it.let { // Only proceed if the event has never been handled
                handleServiceError(it)
            }
        })

        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        updateLastSeen()
        if (!inited) {
            initView()
            onClick()
            setupObserver()
            getData()
        } else {
            refreshView()
        }


        if (mViewModel?.loadBoolean(getTapTargetTag()) == false)
            showGuideDialog()


        return rootView

    }

    open fun refreshView() {

    }

    private fun performDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        Log.i(
            "debugCrashAndroidVe",
            "*************************" + "\n" + "performDataBinding: layout=" + resources.getResourceName(
                getLayoutId()
            ) + "\n" + "className=" + javaClass.simpleName + "\n" + "****************************************"
        )
        /*Timber.tag("debugCrashAndroidVe").i(
            "*************************" + "\n" + "performDataBinding: layout=" + resources.getResourceName(
                getLayoutId()
            ) + "\n" + "className=" + javaClass.simpleName + "\n" + "****************************************"
        )*/
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewDataBinding?.apply {
            setVariable(getBindingVariable().first, getBindingVariable().second)
            lifecycleOwner = this@BaseFragment
            executePendingBindings()
        }
    }


    fun handlePageDestination(
        id: Int,
        bundle: Bundle? = null,
        finishActivity: Boolean = false,
        transitionView: View? = null,
        transitionTitle: String? = null
    ) {

        if (finishActivity) {
            requireActivity().finish()
        }

        navigateSafe(id, bundle)
    }

    fun showAlertDialog(
        type: MessageOfRequestDialogFragment.MessageType,
        desc: String,
        dismissType: DismissType = DismissType.NORMAL,
        titleId: Int? = null
    ) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(type, desc, dismissType = dismissType, titleId = titleId)
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
    }


    fun getDatePicker(selectedJalaliDateStr: String? = null): MyPersianDatePickerDialog? {

        var shYear = 0
        var shMonth = 0
        var shDay = 0
        try {
            if (!selectedJalaliDateStr.isNullOrEmpty()) {
                val split = selectedJalaliDateStr.split('/')
                if (split.isNotEmpty() && split.size == 3) {
                    shYear = split[0].trim().toInt()
                    shMonth = split[1].trim().toInt()
                    shDay = split[2].trim().toInt()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Utility.getDatePicker(requireContext(), shYear, shMonth, shDay)
    }

    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200

    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true


    override fun onRetryClick(tag: String) {


    }

    open fun getGuideItemList(): MutableList<FancyShowCaseModel>? {

        return null
    }

    open fun getGuideDesc(): GuideFragment.GuideDataModel? {
//        val item = arguments?.getSerializable(Constants.GUID_TAG) as? ServiceItem?
//        Timber.tag("GUID_TAG").i("item = $item")
//        if (item != null && item.hintTitle.isNotEmpty() && item.hintTitle.isNotEmpty())
//            return GuideFragment.GuideDataModel(item.hintTitle, item.hintDesc)

        return null
    }

    fun saveRecyclerState(key: String, state: Parcelable?) {
        mViewModel?.recyclerState?.put(key, state)
    }

    fun loadRecyclerState(key: String): Parcelable? = mViewModel?.recyclerState?.get(key)

    fun setupToolbar(
        appBar: ViewAppbarServiceBinding?,
        appbarBackgroundImage: AppCompatImageView?,
        moreViews: View? = null,
        actionIconRes: Int? = null,
        onActionAppBarClickListener: ActionAppBarInterface.OnActionClickListener? = null,
        moreActionIconRes: Int? = null,
        onMoreActionAppBarClickListener: (() -> Unit)? = null,
    ) {

        this.onActionAppBarClickListener = onActionAppBarClickListener

        appBar?.apply {
            var toolbarTitle = ""

            arguments?.let {
                toolbarTitle = Utility.getToolbarTitle(it)

                tvTitle.text = toolbarTitle

                tvSubTitle.text = Utility.getToolbarSubTitle(it)
                tvSubSubTitle.text = Utility.getToolbarSub2(it)
                tvSubSubTitle.visibility =
                    if (tvSubSubTitle.text.isBlank()) View.GONE else View.VISIBLE

                val iconValue = it.get(Constants.TOOLBAR_ICON_IMAGE)

                if (iconValue is String) {
                    val image = Utility.getToolbarIconImage(it)
                    if (image.isNotBlank()) {
                        ImageUtils.loadImage(imgIcon, image)
                    } else {
                        imgIcon.setImageResource(Utility.getToolbarIconDrawable(it))
                    }
                }
            }

            toolbar.apply {
                actionIconRes?.let {
                    imgAction.apply {
                        setImageResource(it)
                        visibility = View.VISIBLE
                        setOnClickListener { onActionAppBarClickListener?.onActionClick() }
                    }
                }
                val guideDescription = getGuideDesc()

                if (moreActionIconRes != null) {
                    imgInfo.setImageResource(moreActionIconRes)
                    imgInfo.visible()
                } else if (guideDescription != null) {
                    imgInfo.visible()
                } else {
                    imgInfo.gone()
                }

                imageBack.setOnClickListener { backButtonPress() }
                if (imgInfo.isVisible) {
                    imgInfo.setOnClickListener {
                        if (guideDescription != null) {
                            showGuideDialog()
                        } else if (moreActionIconRes != null) {
                            onMoreActionAppBarClickListener?.invoke()
                        }
                    }
                }
            }

            appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                handleAlphaOnTitle(percentage, containerAppbarTitle)
                handleToolbarTitleVisibility(
                    percentage,
                    toolbar.tvToolbarTitle,
                    toolbarTitle,
                    appbarBackgroundImage,
                    moreViews
                )
            })
        }
    }

    fun setupCustomToolbar(
        appBar: ViewAppbarServiceStepperBinding?,
        appbarBackgroundImage: AppCompatImageView?,
        moreViews: View? = null,
        actionIconRes: Int? = null,
        onActionAppBarClickListener: ActionAppBarInterface.OnActionClickListener? = null,
    ) {

        this.onActionAppBarClickListener = onActionAppBarClickListener

        appBar?.apply {
            var toolbarTitle = ""

            arguments?.let {
                toolbarTitle = Utility.getToolbarTitle(it)

                tvTitle.text = toolbarTitle
                tvSubTitle.text = Utility.getToolbarSubTitle(it)

                val image = Utility.getToolbarIconImage(it)
                if (image.isBlank()) {
                    imgIcon.setImageResource(Utility.getToolbarIconDrawable(it))
                } else {
                    ImageUtils.loadImage(imgIcon, image)
                }
            }

            toolbar.apply {
                actionIconRes?.let {
                    imgAction.apply {
                        setImageResource(it)
                        visibility = View.VISIBLE
                        setOnClickListener { onActionAppBarClickListener?.onActionClick() }
                    }
                }

                imageBack.setOnClickListener { backButtonPress() }
                if (getGuideDesc() == null)
                    imgInfo.gone()
                imgInfo.setOnClickListener {
                    showGuideDialog()
                }
            }

            appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                handleAlphaOnTitle(percentage, containerAppbarTitle)
                handleToolbarTitleVisibility(
                    percentage,
                    toolbar.tvToolbarTitle,
                    toolbarTitle,
                    appbarBackgroundImage,
                    moreViews
                )
            })
        }
    }

    fun setupToolbarStepper(
        appBar: ViewAppbarServiceStepperBinding?,
        appbarBackgroundImage: AppCompatImageView?,
        moreViews: View? = null,
        actionIconRes: Int? = null,
        onActionAppBarClickListener: ActionAppBarInterface.OnActionClickListener? = null,
    ) {

        appBar?.apply {
            var toolbarTitle = ""
            arguments?.let { args ->
                ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(args))
                toolbarTitle = Utility.getToolbarTitle(args)
                tvTitle.text = toolbarTitle
            }
            toolbar.apply {
                actionIconRes?.let {
                    imgAction.apply {
                        setImageResource(it)
                        visibility = View.VISIBLE
                        setOnClickListener { onActionAppBarClickListener?.onActionClick() }
                    }
                }

                imageBack.setOnClickListener { backButtonPress() }
                if (getGuideDesc() == null)
                    imgInfo.gone()
                imgInfo.setOnClickListener {
                    showGuideDialog()
                }
            }
            toolbar.imgInfo.visibility = View.GONE
            appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                handleAlphaOnTitle(percentage, containerAppbarTitle)
                handleToolbarTitleVisibility(
                    percentage,
                    toolbar.tvToolbarTitle,
                    toolbarTitle,
                    appbarBackgroundImage,
                    moreViews
                )
            })
        }

    }

    private fun showGuideDialog() {
        if (getGuideDesc() == null)
            return
        var isRepetitious = mViewModel?.loadBoolean(getTapTargetTag()) ?: false

        val bundle = Bundle()
        bundle.putSerializable(ARG_GUIDE_MODEL, getGuideDesc()?.apply {
            firstTime = !isRepetitious
            tapTargetEnable = !getGuideItemList().isNullOrEmpty()
        })


        val guidDialog = GuideFragment.newInstance(bundle).apply {
            onDismissListener = object : GuideFragment.DismissListener {
                override fun onDismiss() {
                    if (!isRepetitious) {
                        showTapTargets()
                        mViewModel?.saveBoolean(getTapTargetTag(), true)
                    }
                }
            }
            onShowGuideClickListener = object : GuideFragment.ShowGuideClickListener {
                override fun onShowGuideClick() {
                    dismiss()
                    showTapTargets()
                }
            }
        }
        guidDialog.show(childFragmentManager, GuideFragment.javaClass.simpleName)


    }

    fun getTapTargetTag() = "TapTarget${javaClass.simpleName}"
    fun handleToolbarTitleVisibility(
        percentage: Float,
        toolbarTitle: AppCompatTextView,
        title: String?,
        appbarBackgroundImage: View?,
        moreViews: View?
    ) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(
                    toolbarTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                toolbarTitle.text = title
                appbarBackgroundImage?.let {
                    startAlphaAnimation(
                        it,
                        ALPHA_ANIMATIONS_DURATION.toLong(),
                        View.INVISIBLE
                    )
                }
                moreViews?.let {
                    startAlphaAnimation(
                        it,
                        ALPHA_ANIMATIONS_DURATION.toLong(),
                        View.INVISIBLE
                    )
                }


                mIsTheTitleVisible = true
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(
                    toolbarTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                toolbarTitle.text = ""
                appbarBackgroundImage?.let {
                    startAlphaAnimation(
                        it,
                        ALPHA_ANIMATIONS_DURATION.toLong(),
                        View.VISIBLE
                    )
                }
                moreViews?.let {
                    startAlphaAnimation(
                        it,
                        ALPHA_ANIMATIONS_DURATION.toLong(),
                        View.VISIBLE
                    )
                }
                mIsTheTitleVisible = false
            }
        }
    }

    fun handleAlphaOnTitle(
        percentage: Float,
        detailGroup: LinearLayoutCompat
    ) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(
                    detailGroup,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(
                    detailGroup,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = true
            }
        }
    }

    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation =
            if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }


    fun showTapTargets() {
        val items = getGuideItemList()

        val materialTapTargetSequence = MaterialTapTargetSequence()
        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        items?.forEach { view ->

            materialTapTargetSequence.addPrompt(
                Utility.createMaterialTapTargetPrompt(
                    requireActivity(),
                    view,
                    myTypeface
                )
            )
        }
        materialTapTargetSequence.show()


    }

    fun cancelSequenceMaterialTapTarge() {
        /*  materialTapTargetSequence.dismiss()
          isShowGide = false*/
    }

    var loadingView: LoadingView? = null
    fun showLoading() {
        (requireActivity() as? MainActivity)?.apply {
            showLoading(findViewById(R.id.parent))
        }
    }

    fun hideLoading() {
        //  loadingView?.hideLoading()
        (requireActivity() as? MainActivity)?.hideLoading()
    }


    open fun chooseImage(requestCode: Int = Constants.REQUEST_LUNCHER) {}

    private fun handleServiceError(result: BaseResponseNew) {

        val map = HashMap<String, String>()
        map["CLASS"] = this.javaClass.simpleName
        map["METHOD"] = "handleServiceError"
        map["message"] = result.getMessage()
        map["code"] = result.getCode().toString()

        //TaminLogger.putLog(map)


        var message = result.getMessage()
        val resultName = result.javaClass.simpleName

        message = when {
            result is LoginResponse && result.getCode() == 500 ->
                getString(R.string.error_check_username_or_password)

            resultName == getString(R.string.edictpensionerresponse) && result.getCode() == 500 ->
                getString(R.string.error_edict_not_found)

            resultName == getString(R.string.pdfdownloadresponse) && this.javaClass.simpleName == getString(
                R.string.edictpensionerfragment
            ) && result.getCode() == 500 ->
                getString(R.string.error_edict_pdf_download)

            else -> message
        }

        if (resultName in dismissDialogForResponse)
            return

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()


        dialog.arguments = createBundle(
            when {
                result.isNeedNetwork -> MessageOfRequestDialogFragment.MessageType.INFO
                result.needRefreshToken -> MessageOfRequestDialogFragment.MessageType.REFRESHTOKEN
                else -> MessageOfRequestDialogFragment.MessageType.ERROR
            },
            message,
            dismissType = if (resultName in finishPageForResponse || result.getCode() == 502 || result.getCode() == 1000 || result.isBackToPrevious/*JsonSyntaxException*/) DismissType.BACK_TO_PREVIOUS else DismissType.NORMAL
        )
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")

    }

    open fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
    }

}