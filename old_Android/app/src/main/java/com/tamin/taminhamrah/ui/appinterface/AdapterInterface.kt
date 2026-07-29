package com.tamin.taminhamrah.ui.appinterface

import android.net.Uri
import android.view.View
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel

class AdapterInterface {
    interface OnItemClickListener<T> {
        fun onItemClick(item: T, transitionView: View? = null, tag: String? = "")
    }

    interface OnDownloadClickListener<T> {
        fun onDownload(item: T, transitionView: View? = null, tag: String? = "")
    }

    interface OnShowMoreClickListener<T> {
        fun onShowMoreClick(item: T, transitionView: View? = null, tag: String? = "")
    }

    interface OnDeleteClickListener<T> {
        fun onDelete(item: T)
    }

    interface OnStopDialogListener {
        fun onStop()
    }

    interface OnObjectionInsuranceHistoryListener {
        fun sendObjectionInsurance(item: ObjectionInsuranceHistoryModel?)
    }

    interface OnActionResultInterface<T>{
        fun onEditResult(item:T)
        fun onDeleteResult(item:T)
    }

    interface OnActionResultInterfaceDownload<T>{
        fun onDownloadResult(item:T)
        fun onSendToInboxResult(item:T)
    }

    interface OnActionResultContractListInterface<ContractItem>{
        fun onViewContract(item:ContractItem)
        fun onCancel(item:ContractItem)
        fun onPayment(item:ContractItem)
        fun onAction(item: ContractItem)
    }

    interface OnActionResultContactList<ContractItem>{
        fun onSocialNetworkClick(uri: Uri)
        fun onItemClick(item:ContractItem)
    }
}




