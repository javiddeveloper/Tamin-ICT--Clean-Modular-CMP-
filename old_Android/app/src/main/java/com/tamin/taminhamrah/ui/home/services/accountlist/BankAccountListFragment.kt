package com.tamin.taminhamrah.ui.home.services.accountlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.BankAccount
import com.tamin.taminhamrah.databinding.FragmentBankAccountListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BankAccountListFragment :
    BaseFragment<FragmentBankAccountListBinding, BankAccountListViewModel>(),
    AdapterInterface.OnItemClickListener<BankAccount> {

    lateinit var listAdapter: BankAccountAdapter

    override val mViewModel: BankAccountListViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_bank_account_list
    }

    override fun setupObserver() {
    }

    override fun initView() {


        listAdapter = BankAccountAdapter()
        setupRecycler(viewDataBinding?.recyclerList, listAdapter)

        viewDataBinding?.appBar?.toolbar?.imgInfo?.setOnClickListener {
            mViewModel.saveBoolean(Constants.TapTargetBankAccountListFragment, false)
            setViewForShowGide()
        }

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
        )

        viewDataBinding?.btnNewAccount?.setOnClickListener {
            handlePageDestination(
                R.id.action_account_list_to_declare_bank_account,
                bundle = createToolbarBundle()
            )
        }

        setViewForShowGide()
    }

    override fun getData() {
        this@BankAccountListFragment.lifecycleScope.launchWhenCreated {
            mViewModel.mldBankAccountList.collectLatest { pagingData ->
                listAdapter.submitData(pagingData)
            }
        }
    }

    override fun onClick() {
    }

/*
    private fun onClick() {
         viewDataBinding?.appBar?.toolbar?.imgInfo?.setOnClickListener {
             val bundle = Bundle()
             bundle.putString(
                 ServiceGuideDialogFragment.ARG_GUIDE_TITLE,
                 getString(R.string.label_active_relation_inquiry)
             )
             bundle.putString(
                 ServiceGuideDialogFragment.ARG_GUIDE_RULES,
                 getString(R.string.label_active_relation_rules)
             )
             handlePageDestination(R.id.action_active_relation_to_guide_dialog, bundle)
         }
    }*/

    override fun onItemClick(item: BankAccount, transitionView: View?, tag: String?) {
        // handlePageDestination(R.id.action_active_relation_to_certificate)
    }


    private fun setViewForShowGide() {
        /*  try {
              if (!mViewModel.loadBoolean(Constants.TapTargetBankAccountListFragment)) {
                  val ViewsList = arrayListOf<TapTargetModel>()
                  (viewDataBinding)?.appBar?.toolbar?.imgInfo?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_img_info_tag_target_view,R.string.detail_info_img_tag_target_view))
                  }
                  this.viewDataBinding?.recycler?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_bank_account_list_tag_target_view,R.string.detail_bank_account_list_tag_target_view,shape = Shape.RECT))
                  }
                  mViewModel.saveBoolean(Constants.TapTargetBankAccountListFragment, true)

                  ViewsList?.let {
                      showGide(it)
                  }
              }
          } catch (e: Exception) {
              Log.e("showGide: ", e.message.toString())
          }*/
    }


    fun createToolbarBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(
            Constants.TOOLBAR_TITLE,
            getString(R.string.appbar_title_declare_bank_account)
        )
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconDrawable(arguments))

        return bundle
    }
}