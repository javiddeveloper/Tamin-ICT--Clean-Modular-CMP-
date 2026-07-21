package com.tamin.taminhamrah.utils

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class PickImageUtils {

  companion object {
      private const val REQUEST_CAMERA_AND_GALLERY_PERMISSIONS = 111


       fun hasPermission(context: Context, permission: String): Boolean =
           ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

       fun hasPermissionsOfList(context: Context, permissions: Array<String>): Boolean = permissions.all {
          ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
      }

      fun getImageRequestBody(context:Context, uri: Uri,fromCamera:Boolean): MultipartBody.Part? {
          val selectedImage: Uri = uri
          val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

          var body: MultipartBody.Part?=null
          if (fromCamera) {
              uri.path?.let { path ->
                  val file = File(path)
                  val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                  body = MultipartBody.Part.createFormData(
                      "file",
                      file.name,
                      requestFile
                  )
              }
          }else {
              val cursor: Cursor? = context.contentResolver.query(
                  selectedImage,
                  filePathColumn,
                  null,
                  null,
                  null
              )
              if (cursor != null) {
                  cursor.moveToFirst()
                  val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                  val picturePath = cursor.getString(columnIndex)
                  val file = File(picturePath)
                  val requestFile: RequestBody =
                      file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                  body = MultipartBody.Part.createFormData(
                      "file",
                      file.name,
                      requestFile
                  )
                  cursor.close()
              }
          }
          return body
      }

      fun getImageBody(path: String): MultipartBody.Part {
          val file = File(path)
          val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
          return MultipartBody.Part.createFormData(
              "file",
              file.name,
              requestFile
          )
      }


  }
}