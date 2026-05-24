/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.tools.errorHandling

interface ErrorParser {
    fun parseGeneralError(exception: TaminErrorUriException): TaminApiException
}
