package com.tamin.taminhamrah.data.remote.models.services.contract

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

class Clause38DetailResponse : ListDataModel<Clause38Detail>()

@Parcelize
data class Clause38Detail(
    var rcntassignadrs:@RawValue Any? = null,
    var contractDate: String? = null,
    var csp_cntedat: @RawValue Any? = null,
    var contractRow: String? = null,
    var csp_letno: @RawValue Any? = null,
    var descriptions: @RawValue Any? = null,
    var createdt: @RawValue Any? = null,
    var csp_cntamt: @RawValue Any? = null,
    var csp_letdat: @RawValue Any? = null,
    var boss_mobtdt: @RawValue Any? = null,
    var editdt: @RawValue Any? = null,
    var parvande: @RawValue Any? = null,
    var clearanceAmount:Long?= null,
    var cnt_specialtype: @RawValue Any? = null,
    var boss_drmdnam: @RawValue Any? = null,
    var clearanceNumber: String? = null,
    var boss_mobtid: @RawValue Any? = null,
    var contractStartDate: String? = null,
    var confirmuid: @RawValue Any? = null,
    var cityname: @RawValue Any? = null,
    var contractNumber: String? = null,
    var csp_cntsdat: @RawValue Any? = null,
    var mfs_stat: @RawValue Any? = null,
    var wshadr: @RawValue Any? = null,
    var boss_brchnam: @RawValue Any? = null,
    var contractAmount: @RawValue Any? = null,
    var csp_bldamt: @RawValue Any? = null,
    var contractAssigner: @RawValue Any? = null,
    var rwshid: String? = null,
    var confirmdt: @RawValue Any? = null,
    var boss_drmdid: @RawValue Any? = null,
    var workshopName: String? = null,
    var sumOfDebits: @RawValue Any? = null,
    var cnt_knd: @RawValue Any? = null,
    var drmdDesc: @RawValue Any? = null,
    var boss_brchid: @RawValue Any? = null,
    var cnt_clcseq: @RawValue Any? = null,
    var bldopr: @RawValue Any? = null,
    var boss_drmddt: @RawValue Any? = null,
    var brch_code: @RawValue Any? = null,
    var boss_drmd_sign: @RawValue Any? = null,
    var boss_brch_sign: @RawValue Any? = null,
    var clearanceSerial: String? = null,
    var rcntassignname: @RawValue String? = null,
    var contractSubject: String? = null,
    var contractEndDate: String? = null,
    var clearanceDate: String? = null,
    var branchName: @RawValue Any? = null,
    var clcAmountTxt: @RawValue Any? = null,
    var drmdDate: @RawValue Any? = null,
    var rcntseq: @RawValue Any? = null,
    var contractCharacter: @RawValue Any? = null,
    var debitString: @RawValue Any? = null,
    var createuid: @RawValue Any? = null,
    var edituid: @RawValue Any? = null,
    var boss_brchdt: @RawValue Any? = null
) : Parcelable{
    fun createKeyValue(item: Clause38Detail): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("کد کارگاه", item.rwshid ?: "-"))
        keyValueList.add(KeyValueModel("ردیف پیمان", item.contractRow ?: "-"))
        keyValueList.add(KeyValueModel("نام پیمانکار", item.workshopName ?: "-"))
        keyValueList.add(KeyValueModel("موضوع قرارداد", item.contractSubject ?: "-"))
        keyValueList.add(KeyValueModel("سریال مفاصاحساب", item.clearanceSerial ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ مفاصاحساب", Utility.getDateSeparator(item.clearanceDate)))
        keyValueList.add(KeyValueModel("شماره مفاصاحساب", item.clearanceNumber ?: "-"))
        keyValueList.add(KeyValueModel("شماره قرارداد", item.contractNumber ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ شروع قرارداد", Utility.getDateSeparator(item.contractStartDate)))
        keyValueList.add(KeyValueModel("تاریخ پایان قرارداد", Utility.getDateSeparator(item.contractEndDate)))
        keyValueList.add(KeyValueModel("واگذارنده", item.rcntassignname ?: "-"))
        return keyValueList
    }
}
