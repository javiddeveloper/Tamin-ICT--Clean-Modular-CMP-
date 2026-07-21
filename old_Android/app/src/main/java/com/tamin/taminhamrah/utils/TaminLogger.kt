package com.tamin.taminhamrah.utils

import com.tamin.taminhamrah.BuildConfig
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

object TaminLogger {
    var netErrorCount = 0
    val host1 = "https://www.google.com"

    fun checkNetwork() {
        val thread = Thread {

            val time = PersianDate().toString()
            putPingLog(PingState.SENDING, host1, time)

            try {
                val urlc: HttpURLConnection =
                    URL(host1).openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Test")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 10000 //choose your own timeframe
                urlc.readTimeout = 10000 //choose your own timeframe
                urlc.connect()
                val networkcode2 = urlc.responseCode
                Timber.tag("NetworkStateDebug").i("responseCode=" + networkcode2)
                //   return
                if (networkcode2 in 200..300) {
                    netErrorCount = 0
                    putPingLog(PingState.SUCCESS, host1)
                } else {
                    putPingLog(PingState.FAILED, host1)
                    netErrorCount += 1
                    if (netErrorCount < 5)
                        checkNetwork()

                }

            } catch (e: Exception) {
                Timber.tag("NetworkStateDebug").i("EXCEPTION: $e")
                netErrorCount += 1
                putPingLog(PingState.FAILED, host1)
                if (netErrorCount < 5)
                    checkNetwork()
            }
        }
        thread.start()


    }


    fun putLog(map: HashMap<String, String>, enterApp: Boolean = false) {

//        val destination =
//            "${Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)}/TaminLogger"
//        val directory = File(destination)
//        if (!directory.exists())
//            directory.mkdir()
//
//        val logFile = File(directory.path, "LogFile.txt")
//
//        logFile.createNewFile()
//
//        val time = PersianDate()
//
//
//        val writer = FileWriter(logFile, true)
//        if (enterApp)
//            writer.write("\n ###################### OPEN APP ######################\n")
//
//        writer.write("\n************************* EVENT IN : $time *********************************\n")
//        for (key in map.keys)
//            writer.write("$key : ${map[key]}\n")
//
//        writer.flush()
//        writer.close()


        if(BuildConfig.DEBUG) {
            val builder  = StringBuilder()

            for (key in map.keys) {
                builder.append("$key : ${map[key]}\n")
            }
            Timber.i("LOGGER : %s" ,builder.toString())
        }
    }

    fun putPingLog(state: PingState, host: String, time: String? = null) {
//        val destination =
//            "${Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)}/TaminLogger"
//        val directory = File(destination)
//        if (!directory.exists())
//            directory.mkdir()
//
//        val logFile = File(directory.path, "PingLogFile.txt")
//
//        logFile.createNewFile()
//
//
//        var message = when (state) {
//            PingState.SENDING -> "\n*********** sending request to $host in $time\n"
//            PingState.SUCCESS -> "\nsend request to $host finished with SUCCESSFUL State\n"
//            PingState.FAILED -> "\nsending request to $host finished with FAILED State\n"
//        }
//        val writer = FileWriter(logFile, true)
//        writer.write(message)
//        writer.flush()
//        writer.close()

    }

    enum class PingState {
        SENDING, SUCCESS, FAILED
    }


}