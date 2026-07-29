package com.tamin.taminhamrah.data.entity

import android.net.Uri

data class UploadedFileModel(
    var guid: String? = null,
    var fileName: String? = null,
    var fileType: String? = null,
    var fileUri: Uri? = null
)

