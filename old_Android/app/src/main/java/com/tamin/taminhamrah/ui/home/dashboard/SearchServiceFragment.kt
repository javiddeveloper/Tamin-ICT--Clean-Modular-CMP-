package com.tamin.taminhamrah.ui.home.dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.databinding.FragmentSearchServicesBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint


@Deprecated("We removed it")
@AndroidEntryPoint
class SearchServiceFragment : BaseFragment<FragmentSearchServicesBinding, ServicesViewModel>(),
    AdapterInterface.OnItemClickListener<ServiceModel> {

    companion object {
        const val ARG_SELECTED_SERVICE_ITEM_TITLE = "ARG_SELECTED_SERVICE_ITEM_TITLE"
        const val ARG_SELECTED_SERVICE_ITEM_TYPE = "ARG_SELECTED_SERVICE_ITEM_TYPE"
    }

    lateinit var listAdapter: ServiceAdapter

    override val mViewModel: ServicesViewModel by activityViewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_services
    }

    override fun setupObserver() {
    }

    override fun initView() {
        listAdapter = ServiceAdapter()
        viewDataBinding?.apply {

            ImageUtils.loadUserAvatar(
                viewDataBinding?.toolbar?.imgProfile,
                mViewModel.getUserAvatar()
            )
            toolbar.imgFilter.visibility = View.GONE

            tvTitle.text = getSelectedItemTitle()

            recycler.apply {
                this.adapter = listAdapter
            }
            val service =
                mViewModel.getSelectedService(getSelectedItemTitle(), getSelectedItemType())

            service?.serviceList?.let {
                listAdapter.setItems(
                    it,
                    this@SearchServiceFragment,
                    isSearchList = true
                )
            }

            searchView.apply {
                setOnClickListener { searchView.isIconified = false }

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(searchStr: String?): Boolean {
                        if (searchStr?.isNotBlank() == true)
                            mViewModel.searchList(searchStr)
                        return true
                    }

                    override fun onQueryTextChange(searchStr: String?): Boolean {
                        if (searchStr?.isNotBlank() == true)
                            mViewModel.searchList(searchStr)
                        return true
                    }
                })

                setOnCloseListener {
                    mViewModel.getAllList()
                    false
                }

            }
        }
    }

    override fun getData() {
    }

    override fun onClick() {

    }

    var transitionView: View? = null
    override fun onItemClick(item: ServiceModel, transitionView: View?, tag: String?) {

        this.transitionView = transitionView
        handleNavigation(item)

    }

    private fun handleNavigation(item: ServiceModel) {
        when (item.name) {
            //ok
            "درخواست هدیه ازدواج" -> {
                handlePageDestination(
                    R.id.action_service_to_wedding_present,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "استعلام ارتباط فعال با سازمان تأمین اجتماعی" -> {
                handlePageDestination(
                    R.id.action_service_to_active_relation,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "استعلام اطلاعات هویتی و شماره تأمین اجتماعی" -> {
                handlePageDestination(
                    R.id.action_service_to_identity_info,
                    bundle = createToolbarBundle(item)
                )
            }

            "اعلام شماره حساب بانکی" -> {
                handlePageDestination(
                    R.id.action_service_to_declare_bank_account,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "استعلام شماره حسابهای ثبت شده" -> {
                handlePageDestination(
                    R.id.action_service_to_bank_account_list,
                    bundle = createToolbarBundle(item)
                )
            }
            "کلیه سوابق" -> {
                handlePageDestination(
                    R.id.action_servicesFragment_to_allHistoryInsuranceFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            "سوابق و ریز دستمزد بعد از سال 86" -> {
                handlePageDestination(
                    R.id.action_servicesFragment_to_wageAndHistoryFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "مشاهده فیش حقوقی مستمری بگیران" -> {
                handlePageDestination(
                    R.id.action_services_to_pay_roll,
                    bundle = createToolbarBundle(item)
                )
            }

            //ok
            "مشاهده عناوین شغلی" -> {
                handlePageDestination(
                    R.id.action_servicesFragment_to_viewTitleJobFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            "سوابق تلفیقی" -> {
                handlePageDestination(
                    R.id.action_services_to_combined_record,
                    bundle = createToolbarBundle(item)
                )
            }
            "مشاهده درخواست هاي تعهدات کوتاه مدت" -> {
                handlePageDestination(
                    R.id.action_servicesFragment_to_viewShortTermFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "محاسبه مبلغ هدیه ازدواج" -> {
//                item.iconRes=R.drawable.ic_marriage_gift
//                item.description = requireContext().getString(R.string.marriage_service_description)
//                item.caption = requireContext().getString(R.string.marriage_service_caption)
                handlePageDestination(
                    R.id.action_servicesFragment_to_calculateMarriageAllowanceFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "محاسبه مبلغ غرامت دستمزد ایام بیماری" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_servicesFragment_to_calculateWageIllDaysFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "محاسبه مبلغ غرامت دستمزد ایام بارداری" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_servicesFragment_to_calculateWagePregnancyFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            "حکم حقوقی مستمری بگیران" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_servicesFragment_to_edictPensionerFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "دریافت عکس از پایگاه ثبت احوال" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_service_to_edit_image,
                    bundle = createToolbarBundle(item)
                )
            }
            //ok
            "صدور گواهی حقوق مستمری بگیران" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_servicesFragment_to_issuanceWageCertificateFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            "درخواست گواهی کسر اقساط معوق" -> {
                //  item.iconRes=R.drawable.ic_marriage_gift
                // item.description = requireContext().getString(R.string.marriage_service_description)
                handlePageDestination(
                    R.id.action_servicesFragment_to_deferredInstallmentCertificateFragment,
                    bundle = createToolbarBundle(item)
                )
            }
            else -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    requireContext().resources.getString(R.string.message_not_implemented)
                )
            }

        }
    }

    fun createToolbarBundle(item: ServiceModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.name)
//        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putString(Constants.TOOLBAR_ICON_IMAGE, item.getImageUrl())
        return bundle
    }

    private fun getSelectedItemTitle(): String? {
        return arguments?.getString(ARG_SELECTED_SERVICE_ITEM_TITLE)
    }

    private fun getSelectedItemType(): Int? {
        return arguments?.getInt(ARG_SELECTED_SERVICE_ITEM_TYPE)
    }

}