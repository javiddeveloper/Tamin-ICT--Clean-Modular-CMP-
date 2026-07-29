package com.tamin.taminhamrah.ui.home.dashboard

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.FragmentDrawerBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint

import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility.standardWebFlags
import com.tamin.taminhamrah.utils.extentions.createBundle
import jakarta.inject.Inject
import java.lang.Exception

@AndroidEntryPoint
class DrawerFragment : BaseFragment<FragmentDrawerBinding, DrawerFragmentViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModel: DrawerFragmentViewModel by viewModels()

    @Inject
    lateinit var webLauncher: CustomTabsLauncher

    private lateinit var listAdapter: NavigatorAdapter

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_drawer


    override fun setupObserver() {

    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        ImageUtils.loadUserAvatar(viewDataBinding?.imgProfile, mViewModel.getUserAvatar())
        listAdapter = NavigatorAdapter()

        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }
        viewDataBinding?.imgProfile?.setOnClickListener {
            (requireActivity() as MainActivity).closeDrawer()
            handlePageDestination(
                R.id.profileFragment
            )

        }


        listAdapter.setItems(mViewModel.getItemsList(), this)
    }

    override fun getData() {
        if (mViewModel.mldProfile.value == null)
            mViewModel.getProfileInfo()
    }

    override fun onClick() {
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        (requireActivity() as? MainActivity)?.closeDrawer()
        try {
            when (item.id) {
                "1" -> {
                    webLauncher.launchUrl(Constants.LINK_1420) { intent ->
                        intent.standardWebFlags()
                    }

                }
                "2" -> {
                    Utility.sendShare(requireActivity(), getString(R.string.cafe_bazar_share_link), item.title)
                }
                "3" -> {
                    handlePageDestination(
                        R.id.action_drawer_to_contact_us,
                        createToolbarBundle(item)
                    )
                }
                "4" -> {
                    handlePageDestination(
                        R.id.action_drawer_to_versioning,
                        createToolbarBundle(item)
                    )
                }
                "5" -> {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.label_are_you_sure_to_exit),
                        btnCancel=true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {

                            mViewModel.logOut()
                            handlePageDestination(R.id.action_profile_to_login, finishActivity = true)
                        }

                        override fun onCancelClick() {
                            dialog.dismiss()
                        }
                    }
                    )
                    dialog.show(childFragmentManager, "ExitFromApp")
                }
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

}