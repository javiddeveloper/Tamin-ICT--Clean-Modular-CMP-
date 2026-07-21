package com.tamin.taminhamrah.ui.mytamin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.FragmentMytaminBinding
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyTaminFragment :
    BaseFragment<FragmentMytaminBinding, MyTaminViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    lateinit var listAdapter: NavigatorAdapter

    override val mViewModel: MyTaminViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mytamin
    }

    override fun setupObserver() {
        // mViewModel.mldAccountResult.observe(this, ::showResult)
    }

    override fun onClick() {
       /* viewDataBinding?.apply {
            groupMyRequest.setOnClickListener {
                val bundle = createToolbarBundle(tvTitleMyRequest.text.toString(), tvDescriptionMyRequest.text.toString(), R.drawable.ic_my_request_colorful)
                handlePageDestination(R.id.action_my_tamin_to_my_request, bundle)
            }
            groupMyInbox.setOnClickListener {
                val bundle = createToolbarBundle(tvTitleMyInbox.text.toString(), tvDescriptionMyInbox.text.toString(), R.drawable.ic_my_inbox)
                handlePageDestination(R.id.action_my_tamin_to_inbox, bundle)
            }
        }*/
    }

    override fun getData() {

    }

    override fun initView() {

        listAdapter = NavigatorAdapter()
        viewDataBinding?.recycler?.apply {
            adapter = listAdapter
            if (itemDecorationCount == 0)
                addItemDecoration(UiUtils.createDivider(requireContext()))
        }
        listAdapter.setItems(mViewModel.getMyTaminList(), this)
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        when (item.id) {
            "1" -> {
                val bundle = createToolbarBundle(item)
                handlePageDestination(R.id.action_my_tamin_to_my_request, bundle)
            }
            "3" -> {
                val bundle = createToolbarBundle(item)
                handlePageDestination(R.id.action_my_tamin_to_inbox, bundle)
            }
        }
    }

    fun createToolbarBundle(item:MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }
}