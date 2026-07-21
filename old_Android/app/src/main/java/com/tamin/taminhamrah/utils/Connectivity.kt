package com.tamin.taminhamrah.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager


object Connectivity {

    fun getNetworkInfoString(context: Context): String {
        val info = getNetworkInfo(context)
        return "isConnected=${info != null && info.isConnected} -- type= ${getType(info?.type)} -- subType= ${getSubType(info?.subtype)} "
    }

    fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    fun isConnected(context: Context): Boolean {
        val info: NetworkInfo? = getNetworkInfo(context)
        return info != null && info.isConnected
    }

    fun isConnectedWifi(context: Context): Boolean {
        val info: NetworkInfo? = getNetworkInfo(context)
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
    }

    fun isConnectedMobile(context: Context): Boolean {
        val info: NetworkInfo? = getNetworkInfo(context)
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
    }


    fun isConnectedFast(context: Context): Boolean {
        val info: NetworkInfo? = getNetworkInfo(context)
        return info != null && info.isConnected && isConnectionFast(
            info.type,
            info.subtype
        )
    }

    fun getType(type: Int?) =
        if (type == 0) "TYPE_MOBILE" else if (type == 1) "TYPE_WIFI" else if (type == null) "NULL" else "UNKNOWN_TYPE"

    fun getSubType(subType: Int?) = when (subType) {
        7 -> "NETWORK_TYPE_1xRTT"
        4 -> "NETWORK_TYPE_CDMA"
        2 -> "NETWORK_TYPE_EDGE"
        5 -> "NETWORK_TYPE_EVDO_0"
        6 -> "NETWORK_TYPE_EVDO_A"
        1 -> "NETWORK_TYPE_GPRS"
        8 -> "NETWORK_TYPE_HSDPA"
        10 -> "NETWORK_TYPE_HSPA"
        9 -> "NETWORK_TYPE_HSUPA"
        3 -> "NETWORK_TYPE_UMTS"
        14 -> "NETWORK_TYPE_EHRPD"
        12 -> "NETWORK_TYPE_EVDO_B"
        15 -> "NETWORK_TYPE_HSPAP"
        13 -> "NETWORK_TYPE_LTE"
        11 -> "NETWORK_TYPE_IDEN"
        0 -> "NETWORK_TYPE_UNKNOWN"
        null -> "NULL"
        else -> "UNKNOWN_SUB_TYPE"

    }

    fun isConnectionFast(type: Int, subType: Int): Boolean {
        return when (type) {
            ConnectivityManager.TYPE_WIFI -> {
                true
            }
            ConnectivityManager.TYPE_MOBILE -> {
                when (subType) {
                    TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                    TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                    TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                    TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                    TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                    TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                    TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                    TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                    TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                    TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                    TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                    TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                    else -> false
                }
            }
            else -> {
                false
            }
        }
    }
}