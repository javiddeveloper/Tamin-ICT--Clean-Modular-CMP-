package com.tamin.taminhamrah.ui.appinterface

import com.tamin.taminhamrah.data.entity.ImagePathModel
import java.io.File

class ImagePickerResult {
     interface CompressionUri {
          fun onResultFile(file: File)
     }
     interface GetPathIamge{
          fun OnResult(uriList:ArrayList<ImagePathModel>)
     }
}