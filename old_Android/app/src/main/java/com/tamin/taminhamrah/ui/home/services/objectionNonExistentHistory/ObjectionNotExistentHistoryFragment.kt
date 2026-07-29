package com.tamin.taminhamrah.ui.home.services.objectionNonExistentHistory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.BodySaveNonExistentHistory
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeResponce
import com.tamin.taminhamrah.data.remote.models.services.NotExistRequestsModel
import com.tamin.taminhamrah.data.remote.models.services.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckStatusNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendConfirmNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckSaveNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.DeleteNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendFinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.databinding.FragmentObjectionNonExistentHistoryBinding
import com.tamin.taminhamrah.databinding.ObjectionNonExistentBranchStepBinding
import com.tamin.taminhamrah.databinding.ObjectionNonExistentDurationWorkStepBinding
import com.tamin.taminhamrah.databinding.ObjectionNonExistentWorkshopStepBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.objectionNonExistentHistory.adapter.NonExistsHistoryAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.LocationSelectorWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.annotations.NotNull
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

@ExperimentalPagingApi
@AndroidEntryPoint
class ObjectionNotExistentHistoryFragment :
    BaseFragment<FragmentObjectionNonExistentHistoryBinding, ObjectionNotExistentHistoryViewModel>(),
    AdapterInterface.OnActionResultInterface<NotExistRequestsModel?>,
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    override val mViewModel: ObjectionNotExistentHistoryViewModel by viewModels()
    private val listAdapter by lazy { NonExistsHistoryAdapter() }
    private var dataModel = BodySaveNonExistentHistory()

    enum class ObjectionDialogType {
        PROVINCE_LIST, CITY_LIST, BRANCH_LIST, INSURANCE_LIST
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun initView() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        listAdapter.onClickListener = this
        viewDataBinding?.apply {
            setupRecycler(
                recycler,
                listAdapter,
                emptyMessage = getString(R.string.not_exist_registration_objection)
            )
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null,
            )
        }
    }

    private fun initStepper() {
        viewDataBinding?.apply {
            val stepLayout = listOf(step1(), step2(), step3())
            stepper.initial(stepLayout)
            stepper.onNextStepClickListener = this@ObjectionNotExistentHistoryFragment
            stepper.onPreviousStepClickListener = this@ObjectionNotExistentHistoryFragment
        }
    }

    //step -> branch info
    private fun step1() = ObjectionNonExistentBranchStepBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper, false
    ).apply {

        locationSelector.setListener(object :LocationSelectorWidget.OnLocationSelectListener{
            override fun onLocationSelect(
                type: LocationSelectorWidget.ListType,
                title: String?,
                code: String?
            ) {
                when(type){
                    LocationSelectorWidget.ListType.TYPE_PROVINCE ->{
                        dataModel.provinceName = title?:""
                        dataModel.provinceCode = code?:""
                        dataModel.cityName = ""
                        dataModel.cityCode = ""
                        dataModel.branchName = ""
                        dataModel.branchCode = ""

                    }
                    LocationSelectorWidget.ListType.TYPE_CITY ->{
                        dataModel.cityName = title?:""
                        dataModel.cityCode = code?:""
                        dataModel.branchName = ""
                        dataModel.branchCode = ""
                    }
                    LocationSelectorWidget.ListType.TYPE_BRANCH ->{
                        dataModel.branchName = title?:""
                        dataModel.branchCode = code?:""

                    }
                }
            }
        })

        locationSelector.setProvince(dataModel.provinceName ?: "", dataModel.provinceCode)
        locationSelector.setCity(dataModel.cityName ?: "", dataModel.cityCode)
        locationSelector.setBranch(dataModel.branchName ?: "", dataModel.branchCode)

        selectInsuranceType.getIt().setOnClickListener {
            selectInsuranceType.getLayout().isErrorEnabled = false
            showDialog(
                type = ObjectionDialogType.INSURANCE_LIST,
                getString(R.string.label_select_insurance_type_name),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        dataModel.insuranceType = itemResult.id
                        selectInsuranceType.setValue(itemResult.title ?: "")
                    }
                })
        }
    }

    //step -> work shop info
    private fun step2() = ObjectionNonExistentWorkshopStepBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper, false
    ).apply {
        layoutWorkShopCode.getInput().setOnClickListener {
            layoutWorkShopCode.getLayout().isErrorEnabled = false
        }

        layoutWorkShopName.getInput().setOnClickListener {
            layoutWorkShopName.getLayout().isErrorEnabled = false
        }

        layoutWorkShopCode.getInput().doAfterTextChanged {
            layoutWorkShopCode.getLayout().isErrorEnabled = false
        }
    }

    //step -> specify days of work
    private fun step3() = ObjectionNonExistentDurationWorkStepBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        false
    ).apply {

        widgetStartDatePicker.inputDate.setOnClickListener {
            widgetStartDatePicker.tilDate.isErrorEnabled = false
            val datePicker = getDatePicker()
            datePicker?.setListener(object : MyPersianPickerListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                    dataModel.startDate = MyPersianPickerDate.timestamp.toString()
                    widgetStartDatePicker.inputDate.setText("${MyPersianPickerDate.persianYear}/${MyPersianPickerDate.persianMonth}/${MyPersianPickerDate.persianDay}")
                }

                override fun onDismissed() {}
            })
            datePicker?.show()
        }


        widgetEndDatePicker.inputDate.setOnClickListener {
            widgetEndDatePicker.tilDate.isErrorEnabled = false
            val datePicker = getDatePicker()
            datePicker?.setListener(object : MyPersianPickerListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                    dataModel.endDate = MyPersianPickerDate.timestamp.toString()
                    widgetEndDatePicker.inputDate.setText("${MyPersianPickerDate.persianYear}/${MyPersianPickerDate.persianMonth}/${MyPersianPickerDate.persianDay}")
                }

                override fun onDismissed() {}
            })
            datePicker?.show()
        }
        layoutEnterWorkingDays.getInput().doAfterTextChanged { day ->
            if (day.toString().isNotBlank()) {
                if (day.toString().toInt() > 366) {
                    layoutEnterWorkingDays.getInput().setText(getString(R.string.day366))
                }
                layoutEnterWorkingDays.getLayout().isErrorEnabled = false
            }
        }
    }

    private fun showDialog(
        type: ObjectionDialogType,
        title: String,
        resultCallback: MenuInterface.OnResult,
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title)
        dialog.setMenuListener(object : MenuInterface.OnFetchData {
            override fun onFetch() {
                this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
                    when (type) {
                        ObjectionDialogType.PROVINCE_LIST -> {
                            mViewModel.getProvinceRequests().collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.provinceCode,
                                        title = it.provinceName
                                    )
                                }
                                dialog.updateData(result)
                            }
                        }
                        ObjectionDialogType.CITY_LIST -> {
                            mViewModel.getCityRequests(provinceCode = dataModel.provinceCode)
                                .collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.cityCode,
                                            title = it.cityName
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                        }
                        ObjectionDialogType.BRANCH_LIST -> {
                            mViewModel.getBranchRequests(cityCode = dataModel.cityCode)
                                .collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.code,
                                            title = it.name
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                        }
                        ObjectionDialogType.INSURANCE_LIST -> {
                            this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getInsuranceTypeRequests("")
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.insuranceTypeCode,
                                                title = it.insuranceTypeDesc
                                            )
                                        }
                                        dialog.updateData(result)
                                    }
                            }
                        }
                    }

                }
            }

        }, resultCallback, object : MenuInterface.OnSearch {
            override fun onSearch(str: String) {
                this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
                    when (type) {
                        ObjectionDialogType.PROVINCE_LIST -> {
                            mViewModel.getProvinceRequests(str).collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.provinceCode,
                                        title = it.provinceName
                                    )
                                }
                                dialog.updateData(result)
                            }
                        }
                        ObjectionDialogType.CITY_LIST -> {
                            mViewModel.getCityRequests(
                                provinceCode = dataModel.provinceCode,
                                cityName = str
                            ).collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.cityCode,
                                        title = it.cityName
                                    )
                                }

                                dialog.updateData(result)
                            }
                        }

                        ObjectionDialogType.BRANCH_LIST -> {
                            mViewModel.getBranchRequests(
                                cityCode = dataModel.cityCode,
                                branchName = str
                            )
                                .collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.code,
                                            title = it.name
                                        )
                                    }

                                    dialog.updateData(result)
                                }
                        }
                        ObjectionDialogType.INSURANCE_LIST -> {
                            mViewModel.getInsuranceTypeRequests(str)
                                .collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.insuranceTypeCode,
                                            title = it.insuranceTypeDesc
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                        }
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "trtyutyt")
    }

    override fun onClick() {
        viewDataBinding?.apply {
            nestedScrollView.parent.requestChildFocus(
                nestedScrollView,
                nestedScrollView
            )
            createNonExistentHistory.setOnClickListener {
                initStepper()
                stepper.visibility = View.VISIBLE
                recycler.getRecycler().visibility = View.GONE
                layoutShowObjections.visibility = View.GONE
            }

            btnSendReq.setOnClickListener {
                if (listAdapter.itemCount < 1) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_dont_exist_objection)
                    )
                } else {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.CONFIRM,
                        getString(R.string.label_Agreement_use_absentee_services_Social_Security_organization),
                        true
                    )
                    dialog.setDialogClickListener(object :
                        DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            val desc =
                                ConfirmConflictResponseItem(userDesc = viewDataBinding?.inputDescription?.getInput()?.text.toString())
                            mViewModel.sendConfirmNoTexist(arrayListOf(desc))
                        }

                        override fun onCancelClick() {
                        }
                    }
                    )
                    dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
                }
            }
        }
    }


    private fun showError(
        layoutEditText: TextInputLayout? = null,
        editText: TextInputEditText? = null,
        strError: String,
        scroll: Boolean = true
    ) {
        layoutEditText?.error = strError
        editText?.error = strError
        viewDataBinding?.apply {
            if (scroll) {
                appBar.appBarView.setExpanded(false, true)
                nestedScrollView.scrollTo(
                    0,
                    containerSavedObjection.height + (layoutEditText?.top
                        ?: editText?.top ?: 0)
                )
            }
        }
    }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_objection_non_existent_history
    }

    override fun setupObserver() {
        mViewModel.apply {
            mldPensionCheck.observe(this@ObjectionNotExistentHistoryFragment, ::onPensionCheck)
            mldInsuranceType.observe(this@ObjectionNotExistentHistoryFragment, ::onInsuranceType)
            mldCheckHasObjection.observe(
                this@ObjectionNotExistentHistoryFragment,
                ::onCheckHasObjection
            )
            mldSaveObjection.observe(this@ObjectionNotExistentHistoryFragment, ::onSaveObjection)
            mldDeleteNotExist.observe(this@ObjectionNotExistentHistoryFragment, ::onDeleteObjection)
            mldSendDescriptionAndConfirm.observe(
                this@ObjectionNotExistentHistoryFragment,
                ::onSendDescriptionAndConfirm
            )
            mldSendFinalConfirm.observe(this@ObjectionNotExistentHistoryFragment, ::onFinalConfirm)
        }
    }

    private fun onInsuranceType(result: InsuranceTypeResponce) {
        if (!result.isSuccess) return
        result.data?.list?.get(0)?.apply {
            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? ObjectionNonExistentBranchStepBinding)
                ?.selectInsuranceType?.setValue(insuranceTypeDesc ?: "")
            dataModel.insuranceType = insuranceTypeCode
        }
    }

    //Checks whether the request of non-existent objection insurance history has already been saved
    private fun onCheckHasObjection(result: CheckStatusNotExistModel) {
        if (!result.isSuccess) return
        if (result.data == true) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                getString(R.string.label_you_are_reviewing_request),
                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        } else {
            this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
                mViewModel.getNotExistRequests.collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun onSendDescriptionAndConfirm(result: SendConfirmNotExistResponse) {
        if (!result.isSuccess) return
        if (result.data == true) {
            mViewModel.sendFinalConfirmNotExist()
        }
    }

    private fun onFinalConfirm(result: SendFinalConfirmResponse) {
        if (!result.isSuccess) return
        if (!result.data.isNullOrEmpty()) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.INFO,
                getString((R.string.label_request_with_tracking_number), result.data),
                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
        }
    }

    private fun onDeleteObjection(result: DeleteNotExistResponse) {
        if (!result.isSuccess) return
        listAdapter.refresh() /*  ----- refresh call api again*/

    }

    private fun onSaveObjection(result: CheckSaveNotExistModel) {
        if (!result.isSuccess) return
        this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
            viewDataBinding?.nestedScrollView?.fullScroll(View.FOCUS_UP)
            listAdapter.refresh()
        }
        //    dataModel = BodySaveNonExistentHistory()
        viewDataBinding?.apply {
            stepper.visibility = View.INVISIBLE
            layoutShowObjections.visibility = View.VISIBLE
        }
    }

    override fun onEditResult(item: NotExistRequestsModel?) {
        item?.let { itemInfo ->
            dataModel.apply {
                reqno = itemInfo.reqno
                rowi = itemInfo.rowi
                reqtype = itemInfo.reqtype
                startDate = itemInfo.startDate.toString()
                endDate = itemInfo.endDate.toString()
            }
            viewDataBinding?.apply {
                initStepper()
                layoutShowObjections.visibility = View.GONE
                stepper.visibility = View.VISIBLE
                nestedScrollView.scrollTo(0, containerSavedObjection.bottom)
                dataModel = itemInfo.asDomainModel()
                resetStep1(itemInfo)
                resetStep2(itemInfo)
                resetStep3(itemInfo)
            }
        }
    }

    private fun resetStep1(item: NotExistRequestsModel) {
        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? ObjectionNonExistentBranchStepBinding)?.apply {


            locationSelector.setListener(object : LocationSelectorWidget.OnLocationSelectListener{
                override fun onLocationSelect(
                    type: LocationSelectorWidget.ListType,
                    title: String?,
                    code: String?
                ) {
                    when(type){
                        LocationSelectorWidget.ListType.TYPE_PROVINCE ->{
                            dataModel.provinceName = title?:""
                            dataModel.provinceCode = code?:""
                            dataModel.cityName = ""
                            dataModel.cityCode = ""
                            dataModel.branchName = ""
                            dataModel.branchCode = ""

                        }
                        LocationSelectorWidget.ListType.TYPE_CITY ->{
                            dataModel.cityName = title?:""
                            dataModel.cityCode = code?:""
                            dataModel.branchName = ""
                            dataModel.branchCode = ""
                        }
                        LocationSelectorWidget.ListType.TYPE_BRANCH ->{
                            dataModel.branchName = title?:""
                            dataModel.branchCode = code?:""

                        }
                    }
                }
            })

            locationSelector.setProvince(item.provinceName?:"", item.provinceCode)
            locationSelector.setCity(item.cityName?:"", item.cityCode)
            locationSelector.setBranch(item.branchName?:"", item.branchCode)
            selectInsuranceType.setValue(item.insuranceTypeDesc ?: "")
        }
    }

    private fun resetStep2(item: NotExistRequestsModel) {
        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(2) as?
                ObjectionNonExistentWorkshopStepBinding)?.apply {
            layoutWorkShopCode.getInput().setText(item.rwshid)
            layoutWorkShopName.getInput().setText(item.rwshname)
            layoutEmployeeName.getInput().setText(item.rwshManager)
            inputAddressWorkshop.getInput().setText(item.rwshAddress)
        }
    }

    private fun resetStep3(item: NotExistRequestsModel) {
        val start = PersianDateFormat.format(PersianDate(item.startDate), "y/m/d")
        val end = PersianDateFormat.format(PersianDate(item.endDate), "y/m/d")
        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(3) as?
                ObjectionNonExistentDurationWorkStepBinding)?.apply {
            layoutEnterWorkingDays.getInput().setText(item.workDays)
            bindingStartDate = start
            bindingEndDate = end
        }
    }

    override fun onDeleteResult(item: NotExistRequestsModel?) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.CONFIRM,
            getString(R.string.label_are_you_sure_want_delete),
            true
        )

        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                mViewModel.deleteNotExist(item?.reqno ?: "", item?.rowi ?: "")
            }

            override fun onCancelClick() {
            }
        })
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")

    }

    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
            EnumTypeUser.PENSIONER.title -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    result.data?.list?.get(1)
                        ?: getString(R.string.error_active_relation_user_is_pensioner)
                )
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {
                    }
                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
            EnumTypeUser.INSURED.title -> {
                this@ObjectionNotExistentHistoryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.checkStatusNotExitHistory()
                }
            }
        }
    }

    override fun onNextStepClickListener(
        stepIndex: Int,
        step: VerticalStepperItemView
    ) {
        dataModel.also { data ->
            when (stepIndex) {
                1 -> {
                    (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as ObjectionNonExistentBranchStepBinding).apply {

                        when {
                            dataModel.provinceCode.isNullOrEmpty() -> {
                                showError(
                                    layoutEditText = locationSelector.getProvinceLayout(),
                                    strError = getString(R.string.error_select_province_name)
                                )
                            }

                            dataModel.cityCode.isNullOrEmpty() -> {
                                showError(
                                    layoutEditText = locationSelector.getCityLayout(),
                                    strError = getString(R.string.error_select_city_name)
                                )
                            }

                            dataModel.branchCode.isNullOrEmpty() -> {
                                showError(
                                    layoutEditText = locationSelector.getBranchLayout(),
                                    strError = getString(R.string.error_select_branch_name)
                                )
                            }

                            data.insuranceType.isNullOrBlank() -> {
                                showError(
                                    layoutEditText = selectInsuranceType.getLayout(),
                                    strError = getString(R.string.error_select_insurancy_type_name)
                                )
                            }

                            else -> viewDataBinding?.stepper?.nextStep()
                        }
                    }
                }
                2 -> {
                    (viewDataBinding?.stepper?.getStepLayoutBindingByStep(2) as ObjectionNonExistentWorkshopStepBinding).apply {
                        when {
                            layoutWorkShopCode.getInput().text.toString().isBlank() ->
                                showError(
                                    layoutEditText = layoutWorkShopCode.getLayout(),
                                    strError = getString(R.string.error_enter_workshop_code)
                                )
                            layoutWorkShopCode.getInput().text.toString().count() in 1..9 ->
                                showError(
                                    layoutEditText = layoutWorkShopCode.getLayout(),
                                    strError = getString(R.string.error_enter_count_workshop_code)
                                )
                            layoutWorkShopName.getInput().text.toString().isBlank() ->
                                showError(
                                    layoutEditText = layoutWorkShopName.getLayout(),
                                    strError = getString(R.string.error_enter_workshop_name)
                                )
                            layoutEmployeeName.getInput().text.toString().isBlank() ->
                                showError(
                                    layoutEditText = layoutEmployeeName.getLayout(),
                                    strError = getString(R.string.error_enter_employer_name)
                                )
                            inputAddressWorkshop.getInput().text.toString().isBlank() ->
                                showError(
                                    layoutEditText = inputAddressWorkshop.getLayout(),
                                    strError = getString(R.string.error_enter_workshop_address),
                                    scroll = false
                                )
                            else -> viewDataBinding?.stepper?.nextStep()
                        }
                    }
                }
                3 -> {
                    (viewDataBinding?.stepper?.getStepLayoutBindingByStep(3) as ObjectionNonExistentDurationWorkStepBinding).apply {
                        when {
                            data.startDate.isNullOrBlank() -> showError(
                                layoutEditText = widgetStartDatePicker.tilDate,
                                strError = getString(R.string.error_select_start_date)
                            )
                            data.endDate.isNullOrBlank() -> showError(
                                layoutEditText = widgetEndDatePicker.tilDate,
                                strError = getString(R.string.error_select_end_date)
                            )
                            layoutEnterWorkingDays.getInput().text.toString()
                                .isBlank() -> showError(
                                layoutEditText = layoutEnterWorkingDays.getLayout(),
                                strError = getString(R.string.error_enter_working_days),
                                scroll = false
                            )
                            layoutEnterWorkingDays.getInput().text.toString().toInt() < 1 -> {
                                showError(
                                    layoutEditText = layoutEnterWorkingDays.getLayout(),
                                    strError = getString(R.string.error_minimum_amount_is_not_observed),
                                    scroll = false
                                )
                            }
                            else -> {
                                val diffDay = Utility.differenceBetweenTimestamps(
                                    data.startDate?.toLong(),
                                    data.endDate?.toLong()
                                )
                                val endDateDifferenceWithToday =
                                    Utility.differenceBetweenTimestamps(
                                        data.endDate?.toLong(),
                                        System.currentTimeMillis()
                                    )
                                val workingEnterDate =
                                    (viewDataBinding?.stepper?.getStepLayoutBindingByStep(
                                        3
                                    ) as (ObjectionNonExistentDurationWorkStepBinding))
                                        .layoutEnterWorkingDays.getInput().text.toString()
                                when {
                                    diffDay <= 0 -> {
                                        showAlertDialog(
                                            MessageOfRequestDialogFragment.MessageType.ERROR,
                                            requireContext().getString(R.string.error_diffrent_end_date_and_start_date_selected)
                                        )
                                    }
                                    endDateDifferenceWithToday < 60 -> {
                                        showAlertDialog(
                                            MessageOfRequestDialogFragment.MessageType.ERROR,
                                            requireContext().getString(R.string.error_end_date_must_at_least_two_months_smaller_than_today)
                                        )
                                    }
                                    diffDay < workingEnterDate.toInt() -> {
                                        showAlertDialog(
                                            MessageOfRequestDialogFragment.MessageType.ERROR,
                                            requireContext().getString(R.string.error_select_worked_day)
                                        )
                                    }
                                    else -> {
                                        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(
                                            2
                                        ) as (ObjectionNonExistentWorkshopStepBinding)).apply {
                                            data.rwshAddress =
                                                inputAddressWorkshop.getInput().text.toString()
                                            data.rwshname =
                                                layoutWorkShopName.getInput().text.toString()

                                            data.rwshid =
                                                if (layoutWorkShopCode.getInput().text.toString()
                                                        .isBlank()
                                                )
                                                    null
                                                else layoutWorkShopCode.getInput().text.toString()

                                            data.workDays = workingEnterDate
                                            data.rwshManager =
                                                layoutEmployeeName.getInput().text.toString()
                                        }
                                        mViewModel.sendReqSaveNotExist(dataModel)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPreviousStepClickListener(
        stepIndex: Int,
        step: VerticalStepperItemView
    ) {
        viewDataBinding?.stepper?.previousStep()
    }

}

