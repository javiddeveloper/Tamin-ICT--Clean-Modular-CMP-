package com.tamin.taminhamrah.ui.home.services.wedingpresent

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentModel
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentResponse
import com.tamin.taminhamrah.databinding.FragmentWeddingPresentBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.widget.DatePickerWidget
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class WeddingPresentFragment :
    BaseFragment<FragmentWeddingPresentBinding, WeddingPresentViewModel>(),
    AdapterInterface.OnItemClickListener<ServiceModel> {

    override val mViewModel: WeddingPresentViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wedding_present
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(this,::showResult)
        mViewModel.mldRequestGift.observe(this,  ::showResultRequestGift)
    }

    private fun showResultRequestGift(result: GeneralRes) {
        Log.i( "showResultRequestGift:","showResultRequestGift")
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                requireContext().getString(R.string.message_success_send_request_wedding_present)
            )
        }else{
            Log.i( "showResultRequestGift:","ERROR!")

        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            inputNationalCode.setHintWidget(getString(R.string.national_code_partner))
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                actionIconRes = R.drawable.ic_calculate
            )
            inputNationalCode.getInput().doAfterTextChanged {
                inputNationalCode.getLayout().isErrorEnabled = false
            }
        }
    }
    override fun getData() {
        mViewModel.getWeddingPresentInfo()
    }

    @SuppressLint("SetTextI18n")
    override fun onClick() {
        viewDataBinding?.apply {

            appBar.toolbar.imgAction.setOnClickListener {

                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    getString(R.string.label_caculate_wedding_gift)
                )
                bundle.putString(
                    Constants.TOOLBAR_ICON_IMAGE,
                    Utility.getToolbarIconImage(arguments)
                )
                handlePageDestination(id=R.id.action_wedding_to_calculate_wedding, bundle =bundle)
            }

            btnShowDetail.setOnClickListener {
                if (groupDetails.visibility == View.GONE) {
                    groupDetails.visibility = View.VISIBLE
                    btnShowDetail.text =
                        btnShowDetail.context.getString(R.string.hide_detail)
                } else {
                    groupDetails.visibility = View.GONE
                    btnShowDetail.text = btnShowDetail.context.getString(R.string.show_detail)
                }
            }

            widgetDatePickerWeddingGift.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    mViewModel.selectedDatetimeStamp=timeStamp
                }
            })

           /* labelMarriageDetail.setOnClickListener {
                when (widgetDatePickerInputContractDate.tilDate.visibility) {
                    View.VISIBLE -> {
                        widgetDatePickerInputContractDate.tilDate.visibility = View.GONE
                        inputNationalCode.visibility = View.GONE
                        btnSubmit.visibility = View.GONE
                    }
                    else -> {
                        widgetDatePickerInputContractDate.tilDate.visibility = View.VISIBLE
                        inputNationalCode.visibility = View.VISIBLE
                        btnSubmit.visibility = View.GONE
                    }
                }
            }*/

            btnSubmitCommitment.setOnClickListener {
                when {
                    mViewModel.selectedDatetimeStamp == 0L -> {
                        widgetDatePickerWeddingGift.setError(getString(R.string.message_selecte_marriage_date))
                    }
                    inputNationalCode.getValueNationalCode(false).isBlank() || inputNationalCode.getLayout().isErrorEnabled -> {
                        inputNationalCode.getLayout().error = getString(R.string.error_not_valid_national_id)
                    }
                    !checkboxCommitment.isChecked -> {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_select_check_box)
                        )
                    }
                    else -> {
                        mViewModel.postRequest(
                            inputNationalCode.getValueNationalCode(),
                            mViewModel.selectedDatetimeStamp ?: 0L,
                            mViewModel.userInfo
                        )
                    }
                }
                /*  if (selectedDatetimeStamp == 0L) {
                    widgetDatePickerInputContractDate.tilDate.error = getString(R.string.message_selecte_marriage_date)
                } else if (!checkboxCommitment.isChecked) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_select_check_box)
                    )
                } else {
                    if (mViewModel.mldUserInfo.value?.data != null) {
                        mViewModel.mldUserInfo.value?.data?.let { data->
                            selectedDatetimeStamp?.let {timeStamp->
                                mViewModel.postRequest(
                                    inputNationalCode.getValueNationalCode(),
                                    timeStamp,
                                    data
                                )
                            }
                        }
                    } else {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.INFO,
                            mViewModel.mldUserInfo.value?.getMessage()
                                ?: getString(R.string.error_select_bank_account)
                        )
                        }
                    }*/

            }
        }
    }

    private fun showResult(result: WeddingPresentResponse) {
        Log.i( "showResultRequestGift:","showResultRequestGift")
        if (result.isSuccess) {
            viewDataBinding?.let {
                it.groupDetailsRequest.visibility = View.VISIBLE
                it.groupDetails.visibility = View.GONE
                it.item = result.data
            }
            mViewModel.userInfo = result.data ?: WeddingPresentModel()
       //     mViewModel.getReceiverList()
        }else{
            Log.i( "showResultRequestGift:","ERROR!")

        }
    }


    override fun onItemClick(item: ServiceModel, transitionView: View?, tag: String?) {
        //   if (mViewModel.isMainItem(item) == true) {
        //     val bundle = Bundle()
//            bundle.putParcelable(SearchServiceFragment.ARG_SELECTED_SERVICE_ITEM, item)
//            handlePageDestination(R.id.action_service_to_search, bundle)
        //    } else {

        ///         showSnackbar("click")
        //     }
    }

 }



