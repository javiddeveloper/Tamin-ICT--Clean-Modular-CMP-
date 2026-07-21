package com.tamin.taminhamrah.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.util.Base64
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ImageHelper {

  companion object {
      fun compressImage(bmp_source: Bitmap): Bitmap {
          val nh = (bmp_source.height * (1024.0 / bmp_source.width)).toInt()
          return Bitmap.createScaledBitmap(bmp_source, 1024, nh, true)
      }

      fun compressImageForUpload(file: File, newFile: File): File {
          val bitmap =BitmapFactory.decodeFile(file.path)
          val os: OutputStream = BufferedOutputStream(FileOutputStream(newFile))
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
          os.close()
          return newFile
      }


      fun bitmapToBase64(bmp: Bitmap): String? {
          val byteArrayOutputStream = ByteArrayOutputStream()
          bmp.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
          val byteArray = byteArrayOutputStream.toByteArray()
          return Base64.encodeToString(byteArray, Base64.DEFAULT)
      }

      fun base64ToBitmap(encodedImage: String?): Bitmap? {
          val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
          return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
      }

      fun bitmapToByte(bitmap: Bitmap): ByteArray? {
          val stream = ByteArrayOutputStream()
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
          return stream.toByteArray()
      }

      private fun exifToDegrees(exifOrientation: Int): Int {
          when (exifOrientation) {
              ExifInterface.ORIENTATION_ROTATE_90 -> {
                  return 90
              }
              ExifInterface.ORIENTATION_ROTATE_180 -> {
                  return 180
              }
              ExifInterface.ORIENTATION_ROTATE_270 -> {
                  return 270
              }
              else -> return 0
          }
      }
  }


}