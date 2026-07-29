package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.CheckStatusConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.FinalConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.ResultRequestSaveOfObjectionInsuranceResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.SendConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.databinding.FragmentObjectionInsuranceHistoryBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.historyinsurance.HistoryAdapter
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter.HistoryEditedAdapter
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.detailInfo.ObjectionInsuranceHistoryDetailDialog
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ObjectionInsuranceHistoryFragment :
    BaseFragment<FragmentObjectionInsuranceHistoryBinding,
            ObjectionInsuranceHistoryViewModel>(){

    //region Variables
    override val mViewModel: ObjectionInsuranceHistoryViewModel by viewModels()
    lateinit var listAdapter: HistoryAdapter
    private val editedWorkshopAdapter by lazy { HistoryEditedAdapter() }
    private val onDeleteListener by lazy {
      object: AdapterInterface.OnDeleteClickListener<String>{
          override fun onDelete(item: String) {
              val deleteEditedHistories = arrayListOf<ObjectionInsuranceHistoryModel>()
              editedHistory.forEach { itemHistory ->
                  if (itemHistory.year == item) {
                      deleteEditedHistories.add(itemHistory)
                  }
              }
              editedHistory.removeAll(deleteEditedHistories.toSet())
              allHistory.forEach { oldItem ->
                  if (oldItem.year == item) {
                      oldItem.deletedEditItem()
                  }
              }
              listHistoryEdited.remove(item)
              editedWorkshopAdapter.deleteItem(item)
              if (listHistoryEdited.size < 1) {
                  viewDataBinding?.apply {
                      groupEditedHisories.isVisible = false
                  }
              }
          }
      }
    }
    private val onSubmitConflictList by lazy {
       object :AdapterInterface.OnObjectionInsuranceHistoryListener{
           override fun sendObjectionInsurance(info: ObjectionInsuranceHistoryModel?) {
               info?.let { item ->
                   val itemsToRemove = mutableListOf<ObjectionInsuranceHistoryModel>()

                   editedHistory.forEach { olditem ->
                       if(olditem.year == item.year && olditem.historyTypeName == item.historyTypeName && olditem.workShopName == item.workShopName) {
                        //   editedHistory.remove(olditem)
                           itemsToRemove.add(olditem)
                           return@forEach
                       }
                   }
                   editedHistory.removeAll(itemsToRemove.toSet())

                   allHistory.forEach { olditem ->
                           if(olditem.year == item.year && olditem.historyTypeName == item.historyTypeName && olditem.workShopName == item.workShopName) {
                               olditem.editedItem(item)
                               return@forEach
                           }
                   }
                   editedHistory.add(item)

                   item.setDefaultValue()
                   item.year.let {
                       listHistoryEdited.add(it)
                   }
                   viewDataBinding?.groupEditedHisories?.visibility = View.VISIBLE

                   editedWorkshopAdapter.setItems(listHistoryEdited.toList())
               }
           }
       }
    }
    private val onCleanConflictList by lazy {
        object : AdapterInterface.OnDeleteClickListener<ObjectionInsuranceHistoryModel> {
            override fun onDelete(item: ObjectionInsuranceHistoryModel) {
                editedHistory.remove(item)
                allHistory.forEach { oldItem ->
                    if (oldItem == item) {
                        oldItem.deletedEditItem()
                    }
                }
                listHistoryEdited.remove(item.year)
                item.year.let { editedWorkshopAdapter.deleteItem(it) }

                if (listHistoryEdited.size < 1) {
                    viewDataBinding?.let {
                        it.groupEditedHisories.visibility =
                            View.GONE
                    }
                }
            }

        }
    }

    var allHistory = arrayListOf<ObjectionInsuranceHistoryModel>()
    var editedHistory = arrayListOf<ObjectionInsuranceHistoryModel>()
    var listHistoryEdited = mutableSetOf<String>()
    var idForItem: Int = 0
    val onClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<String> {
            override fun onItemClick(item: String, transitionView: View?, tag: String?) {
                val sameHistoryList = arrayListOf<ObjectionInsuranceHistoryModel>()
                allHistory.forEach { itemHistory ->
                        if (itemHistory.year == item) {
                            itemHistory.detailInfo =
                                MenuModel(
                                    id = item,
                                    title = (itemHistory.workShopName
                                        ?: "_") + "-" + itemHistory.year,
                                    description2 = itemHistory.workShopName,
                                    description = itemHistory.historyTypeName,
                                    showDesc = true,
                                    isEdited = itemHistory.isEdited
                                )
                            sameHistoryList.add(itemHistory)
                        }
                    }
                    if (sameHistoryList.size == 1) {
                        showDetailInfoDialog(sameHistoryList[0])
                    } else {
                        MenuDialogFragment.newInstance().apply {
                            setMenuListener(object : MenuInterface.OnFetchData {
                                override fun onFetch() {
                                    this@ObjectionInsuranceHistoryFragment.lifecycleScope.launchWhenCreated {
                                        val pager = Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                            pagingSourceFactory = { LocalPagingSource(sameHistoryList) })
                                        pager.flow.cachedIn(lifecycleScope)
                                            .collectLatest { pagingData ->
                                                val result = pagingData.map {item->
                                                        MenuModel(
                                                        id = item.year.toString(),
                                                        title = item.detailInfo.title,
                                                        description = item.detailInfo.description,
                                                        description2 = item.detailInfo.description2,
                                                        showDesc = item.detailInfo.showDesc,
                                                        isEdited = item.isEdited
                                                        )
                                                }
                                                updateData(result)
                                        }
                                    }
                                }
                            }, object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {
                                    view?.windowToken?.let {
                                        Utility.hideKeyboard(
                                            requireContext(),
                                            it
                                        )
                                    }
                                    allHistory.forEach { info ->
                                        if (itemResult.id == info.year && itemResult.description == info.historyTypeName && itemResult.description2 == info.workShopName) {
                                            showDetailInfoDialog(info)
                                            return@forEach
                                        }
                                    }

                                }
                            })
                        }.show(childFragmentManager, "MenuDialogFragment")
                    }
              }
        }
    }
    //endregion

    //region Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_objection_insurance_history
    override fun setupObserver() {
        mViewModel.mldCheckStatusConflict.observe(this, ::onCheckStatusConflict)
        mViewModel.mldRequestObjectionInsurance.observe(this, ::onSendRequest)
        mViewModel.mldSendConfirmConflict.observe(this, ::onConfirmConflict)
        mViewModel.mldSendFinalConfirmConflict.observe(this, ::onSendFinalConfirm)
    }

    override fun getData() { mViewModel.checkStatusConflict() }

    override fun initView() {
        listAdapter = HistoryAdapter().apply {
            onItemClickListener = onClickListener
        }
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)
            appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetHistoryFragment, false)
            }
            initAdapterEditedWorkShop()
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null,
            )
        }
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnSendReq.setOnClickListener {
                if (editedHistory.size > 0) {
                   DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                       arguments = createBundle(
                           MessageOfRequestDialogFragment.MessageType.CONFIRM,
                           this@ObjectionInsuranceHistoryFragment.getString(R.string.label_Agreement_use_absentee_services_Social_Security_organization), btnCancel = true
                       )
                       isCancelable = true
                       setDialogClickListener(object : DialogClickInterface.onClickListener {
                           override fun onConfirmClick() {
                               mViewModel.postRequestObjectionInsuranceHistory(editedHistory)
                           }

                           override fun onCancelClick() {
                           }
                       }
                       )
                   }.show(childFragmentManager, "Alert Dialog MessageOfRequest")


                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        this@ObjectionInsuranceHistoryFragment.getString(R.string.error_any_edited_history)
                    )
                }
            }
        }
    }
    //endregion

    //region Listeners
    private fun onConfirmConflict(result: SendConfirmConflictResponse) {
        if (!result.isSuccess) {
            return
        }
            if (result.data == true) {
                mViewModel.sendfinalconfirmconflict()
            }else{
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_any_edited_history)
                )
            }
    }

    private fun onSendRequest(result: ResultRequestSaveOfObjectionInsuranceResponse) {
        if (result.isSuccess) {
            val desc =
                ConfirmConflictResponseItem(userDesc = viewDataBinding?.inputDescription?.getInput()?.text.toString())
            mViewModel.sendConfirmConflict(arrayListOf(desc))
        }
    }

    private fun onCheckStatusConflict(result: CheckStatusConflictResponse) {
        if (!result.isSuccess)
            return
            if (result.data == true) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.INFO,getString(R.string.label_you_are_reviewing_request))
            } else {
                this@ObjectionInsuranceHistoryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getObjectionInsuranceHistory.collectLatest { pagingData ->
                        val yearMap = mutableSetOf<String>()
                        val yearItem = pagingData.map {
                            it.id = idForItem++
                            allHistory.add(it)
                            it.year
                        }.filter { year ->
                            if (yearMap.contains(year)) {
                                false
                            } else {
                                yearMap.add(year)
                            }
                        }
                        listAdapter.submitData(yearItem)
                    }
                }
            }
    }

    private fun onSendFinalConfirm(result: FinalConfirmConflictResponse) {
        if (result.isSuccess) {
            if (result.data.isNullOrEmpty()) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data)
                )
            } else {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    getString(R.string.label_request_with_tracking_number,result.data), dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
        }
    }
    //endregion

    //region Utils
    private fun initAdapterEditedWorkShop() {
        viewDataBinding?.apply {
                recyclerEditedHisories.apply {
                    adapter = editedWorkshopAdapter
                    layoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
                    if (itemDecorationCount == 0) {
                        addItemDecoration(UiUtils.GridSpacingItemDecoration(4, 15))
                    }
                }
                editedWorkshopAdapter.setListener(onDeleteListener)
        }
    }
    private fun showDetailInfoDialog(detailInfo: ObjectionInsuranceHistoryModel) {
        ObjectionInsuranceHistoryDetailDialog().apply {
            arguments = Bundle().apply {
                putParcelable(
                    Constants.DATA_CLASS,
                    detailInfo
                )
            }
            setListener(onSubmitConflictList,onCleanConflictList)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        }.show(childFragmentManager, "ObjectionInsuranceHistoryDetailDialog")
    }
    //endregion

}