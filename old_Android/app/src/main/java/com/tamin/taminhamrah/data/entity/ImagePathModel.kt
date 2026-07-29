package com.tamin.taminhamrah.data.entity

import android.net.Uri

//created for save original path and compression path
data class ImagePathModel(
     var orgUri: Uri? =null,
     var compressionPath:String?=null
)