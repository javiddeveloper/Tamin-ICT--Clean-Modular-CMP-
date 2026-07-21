package com.tamin.taminhamrah.ui.treatment.electronicPrescription

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.local.models.SquareRadioButtonModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescription
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.databinding.FragmentMyElectronicPrescriptionBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.treatment.TreatmentViewModel
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.adapter.ElectronicPrescriptionAdapter
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.detail.ElectronicPrescriptionDetailFragment
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.widget.DatePickerWidget
import com.tamin.taminhamrah.widget.squareRadioGroup.SquareRadioGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class ElectronicPrescriptionListFragment :
    BaseFragment<FragmentMyElectronicPrescriptionBinding, TreatmentViewModel>() {


    override val mViewModel: TreatmentViewModel by viewModels()

    var mainUser: MenuModel? = null
    var isDarkMode = false
    val listAdapter: ElectronicPrescriptionAdapter by lazy {
        ElectronicPrescriptionAdapter(
            onItemClickListener = onItemClickListener,
            onDownloadClickListener = onDownloadClickListener
        )
    }


    val onItemClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<ElectronicPrescription> {
            override fun onItemClick(
                item: ElectronicPrescription,
                transitionView: View?,
                tag: String?
            ) {
                handlePageDestination(
                    R.id.action_service_to_prescription_detail,
                    createToolbarBundle(item)
                )
            }
        }
    }

    private val onDownloadClickListener by lazy {
        object : AdapterInterface.OnDownloadClickListener<ElectronicPrescription> {
            override fun onDownload(
                item: ElectronicPrescription,
                transitionView: View?,
                tag: String?
            ) {
                if (item.flagSata == "2") {
                    mViewModel.getTestResultPdfFile(
                        item.patientID ?: "0",
                        item.noteHeadEprescID?.toString() ?: "0",
                        mViewModel.getUserNationalCode()
                    )
                } else {
                    mViewModel.getPrescriptionPdfFile(
                        item.noteHeadEprescID?.toString() ?: "0",
                        true
                    )
                }

            }
        }
    }


    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_my_electronic_prescription
    override fun setupObserver() {
        mViewModel.mldCurrentUser.observe(this, ::onUserInfoResponse)
        mViewModel.mldPDF.observe(this, ::onPdfResponse)
        mViewModel.mldTestResultPDF.observe(this, ::onTestResultPdfResponse)
        collectPrescriptions()
    }

    private fun collectPrescriptions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.prescriptionFlow.collectLatest { pagingData ->
                    viewDataBinding?.groupList?.visibility = View.VISIBLE
                    listAdapter.submitData(pagingData)
                }
            }
        }
        listAdapter.addLoadStateListener { loadStates ->
            val isListEmpty =
                loadStates.refresh is LoadState.NotLoading && listAdapter.itemCount == 0
            if (isListEmpty) {
                viewDataBinding?.recycler?.showMessage(
                    mViewModel.listLabel.ifBlank {
                        getString(R.string.prescriptions_with_value, getString(R.string.medical))
                    }
                )
            } else {
                viewDataBinding?.recycler?.hideMessage()
            }
        }
    }

    override fun initView() {
        setupRecycler(viewDataBinding?.recycler, listAdapter)
        if (mViewModel.listLabel.isBlank()) {
            mViewModel.listLabel =
                getString(R.string.prescriptions_with_value, getString(R.string.medical))
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
        )
        onClick()
        setDatePicker()
        isDarkMode = context?.let {
            (it.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        } ?: false

    }

    override fun getData() {
        //  getDependentUserInfo()

        mViewModel.selectedUserNationalId = arguments?.getString(Constants.SELECTED_USER)
        arguments?.getParcelableArrayList<MenuModel>(Constants.USERS)?.let {
            mViewModel.usersInfo.addAll(it.filter { it !in mViewModel.usersInfo })
        }
        mViewModel.getCurrentUserInfo()

    }


    private fun onUserInfoResponse(result: CurrentUserResponse) {
        if (result.isSuccess) {
            mainUser = MenuModel(
                "${result.data?.firstName} ${result.data?.lastName} - ${result.data?.nationalCode}",
                result.data?.nationalCode
            )
            if (mViewModel.selectedUserNationalId == mainUser?.id || mViewModel.selectedUserNationalId == "0") {
                mViewModel.selectedUserNationalId = "0"
                viewDataBinding?.selectableDependant?.setValue(mainUser?.title ?: "-")
            } else if (mViewModel.selectedUserNationalId != "0") {
                mViewModel.usersInfo.forEach { user ->
                    if (user.id == mViewModel.selectedUserNationalId) {
                        viewDataBinding?.selectableDependant?.setValue(user.title ?: "-")
                    }
                }
            }
            mViewModel.initQuery()
        }
    }

    private fun onPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = Utility.getToolbarTitle(arguments)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            } else {
                if (mViewModel.navigateToPDF) {
                    mViewModel.navigateToPDF = false
                    handlePageDestination(
                        R.id.action_prescription_list_to_pdf_viewer,
                        Bundle().apply {
                            putString(PdfViewerActivity.ARG_TITLE, title)
                            putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                        })
                }
            }
        }
    }

    private fun onTestResultPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = Utility.getToolbarTitle(arguments)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            } else {
                handlePageDestination(R.id.action_prescription_list_to_pdf_viewer, Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, title)
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                })
            }
        }
    }


    //Utils
    private fun setDatePicker() {
        viewDataBinding?.apply {
            if (mViewModel.startDate == 0L) {
                mViewModel.startDate = (System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000))
            }
            datePickerFrom.setJalaliDate(
                ConvertDate.convertTimestampToPersianDate(
                    mViewModel.startDate
                )
            )

            if (mViewModel.endDate == 0L) {
                mViewModel.endDate = System.currentTimeMillis()
            }
            datePickerTo.setJalaliDate(ConvertDate.convertTimestampToPersianDate(mViewModel.endDate))
        }
    }


    override fun onClick() {
        /*   viewDataBinding?.btnSearchMyRequest?.setOnClickListener {
               Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
           }*/
        viewDataBinding?.apply {
            selectableDependant.setValue(mViewModel.selectedDependantUserTitle ?: "")
            squareRadioGroup.setItems(
                getButtonList(),
                listener = object : SquareRadioGroup.OnClickListener {
                    override fun onClick(button: SquareRadioButtonModel) {
                        /*  prescriptionList?.let { pagingData ->
                              this@ElectronicPrescriptionListFragment.lifecycleScope.launchWhenCreated {
                                  listAdapter.submitData(pagingData.filter {
                                     *//* if (button.id == 6)
                                        true
                                    else*//*
                                        it.prescType == button.id.toString()
                                })
                            }*/
                        if (mViewModel.prescriptionTypeL != button.id.toString()) {
                            squareRadioGroup.selectButton(button)
                            mViewModel.setPrescriptionType(button.id.toString())  // triggers flatMapLatest
                            mViewModel.listLabel =
                                getString(R.string.prescriptions_with_value, button.label)
                            labelPrescriptionList.text = mViewModel.listLabel
                        }
                    }
                })
            squareRadioGroup.selectButton(getButtonList().find { mViewModel.prescriptionTypeL == it.id.toString() }
                ?: getButtonList().last())

            selectableDependant.getIt().setOnClickListener {
                MenuDialogFragment.newInstance(
                    true,
                    getString(R.string.under_18_dependant_user)
                ).apply {
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            lifecycleScope.launch {
                                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                    val list = ArrayList<MenuModel>()
                                    list.addAll(mViewModel.usersInfo.toList())

                                    mViewModel.getUsersInfoLocalPaging(list).collectLatest {
                                        updateData(it)
                                    }
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.let {
                                viewDataBinding?.selectableDependant?.setValue(
                                    itemResult.title ?: "-"
                                )
                                mViewModel.selectedUserNationalId =
                                    if (itemResult.id == mainUser?.id) {
                                        "0"
                                    } else {
                                        itemResult.id ?: "0"
                                    }
                                mViewModel.selectedDependantUserTitle = itemResult.title ?: "-"
                            }
                        }
                    })
                }.show(childFragmentManager, "ElectronicPrescriptionListFragment")
            }

            datePickerFrom.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    mViewModel.startDate = timeStamp
                }
            })
            datePickerTo.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    mViewModel.endDate = timeStamp
                }
            })
            handleDescVisibility()

            btnSearchMyRequest.setOnClickListener {
                handleDescVisibility()
                if (mViewModel.startDate == 0L || mViewModel.endDate == 0L) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_message_select_date)
                    )
                } else {
                    mViewModel.doSearch() // triggers _searchTrigger++ → flatMapLatest re-runs with new dates
                }
            }
            if (mViewModel.listLabel.isNotBlank())
                labelPrescriptionList.text = mViewModel.listLabel
        }


    }

    private fun handleDescVisibility() {
        val diff = kotlin.math.abs(mViewModel.endDate - mViewModel.startDate)

        val daysDiff = TimeUnit.MILLISECONDS.toDays(diff)
        /*     Timber.tag("onClick: ").i("endDate: %s", endDate)
             Timber.tag("onClick: ").i("startDate: %s", startDate)
             Timber.tag("onClick").i(" %s",daysDiff)*/
        if (daysDiff > 7) {
            viewDataBinding?.itemDesc1?.root?.visibility = View.GONE
        } else {
            viewDataBinding?.itemDesc1?.root?.visibility = View.VISIBLE
        }
    }

    private fun getButtonList() = listOf(
        SquareRadioButtonModel(
            id = 2,
            label = getString(R.string.para_clinic),
            backgroundColor = if (isDarkMode) R.color.md_theme_dark_surface else R.color.white,
            textColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary),
            selectedBackground = R.drawable.bg_button_square_primary,
            iconResId = R.drawable.ic_paraclinic
        ),

        SquareRadioButtonModel(
            id = 5,
            label = getString(R.string.medical_service),
            backgroundColor = if (isDarkMode) R.color.md_theme_dark_surface else R.color.white,
            textColor = ContextCompat.getColor(
                requireContext(),
                R.color.text_color_green_blue_filter
            ),
            selectedBackground = R.drawable.bg_button_square_green_blue,
            iconResId = R.drawable.ic_services
        ),

        SquareRadioButtonModel(
            id = 3,
            label = getString(R.string.visit),
            backgroundColor = if (isDarkMode) R.color.md_theme_dark_surface else R.color.white,
            textColor = ContextCompat.getColor(requireContext(), R.color.text_color_orange_filter),
            selectedBackground = R.drawable.bg_button_square_orange,
            iconResId = R.drawable.ic_visit
        ),

        SquareRadioButtonModel(
            id = 1,
            label = getString(R.string.medical),
            backgroundColor = if (isDarkMode) R.color.md_theme_dark_surface else R.color.white,
            textColor = ContextCompat.getColor(requireContext(), R.color.text_color_green_filter),
            selectedBackground = R.drawable.bg_button_square_green,
            iconResId = R.drawable.ic_medicinal
        ),

