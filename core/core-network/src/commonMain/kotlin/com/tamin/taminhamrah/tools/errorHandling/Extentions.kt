/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.core.network.tools.errorHandling

import android.util.Log

fun debugLog(message: String) {
    Log.d("NetworkClient", message)
    println("NetworkClient: $message")
}