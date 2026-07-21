package com.tamin.taminhamrah.ui.home.services.dependentCancellation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.FragmentDependentCancellationBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.base.BaseFragmentMVI
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.widget.DatePickerWidget
import com.tamin.taminhamrah.widget.edittext.SelectableItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import kotlin.getValue

@AndroidEntryPoint
class CancellationDependentFragment :
    BaseFragmentMVI<FragmentDependentCancellationBinding, DependentCancellationViewModel,
            DependentCancellationContract.DependentCancellationState,
            DependentCancellationContract.DependentCancellationEvent>() {

    override val viewModel: DependentCancellationViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDependentCancellationBinding {
        return FragmentDependentCancellationBinding.inflate(inflater, container, false)
    }

    override fun onViewBindingCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewBindingCreated(view, savedInstanceState)
        initView()
        onClick()
    }

    private fun initView() {
        binding?.apply {
            state = viewModel.state.value
            lifecycleOwner = viewLifecycleOwner

            var toolbarTitle = Utility.getToolbarTitle(arguments)
            if (toolbarTitle.isEmpty()) {
                toolbarTitle = getString(R.string.title_dependent_cancellation)
            }
            appBar.tvTitle.text = toolbarTitle

            appBar.toolbar.imageBack.setOnClickListener {
                viewModel.processIntent(DependentCancellationContract.DependentCancellationIntent.OnBackPress)
            }
        }
    }

    private fun onClick() {
        binding?.apply {
            selectDependent.setOnClickListener(object : SelectableItemView.OnClickListener {
                override fun onclick() {
                    showDependentMenu()
                }
            })

            selectReason.setOnClickListener(object : SelectableItemView.OnClickListener {
                override fun onclick() {
                    showReasonMenu()
                }
            })

            cbApproval.setOnCheckedChangeListener { _, _ ->
                viewModel.processIntent(DependentCancellationContract.DependentCancellationIntent.ToggleApproval)
            }

            btnSubmit.setOnClickListener {
                viewModel.processIntent(DependentCancellationContract.DependentCancellationIntent.Submit)
            }
            datePickerFrom.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    viewModel.processIntent(
                        DependentCancellationContract.DependentCancellationIntent.SelectDate(
                            serverFormattedDate
                        )
                    )
                }
            })
        }
    }

    private fun showDependentMenu() {
        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        val dialog = MenuDialogFragment.newInstance(true, getString(R.string.select_dependent))
        val dependents = viewModel.state.value.dependents

        dialog.setMenuListener(object : MenuInterface.OnFetchData {
            override fun onFetch() {
                lifecycleScope.launchWhenCreated {
                    Pager(
                        config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                        pagingSourceFactory = { LocalPagingSource(dependents) }
                    ).flow.collectLatest { pagingData ->
                        val menuModels = pagingData.map {
                            MenuModel(
                                id = it.identityInfo.nationalId,
                                title = "${it.identityInfo.firstName} ${it.identityInfo.lastName}",
                                baseModel = it
                            )
                        }
                        dialog.updateData(menuModels)
                    }
                }
            }
        }, object : MenuInterface.OnResult {
            override fun onResult(itemResult: MenuModel) {
                (itemResult.baseModel as? com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoModel)?.let {
                    viewModel.processIntent(
                        DependentCancellationContract.DependentCancellationIntent.SelectDependent(
                            it
                        )
                    )
                }
            }
        })
        dialog.show(childFragmentManager, "DependentMenu")
    }

    private fun showReasonMenu() {
        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
        val dialog =
            MenuDialogFragment.newInstance(false, getString(R.string.cancellation_reason_hint))
        val reasons = DependentCancellationContract.CancellationReason.values()

        dialog.setMenuListener(object : MenuInterface.OnFetchData {
            override fun onFetch() {
                lifecycleScope.launchWhenCreated {
                    Pager(
                        config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                        pagingSourceFactory = { LocalPagingSource(reasons.toList()) }
                    ).flow.collectLatest { pagingData ->
                        val menuModels = pagingData.map {
                            MenuModel(
                                id = it.name,
                                title = getString(
                                    when (it) {
                                        DependentCancellationContract.CancellationReason.DEATH -> R.string.reason_death
                                        DependentCancellationContract.CancellationReason.DIVORCE -> R.string.reason_divorce
                                        DependentCancellationContract.CancellationReason.JOB_MALE -> R.string.reason_job_male
                                        DependentCancellationContract.CancellationReason.JOB_FEMALE -> R.string.reason_job_female
                                        else -> R.string.retry
                                    }
                                )
                            )
                        }
                        dialog.updateData(menuModels)
                    }
                }
            }
        }, object : MenuInterface.OnResult {
            override fun onResult(itemResult: MenuModel) {
                itemResult.id?.let { id ->
                    val reason = DependentCancellationContract.CancellationReason.valueOf(id)
                    viewModel.processIntent(
                        DependentCancellationContract.DependentCancellationIntent.SelectCancellationReason(
                            reason
                        )
                    )
                }
            }
        })
        dialog.show(childFragmentManager, "ReasonMenu")
    }

    override fun renderState(state: DependentCancellationContract.DependentCancellationState) {
        binding?.state = state
        binding?.executePendingBindings()

        state.selectedDependent?.let {
            binding?.selectDependent?.setValue("${it.identityInfo.firstName} ${it.identityInfo.lastName}")
        }

        state.selectedReason?.let {
            val reasonTitle = getString(
                when (it) {
                    DependentCancellationContract.CancellationReason.DEATH -> R.string.reason_death
                    DependentCancellationContract.CancellationReason.DIVORCE -> R.string.reason_divorce
                    DependentCancellationContract.CancellationReason.JOB_MALE -> R.string.reason_job_male
                    DependentCancellationContract.CancellationReason.JOB_FEMALE -> R.string.reason_job_female
                }
            )
            binding?.selectReason?.setValue(reasonTitle)
        }
    }

    override fun handleEvent(event: DependentCancellationContract.DependentCancellationEvent) {
        when (event) {
            DependentCancellationContract.DependentCancellationEvent.NavigateBack -> {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

            is DependentCancellationContract.DependentCancellationEvent.ShowSuccess -> {
                showSuccessDialog(event.message)
            }

            DependentCancellationContract.DependentCancellationEvent.ShowEmptyDependentsDialog -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.no_dependents_message)
                )
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        val dialog =
            com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = com.tamin.taminhamrah.utils.extentions.createBundle(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            message
        )
        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

            override fun onCancelClick() {}
        })
        dialog.show(childFragmentManager, "SuccessDialog")
    }
}
