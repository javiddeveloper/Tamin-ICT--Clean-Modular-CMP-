package com.tamin.taminhamrah.ui.appinterface

class DialogResultInterface{
    interface OnResultListener<T> {
        fun onDialogResult(item: T)
    }

    interface onActionResultInterface<T>{
        fun onEditResult(item:T)
        fun onDeleteResult(item:T)
    }

}




