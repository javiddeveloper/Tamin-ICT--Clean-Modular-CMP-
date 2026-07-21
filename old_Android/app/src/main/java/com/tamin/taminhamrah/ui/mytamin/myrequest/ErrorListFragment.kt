package com.tamin.taminhamrah.ui.mytamin.myrequest

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.user.MyRequestListResponse
import com.tamin.taminhamrah.databinding.FragmentListBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ErrorListFragment :
    BaseFragment<FragmentListBinding, MyRequestListViewModel>() {

    lateinit var listAdapter: MyRequestAdapter

    override val mViewModel: MyRequestListViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list
    }

    override fun setupObserver() {
//        mViewModel.mldRequestList.observe(this, ::showResult)
    }

    override fun initView() {
        listAdapter = MyRequestAdapter()
        val layoutManager =
            LinearLayoutManager(requireContext())
      /*  viewDataBinding?.recycler?.apply {
            this.layoutManager = layoutManager
            this.adapter = listAdapter

            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }
*/
        setupToolbar(

            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_outbox
        )
    }

    override fun getData() {
//        mViewModel.getMyRequestList()
    }

    override fun onClick() {
       /* viewDataBinding?.appBar?.toolbar?.apply {
            imageBack.setOnClickListener { backButtonPress() }
//            btnShowError?.setOnClickListener {  }
        }*/

        /* imgInfo?.setOnClickListener {
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
 */
    }

    private fun showResult(result: Resource<BaseListResponse<MyRequestListResponse>?>?) {

        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result?.status == Resource.Status.SUCCESS){
        }

     /*   when (model?.status) {
            Resource.Status.SUCCESS -> {
                (requireActivity() as? MainActivity)?.hideLoading()

//                model.data?.let { listAdapter.setItems(it) }

            }
            Resource.Status.LOADING -> {
                (requireActivity() as? MainActivity)?.showLoading()
            }
            Resource.Status.ERROR -> {
                (requireActivity() as? MainActivity)?.hideLoading()
                showSnackbar(model.message ?: getString(R.string.message_invalide_error))
            }
            Resource.Status.NEED_REFRESH_TOKEN -> {
                (requireActivity() as? MainActivity)?.hideLoading()
                handlePageDestination(R.id.action_bank_account_list_to_refreshToken)
            }
            Resource.Status.NEED_NETWORK -> {
                (requireActivity() as? MainActivity)?.hideLoading()
                model.message?.let { showSnackbar(it) }
            }

        }*/
    }
}