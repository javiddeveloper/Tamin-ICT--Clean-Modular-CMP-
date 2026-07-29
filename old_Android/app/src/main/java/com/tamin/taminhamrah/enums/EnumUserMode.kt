package com.tamin.taminhamrah.enums

enum class EnumUserMode constructor(var methodName:String, var methodValue:Int) {
    TYPE_ALL("ALL",4),
    MODE_INSURED("بیمه شده", 1),
    MODE_PENSIONER("مستمری بگیر", 2),
    MODE_EMPLOYER("کارفرما", 3)

}