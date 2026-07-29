package com.tamin.taminhamrah.ui.home.services.deservedTreatment

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.fragment.app.viewModels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.DeservedTreatmentResponse
import com.tamin.taminhamrah.databinding.FragmentDeservedTreatmentBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.home.services.deservedTreatment.adapter.DeservedTreatmentStatusAdapter
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint
import saman.zamani.persiandate.PersianDate


@AndroidEntryPoint
class DeservedTreatmentFragment :
    BaseFragment<FragmentDeservedTreatmentBinding, DeservedTreatmentViewModel>(),
    AdapterInterface.OnItemClickListener<ActiveRelation> {

    lateinit var listAdapter: DeservedTreatmentStatusAdapter

    override val mViewModel: DeservedTreatmentViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_deserved_treatment
    }

    override fun setupObserver() {
        mViewModel.mldDeserved.observe(this, ::showResult)
    }

    override fun initView() {
        listAdapter = DeservedTreatmentStatusAdapter()
        // viewDataBinding?.layoutAnimation?.rootLayout?.getTransition(R.id.startTransition)?.isEnabled=false
        //  viewDataBinding?.layoutAnimation?.rootLayout?.getTransition(R.id.betweenTransition)?.trans=false
        //  viewDataBinding?.layoutAnimation?.rootLayout?.visibility = View.GONE
        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        //   viewDataBinding?.labelPensionId?.backgroundTintList = getBackgroundTintList()


    }

    override fun getData() {
        mViewModel.getPersonalInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {

        }
    }

    private fun showResult(result: DeservedTreatmentResponse) {
        if (result.isSuccess && result.data?.list?.isNotEmpty() == true) {
            result.data?.list?.let { listAdapter.setItems(it) }
            val today = PersianDate()
            viewDataBinding?.tvResult?.apply {
                if (result.data?.list?.get(0)?.isDeserved() != true) {
                    text = getString(R.string.message_deserved_treatment, today)
                    //    setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    /*  viewDataBinding?.apply {
                          isActive=true
                      }*/
                } else {
                    text = getString(R.string.message_not_deserved_treatment, today)
                    //  setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    /*   viewDataBinding?.apply {
                           isActive=false
                       }*/
                }
                /*    viewDataBinding?.layoutAnimation?.apply {
                        rootLayout.visibility=View.VISIBLE
                        rootLayout.transitionToEnd()
                    }*/
            }

            val bitmap = encodeAsBitmap(result.data?.list?.get(0)?.natCode)
            viewDataBinding?.imgBarcode?.setImageBitmap(bitmap)
        }
    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(nationalId: String?): Bitmap? {
        val content = "https://eservices.tamin.ir/pwa/#/medical/detail/$nationalId"
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE, 512, 512, null
            )
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val width = bitMatrix.width
        val height = bitMatrix.height

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun generateQrCode(nationalId: String?) {
        val content = "https://eservices.tamin.ir/pwa/#/medical/detail/$nationalId"

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = intArrayOf()
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (bitMatrix[j, i]) {
                    if (j == 0) {
                        pixels[i*width+j] = 0x000000 // RED
                    } else {
                        pixels[i*width+j] = 0x888888 // BLACK
                    }
                } else {
                    pixels[i*width+j] = 0x444444 // WHITE
                }
            }
        }

        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels (pixels, 0 , width, 0 , 0 , width, height)

       /*   val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
             for (y in 0 until height) {
                 bitmap.setPixel(
                     x,
                     y,
                     if (bitMatrix.get(x, y))
                         ContextCompat.getColor(requireContext(), R.color.black)
                     else Color.WHITE
                 )
             }
         }*/

        viewDataBinding?.imgBarcode?.setImageBitmap(bitmap)
    }

    override fun onItemClick(item: ActiveRelation, transitionView: View?, tag: String?) {
        handlePageDestination(R.id.action_active_relation_to_certificate)
    }
}