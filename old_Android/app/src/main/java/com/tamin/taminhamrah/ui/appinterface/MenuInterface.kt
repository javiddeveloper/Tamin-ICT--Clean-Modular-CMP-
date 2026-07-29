package com.tamin.taminhamrah.ui.appinterface

import com.tamin.taminhamrah.data.entity.MenuModel

class MenuInterface {

    interface OnFetchData{
        fun onFetch()
    }
    interface OnSearch{
        fun onSearch(str:String)
    }

    interface OnResult {
        fun onResult(itemResult: MenuModel)
    }
    interface OnResultListItem {
        fun onResult(item: List<MenuModel>)
    }

}




