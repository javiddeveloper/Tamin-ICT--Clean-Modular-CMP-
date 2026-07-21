package com.tamin.taminhamrah.ui.home.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.data.local.services.entity.AppliedServiceEntity
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModel
import com.tamin.taminhamrah.databinding.FragmentServicesBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.login.usermode.UserModeDialogFragment
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@Deprecated("Replaced with DashboardFragment")
@AndroidEntryPoint
class ServicesFragment : BaseFragment<FragmentServicesBinding, ServicesViewModel>(),
    DialogResultInterface.OnResultListener<MenuModel>,
    AdapterInterface.OnItemClickListener<ServiceModel>,
    AdapterInterface.OnShowMoreClickListener<ServiceMainModel> {

    private val TAG_RECYCLER_VIEW_SCOLL_STATE = "TAG_RECYCLER_VIEW_SCOLL_STATE"

    lateinit var listAdapter: ServiceMainAdapter

    private var resetActivity = false
    override val mViewModel: ServicesViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_services
    }

    override fun setupObserver() {
        mViewModel.getAppliedServices().observe(viewLifecycleOwner, ::onAppliedService)
        mViewModel.mldServices.observe(viewLifecycleOwner, ::showServices)
    }

    var appliedServiceIsOpen = true
    fun onAppliedService(list: List<AppliedServiceEntity>) {
        if (list.isNotEmpty()) {
            val appliedService: MutableList<ServiceModel> = ArrayList()
            for (item in list) {
                appliedService.add(item.serviceModel)
                Timber.tag("database:").d(item.serviceModel.name ?: "66666666666666")
            }

            setupAppliedServiceRecycler(appliedService)

        } else {
            viewDataBinding?.appliedServiceLayout?.root?.gone()
        }

    }

    private fun setupAppliedServiceRecycler(appliedService: MutableList<ServiceModel>) {
        viewDataBinding?.appliedServiceLayout?.apply {
            title = getString(R.string.applied_services)
            serviceCount = appliedService.size.toString()
            serviceAdapter = AppliedServiceAdapter().apply {
                setItems(appliedService)
                onItemClickListener = this@ServicesFragment
            }

            appliedRecycler.apply {
                if (itemDecorationCount == 0)
                    addItemDecoration(UiUtils.HorizontalItemMarginDecoration(40))

                (layoutManager as LinearLayoutManager).onRestoreInstanceState(
                    loadRecyclerState(
                        AppliedServiceAdapter.tag
                    )
                )
            }

            /*  arrow.setOnClickListener {
                  updateListVisibility()
              }
              tvSeeAll.setOnClickListener {
                  updateListVisibility()
              }*/
        }

        viewDataBinding?.appliedServiceLayout?.root?.visible()
    }

    private fun updateListVisibility() {
        viewDataBinding?.appliedServiceLayout?.apply {
            if (appliedServiceIsOpen) {
                arrow.animate().rotation(0f).duration = 400


                /*appliedRecycler.animate()
                    .translationY(0f)
                    .alpha(0.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            appliedRecycler.visibility = View.GONE
                        }
                    })*/


                appliedRecycler.visibility = View.GONE
            } else {
                arrow.animate().rotation(-90f).duration = 400
                appliedRecycler.visibility = View.VISIBLE

/*
                appliedRecycler.animate()
                    .translationY(0f)
                    .alpha(1.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            appliedRecycler.visibility = View.VISIBLE
                        }
                    })*/

            }
            appliedServiceIsOpen = !appliedServiceIsOpen
        }

    }

    override fun onStop() {
        for (item in listAdapter.getItems())
            saveRecyclerState(item.title ?: item.toString(), item.scrollState)

        saveRecyclerState(
            AppliedServiceAdapter.tag,
            (viewDataBinding?.appliedServiceLayout?.appliedRecycler?.layoutManager as LinearLayoutManager).onSaveInstanceState()
        )

        super.onStop()
    }

    override fun initView() {
        ImageUtils.loadUserAvatar(viewDataBinding?.toolbar?.imgProfile, mViewModel.getUserAvatar())
        listAdapter = ServiceMainAdapter()
        viewDataBinding?.recycler?.adapter = listAdapter
        initSpeedDial()

    }

    private fun initSpeedDial() {

        addFabAActionItem(
            R.id.fab_1420,
            R.drawable.ic_phone,
            getString(R.string.label_cantact_to_1420)
        )
        /*   addFabAActionItem(
               R.id.fab_contact_us,
               R.drawable.ic_ringing_phone,
               getString(R.string.label_contact_us))*/
        addFabAActionItem(
            R.id.fab_user_mode,
            R.drawable.ic_user_mode,
            getString(R.string.label_change_user_mode)
        )
        addFabAActionItem(
            R.id.fab_filter,
            R.drawable.ic_filter_2,
            getString(R.string.label_filter_by_title)
        )
        viewDataBinding?.speedDialView?.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_1420 -> {
                    call1420()
                }

                R.id.fab_filter -> {
                    val dialog = FilterDialogFragment()
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(
                        FilterDialogFragment.ARG_FILTER_LIST,
                        mViewModel.getFilterList()
                    )
                    bundle.putString(
                        FilterDialogFragment.ARG_FILTER_TITLE,
                        getString(R.string.label_filter_by_title)
                    )
                    dialog.arguments = bundle

                    dialog.setListener(object : MenuInterface.OnResultListItem {
                        override fun onResult(itemList: List<MenuModel>) {

                            mViewModel.mldMainServiceList.value?.forEach { service ->
                                itemList?.onEach { menuItem ->
                                    if (service.title == menuItem.title)
                                        service.isSelected = menuItem.isSelected
                                }
                            }
                            mViewModel.filterByTitle()
                        }
                    })

                    dialog.show(childFragmentManager, "FilterDialogFragment")
                    return@OnActionSelectedListener false // false will close it without animation
                }
                R.id.fab_user_mode -> {
                    val dialog = UserModeDialogFragment()
                    val bundle = Bundle()
                    dialog.arguments = bundle
                    dialog.setResultListener(this@ServicesFragment)
                    dialog.show(childFragmentManager, "trjthlgkj")
                    return@OnActionSelectedListener false // false will close it without animation
                }
                R.id.fab_contact_us -> {
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.TOOLBAR_TITLE,
                        getString(R.string.label_contact_us)
                    )
                    bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_phone)
                    handlePageDestination(R.id.action_service_to_contact_us, bundle)
                    return@OnActionSelectedListener false // false will close it without animation
                }
            }
            true // To keep the Speed Dial open
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
//                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, theme))
                .create()
        )?.apply {
            speedDialActionItem = speedDialActionItemBuilder.create()
        }
    }

    override fun getData() {
        // throw NetworkOnMainThreadException()

//        mViewModel.getServices()
        //  if (resetActivity)
        //  requireActivity().recreate()
    }

    override fun onClick() {

        viewDataBinding?.apply {

            toolbar.imgFilter.setOnClickListener {
                val dialog = FilterDialogFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    FilterDialogFragment.ARG_FILTER_LIST,
                    mViewModel.getFilterList()
                )
                bundle.putString(
                    FilterDialogFragment.ARG_FILTER_TITLE,
                    getString(R.string.label_filter_by_title)
                )
                dialog.arguments = bundle

                dialog.setListener(object : MenuInterface.OnResultListItem {
                    override fun onResult(itemList: List<MenuModel>) {

                        mViewModel.mldMainServiceList.value?.forEach { service ->
                            itemList?.onEach { menuItem ->
                                if (service.title == menuItem.title)
                                    service.isSelected = menuItem.isSelected
                            }
                        }
                        mViewModel.filterByTitle()
                    }
                })

                dialog.show(childFragmentManager, "FilterDialogFragment")
            }

            /*toolbar.imgProfile.setOnClickListener {
                val dialog = UserModeDialogFragment()
                val bundle = Bundle()
                bundle.putBoolean(UserModeDialogFragment.ARG_ENABLE_LOG_OUT_BUTTON, true)
                bundle.putString(
                    UserModeDialogFragment.ARG_USER_NATIONAL_CODE,
                    mViewModel.getUserLocalInfo().nationalCode
                )
                dialog.arguments = bundle
                dialog.setResultListener(this@ServicesFragment)
                dialog.show(childFragmentManager, "trjthlgkj")
            }*/

            searchView.apply {
                setOnClickListener { isIconified = false }
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(searchStr: String?): Boolean {
                        appliedServiceLayout.root.visibility = View.GONE

                        if (searchStr?.isNotBlank() == true) {
                            mViewModel.searchList(searchStr)
                        }
                        return true
                    }

                    override fun onQueryTextChange(searchStr: String?): Boolean {
                        if (searchStr?.isNotBlank() == true) {
                            mViewModel.searchList(searchStr)
                            appliedServiceLayout.appliedRecycler.adapter?.itemCount?.let {
                                appliedServiceLayout.root.visibility = View.GONE
                            }
                        } else {
                            mViewModel.getAllList()

                            appliedServiceLayout.appliedRecycler.adapter?.itemCount?.let {
                                if (it >= 1)
                                    appliedServiceLayout.root.visibility = View.VISIBLE
                            }
                        }
                        return true
                    }
                })
                setOnCloseListener {
                    view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                    mViewModel.getAllList()
                    appliedServiceLayout.appliedRecycler.adapter?.itemCount?.let {
                        if (it >= 1)
                            appliedServiceLayout.root.visibility = View.VISIBLE
                    }
                    false
                }
            }
        }
    }

    var isFirstTime = true

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showServices(result: ServiceResponseModel) {

        if (result.isSuccess) {
            //    showGide()
            result.data?.menu?.get(0)?.groups.let {
                if (it.isNullOrEmpty()) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.message_empty_list)
                    )
                }

                if (it != null) {
                    for (item in it)
                        item.scrollState = loadRecyclerState(item.title ?: item.toString())


//                    for (i in mViewModel.childRecyclerScrollState.indices)
//                        it[i].scrollState = mViewModel.childRecyclerScrollState[i]

                    listAdapter.setItems(it, this, this)
                }

                if (resetActivity)
                    requireActivity().recreate()
            }

        }
    }

    override fun onShowMoreClick(item: ServiceMainModel, transitionView: View?, tag: String?) {
        /*   val bundle = Bundle()
       bundle.putString(SearchServiceFragment.ARG_SELECTED_SERVICE_ITEM_TITLE, item.title)
       bundle.putInt(SearchServiceFragment.ARG_SELECTED_SERVICE_ITEM_TYPE, item.type ?: 0)
       handlePageDestination(R.id.action_servicesFragment_to_searchServiceFragment, bundle)*/
    }

    override fun onItemClick(item: ServiceModel, transitionView: View?, tag: String?) {

        handleNavigation(item)

    }

    private fun handleNavigation(item: ServiceModel) {
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

    fun createToolbarBundle(item: ServiceModel): Bundle {
        val bundle = Bundle()
      //  bundle.putParcelable("serviceItem",item)
        bundle.putString(Constants.TOOLBAR_TITLE, item.name)


        bundle.putInt(Constants.SERVICE_ID, item.id)
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, item.getImageUrl())
        return bundle
    }

    override fun onDialogResult(userMode: MenuModel) {

        mViewModel.setEmployerMode(userMode.title)
        Timber.tag("DBTestRezaei").i("onDialogResult: isFirstTime=true called")
        resetActivity = true
        getData()

        //   requireActivity().recreate()
        /*    showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.INFO,
            getString(
                R.string.message_success_change_userMode,
                userMode.title
            )
        )*/
    }

    private fun call1420() {
//        val permission = Manifest.permission.CALL_PHONE
//        if (isPermissionsAllowed(permission)) {
        callPhone()
//        } else {
//            checkPermission()

//            permissionLauncher.launch(arrayOf(permission))
//        }
    }

    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { perm ->
                if (perm.key == "android.permission.CALL_PHONE") {
                    if (perm.value == true) {
                        callPhone()
                    } else {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.message_denied_call_permission)
                        )
                    }
                }
            }

        }

    private fun callPhone() {
        val intent =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.tel_1420)))
        startActivity(intent)
    }

}


