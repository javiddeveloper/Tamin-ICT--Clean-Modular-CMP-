package com.tamin.taminhamrah.ui.aiAgent.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.HistoryCategoryBottomSheetBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.CATEGORY_ITEM
import com.tamin.taminhamrah.ui.aiAgent.ui.LOAD_ITEM
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.dismissWithResult
import com.tamin.taminhamrah.utils.setBackStackResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



@AndroidEntryPoint
class HistoryCategoryBottomSheet() :
    BaseBottomSheetDialogFragment<HistoryCategoryBottomSheetBinding, HistoryCategoryViewModel>() {


    override val mViewModelDialog: HistoryCategoryViewModel by viewModels()

    private val args: HistoryCategoryBottomSheetArgs by navArgs()

    private lateinit var historyCategoryAdapter: HistoryCategoryAdapter

    override fun getLayoutId() = R.layout.history_category_bottom_sheet


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        collectData()
        setupChipGroup()
        clickNewChat()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModelDialog.historyCategory
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { pagingData ->
                    historyCategoryAdapter.submitData(pagingData)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            historyCategoryAdapter.loadStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { loadState ->
                    val isEmpty =
                        loadState.refresh is LoadState.NotLoading && historyCategoryAdapter.itemCount == 0
                    viewBinding?.apply {
                        txtEmpty.isInvisible = !isEmpty
                        rcvCategory.isInvisible = isEmpty
                    }
                }
        }
        mViewModelDialog.getHistoryCategory()
    }

    private fun initView() {

        historyCategoryAdapter = HistoryCategoryAdapter({
            when (it) {
                is CategoryHistoryClickType.Delete -> {
                    mViewModelDialog.deleteCategory(it.id)
                }

                is CategoryHistoryClickType.Edit -> {
                    dismissWithResult(it.item, CATEGORY_ITEM)
                }

                is CategoryHistoryClickType.LoadItems -> {
                    dismissWithResult(it.id, LOAD_ITEM)
                }
            }


        })

        viewBinding?.rcvCategory?.apply {
            adapter = historyCategoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(context))

        }
    }

    private fun setupChipGroup() {
        viewBinding?.apply {
            val initialChipId = if (args.isLawSearch) R.id.chip_law else R.id.chip_assistant
            chipGroupMode.check(initialChipId)
            chipGroupMode.setOnCheckedChangeListener { _, checkedId ->
                val isLawSearch = checkedId == R.id.chip_law
                setBackStackResult(isLawSearch, "IS_LAW_SEARCH")
            }
        }
    }

    private fun clickNewChat() {
        viewBinding?.newChat?.setOnClickListener {
            dismissWithResult(true, "START_NEW_CHAT")
        }
    }

}
