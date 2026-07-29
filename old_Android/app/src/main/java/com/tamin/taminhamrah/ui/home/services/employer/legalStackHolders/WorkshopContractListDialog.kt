package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogWorkshopContractListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopContractListDialog :
    BaseBottomSheetDialogFragment<DialogWorkshopContractListBinding, BaseViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    companion object {
        private const val ARG_WORKSHOP_CODE = "ARG_WORKSHOP_CODE"
        private const val ARG_BRANCH_CODE = "ARG_BRANCH_CODE"
        private const val ARG_CONTRACT_ROW = "ARG_CONTRACT_ROW"

        fun newInstance(
            workshopId: String?,
            branchCode: String?,
            contractRowList: ArrayList<String?>?
        ): WorkshopContractListDialog {
            val args = Bundle()
            args.putString(ARG_WORKSHOP_CODE, workshopId)
            args.putString(ARG_BRANCH_CODE, branchCode)
            args.putStringArrayList(ARG_CONTRACT_ROW, contractRowList)
            val fragment = WorkshopContractListDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private var onListener: DialogResultInterface.OnResultListener<ArrayList<String?>>? = null

    fun setListener(listener: DialogResultInterface.OnResultListener<ArrayList<String?>>) {
        onListener = listener
    }

    private val contractRowList: ArrayList<String?> by lazy {
        arguments?.getStringArrayList(ARG_CONTRACT_ROW) ?: ArrayList()
    }
    private val listAdapter: WorkshopContractListAdapter by lazy {
        WorkshopContractListAdapter(object :
            AdapterInterface.OnItemClickListener<ArrayList<String?>> {
            override fun onItemClick(
                item: ArrayList<String?>,
                transitionView: View?,
                tag: String?
            ) {
                for (row in item)
                    if (!contractRowList.contains(row))
                        contractRowList.add(row)

            }
        }, contractRowList)

    }

    private val workshopCode by lazy { arguments?.getString(ARG_WORKSHOP_CODE) }
    private val branchCode by lazy { arguments?.getString(ARG_BRANCH_CODE) }

    override fun getLayoutId() = R.layout.dialog_workshop_contract_list
    override val mViewModelDialog: LegalStackHolderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getData()
        onClick()
    }

    private fun setupObserver() {
    }

    private fun initView() {
        setupRecycler(viewBinding?.recycler, adapter = listAdapter)
//        adapter.onItemClickListener = onItemClickListener
        setupRecycler(viewBinding?.recycler, listAdapter)

    }

    private fun getData() {
        if (!workshopCode.isNullOrBlank() && !branchCode.isNullOrBlank()) {

            this@WorkshopContractListDialog.lifecycleScope.launchWhenCreated {
                mViewModelDialog.getEmployerWorkshopList(workshopCode, branchCode)
                    .collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
            }
        }
    }

    private fun onClick() {

    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onListener?.onDialogResult(contractRowList)
    }
}