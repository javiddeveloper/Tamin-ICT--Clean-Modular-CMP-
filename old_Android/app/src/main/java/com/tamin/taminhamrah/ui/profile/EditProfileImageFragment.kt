package com.tamin.taminhamrah.ui.profile

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.profile.TaminRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentEditProfileImageBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileImageFragment :
    BaseFragment<FragmentEditProfileImageBinding, ProfileViewModel>() {

    var relatedPersonNationalId: String? = ""
    override val mViewModel: ProfileViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_edit_profile_image
    }

    override fun setupObserver() {
        mViewModel.mldFetchRelationInfo.observe(this, ::showResult)
        mViewModel.mldImageRequestResult.observe(this, ::showImageRequestResult)
    }

    override fun initView() {
        ImageUtils.loadUserAvatar(viewDataBinding?.imgProfile, mViewModel.getUserAvatar())
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.iran_sans_mobile_fa_num)
        viewDataBinding?.inputSerialNumber?.typeface = typeface
        viewDataBinding?.inputRelated?.tilSelectableInput?.typeface = typeface
        onClick()
    }

    override fun getData() {
        mViewModel.fetchRelationInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            btnSubmit.setOnClickListener {
                if (!inputSerialNumber.editText?.text.isNullOrBlank()) {
                    if ((swChangeRelatedPerson.isChecked && inputRelated.selectableInput.text?.isBlank() == true)) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_select_relatedPerson_or_uncheck_it)
                        )
                    } else {
                        mViewModel.mldFetchRelationInfo.value?.data?.brhCode?.let { it1 ->
                            mViewModel.sendImageRequest(
                                it1,
                                if (swChangeRelatedPerson.isChecked) relatedPersonNationalId
                                    ?: "0"
                                else inputSerialNumber.editText?.text.toString()
                            )
                        }
                    }
                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        getString(R.string.error_nationl_code_serial_number)
                    )
                }
            }


            swChangeRelatedPerson.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    inputRelated.tilSelectableInput.visibility = View.VISIBLE
                } else {
                    inputRelated.tilSelectableInput.visibility = View.GONE
                }
            }

            inputRelated.selectableInput.setOnClickListener {

                val dialog = MenuDialogFragment.newInstance(true,  getString(R.string.label_related_persons))
                dialog.setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {

                        this@EditProfileImageFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.mldRelatedList.collectLatest { pagingData ->
                                dialog.updateData(mViewModel.getRelatedListAsMenuModel(pagingData))
                            }
                        }
                    }

                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        itemResult.title?.let {
                            relatedPersonNationalId = itemResult.description
                            itemResult.title?.let { it1 -> inputRelated.selectableInput.setText(it1) }
                        }
                    }
                })
                dialog.show(childFragmentManager, "65gfmhgfhg")
            }
        }

    }

    private fun showResult(result: TaminRelationResponse) {
    }

    private fun showImageRequestResult(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_image_request_success_result)
            )
        }
    }

}