package com.tamin.taminhamrah.ui.profile

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.databinding.FragmentProfileBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.CustomTabsLauncher
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.login.usermode.UserModeDialogFragment
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.isPermissionsAllowed
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(),
    DialogResultInterface.OnResultListener<MenuModel>,
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var webLauncher: CustomTabsLauncher
    private lateinit var listAdapter: NavigatorAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun setupObserver() {
        mViewModel.mldProfile.observe(this, ::showResult)

        mViewModel.mldUserAvatar.observe(this) {
            ImageUtils.loadUserAvatar(viewDataBinding?.imgProfile, mViewModel.getUserAvatar())
        }
        mViewModel.mldProfileStatus.observe(this) {
            when (it) {
                ProfileViewModel.ProfileDataState.ERROR -> {

                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        it.message
                    )
                }

                else -> {

                }
            }
        }
    }

    override fun initView() {
        webLauncher.registerLifecycle(viewLifecycleOwner)
        if (arguments?.getBoolean("showBackButton") == true)
            viewDataBinding?.layHeader?.imbBack?.apply {
                visible()
                setOnClickListener {
                    findNavController().popBackStack()
                }
            }

        ImageUtils.loadUserAvatar(viewDataBinding?.imgProfile, mViewModel.getUserAvatar())
        listAdapter = NavigatorAdapter()

        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }
        listAdapter.setItems(mViewModel.getItemsList(), this)


    }

    override fun getData() {
        mViewModel.getUserInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnEdiImage.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    getString(R.string.lable_get_image_from_sabte_ahval)
                )
                bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_camera)
                handlePageDestination(R.id.action_profile_to_edit_image, bundle)
            }


            btnUserMode.setOnClickListener {
                val dialog = UserModeDialogFragment()
                dialog.setResultListener(this@ProfileFragment)
                dialog.show(childFragmentManager, "trjthlgkj")
            }

            groupAnswering.setOnClickListener {
                // call1420()
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        ("tel:" + getString(R.string.tel_1420)).toUri()
                    )
                )
            }
        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    private fun showResult(result: ProfileModel) {
        viewDataBinding?.apply {
            tvUsername.text = result.fullName
            tvNationalCode.text = result.nationalCode
            tvMobile.text = result.phonenumber
        }
    }

    override fun onDialogResult(userMode: MenuModel) {
        mViewModel.setEmployerMode(userMode.title)
        requireActivity().recreate()
        (requireActivity() as MainActivity).redirectToServicesFragment()
        /* showAlertDialog(
             MessageOfRequestDialogFragment.MessageType.INFO,
             getString(
                 R.string.message_success_change_userMode,
                 userMode.title
             )
         )*/


        /* handlePageDestination(
             R.id.action_user_mode_to_home,
             finishActivity = true
         )*/
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        when (item.id) {
            "1" -> {
                handlePageDestination(
                    R.id.action_profile_to_identity_info,
                    createToolbarBundle(item)
                )
            }

            "2" -> {
                handlePageDestination(
                    R.id.action_profile_to_deserved_treatment,
                    createToolbarBundle(item)
                )
            }

            "3" -> {
                handlePageDestination(
                    R.id.action_profile_to_active_relation,
                    createToolbarBundle(item)
                )
            }

            "4" -> {
                handlePageDestination(
                    R.id.action_profile_to_DependentsFragment,
                    createToolbarBundle(item)
                )
            }

            "5" -> {
                handlePageDestination(
                    R.id.action_profile_to_myElectronicFile,
                    createToolbarBundle(item)
                )
            }
            "6" -> {
                handlePageDestination(
                    R.id.action_profile_to_bank_account_list,
                    createToolbarBundle(item)
                )
            }

            "7" -> {
                handlePageDestination(
                    R.id.action_profile_to_cancel_dependent_fragment,
                    createToolbarBundle(item)
                )
            }

            "8" -> {
                val bundle = createToolbarBundle(item)
                handlePageDestination(R.id.action_profile_to_edit, bundle)
            }
            /*"6" -> {
                Utility.sendShare(
                    requireActivity(),
                    getString(R.string.cafe_bazar_share_link),
                    item.title
                )
            }*/
            "10" -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WITHOUT_TITLE,
                    getString(R.string.label_are_you_sure_to_exit),
                    btnCancel=true
                )
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.revokeRefreshToken()
                        mViewModel.logOut()
                        (requireActivity() as? MainActivity)?.resetView()

                        val url = "${Constants.BASE_URL_ACCOUNT}signout?" +
                                "redirect_uri=mytamin://logout" +
                                "&response_type=assertion" +
                                "&client_id=${Constants.CLIENT_ID}"

                        webLauncher.launchUrl(url, launchInBrowser = true) { intent ->
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        }
                    }

                    override fun onCancelClick() {
                        dialog.dismiss()
                    }
                }
                )
                dialog.show(childFragmentManager, "ExitFromApp")


            }

            "9" -> {
                val bundle = createToolbarBundle(item)
                bundle.putBoolean("showBackButton", true)
                handlePageDestination(R.id.settingsFragment, bundle)
            }
        }
    }


    private fun call1420() {
        val permission = Manifest.permission.CALL_PHONE
        if (isPermissionsAllowed(permission)) {
            callPhone()
        } else {
//            checkPermission()
            permissionLauncher.launch(arrayOf(permission))
        }
    }

    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { perm ->
                if (perm.key == "android.permission.CALL_PHONE") {
                    if (perm.value) {
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
            Intent(Intent.ACTION_CALL, ("tel:" + getString(R.string.tel_1420)).toUri())
        startActivity(intent)
    }

}