package com.tamin.taminhamrah.ui.mytamin

import androidx.lifecycle.MutableLiveData
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyTaminViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldAccountResult = MutableLiveData<Resource<MutableList<MenuModel>?>>()

    fun getMyTaminList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()

        itemList.add(
            MenuModel(
                "درخواست های من",
                "1",
                "ic_my_request_colorful",
                R.drawable.ic_my_request_colorful,
                "کارتابل پیگیری درخواست ها، اطلاع از نتیجه اقدامات و ..."
            )
        )
       /* itemList.add(
            MenuModel(
                "تیکت های پشتیبانی من",
                "2",
                "ic_ticket_colorful",
                0,
                "اطلاعات درخواست های پشتیبانی ثبت شده"
            )
        )*/
        itemList.add(
            MenuModel(
                "صندوق شخصی من",
                "3",
                "ic_inbox_colorful",
                R.drawable.ic_my_inbox,
                "فضایی برای نگهداری و اشتراک گذاری اسناد و مکاتبات"
            )
        )
       /* itemList.add(
            MenuModel(
                "پرونده الکترونیک من",
                "4",
                "",
                0,
                "اسناد و مدارک ثبت شده الکترونیک"
            )
        )
        itemList.add(
            MenuModel(
                "نسخ الکترونیک من",
                "5",
                "",
                0,
                "نسخه های دارویی، پاراکلینیک، ویزیت و خدمات پزشکی"
            )
        )*/

        return itemList
    }

}

