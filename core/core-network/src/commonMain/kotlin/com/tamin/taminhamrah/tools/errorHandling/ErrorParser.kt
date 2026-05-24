/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.tools.errorHandling

import com.tamin.core.network.tools.errorHandling.TaminApiException
import com.tamin.core.network.tools.errorHandling.TaminErrorUriException

interface ErrorParser {
    fun parseGeneralError(exception: TaminErrorUriException): TaminApiException
}
