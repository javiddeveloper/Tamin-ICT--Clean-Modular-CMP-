package com.tamin.taminhamrah.ui.home.services.identityinfo

import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse.IdentityInfo
import com.tamin.taminhamrah.databinding.FragmentIdentityInfoInquiryBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IdentityInfoInquiryFragment :
    BaseFragment<FragmentIdentityInfoInquiryBinding, IdentityInfoInquiryViewModel>(),
    AdapterInterface.OnItemClickListener<ActiveRelation> {

    lateinit var listAdapter: KeyValueAdapter
    var infoUser = listOf<KeyValueModel>()

    override val mViewModel: IdentityInfoInquiryViewModel by viewModels()

    private var isRefreshingContactInfo = false

    private fun showResult(result: IdentityInfo?) {
        result?.let { identity ->
            if (!isRefreshingContactInfo) {
                mViewModel.getCityName(
                    identity.cityOfBirthId.toString(),
                    identity.cityOfIssueId.toString()
                )

                if (identity.mobileNumber.isNullOrEmpty() || identity.email.isNullOrEmpty()) {
                    isRefreshingContactInfo = true
                    mViewModel.getIdentityInfo()
                    return
                } else {
                    displayIdentityInfo(identity)
                }
            } else {
                isRefreshingContactInfo = false
                displayIdentityInfo(identity)
            }
        }
    }

    private fun displayIdentityInfo(identity: IdentityInfo) {
        infoUser = identity.createKeyValue(identity)
        listAdapter.setItems(infoUser)
    }


    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_identity_info_inquiry
    }

    override fun setupObserver() {
        mViewModel.mldIdentityInfo.observe(this, ::showResult)
        mViewModel.mldCityBirth.observe(this, ::showResultBirthCityName)
        mViewModel.mldCityPlaceIssue.observe(this, ::showResultCityPlaceIssue)
    }

    private fun showResultCityPlaceIssue(result: CityNameListResponse?) {
        val cityData = result?.data?.list
        infoUser.forEach { item ->
            if (item._key.contains("شهر محل صدور") && !cityData.isNullOrEmpty()) {
                item._value = cityData[0].cityName
            }
        }

        listAdapter.setItems(infoUser)
    }

    private fun showResultBirthCityName(result: CityNameListResponse?) {
        val cityData = result?.data?.list

        infoUser.forEach { item ->
            if (item._key.contains("شهر محل تولد") && !cityData.isNullOrEmpty()) {
                item._value = cityData[0].cityName
            }
        }

    }

    override fun initView() {
        listAdapter = KeyValueAdapter()
        viewDataBinding?.apply {
            recyclerPersonalInfo.apply {
                adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetIdentityInfoInquiryFragment, false)
                setViewForShowGide()
            }
        }

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        setViewForShowGide()
    }

    override fun getData() {
        mViewModel.getIdentityInfo()
    }

    override fun onClick() {

    }

    override fun onItemClick(item: ActiveRelation, transitionView: View?, tag: String?) {
        handlePageDestination(R.id.action_active_relation_to_certificate)
    }

    private fun setViewForShowGide() {
        /*try {
            if (!mViewModel.loadBoolean(Constants.TapTargetIdentityInfoInquiryFragment)) {
                val ViewsList = arrayListOf<TapTargetModel>()
                (viewDataBinding)?.appBar?.toolbar?.imgInfo?.let {
                    ViewsList.add(TapTargetModel(it,R.string.title_img_info_tag_target_view,R.string.detail_info_img_tag_target_view))
                }
                this.viewDataBinding?.parent?.let {
                    ViewsList.add(TapTargetModel(it,R.string.title_identity_Info_tag_target_view,R.string.detail_identity_Info_tag_target_view,shape = Shape.RECT))
                }
                ViewsList?.let {
                    showGide(it)
                }
                mViewModel.saveBoolean(Constants.TapTargetIdentityInfoInquiryFragment, true)
            }
        } catch (e: Exception) {
            Log.e("showGide: ", e.message.toString())
        }*/
    }
}