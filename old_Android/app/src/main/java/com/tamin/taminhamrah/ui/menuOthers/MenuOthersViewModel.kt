package com.tamin.taminhamrah.ui.menuOthers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.data.repository.OthersInfoRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuOthersViewModel @Inject constructor(
    private val repository: OthersInfoRepository
) : BaseViewModel() {

    val mldVersioning = MutableLiveData<List<VersionInfoModel>>()

    fun getItemsList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(
            MenuModel(
                "تاریخچه نسخه",
                "1",
                iconRes = R.drawable.ic_edit_mobile,
                description = ""
            )
        )
        itemList.add(
            MenuModel(
                "تماس با ما",
                "2",
                iconRes = R.drawable.ic_phone,
                description = ""
            )
        )
        return itemList
    }

    fun getVersioningInfo() {

        viewModelScope.launch {
            mldVersioning.postValue(repository.getVersioningInfo())
        }

    }


    fun getContactUsInfo(): java.util.ArrayList<MenuModel> {
        val itemList = java.util.ArrayList<MenuModel>()
        itemList.add(
            MenuModel(
                title = "تلفن : ٦٤٥٠١ - ٠٢١",
                iconRes = R.drawable.ic_megaphone,
                id = "0"
            )
        )
        itemList.add(
            MenuModel(
                title = "تلفن : ٦٤٥٠١ - ٠٢١",
                iconRes = R.drawable.ic_ringing_phone,
                id = "1"
            )
        )
        itemList.add(MenuModel(title = "فکس : ۶۶۹۳۱۰۰۸-٠٢١", iconRes = R.drawable.ic_fax, id = "2"))
        itemList.add(
            MenuModel(
                title = "نشانی : تهران،خيابان آزادی، جنب وزارت تعاون،كار و رفاه اجتماعی، پلاك ۳۵۹، سازمان تامين اجتماعی",
                iconRes = R.drawable.ic_location,
                id = "3"
            )
        )
        itemList.add(
            MenuModel(
                title = "کد پستی : ١٤٥٧٩٦٥٥٩٥",
                iconRes = R.drawable.ic_postal_code,
                id = "4"
            )
        )
        itemList.add(
            MenuModel(
                title = " درگاه رسمی : tamin.ir",
                iconRes = R.drawable.ic_website,
                id = "5"
            )
        )
        itemList.add(
            MenuModel(
                title = "پایگاه خبری : news.tamin.ir",
                iconRes = R.drawable.ic_news,
                id = "6"
            )
        )
/*
        itemList.add(MenuModel(title ="ساعت کار ستاد مرکزی : 7:30 لغایت 14:30", iconRes = R.drawable.ic_clock,id = "7"))
*/
        itemList.add(
            MenuModel(
                title = "نشانی پست الکترونیک : info@tamin.ir",
                iconRes = R.drawable.ic_letter,
                id = "8"
            )
        )
        return itemList
    }

    fun getSocialResponsibility()= arrayListOf(
            MenuModel(
                title = "موسسه خیریه عترت فاطمی",
                iconRes = R.drawable.atrat_fatemi_logo,
                id = "0"
            )
    )


}

