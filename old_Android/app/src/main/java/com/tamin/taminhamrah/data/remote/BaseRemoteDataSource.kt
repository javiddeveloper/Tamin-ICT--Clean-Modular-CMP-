package com.tamin.taminhamrah.data.remote


import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.BaseResponse
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.utils.TaminLogger
import com.tamin.taminhamrah.utils.ValidationUtil
import okhttp3.ResponseBody
import org.acra.ACRA
import retrofit2.HttpException
import retrofit2.Response
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class BaseRemoteDataSource {

    private fun normalizeRawNetworkMessage(message: String?, statusCode: Int? = null): String {
        val raw = message?.trim().orEmpty()
        if (raw.isBlank() || raw.equals("null", ignoreCase = true)) return raw

        // If it already looks Persian/Arabic, keep as-is.
        if (ValidationUtil.isProbablyArabic(raw)) return raw

        val lower = raw.lowercase()

        // Timeout (from exceptions or server reason/body)
        if (
            statusCode == 408 ||
            statusCode == 504 ||
            lower.contains("timeout") ||
            lower.contains("timed out") ||
            lower.contains("read timed out") ||
            lower.contains("connect timed out")
        ) {
            return "زمان درخواست به پایان رسید. لطفاً مجدداً تلاش کنید."
        }

        // DNS / no network
        if (
            lower.contains("unable to resolve host") ||
            lower.contains("no address associated with hostname") ||
            lower.contains("unknownhostexception")
        ) {
            return "اینترنت شما ضعیف است.دسترسی اینترنت رابررسی کنید."
        }

        // Generic connection issues
        if (
            lower.contains("failed to connect") ||
            lower.contains("connection refused") ||
            lower.contains("connection reset")
        ) {
            return "خطا در برقراری ارتباط."
        }

        return raw
    }

    protected suspend fun <T> getResultList(call: suspend () -> Response<BaseResponse<BaseListResponse<T>?>?>?): Resource<List<T>?> {
        try {
            val response = call()
            if (response != null && response.isSuccessful && response.body()?.status == Constants.RESPONSE_OK) {
                val body = response.body()
                if (body != null)
                    return Resource.success(body.data?.list)
            }

            return handleError(response?.errorBody(), response?.raw()?.code)
        } catch (e: Exception) {
            return Resource.error(handleError(e))
        }
    }

    protected suspend fun <T> getResult(call: suspend () -> Response<BaseResponse<T?>?>?): Resource<T?> {
        try {
            val response = call()
            if (response != null && response.isSuccessful && response.body()?.status == Constants.RESPONSE_OK) {
                val body = response.body()
                if (body != null)
                /* return if ((body.data as? ArrayList<String>)?.get(0)?.contains("PL/SQL") == true
                 ) {
                     Resource.error(MessageModel("خطا در پردازش"))
                 } else {*/
                    return Resource.success(body.data)
                //   }
            }

            return handleError(response?.errorBody(), response?.raw()?.code)
        } catch (e: Exception) {
            return Resource.error(handleError(e))
        }
    }

    protected inline fun <reified T : BaseResponseNew> getResultFile(call: () -> Response<T?>?): T {
        try {
            val response = call()
            if (response != null && response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    body.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                    return body
                }
            }
            return handleError(
                response?.errorBody(),
                response?.raw()?.code,
                T::class.java.newInstance()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return handleErrorNew(e, T::class.java.newInstance())
        }
    }

    protected inline fun getPdfResult(call: () -> Response<ResponseBody?>?): PdfDownloadResponse {
        val result = PdfDownloadResponse()
        try {
            val response = call()
            if (response != null && response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    return result.apply {
                        pdf = data.byteStream()
                        status = 200
                        baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                    }
                }
            }
            return handleError(
                response?.errorBody(),
                response?.raw()?.code,
                result
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return handleErrorNew(e, result)
        }
    }

    protected inline fun <reified T : BaseResponseNew> getResultNew(
        call: () -> Response<T?>?,
        checkStatus: Boolean = true,
    ): T {

        val map = HashMap<String, String>()
        map["CLASS"] = this.javaClass.simpleName
        map["METHOD"] = "getResultNew"

        try {
            val response = call()

            map["response"] = response.toString()
            map["condition"] = "After Call Request and get Response"
            TaminLogger.putLog(map)

            // TODO: We Can Checked Respond the network action
            if (response != null && response.isSuccessful && (if (checkStatus) response.body()?.status == Constants.RESPONSE_OK else true)) {
                val body = response.body()
                if (body != null) {
                    body.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                    return body
                }
            }
            val t = T::class.java.newInstance()
            // Check if the API call URL is the specific one you are interested in
            if (response?.raw()?.request?.url.toString().contains("https://ssodcfs.tamin.ir/eservices/menu_data")) {
                // Log the error with AcraCrashReporter
                ACRA.errorReporter.handleSilentException(Exception("خطا در دریافت فایل منوی ورژن - ${BuildConfig.VERSION_CODE}"))
            }

            return handleError(
                response?.errorBody(),
                response?.raw()?.code,
                t
            )
        } catch (e: Exception) {
            e.printStackTrace()
            map["Exception"] = e.toString()
            var stackTraceString = ""
            for (i in e.stackTrace.indices) {
                stackTraceString += " 0:${e.stackTrace[i]}\n"
            }
            var backTraceString = ""
            if (e.cause != null) {
                for (i in e.cause!!.stackTrace.indices) {
                    backTraceString += "  0:${e.cause!!.stackTrace[i]}\n"
                }
            }
            map["StackTrace"] = stackTraceString
            map["CauseStackTrace"] = backTraceString
            map["condition"] = "Exception"
            TaminLogger.putLog(map)
            return handleErrorNew(e, T::class.java.newInstance())
        }
    }

    protected suspend fun <T> getResultTamin(call: suspend () -> Response<T?>?): T? {
        try {
            val response = call()
            if (response != null && response.isSuccessful) {
                val body = response.body()
                // if (body != null)
                //     return body
            }
            return response?.body()
            //   return handleError(response?.errorBody(), response?.raw()?.code)
        } catch (e: Exception) {
            val a = e
            //  return Resource.error(handleError(e))
        }
        return null

    }

    protected suspend fun <T> getTaminPdf(call: suspend () -> Response<T?>?): Resource<Response<T?>?> {
        try {
            val response = call()
            if (response != null && response.isSuccessful) {
                return Resource.success(response)
            }

            return handleError(response?.errorBody(), response?.raw()?.code)
        } catch (e: Exception) {
            return Resource.error(handleError(e))
        }
    }

    fun <T : BaseResponseNew> handleErrorNew(it: Exception, responseNew: T): T {
        var message = ""
        var errorCode: Int? = 0
        when (it) {
            is JsonSyntaxException -> {
                errorCode = 1000
//                message = "سیستم در حال به روز رسانی میباشد، لطفا در روزهای آتی دوباره مراجعه فرمایید"
                message =
                    "در حال حاضر ارائه خدمت امکان پذیر نمی باشد، لطفا در روزهای آتی دوباره مراجعه فرمایید"
            }
            is SocketTimeoutException -> {
                message = "زمان درخواست به پایان رسید. لطفاً مجدداً تلاش کنید."
            }
            is NoRouteToHostException -> {
                message = "خطا در برقراری ارتباط"
            }
            is UnknownHostException -> {
                message = "اینترنت شما ضعیف است.دسترسی اینترنت رابررسی کنید"
            }
            is IllegalStateException -> {
                message = it.localizedMessage ?: "ERROR::::::IllegalStateException"

            }
            is HttpException->{
                errorCode = (it as? HttpException)?.response()?.code()
                when (errorCode) {
                    403 -> {
                           message = "درخواست از IP یا محدوده جغرافیایی مورد نظر مسدود می باشد. عدم دسترسی از محدوده جغرافیایی"
                           responseNew.isBackToPrevious = true
                    }
                    404 -> {
                        message = it.localizedMessage ?: "404"
                    }
                    422 -> {
                        message = it.localizedMessage ?: "422"
                    }
                    500 -> {
                        if (message.contains("شما فاقد شماره حساب بانکی می باشید"))
                            "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی جدید(منوی حساب کاربری) اقدام نمایید"
                        else if (message.contains("INTERNAL_SERVER_ERROR") || message.contains("Internal Server Error", ignoreCase = true))
                            "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                                    "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                                    "<br/>" +
                                    "_امکان اتصال به اینترنت وجود ندارد." +
                                    "<br/>" +
                                    "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                                    "<br/>" +
                                    "_اطلاعات ثبت شده برای شما دارای اشکال است."
                         else if (message.contains("sso.to.sa.connection.exception"))
                            "ارتباط بر خط با سازمان ثبت احوال قطع می باشد، لطفا دقایقی دیگر مجددا تلاش نمایید."
                        else if (message.contains("pension.request.current.request.open"))
                            "شما دارای درخواست در حال بررسی می باشید و امکان ثبت درخواست جدید وجود ندارد"
                        else if (message.contains("pension.survivor.request.survivor.already.saved"))
                            "درخواست برای بازمانده مورد نظر قبلا ثبت گردیده است."
                        else if (ValidationUtil.isProbablyArabic(message))
                                message
                            else
                                "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                                        "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                                        "<br/>" +
                                        "_امکان اتصال به اینترنت وجود ندارد." +
                                        "<br/>" +
                                        "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                                        "<br/>" +
                                        "_اطلاعات ثبت شده برای شما دارای اشکال است."

                    }
                    503 -> {
                        message =
                            "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید"
                    }
                    504 -> {
//                        message =
//                            "سرویس دهنده مرکزی در حال بروزرسانی می\u200Cباشد.  پس از چند دقیقه، مجددا تلاش نمائید."
                        message = "با عرض پوزش، سرور در زمان معقول به درخواست پاسخ نداد..ممکن است به دلیل اشکال در اتصال به سرور یا زمان پاسخگویی بیش از حد باشد"
                    }
                }
            }
            else -> {
                message = it.localizedMessage ?: "خطایی رخ داده است"
            }
        }
        message = normalizeRawNetworkMessage(message, errorCode)
        responseNew.baseStatus =
            BaseStatus(MessageModel(message = message, errorCode ?: 0), ServiceStatus.ERROR)
        return responseNew
    }

    fun handleError(it: Exception): MessageModel {
        var message = ""
        var errorCode: Int? = 0
        when (it) {
            is JsonSyntaxException -> {
                errorCode = 1000
//                message = "سیستم در حال به روز رسانی میباشد، لطفا در روزهای آتی دوباره مراجعه فرمایید"
                message =
                    "در حال حاضر ارائه خدمت امکان پذیر نمی باشد، لطفا در روزهای آتی دوباره مراجعه فرمایید"
            }
            is SocketTimeoutException -> {
                message = "زمان درخواست به پایان رسید. لطفاً مجدداً تلاش کنید."
            }
            is NoRouteToHostException -> {
                message = "خطا در برقراری ارتباط"
            }
            is UnknownHostException -> {
                message = "اینترنت شما ضعیف است.دسترسی اینترنت رابررسی کنید"
            }
            is IllegalStateException -> {
                message = it.localizedMessage ?: "ERROR::::::IllegalStateException"

            }
            else -> {
                errorCode = (it as? HttpException)?.response()?.code()
                when (errorCode) {
                    400 ->{
                        if (message.contains("pension.disability.commission.notready.exception"))
                            "نتیجه رای کمیسیون پزشکی هنوز صادر نشده است و امکان ثبت درخواست نمی باشد."
                        else if (message.contains("pension.disability.saved.before.exception"))
                            "درخواست قبلا ذخیره شده است.در صورتیکه اطلاعات هنوز به شعبه ارسال نشده باشد امکان مشاهده و ادامه مراحل از طریق منوی \"کارتابل/درخواست‌های من/عملیات/مشاهده\" وجود دارد."
                        else if(message.contains("pension.disability.history.not.found.exception"))
                            "اطلاعات سابقه یافت نشد امکان ادامه وجود ندارد."
                        else if(message.contains("pension.disability.commission.not.possible.exception"))
                            "شما دارای حکم مستمری فعال می باشید و ثبت درخواست از کارافتادگی مقدور نیست."
                        else if(message.contains("Failed to validate code challenge claim.")||message.contains("invalid_grant"))
                            "ورود شما با خطا مواجه شده است. لطفا دوباره با کلیک دکمه، وارد حساب کاربری خود شوید."
                        else
                            message
                    }
                    403 -> {
                        message = "درخواست از IP یا محدوده جغرافیایی مورد نظر مسدود می باشد. عدم دسترسی از محدوده جغرافیایی"

                    }
                    404 -> {
                        message = it.localizedMessage ?: "404"
                    }
                    422 -> {
                        message = it.localizedMessage ?: "422"
                    }
                    500 -> {

                        if (message.contains("شما فاقد شماره حساب بانکی می باشید"))
                            "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی جدید(منوی حساب کاربری اقدام نمایید)"
                        else if (message.contains("INTERNAL_SERVER_ERROR"))
                            "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                                    "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                                    "<br/>" +
                                    "_امکان اتصال به اینترنت وجود ندارد." +
                                    "<br/>" +
                                    "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                                    "<br/>" +
                                    "_اطلاعات ثبت شده برای شما دارای اشکال است."
                        else
                            if (ValidationUtil.isProbablyArabic(message))
                                message
                            else
                                "کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید.."

                    }
                    503 -> {
                        message =
                            "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید"
                    }
                    504 -> {
                        message =
                            "سرویس دهنده مرکزی در حال بروزرسانی می\u200Cباشد.  پس از چند دقیقه، مجددا تلاش نمائید."
                    }
                }
            }
        }
        message = normalizeRawNetworkMessage(message, errorCode)
        return MessageModel(message, errorCode ?: 0)
    }


    fun <T : BaseResponseNew> handleError(
        errorBody: ResponseBody?,
        errorCode: Int?,
        responseNew: T
    ): T {
        val errorResponse = getErrorResult(errorBody, errorCode)
        val message = normalizeRawNetworkMessage("${errorResponse.data?.message ?: errorResponse.reason}", errorResponse.status ?: errorCode)


        responseNew.baseStatus = when (errorResponse.status) {
            400 ->{
                val errMessage = if (message.contains("pension.disability.commission.notready.exception"))
                    "نتیجه رای کمیسیون پزشکی هنوز صادر نشده است و امکان ثبت درخواست نمی باشد."
                else if (message.contains("pension.disability.saved.before.exception"))
                    "درخواست قبلا ذخیره شده است.در صورتیکه اطلاعات هنوز به شعبه ارسال نشده باشد امکان مشاهده و ادامه مراحل از طریق منوی \"کارتابل/درخواست‌های من/عملیات/مشاهده\" وجود دارد."
                else if(message.contains("pension.disability.history.not.found.exception"))
                    "اطلاعات سابقه یافت نشد امکان ادامه وجود ندارد."
                else if(message.contains("pension.disability.commission.not.possible.exception"))
                    "شما دارای حکم مستمری فعال می باشید و ثبت درخواست از کارافتادگی مقدور نیست."
              else if(message.contains("Failed to validate code challenge claim.")||message.contains("invalid_grant"))
                    "ورود شما با خطا مواجه شده است. لطفا دوباره با کلیک دکمه، وارد حساب کاربری خود شوید."
                else
                    message
                BaseStatus(
                    MessageModel(message = errMessage, errorResponse.status),
                    ServiceStatus.ERROR
                )
            }
            401 -> {
                BaseStatus(
                    MessageModel(message = message, errorResponse.status),
                    ServiceStatus.NEED_REFRESH_TOKEN
                )

                //    Resource.needRefreshToken(MessageModel(message, errorResponse.status))
            }
            403 -> {
                BaseStatus(
                    MessageModel(
                       "دسترسی به سرویس مورد نظر مقدور نمی‌باشد؛ در صورت استفاده از هرگونه VPN یا پروکسی، لطفا آن را غیرفعال و مجدد تلاش نمائید." ,
                        errorResponse.status
                    ), ServiceStatus.ERROR
                )

//                Resource.error(
//                    MessageModel(
//                        if (message == "null") "دسترسی به سرویس مورد نظر مقدور نمی‌باشد؛ در صورت استفاده از هرگونه VPN یا پروکسی، لطفا آن را غیرفعال و مجدد تلاش نمائید." else message,
//                        errorResponse.status
//                    )
//                )
            }
            500 -> {

                val errMessage = if (message.contains("شما فاقد شماره حساب بانکی می باشید"))
                    "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی جدید(منوی حساب کاربری اقدام نمایید)"
                else if (errorResponse.data?.cause == "ProxyProcessingException")
                    message
                else if (message.contains("INTERNAL_SERVER_ERROR") || message.contains("Internal Server Error", ignoreCase = true))
                    "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                            "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                           "<br/>" +
                            "_امکان اتصال به اینترنت وجود ندارد." +
                            "<br/>" +
                            "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                            "<br/>" +
                            "_اطلاعات ثبت شده برای شما دارای اشکال است."

                else if (message.contains("sso.to.sa.connection.exception"))
                    "ارتباط بر خط با سازمان ثبت احوال قطع می باشد، لطفا دقایقی دیگر مجددا تلاش نمایید."
                else if (message.contains("pension.request.current.request.open"))
                    "شما دارای درخواست در حال بررسی می باشید و امکان ثبت درخواست جدید وجود ندارد"
                else if (message.contains("pension.survivor.request.survivor.already.saved"))
                    "درخواست برای بازمانده مورد نظر قبلا ثبت گردیده است."
                else if (ValidationUtil.isProbablyArabic(message))
                        message
                    else
                        "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                                "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                                "<br/>" +
                                "_امکان اتصال به اینترنت وجود ندارد." +
                                "<br/>" +
                                "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                                "<br/>" +
                                "_اطلاعات ثبت شده برای شما دارای اشکال است."
                //"کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید.."


                BaseStatus(
                    MessageModel(message = errMessage, errorResponse.status),
                    ServiceStatus.ERROR
                )
            }
            404 -> {

                BaseStatus(
                    MessageModel(
                        message = "در این لحظه اطلاعات مورد نظر یافت نشد",
                        errorResponse.status
                    ),
                    ServiceStatus.ERROR
                )
            }
            400, 422 -> {
                //message = "موردی یافت نشد"
                //    Resource.error(MessageModel(message, errorResponse.status))
                var localMessage = message
                if(message.contains("Failed to validate code challenge claim.")||message.contains("invalid_grant"))
                    localMessage = "ورود شما با خطا مواجه شده است. لطفا دوباره با کلیک دکمه، وارد حساب کاربری خود شوید."


                BaseStatus(
                    MessageModel(message = localMessage, errorResponse.status),
                    ServiceStatus.ERROR
                )

            }
//            422 -> {
//                Resource.error(MessageModel(message, errorResponse.status))
//            }
//            500, 502 -> {
//                Resource.error(MessageModel(message, errorResponse.status))
//            }
            502 -> {
                BaseStatus(
                    MessageModel(
                        "کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید..",
                        errorResponse.status
                    ), ServiceStatus.ERROR
                )
            }
            503 -> {
                BaseStatus(
                    MessageModel(
                        if (message == "null") "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید."
                        else if (message.contains("(&#1593;&#1583;&#1605; &#1583;&#1587;&#1578;&#1585;&#1587;&#1740;)&#1587;&#1575;&#1586;&#1605;&#1575;&#1606; &#1578;&#1575;&#1605;&#1740;&#1606; &#1575;&#1580;&#1578;&#1605;&#1575;&#1593;&#1740;")) "کاربر گرامی سیستم در حال بروزرسانی می باشد ، لطفاً در روزهای آتی مجدداً مراجعه فرمایید." else message,
                        errorResponse.status
                    ), ServiceStatus.ERROR
                )

//                Resource.error(
//                    MessageModel(
//                        if (message == "null") "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید." else message,
//                        errorResponse.status
//                    )
//                )
            }
            504 -> {
                BaseStatus(
                    MessageModel(
                        if (message == "null") "سرویس دهنده مرکزی در حال بروزرسانی می‌باشد.  پس از چند دقیقه، مجددا تلاش نمائید." else message,
                        errorResponse.status
                    ), ServiceStatus.ERROR
                )

//                Resource.error(
//                    MessageModel(
//                        if (message == "null") "سرویس دهنده مرکزی در حال بروزرسانی می‌باشد.  پس از چند دقیقه، مجددا تلاش نمائید." else message,
//                        errorResponse.status
//                    )
//                )
            }
            204 -> {
                //204 No Content
                val errMessage = "محتوای مورد نظر شما قابل دسترس نیست"

                BaseStatus(
                    MessageModel(message = errMessage, errorResponse.status),
                    ServiceStatus.ERROR
                )
            }
            else -> {
                val errMessage =      "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                        "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                       "<br/>" +
                        "_امکان اتصال به اینترنت وجود ندارد." +
                        "<br/>" +
                        "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                        "<br/>" +
                        "_اطلاعات ثبت شده برای شما دارای اشکال است."

                BaseStatus(
                    MessageModel(message = errMessage, errorResponse.status ?: 2000),
                    ServiceStatus.ERROR
                )
            }

        }
        return responseNew
    }

    fun <T> handleError(errorBody: ResponseBody?, errorCode: Int?): Resource<T> {
        var message = ""
        val errorResponse = getErrorResult(errorBody, errorCode)

        /*   message =
               "${errorResponse.status}:::" +
                       "${errorResponse.family}:::" +
                       "${errorResponse.reason}:::" +
                       "${errorResponse.data?.message}"*/

        message = normalizeRawNetworkMessage("${errorResponse.data?.message}", errorResponse.status ?: errorCode)

        return when (errorResponse.status) {
            401 -> {
                Resource.needRefreshToken(MessageModel(message, errorResponse.status))
            }
            403 -> {
                val errorResponse =Resource.error<T>(
                    MessageModel(
                        "دسترسی به سرویس مورد نظر مقدور نمی‌باشد؛ در صورت استفاده از هرگونه VPN یا پروکسی، لطفا آن را غیرفعال و مجدد تلاش نمائید.",
                        errorResponse.status
                    )
                )
                errorResponse.isBackToPrevious = true
                errorResponse
            }
            404 -> {
                //message = "موردی یافت نشد"
                Resource.error(MessageModel(message, errorResponse.status))
            }
            422 -> {
                Resource.error(MessageModel(message, errorResponse.status))
            }
            500 -> {

                val errMessage = if (message.contains("شما فاقد شماره حساب بانکی می باشید"))
                    "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی جدید(منوی حساب کاربری اقدام نمایید)"
                else if (errorResponse.data?.cause == "ProxyProcessingException")
                    message
                else if (message.contains("INTERNAL_SERVER_ERROR") || message.contains("Internal Server Error", ignoreCase = true))
                    "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                            "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                           "<br/>" +
                            "_امکان اتصال به اینترنت وجود ندارد." +
                            "<br/>" +
                            "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                            "<br/>" +
                            "_اطلاعات ثبت شده برای شما دارای اشکال است."
                else if (ValidationUtil.isProbablyArabic(message))
                        message
                    else
                        "دریافت اطلاعات از سرویس دهنده مرکزی در این لحظه مقدور نیست." +
                                "ممکن است علت اشکال پیش آمده یکی از موارد زیر باشد:" +
                                "<br/>" +
                                "_امکان اتصال به اینترنت وجود ندارد." +
                                "<br/>" +
                                "_به صورت موقت ترافیک سامانه از حد مجاز بیشتر شده است." +
                                "<br/>" +
                                "_اطلاعات ثبت شده برای شما دارای اشکال است."
                //"کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید.."

                Resource.error(MessageModel(message, errorResponse.status))
            }
            502 -> {
                Resource.error(
                    MessageModel(
                        "کاربر گرامی با عرض پوزش اختلالی در سیستم رخ داده است ، لطفاً دقایقی دیگر دوباره تلاش فرمایید...",
                        errorResponse.status
                    )
                )
            }
            503 -> {
                Resource.error(
                    MessageModel(
                        if (message == "null") "در حال حاضر امکان برقراری ارتباط با سرویس دهنده مرکزی وجود ندارد. پس از چند دقیقه، مجددا تلاش نمائید." else message,
                        errorResponse.status
                    )
                )
            }
            504 -> {
                Resource.error(
                    MessageModel(
                        if (message == "null") "سرویس دهنده مرکزی در حال بروزرسانی می‌باشد.  پس از چند دقیقه، مجددا تلاش نمائید." else message,
                        errorResponse.status
                    )
                )
            }
            else -> Resource.error(MessageModel(message, errorResponse.status ?: 0))
        }
    }

    open fun getErrorResult(errorBody: ResponseBody?, errorCode: Int?): ErrorResponse {

        val errorRes = errorBody?.string()
        return if (errorRes?.contains("status") == true) {
            val jsonParser = JsonParser().parse(errorRes)
            if (jsonParser.isJsonNull) {
                (ErrorResponse(errorCode, "UNKNOWN_ERROR", "UNKNOWN_REASON", null))
            } else {
                val jsonObjectError = jsonParser?.asJsonObject
                val status = jsonObjectError?.get("status")?.asInt
                val family = jsonObjectError?.get("family")?.asString
                val reason = jsonObjectError?.get("reason")?.asString
                if (jsonObjectError?.get("data")?.isJsonObject == true) {
                    val data = jsonObjectError.get("data").asJsonObject
                    val message = data?.get("message")?.asString
                    val cause = data?.get("cause")?.asString
                    (ErrorResponse(status, family, reason, ErrorData(cause, message)))
                } else if (jsonObjectError?.get("data")?.isJsonArray == true) {
                    val dataArray = jsonObjectError.get("data").asJsonArray
                    var message = ""
                    try {
                        if (dataArray.size() > 0) {
                            val item = dataArray.get(0).asJsonObject
                            if (item.has("propertyViolations")) {
                                val violations = item.get("propertyViolations").asJsonObject
                                val sb = StringBuilder()
                                for (key in violations.keySet()) {
                                    val msgs = violations.get(key).asJsonArray
                                    if (msgs.size() > 0) {
                                        sb.append(msgs.get(0).asString).append("\n")
                                    }
                                }
                                message = sb.toString()
                            }
                        }
                    } catch (e: Exception) {
                        message = dataArray.toString()
                    }
                     if(message.isEmpty()) message = dataArray.toString()
                    (ErrorResponse(status, family, reason, ErrorData("", message)))
                } else {
                    val data = if(jsonObjectError?.get("data")?.isJsonPrimitive == true) jsonObjectError.get("data").asString else jsonObjectError?.get("data").toString()
                    (ErrorResponse(status, family, reason, ErrorData("", data)))
                }
            }
        } else {
            (ErrorResponse(errorCode, "", errorRes, null))
        }
    }

    inner class ErrorResponse(
        val status: Int?,
        val family: String?,
        val reason: String?,
        val data: ErrorData?
    )

    inner class ErrorData(val cause: String?, val message: String?)


}
