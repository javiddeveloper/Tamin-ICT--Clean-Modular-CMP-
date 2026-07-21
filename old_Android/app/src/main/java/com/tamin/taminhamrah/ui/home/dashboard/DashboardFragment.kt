package com.tamin.taminhamrah.ui.home.dashboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.ServiceItem
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.databinding.FragmentDashboardBinding
import com.tamin.taminhamrah.enums.EnumUserMode
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.viewInspectionsPerformedByOrganization.ListOfInspectionsPerformedFragment
import com.tamin.taminhamrah.ui.login.usermode.UserModeDialogFragment
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.startInfiniteBounce
import com.tamin.taminhamrah.utils.extentions.stopInfiniteBounce
import com.tamin.taminhamrah.utils.imageAnimator.setLookDirection
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ArcMotion
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.transition.MaterialContainerTransform
import com.tamin.taminhamrah.utils.extentions.dpToPx
import timber.log.Timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, ServicesViewModel>(),
    DialogResultInterface.OnResultListener<MenuModel>,
    AdapterInterface.OnItemClickListener<ServiceItem> {

    override val mViewModel: ServicesViewModel by viewModels()

    private var aiAnimationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 1000L
            scrimColor = Color.TRANSPARENT
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 1000L
            scrimColor = Color.TRANSPARENT
            addListener(object : androidx.transition.Transition.TransitionListener {
                override fun onTransitionStart(transition: androidx.transition.Transition) {
                    viewDataBinding?.fabLawAi?.stopInfiniteBounce()
                }

                override fun onTransitionEnd(transition: androidx.transition.Transition) {
                    if (mViewModel.isAiIntroPlayed) {
                        viewDataBinding?.fabLawAi?.startInfiniteBounce(
                            distance = 15f,
                            durationMs = 2500L
                        )
                    }
                }

                override fun onTransitionCancel(transition: androidx.transition.Transition) {}
                override fun onTransitionPause(transition: androidx.transition.Transition) {}
                override fun onTransitionResume(transition: androidx.transition.Transition) {}
            })
        }
    }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    val listAdapter: DashboardAdapter by lazy { DashboardAdapter() }

    override fun getLayoutId() = R.layout.fragment_dashboard

    override fun setupObserver() {
        mViewModel.mldDashboardServices.observe(this, ::onDashboardServicesResponse)
        mViewModel.mldUserAvatar.observe(this) {
            ImageUtils.loadUserAvatar(
                viewDataBinding?.toolbar?.imgProfile,
                mViewModel.getUserAvatar()
            )
        }
        mViewModel.mldIsChatAllowed.observe(this) { isAllowed ->
            Timber.tag("AI_DEBUG").d("mldIsChatAllowed emit: $isAllowed")
           handleAiFabVisibility(isAllowed)
        }
    }

    override fun initView() {
        viewDataBinding?.recycler?.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        initSpeedDial()
    }

    override fun onDestroyView() {
        aiAnimationJob?.cancel()
        viewDataBinding?.fabLawAi?.stopInfiniteBounce()
        super.onDestroyView()
    }

    private fun applyFabCornerConstraints() {
        val parent = viewDataBinding?.dashboardRoot ?: return
        val fab = viewDataBinding?.fabLawAi ?: return
        val set = ConstraintSet()
        set.clone(parent)
        set.clear(fab.id, ConstraintSet.TOP)
        set.clear(fab.id, ConstraintSet.END)
        set.connect(
            fab.id,
            ConstraintSet.BOTTOM,
            parent.id,
            ConstraintSet.BOTTOM,
            20.dpToPx(requireContext())
        )
        set.connect(
            fab.id,
            ConstraintSet.START,
            parent.id,
            ConstraintSet.START,
            20.dpToPx(requireContext())
        )
        set.applyTo(parent)
    }

    private fun applyFabCenterConstraints() {
        val parent = viewDataBinding?.dashboardRoot ?: return
        val fab = viewDataBinding?.fabLawAi ?: return
        val set = ConstraintSet()
        set.clone(parent)
        set.connect(fab.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
        set.connect(fab.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)
        set.connect(fab.id, ConstraintSet.START, parent.id, ConstraintSet.START)
        set.connect(fab.id, ConstraintSet.END, parent.id, ConstraintSet.END)
        set.applyTo(parent)
    }

    private fun handleAiFabVisibility(isAllowed: Boolean) {
        aiAnimationJob?.cancel()

        val fab = viewDataBinding?.fabLawAi ?: return
        val parent = viewDataBinding?.dashboardRoot ?: return
        val helpCard = viewDataBinding?.cardAiHelp ?: return

        Timber.tag("AI_DEBUG")
            .d("handleAiFabVisibility: isAllowed=$isAllowed, isAiIntroPlayed=${mViewModel.isAiIntroPlayed}")

        if (isAllowed) {
            fab.load(R.drawable.ic_direct)
            fab.stopInfiniteBounce()

            if (mViewModel.isAiIntroPlayed) {
                applyFabCornerConstraints()

                helpCard.visibility = View.GONE
                helpCard.alpha = 0f

                if (fab.isLaidOut) {
                    aiAnimationJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(1050)
                        fab.visibility = View.VISIBLE
                        fab.alpha = 1f
                        fab.startInfiniteBounce(distance = 15f, durationMs = 2500L)
                    }
                } else {
                    fab.visibility = View.VISIBLE
                    fab.alpha = 1f
                    fab.startInfiniteBounce(distance = 15f, durationMs = 2500L)
                }
                return
            }

            aiAnimationJob = viewLifecycleOwner.lifecycleScope.launch {
                Timber.tag("AI_DEBUG").d("aiAnimationJob: Starting Coroutine")

                // 1. Setup initial state in center
                applyFabCenterConstraints()
                fab.visibility = View.VISIBLE
                fab.alpha = 0f

                // 2. Fade in at the center
                fab.animate().alpha(1f).setDuration(1000).start()
                delay(1000)

                // 3. Play internal "look" animations while in center
                delay(500)
                setLookDirection(fab, requireContext(), true)
                delay(1500)
                setLookDirection(fab, requireContext(), false)
                delay(1000)

                // 4. Move to corner
                val moveTransition = TransitionSet().apply {
                    addTransition(ChangeBounds().apply {
                        setPathMotion(ArcMotion().apply {
                            maximumAngle = 90f
                            minimumHorizontalAngle = 15f
                            minimumVerticalAngle = 0f
                        })
                    })
                    duration = 1200
                    interpolator = FastOutSlowInInterpolator()
                }
                TransitionManager.beginDelayedTransition(parent, moveTransition)
                fab.animate()
                    .scaleX(1.2f).scaleY(1.2f)
                    .setDuration(600)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction {
                        fab.animate()
                            .scaleX(1.0f).scaleY(1.0f)
                            .setDuration(600)
                            .setInterpolator(FastOutSlowInInterpolator())
                            .start()
                    }.start()
                applyFabCornerConstraints()

                // Wait for the move to finish
                delay(1500)

                // 5. Final state: bounce and show help card
                fab.startInfiniteBounce(distance = 15f, durationMs = 2500L)

                showCard(helpCard)
                viewDataBinding?.txtAiHelpTitle?.animateText(getString(R.string.help_ai_message))

                mViewModel.isAiIntroPlayed = true

                // 6. Auto-hide the card after a delay
                delay(7000)
                hideCard(helpCard)
            }
        } else {
            mViewModel.isAiIntroPlayed = false
            fab.stopInfiniteBounce()
            fab.clearAnimation()
            fab.visibility = View.GONE

            helpCard.animate().cancel()
            helpCard.clearAnimation()
            helpCard.visibility = View.GONE
            helpCard.alpha = 0f
        }
    }

    override fun getData() {
        checkUserMode()
        mViewModel.getServiceForDashboard()
        mViewModel.getUserInfo()
        mViewModel.checkChatAllowed()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            toolbar.parent?.setOnClickListener {
                (requireActivity() as MainActivity).openDrawer()
            }

            fabLawAi.setOnClickListener {
                mViewModel.isAiIntroPlayed = true
                fabLawAi.stopInfiniteBounce()
                val action = DashboardFragmentDirections.actionDashboardToAiFragment("")
                val extras = FragmentNavigatorExtras(
                    fabLawAi to "shared_fab"
                )
                findNavController().navigate(action, extras)
            }
            imgCloseHelpAi.setOnClickListener {
                viewDataBinding?.cardAiHelp?.let { hideCard(it) }
            }
        }
    }

    private fun onDashboardServicesResponse(result: ServiceResponseModelNew) {
        val list: MutableList<ServiceItem> = ArrayList()
        mViewModel.getAcraConfig()
        if (result.isSuccess) {
            result.data?.let { rawData ->
                for (service in rawData) {
                    if (service.type == mViewModel.getSelectedModeValue() && service.active) {
                        if (service.hiddenForVersions.isNullOrEmpty() || service.hiddenForVersions?.contains(
                                BuildConfig.VERSION_CODE
                            ) == false
                        )
                            list.add(service)
                    }
                }
            }
        }

        if (list.isEmpty()) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.REFRESHTOKEN,
                getString(R.string.message_need_to_refresh_token)
            )
        }

        val isDarkMode = context?.let {
            (it.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        } ?: false

        listAdapter.setItems(list.apply { sortBy { it.sorting } }, this, isDarkMode)
    }

    private fun initSpeedDial() {
        addFabAActionItem(
            R.id.fab_1420,
            R.drawable.ic_phone,
            getString(R.string.label_cantact_to_1420)
        )

        addFabAActionItem(
            R.id.fab_user_mode,
            R.drawable.ic_user_mode,
            getString(R.string.label_change_user_mode)
        )
        viewDataBinding?.speedDialView?.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_1420 -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + getString(R.string.tel_1420))
                        )
                    )
                }

                R.id.fab_user_mode -> {
                    val dialog = UserModeDialogFragment()
                    val bundle = Bundle()
                    dialog.arguments = bundle
                    dialog.setResultListener(this@DashboardFragment)
                    dialog.show(childFragmentManager, "trjthlgkj")
                    return@OnActionSelectedListener false
                }

                R.id.fab_contact_us -> {
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.TOOLBAR_TITLE,
                        getString(R.string.label_contact_us)
                    )
                    bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_phone)
                    handlePageDestination(R.id.action_service_to_contact_us, bundle)
                    return@OnActionSelectedListener false
                }
            }
            true
        })
    }

    private fun addFabAActionItem(
        actionId: Int,
        actionDrawableId: Int,
        label: String
    ) {
        val theme = requireActivity().theme
        viewDataBinding?.speedDialView?.addActionItem(
            SpeedDialActionItem
                .Builder(
                    actionId,
                    AppCompatResources.getDrawable(requireContext(), actionDrawableId)
                )
                .setFabImageTintColor(
                    ResourcesCompat.getColor(
                        resources,
                        android.R.color.white,
                        theme
                    )
                )
                .setLabel(label)
                .setLabelColor(Color.GRAY)
                .create()
        )?.apply {
            speedDialActionItem = speedDialActionItemBuilder.create()
        }
    }

    override fun onDialogResult(item: MenuModel) {
        mViewModel.setEmployerMode(item.title)
        requireActivity().recreate()
    }

    private fun checkUserMode() {
        when (mViewModel.getSelectedModeValue()) {
            EnumUserMode.MODE_INSURED.methodValue -> {
                val userType = mViewModel.getInsuredType()
                if (userType == EnumTypeUser.ANONYMOUS.title || userType == EnumTypeUser.TEMPORARY.title) {
                    mViewModel.checkInsuredInfo()
                }
            }

            EnumUserMode.MODE_PENSIONER.methodValue -> {
                val userType = mViewModel.getPensionerType()
                if (userType == EnumTypeUser.ANONYMOUS.title || userType == EnumTypeUser.TEMPORARY.title) {
                    mViewModel.checkPensionInfo()
                }
            }
        }
    }

    private fun handleNavigation(item: ServiceItem) {
        val destinationId = Utility.getPageIdByModelId(item.id)
        if (destinationId != null)
            handlePageDestination(
                destinationId,
                bundle = createToolbarBundle(item)
            )
        else
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                requireContext().resources.getString(R.string.message_not_implemented)
            )
    }

    fun createToolbarBundle(item: ServiceItem): Bundle {
        val bundle = Bundle()
        when (item.id) {
            34 -> bundle.putSerializable(
                ContractBaseFragment.ARG_CONTACT_TYPE,
                EnumInsuranceType.TYPE_STUDENT
            )

            36 -> bundle.putSerializable(
                ContractBaseFragment.ARG_CONTACT_TYPE,
                EnumInsuranceType.TYPE_WOMAN
            )

            37 -> bundle.putSerializable(
                ContractBaseFragment.ARG_CONTACT_TYPE,
                EnumInsuranceType.TYPE_OPTIONAL
            )

            33 -> bundle.putSerializable(
                ContractBaseFragment.ARG_CONTACT_TYPE,
                EnumInsuranceType.TYPE_FREELANCE
            )

            1008 -> bundle.putBoolean(ListOfInspectionsPerformedFragment.ARG_IS_WORKSHOP, true)
        }
        bundle.putSerializable(Constants.GUID_TAG, item)
        bundle.putString(Constants.TOOLBAR_TITLE, item.name)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.subtitle)
        bundle.putInt(Constants.SERVICE_ID, item.id)
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, item.getImageUrl())
        return bundle
    }

    override fun onItemClick(item: ServiceItem, transitionView: View?, tag: String?) {
        if (item.showRole.isNullOrEmpty())
            handleNavigation(item)
        else {
            var userType: String? = EnumTypeUser.ANONYMOUS.title
            when (mViewModel.getSelectedModeValue()) {
                EnumUserMode.MODE_INSURED.methodValue -> userType = mViewModel.getInsuredType()
                EnumUserMode.MODE_PENSIONER.methodValue -> userType = mViewModel.getPensionerType()
            }

            when (userType) {
                EnumTypeUser.TEMPORARY.title, EnumTypeUser.ANONYMOUS.title -> handleNavigation(item)
                EnumTypeUser.INSURED.title -> {
                    if (item.showRole?.contains(1) == true) {
                        val message = mViewModel.getInsuredMessage()
                        if (message != null && item.type == 1) {
                            DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                                arguments = createBundle(
                                    MessageOfRequestDialogFragment.MessageType.WARNING,
                                    message
                                )
                                setDialogClickListener(object :
                                    DialogClickInterface.onClickListener {
                                    override fun onConfirmClick() {
                                        handleNavigation(item)
                                    }

                                    override fun onCancelClick() {}
                                })
                            }.show(childFragmentManager, "Alert Dialog MessageOfRequest")
                        } else handleNavigation(item)
                    } else {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_active_relation_user_is_insured)
                        )
                    }
                }

                EnumTypeUser.PENSIONER.title -> {
                    if (item.showRole?.contains(2) == true) handleNavigation(item)
                    else showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_active_relation_user_is_pensioner)
                    )
                }
            }
        }
    }

    override fun refreshView() {
        super.refreshView()
        Timber.tag("AI_DEBUG").d("refreshView: fragment refreshed/returned to")
        // Force trigger the observer or fetch from network if TTL expired
        mViewModel.checkChatAllowed()
    }


    private fun showCard(card: View) {
        card.visibility = View.VISIBLE
        card.alpha = 0f
        card.scaleX = 0.8f
        card.scaleY = 0.8f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start()
    }

    private fun hideCard(card: View) {
        card.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(200).withEndAction {
            card.alpha = 0f
            card.visibility = View.GONE
        }.start()
    }
}
