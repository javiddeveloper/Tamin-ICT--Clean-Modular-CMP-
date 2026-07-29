package com.tamin.taminhamrah.ui.login.usermode

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UserInfo
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.databinding.DialogUserModeBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseDialogFragment
import com.tamin.taminhamrah.ui.login.LoginViewModel
import com.tamin.taminhamrah.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserModeDialogFragment :
    BaseDialogFragment<DialogUserModeBinding>(DialogUserModeBinding::inflate),
    AdapterInterface.OnItemClickListener<MenuModel> {

    companion object {
        const val ARG_TEMP_TOKEN = "ARG_TEMP_TOKEN"
        const val ARG_USER_NATIONAL_CODE = "ARG_USER_NATIONAL_CODE"
        const val ARG_ENABLE_LOG_OUT_BUTTON = "ARG_ENABLE_LOG_BUTTON"
    }

    private lateinit var listAdapter: UserModeAdapter

    val mViewModel: LoginViewModel by viewModels()

    private var mListener: DialogResultInterface.OnResultListener<MenuModel>? = null

    fun setResultListener(listener: DialogResultInterface.OnResultListener<MenuModel>) {
        mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupObserver() {
        mViewModel.mldUserMode.observe(this, ::showResult)
        mViewModel.mldProfile.observe(this, ::showProfileInfoResult)
        mViewModel.mldShowInfoLoading.observe(this, ::showLoading)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        getData()
        setupObserver()
        setupLoadingView()
        this.isCancelable = false
    }

    private fun setupLoadingView() {
        viewBinding.loadingView.bgView.alpha = 0.3f
        viewBinding.loadingView.bgView.setOnClickListener { }
    }

    private fun getData() {
        //    Timber.tag("debugServiceMultiple").i("getData: Called")
        val localInfo = mViewModel.getUserLocalInfo()

        if (localInfo.fullName.isNullOrBlank())
            mViewModel.getUserInfoFromServer(arguments?.getString(ARG_TEMP_TOKEN))
         else
           fillData()
        mViewModel.getUserModeData()
    }
    fun fillData() {
        viewBinding.tvUsername.text = (mViewModel.getUserLocalInfo().fullName) ?: getString(R.string.label_user)
        ImageUtils.loadUserAvatar(viewBinding.imgProfile, mViewModel.getUserAvatar())
    }

    private fun showLoading(show: Boolean) { viewBinding.loadingView.isVisible = show }

    private fun init(list: ArrayList<MenuModel>) {
        if (getStatusEnableLogoutButton()) {
            viewBinding.groupLogout.visibility = View.VISIBLE
        }
        listAdapter = UserModeAdapter(list, this)
        for (model in list)
            if (model.isSelected)
                enableEnterButton()
        /* mViewModel.getUserAvatar()?.let {
             listAdapter.setAvatar(it)
         }*/
        viewBinding.recycler.apply {
            adapter = listAdapter
        }
    }

    private fun onClick() {
        viewBinding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnOk.setOnClickListener {
                if (listAdapter.hasSelectedItem()) {

                    labelError.visibility = View.GONE

                    listAdapter.getSelectedItem()?.let {
                        mViewModel.setEmployerMode(it.title)
                        it.description2 = mViewModel.mldProfile.value?.data?.fullName ?: ""
                        mListener?.onDialogResult(it)
                    }
                    dismiss()
                } else {
                    labelError.visibility = View.VISIBLE
                }
            }

            btnLogOut.setOnClickListener {
                mViewModel.logOut()
                handlePageDestination(
                    R.id.action_servicesFragment_to_loginActivity,
                    finishActivity = true
                )
            }
        }
    }

    private fun showResult(result: UserInfo?) {
            result?.modeList?.let { init(it) }
    }

    private fun showProfileInfoResult(profileResponse: ProfileResponse) {
        if (profileResponse.isSuccess)
           fillData()
    }


    private fun getStatusEnableLogoutButton(): Boolean {
        return arguments?.getBoolean(ARG_ENABLE_LOG_OUT_BUTTON, false) ?: false

    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        viewBinding.labelError.visibility = View.GONE
        enableEnterButton()

    }

    private fun enableEnterButton() {
        viewBinding.btnOk.setBackgroundResource(R.drawable.bg_btn_dialog_dark_blue)
        viewBinding.btnOk.isEnabled = true
    }
}