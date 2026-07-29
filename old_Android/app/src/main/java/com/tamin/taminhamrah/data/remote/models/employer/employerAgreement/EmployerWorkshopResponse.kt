package com.tamin.taminhamrah.data.remote.models.employer.employerAgreement

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.utils.Utility

class EmployerWorkshopResponse : ListDataModel<EmployerWorkshop>()
data class EmployerWorkshop(

    var pymseq: String? = null,
    var regno: Any? = null,
    var firstname: String? = null,
    var emailaddr: String? = null,
    var workshop: ContractInfo.Workshop? = null,
    var nationalno: Any? = null,
    var mobileno: String? = null,
    var startdate: String? = null,
    var mastcusttype: String? = null,
    var createdt: String? = null,
    var masttyp: String? = null,
    var logicalDeleted:Boolean? = false,
    var regemailseq: String? = null,
    var lastname: String? = null,
    var risuid: Any? = null,
    var nationalcode: String? = null,
    var enddate: Any? = null,
    var letDate: String? = null,
    var regdate: Any? = null,
    var roletype: String? = null,
    var dname: Any? = null,
    var letNo: String? = null,
    var createuid: String? = null
){

    var isSelectedItem:Boolean=false
    
    fun getTitle(str:String?) = str?:"-"

    fun getLocalDate(dateStr:String?) = Utility.getDateSeparator(dateStr)
}
