package com.tamin.taminhamrah.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageUtils {
    companion object {
        fun imageDrawable(view: ImageView?, drawableRes: Int?) {

            drawableRes?.let {
                view?.load(it) {
                    placeholder(R.drawable.ic_logo_placeholder)
                    error(R.drawable.ic_logo_placeholder)
                }
            }
        }

        fun setImageResourceDrawable(view: AppCompatImageView, resourceId: Int) {
            view.setImageResource(resourceId)
        }

        fun loadImageBase64(view: ImageView?, image: String?) {

            if (image != null) {
                val decodedString = Base64.decode(image, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                view?.load(decodedByte) {
//                    transformations(RoundedCornersTransformation(20f, 20f, 20f, 20f))
                    placeholder(R.drawable.ic_logo_placeholder)
                    error(R.drawable.ic_logo_placeholder)

                    listener(onSuccess = { _, _ ->
                        // do something
                        val a=1
                    }, onError = { _,_  ->
                        // handle error here
                    })
                }
            }
        }


        fun loadImage(view: ImageView?, imageUrl: String?, isCircleImage: Boolean = false) {
            if (imageUrl != null)

                view?.load(imageUrl) {
                    placeholder(R.drawable.ic_logo_placeholder)
                    error(R.drawable.ic_logo_placeholder)
                    if (isCircleImage) {
                        transformations(CircleCropTransformation())
                    }

                }
        }

        fun loadImage(view: ImageView?, imageUri: Uri?) {
            try {
                if (imageUri != null)
                    view?.load(imageUri) {

                        placeholder(R.drawable.ic_logo_placeholder)
                        error(R.drawable.ic_logo_placeholder)

                    }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        fun loadImage(view: ImageView?, imageUrl: String?, token: String) {
            if (imageUrl != null)
                view?.load(imageUrl) {
//                    crossfade(750)

//                    transformations(RoundedCornersTransformation(50f, 50f, 50f, 50f))
                    placeholder(R.drawable.ic_logo_placeholder)
                    error(R.drawable.ic_logo_placeholder)
                    setHeader(Constants.AUTHENTICATION, token)
                    build()
                }
        }

        fun loadImage(view: ImageView?, imageFile: File) {

            if (imageFile.exists())
                view?.load(imageFile) {
                    placeholder(R.drawable.ic_logo_placeholder)
                    error(R.drawable.ic_logo_placeholder)
                }
        }

        fun loadUserAvatar(view: AppCompatImageView?, encodedImage: String?) {
            view?.let {
                if (!encodedImage.isNullOrBlank() && encodedImage!="null") {
                    val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                    if (decodedByte != null) {
                        view.load(decodedByte) {
                            placeholder(R.drawable.ic_account)
                            error(R.drawable.ic_account)
                            transformations(CircleCropTransformation())
                        }
                    } else {
                        setImageResourceDrawable(view, R.drawable.ic_account)
                    }
                }else{
                    setImageResourceDrawable(view, R.drawable.ic_account)
                }
            }
        }

        fun saveImageExternal(image: Bitmap, context: Context): Uri? {
            //TODO - Should be processed in another thread
            var uri: Uri? = null
            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "${System.currentTimeMillis()}-tamin.png"
                )
                val stream = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                stream.close()
                uri = Uri.fromFile(file)
            } catch (e: IOException) {
                Timber.tag(TAG)
                    .d("IOException while trying to write file for sharing: " + e.message)
            }
            return uri
        }

        /* fun AppCompatImageView.loadSvgOrOthers(myUrl: String?) {
             myUrl?.let {
                 if (it.toLowerCase(Locale.ENGLISH).endsWith("svg")) {
                     val imageLoader = ImageLoader.Builder(this.context)
                         .componentRegistry {
                             add(SvgDecoder(this@loadSvgOrOthers.context))
                         }
                         .build()
                     val request = LoadRequest.Builder(this.context)
                         .data(it)
                         .target(this)
                         .build()
                     imageLoader.execute(request)
                 } else {
                     this.load(myUrl)
                 }
             }
         }
 */

        /*  fun ImageView.loadImageFromUrl( imageUrl: String?, isCircleImage: Boolean=false) {

                  val imageLoader = ImageLoader.Builder(this)
                      .componentRegistry { add(SvgDecoder(context))
                      }
                      .build()

                  val imageRequest = ImageRequest.Builder(context)
                      .crossfade(true)
                      .crossfade(300)
                      .data(imageUrl)
                      .target(
                          onStart = {
                              R.drawable.place_holder
                              //set up an image loader or whatever you need
                          },
                          onSuccess = { result ->
                              val bitmap = (result as BitmapDrawable).bitmap
                              setImageBitmap(bitmap)
                              //dismiss the loader if any
                          },
                          onError = {
                              R.drawable.ic_logo_placeholder
                              */
        /**
         * TODO: set an error drawable
         *//*
                        }
                    )
                    .build()

                imageLoader.enqueue(imageRequest)
            }*/

    }

}