//        SquareRadioButtonModel(
//            id = 6,
//            label = getString(R.string.all),
//            backgroundColor = if (isDarkMode) R.color.md_theme_dark_surface else R.color.white,
//            textColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//            selectedBackground = R.drawable.bg_button_square_primary,
//            iconResId = R.drawable.ic_all_filter,
//            isSelected = true
//        )
    )

    fun createToolbarBundle(item: ElectronicPrescription) =
        Bundle().apply {
            putLong(
                ElectronicPrescriptionDetailFragment.ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION,
                item.noteHeadEprescID ?: 0L
            )
            putString(ElectronicPrescriptionDetailFragment.ARG_REQUEST_TYPE, item.prescType)
            putString(ElectronicPrescriptionDetailFragment.PRES_TYPE, item.prescType)

            putString(
                ElectronicPrescriptionDetailFragment.ARG_NATIONAL_CODE,
                mViewModel.getMainUserNationalId()
            )
            putString(
                ElectronicPrescriptionDetailFragment.ARG_REGISTER_DATE,
                item.prescDate
            )
            putString(
                ElectronicPrescriptionDetailFragment.ARG_CHILD_NATIONAL_CODE,
                mViewModel.selectedUserNationalId
            )
            putString(
                ElectronicPrescriptionDetailFragment.PRES_NAME,
                item.prescName
            )
            putString(ElectronicPrescriptionDetailFragment.ARG_FLAG_SATA, item.flagSata)
            putString(Constants.TOOLBAR_TITLE, item.prescName)
            putString(
                Constants.TOOLBAR_SUBTITLE,
                "${getString(R.string.doc_name)} : ${item.docName}"
            )
            putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes ?: 0)
        }

}