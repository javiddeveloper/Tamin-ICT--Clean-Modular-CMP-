package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.FragmentMessagesBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding, MessageFragmentViewModel>() {

    override val mViewModel: MessageFragmentViewModel by viewModels()
    lateinit var listAdapter: ObjectionSmsAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_messages
    }

    override fun setupObserver() {
    }

    override fun initView() {
        listAdapter = ObjectionSmsAdapter()

        setupRecycler(viewDataBinding?.recycler, listAdapter)

        viewDataBinding?.apply {
            tvToolbarTitle.text = getString(R.string.sms)
            imageBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        }
    override fun getData() {
        val objectionId = arguments?.getString(Constants.OBJECTION_ID) ?: ""
        this@MessagesFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getObjectionSms(objectionId).collectLatest { pagingData ->
                var index = 0
                val result = pagingData.map {
                    index++
                    it.index = index.toString()
                    it
                }
                listAdapter.submitData(result)
            }
        }
    }

    override fun onClick() {
    }

}