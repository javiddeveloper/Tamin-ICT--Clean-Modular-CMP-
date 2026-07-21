package com.tamin.taminhamrah.data.entity

import android.net.Uri
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class DownloadFileResponse(
    var data: String? = null,
    var uri: Uri? = null,
    var detail: DownloadFileModel? = DownloadFileModel()
) : BaseResponseNew()

data class DownloadFileModel(
    var guid: String? = null,
    var fileName: String? = null,
    var fileNameRes: Int = 0,
    var fileType: String? = null,
    var fileUri: Uri? = null
)

