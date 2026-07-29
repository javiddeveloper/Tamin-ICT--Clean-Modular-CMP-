package com.tamin.taminhamrah.data.remote.models.pdfFile

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import java.io.InputStream


class PdfDownloadResponse:BaseResponseNew(){
    var pdf:InputStream? = null
}
